package com.github.theredbrain.rogueloot.entity.player;

import com.github.theredbrain.rogueloot.block.entity.InteractiveLootBlockEntity;

public interface DuckPlayerEntityMixin {

	default void lootableblocks$openInteractiveLootBlockScreen(InteractiveLootBlockEntity interactiveLootBlockEntity) {}

}
