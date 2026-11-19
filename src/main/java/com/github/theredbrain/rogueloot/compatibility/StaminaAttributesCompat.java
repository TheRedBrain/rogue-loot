package com.github.theredbrain.rogueloot.compatibility;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.compatibility.stamina_attributes.AddStaminaType;
import com.github.theredbrain.rogueloot.compatibility.stamina_attributes.SetStaminaType;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import net.minecraft.core.Registry;

public class StaminaAttributesCompat {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("add_stamina"), AddStaminaType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("set_stamina"), SetStaminaType.MAP_CODEC);
	}
}
