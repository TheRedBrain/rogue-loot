package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.AddMobEffectInstanceEntryComponentType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.GiveItemStackEntryComponentType;
import net.minecraft.core.Registry;

public class RogueLootTableEntryComponentTypes {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("apply_mob_effect"), AddMobEffectInstanceEntryComponentType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("give_item_stack"), GiveItemStackEntryComponentType.MAP_CODEC);
	}

}
