package com.github.theredbrain.rogueloot.compatibility;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.compatibility.mana_attributes.AddManaType;
import com.github.theredbrain.rogueloot.compatibility.mana_attributes.SetManaType;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import net.minecraft.core.Registry;

public class ManaAttributesCompat {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("add_mana"), AddManaType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("set_mana"), SetManaType.MAP_CODEC);
	}
}
