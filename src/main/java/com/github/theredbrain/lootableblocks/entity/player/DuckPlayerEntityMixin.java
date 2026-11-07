package com.github.theredbrain.lootableblocks.entity.player;

import com.github.theredbrain.lootableblocks.block.entity.InteractiveLootBlockEntity;

public interface DuckPlayerEntityMixin {

	default void lootableblocks$openInteractiveLootBlockScreen(InteractiveLootBlockEntity interactiveLootBlockEntity) {}

}
