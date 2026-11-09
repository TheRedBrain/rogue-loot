package com.github.theredbrain.rogueloot.mixin.client.network;

import com.github.theredbrain.rogueloot.block.entity.InteractiveLootBlockEntity;
import com.github.theredbrain.rogueloot.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rogueloot.gui.screen.ingame.InteractiveLootBlockScreen;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer implements DuckPlayerEntityMixin {

	@Shadow
	@Final
	protected Minecraft minecraft;

	public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
		super(world, profile);
	}

	@Override
	public void lootableblocks$openInteractiveLootBlockScreen(InteractiveLootBlockEntity interactiveLootBlockEntity) {
		this.minecraft.setScreen(new InteractiveLootBlockScreen(interactiveLootBlockEntity));
	}

}
