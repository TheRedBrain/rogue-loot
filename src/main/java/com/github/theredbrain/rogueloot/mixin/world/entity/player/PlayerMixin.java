package com.github.theredbrain.rogueloot.mixin.world.entity.player;

import com.github.theredbrain.rogueloot.entity.player.DuckPlayerMixin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin implements DuckPlayerMixin {
}
