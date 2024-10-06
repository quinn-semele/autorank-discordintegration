package dev.compasses.autorank_discordintegration.mixin;

import com.mojang.authlib.GameProfile;
import dev.compasses.autorank_discordintegration.AutoRanks;
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
        AutoRanks.queueRollAssignment(gameProfile);
    }
}
