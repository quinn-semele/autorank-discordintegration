package dev.compasses.autorank_discordintegration.mixin;

import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.PlayerLink;
import dev.compasses.autorank_discordintegration.AutoRanks;
import dev.compasses.autorank_discordintegration.AutoRanksConfig;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class ModifyRanksOnJoin {
    @Inject(
            method = "canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD")
    )
    private void autorank_discordintegration$modifyRanks(SocketAddress socketAddress, GameProfile gameProfile, CallbackInfoReturnable<Component> cir) {
        AutoRanks.assignRoles(gameProfile);
    }
}
