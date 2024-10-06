package dev.compasses.autorank_discordintegration;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class AutoRanksConfig {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("autoranks_discordintegration.json");
    private static final HashBiMap<String, Rank> ROLES = HashBiMap.create();
    public static boolean NEEDS_RELOAD = true;

    public static HashBiMap<String, Rank> load() {
        if (!NEEDS_RELOAD) {
            return ROLES;
        }

        if (Files.exists(PATH)) {
            try (var reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                JsonObject object = GSON.fromJson(reader, JsonObject.class);

                if (object.has("roles") && object.get("roles") instanceof JsonObject rolesObj) {
                    Collection<Rank> ftbRanks = FTBRanksAPI.manager().getAllRanks();

                    rolesObj.entrySet().stream().map(it -> Map.entry(it.getKey(), it.getValue().getAsString())).forEach(entry -> {
                        Rank rank = findFTBRank(entry.getValue(), ftbRanks);

                        if (entry.getKey() == null) {
                            AutoRanks.LOGGER.error("Skipping {} = {}, discord role id is null?", null, entry.getValue());
                        } else {
                            if (rank != null) {
                                ROLES.put(entry.getKey(), rank);
                            } else {
                                AutoRanks.LOGGER.error("Skipping {} = {}, cannot find a ftb rank with that id or name.", entry.getKey(), entry.getValue());
                            }
                        }
                    });
                } else {
                    AutoRanks.LOGGER.error("Expected a map of ");
                }
            } catch (IOException error) {
                AutoRanks.LOGGER.error("Failed to read config.", error);
            }
        } else {
            try (var writer = GSON.newJsonWriter(Files.newBufferedWriter(PATH, StandardCharsets.UTF_8))) {
                writer.beginObject();
                writer.name("_comment").value("Should be a mapping of discord role id/discord role name -> ftb rank id/ftb rank name");
                writer.name("roles").beginObject().endObject();
                writer.endObject();
            } catch (IOException error) {
                AutoRanks.LOGGER.error("Failed to create default config.", error);
            }
        }

        NEEDS_RELOAD = false;
        return ROLES;
    }

    private static Rank findFTBRank(String key, Collection<Rank> ranks) {
        for (Rank rank : ranks) {
            if (key.equals(rank.getId())) {
                return rank;
            }
        }

        return null;
    }
}
