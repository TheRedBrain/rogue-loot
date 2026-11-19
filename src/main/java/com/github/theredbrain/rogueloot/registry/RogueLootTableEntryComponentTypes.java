package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.BanishRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.DealDamageType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.HealToXPercentType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.HealXAmountType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.LockRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.MobEffectInstanceType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.ItemStackType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.AdvancementCriterionType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.PlaySoundType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RandomComponentsType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UnbanishRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UnlockRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.VanillaLootTableType;
import net.minecraft.core.Registry;

public class RogueLootTableEntryComponentTypes {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("advancement_criterion"), AdvancementCriterionType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("banish_rogue_loot_table_entries"), BanishRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("deal_damage"), DealDamageType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("heal_to_x_percent"), HealToXPercentType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("heal_x_amount"), HealXAmountType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("item_stack"), ItemStackType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("lock_rogue_loot_table_entries"), LockRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("mob_effect_instance"), MobEffectInstanceType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("play_sound"), PlaySoundType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("random_components"), RandomComponentsType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("unbanish_rogue_loot_table_entries"), UnbanishRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("unlock_rogue_loot_table_entries"), UnlockRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("vanilla_loot_table"), VanillaLootTableType.MAP_CODEC);
	}

}
