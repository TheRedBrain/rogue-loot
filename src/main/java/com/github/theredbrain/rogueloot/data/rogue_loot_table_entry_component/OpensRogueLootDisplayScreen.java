package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface OpensRogueLootDisplayScreen {
	void openRogueLootDisplayScreen(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final List<Identifier> additionalEntryIdentifiers);
}
