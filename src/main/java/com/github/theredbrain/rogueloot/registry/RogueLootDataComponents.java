package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.world.item.component.SupplyRogueLootOnUseDataComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class RogueLootDataComponents {
	public static final DataComponentType<SupplyRogueLootOnUseDataComponent> SUPPLY_ROGUE_LOOT_ON_USE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			RogueLoot.identifier("supply_rogue_loot_on_use"),
			DataComponentType.<SupplyRogueLootOnUseDataComponent>builder().persistent(SupplyRogueLootOnUseDataComponent.CODEC).build()
	);

	public static void bootstrap() {
	}
}
