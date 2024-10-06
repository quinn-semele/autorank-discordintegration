package dev.compasses.autorank_discordintegration;

import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.PlayerLink;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("autorank_discordintegration")
public class AutoRanks {
    public static final Logger LOGGER = LoggerFactory.getLogger("AutoRank for DiscordIntegration");

    public AutoRanks(IEventBus bus, ModContainer container) {
        // no-op
    }

    public static void assignRoles(GameProfile gameProfile) {
        PlayerLink link = LinkManager.getLink(null, gameProfile.getId());

        if (link == null) return;

        HashBiMap<Role, Rank> checks = AutoRanksConfig.load();

        if (checks.isEmpty()) return;

        try {
            Member discordMember = DiscordIntegration.INSTANCE.getMemberById(link.discordID);

            if (discordMember == null) {
                AutoRanks.LOGGER.error("Failed to get the discord user for {}, it is null?", gameProfile.getName());
                return;
            }

            for (Rank rank : FTBRanksAPI.manager().getAddedRanks(gameProfile)) {
                Role role = checks.inverse().get(rank);

                if (role != null) {
                    if (!discordMember.getRoles().contains(role)) {
                        if (FTBRanksAPI.manager().getAddedRanks(gameProfile).contains(rank)) {
                            rank.remove(gameProfile);
                            AutoRanks.LOGGER.info("Removing {}({}) from {}({})", role.getName(), role.getId(), gameProfile.getName(), gameProfile.getId());
                        }
                    }
                }
            }

            for (Role role : discordMember.getRoles()) {
                Rank rank = checks.get(role);

                if (rank != null) {
                    if (!FTBRanksAPI.manager().getAddedRanks(gameProfile).contains(rank)) {
                        rank.add(gameProfile);
                        AutoRanks.LOGGER.info("Adding {}({}) from {}({})", role.getName(), role.getId(), gameProfile.getName(),gameProfile.getId());
                    }
                }
            }
        } catch (AssertionError error) {
            AutoRanks.LOGGER.error("Failed to get the discord user for {}: ", gameProfile.getName(), error);
        }
    }
}