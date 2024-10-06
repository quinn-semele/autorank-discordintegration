package dev.compasses.autorank_discordintegration;

import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.storage.Configuration;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.PlayerLink;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(value = "autorank_discordintegration", dist = Dist.DEDICATED_SERVER)
public class AutoRanks {
    public static final Logger LOGGER = LoggerFactory.getLogger("AutoRank for DiscordIntegration");
    public static final ConcurrentLinkedQueue<GameProfile> QUEUE = new ConcurrentLinkedQueue<>();
    public static boolean RUNNING = true;
    private static final Thread WORKER_THREAD = new Thread() {
        @Override
        public void run() {
            while (RUNNING) {
                GameProfile gameProfile = QUEUE.poll();

                if (gameProfile != null) {
                    AutoRanks.LOGGER.debug("Processing queue entry for: {}", gameProfile.getName());
                    assignRoles(gameProfile);
                }

                try {
                    wait(5000);
                } catch (InterruptedException e) {
                    // NO-OP
                }
            }
        }
    };

    public AutoRanks(IEventBus bus, ModContainer container) {
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> WORKER_THREAD.start());
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> RUNNING = false);
        NeoForge.EVENT_BUS.addListener(AutoRanks::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        if (event.getCommandSelection() == Commands.CommandSelection.DEDICATED) {

            event.getDispatcher().register(Commands.literal("autoranks")
                    .requires(source -> source.hasPermission(3))
                    .then(Commands.literal("reload").executes(context -> {
                AutoRanksConfig.NEEDS_RELOAD = true;
                return Command.SINGLE_SUCCESS;
            })));
        }
    }

    public static void queueRollAssignment(GameProfile gameProfile) {
        if (Configuration.instance().linking.whitelistMode && ServerLifecycleHooks.getCurrentServer().usesAuthentication()) {
            LinkManager.checkGlobalAPI(gameProfile.getId());
            if (LinkManager.isPlayerLinked(gameProfile.getId())) {
                QUEUE.offer(gameProfile);
            }
        }
    }

    public static void assignRoles(GameProfile gameProfile) {
        PlayerLink link = LinkManager.getLink(null, gameProfile.getId());

        if (link == null) return;

        HashBiMap<String, Rank> checks = AutoRanksConfig.load();

        if (checks.isEmpty()) return;

        try {
            Member discordMember = DiscordIntegration.INSTANCE.getMemberById(link.discordID);

            if (discordMember == null) {
                AutoRanks.LOGGER.error("Failed to get the discord user for {}, it is null?", gameProfile.getName());
                return;
            }

            Set<Rank> addedRanks = Set.copyOf(FTBRanksAPI.manager().getAddedRanks(gameProfile));
            Set<Rank> ranksToRemove = new HashSet<>();
            Set<Rank> ranksToAdd = new HashSet<>();

            for (Rank rank : addedRanks) {
                String role = checks.inverse().get(rank);
                Role actual = findRole(discordMember.getRoles(), role);

                if (actual != null) {
                    if (addedRanks.contains(rank)) {
                        ranksToRemove.add(rank);
                    }
                }
            }

            for (Role role : discordMember.getRoles()) {
                Rank rank = checks.get(role.getId());

                if (rank != null) {
                    if (!addedRanks.contains(rank)) {
                        ranksToAdd.add(rank);
                    }
                }
            }

            ServerLifecycleHooks.getCurrentServer().execute(() -> {
                for (Rank rank : ranksToRemove) {
                    rank.remove(gameProfile);
                    AutoRanks.LOGGER.info("Removing {}({}) from {}({})", rank.getName(), rank.getId(), gameProfile.getName(), gameProfile.getId());
                }

                for (Rank rank : ranksToAdd) {
                    rank.add(gameProfile);
                    AutoRanks.LOGGER.info("Adding {}({}) from {}({})", rank.getName(), rank.getId(), gameProfile.getName(),gameProfile.getId());
                }
            });
        } catch (AssertionError error) {
            AutoRanks.LOGGER.error("Failed to get the discord user for {}: ", gameProfile.getName(), error);
        }
    }

    private static Role findRole(List<Role> roles, String id) {
        for (Role role : roles) {
            if (id.equals(role.getId())) {
                return role;
            }
        }

        return null;
    }
}