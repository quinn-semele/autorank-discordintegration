package dev.compasses.autorank_discordintegration;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import net.dv8tion.jda.api.entities.Role;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AutoRanksConfig {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("autoranks_discordintegration.json");
    private static final HashBiMap<Role, Rank> ROLES = HashBiMap.create();
    private static boolean LOADED = false;

    public static boolean isLoaded() {
        return LOADED;
    }

    public static HashBiMap<Role, Rank> load() {
        if (LOADED) {
            return ROLES;
        }

        if (Files.exists(PATH)) {
            try (var reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                JsonObject object = GSON.fromJson(reader, JsonObject.class);

                if (object.has("roles") && object.get("roles") instanceof JsonObject rolesObj) {
                    List<Role> discordRoles = DiscordIntegration.INSTANCE.getJDA().getRoles();
                    Collection<Rank> ftbRanks = FTBRanksAPI.manager().getAllRanks();

                    rolesObj.entrySet().stream().map(it -> Map.entry(it.getKey(), it.getValue().getAsString())).forEach(entry -> {
                        Role role = findDiscordRole(entry.getKey(), discordRoles);

                        if (role != null) {
                            Rank rank = findFTBRank(entry.getValue(), ftbRanks);

                            if (rank != null) {
                                ROLES.put(role, rank);
                            } else {
                                AutoRanks.LOGGER.error("Skipping {} = {}, cannot find a ftb rank with that id or name.", entry.getKey(), entry.getValue());
                            }
                        } else {
                            AutoRanks.LOGGER.error("Skipping {} = {}, cannot find a discord role with that id or name.", entry.getKey(), entry.getValue());
                        }
                    });
                } else {
                    AutoRanks.LOGGER.error("Expected a map of ");
                }
            } catch (IOException e) {
                AutoRanks.LOGGER.error("Failed to read config.");
            }
        } else {
            try (var writer = GSON.newJsonWriter(Files.newBufferedWriter(PATH, StandardCharsets.UTF_8))) {
                writer.beginObject();
                writer.name("_comment").value("Should be a mapping of discord role id/discord role name -> ftb rank id/ftb rank name");
                writer.name("roles").beginObject().endObject();
                writer.endObject();
            } catch (IOException e) {
                AutoRanks.LOGGER.error("Failed to create default config.");
            }
        }

        LOADED = true;
        return ROLES;
    }

    private static Role findDiscordRole(String key, List<Role> roles) {
        for (Role role : roles) {
            if (key.equals(role.getId()) || key.equals(role.getName())) {
                return role;
            }
        }

        return null;
    }

    private static Rank findFTBRank(String key, Collection<Rank> ranks) {
        for (Rank rank : ranks) {
            if (key.equals(rank.getId()) || key.equals(rank.getName())) {
                return rank;
            }
        }

        return null;
    }
}
