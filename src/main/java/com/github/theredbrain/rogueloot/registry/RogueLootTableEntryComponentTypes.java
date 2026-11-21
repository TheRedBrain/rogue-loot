package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.AddExperienceLevelType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.AddExperiencePointType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.BanishRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.DealDamageType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.HealToXPercentType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.HealXAmountType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.ModifyEquipmentType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.ReserveRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.MobEffectInstanceType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.ItemStackType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.AdvancementCriterionType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.PlaySoundType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RandomComponentsType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.SetExperienceLevelType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.SetExperiencePointType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UnbanishRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UnreserveRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UnuseRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.UseRogueLootTableEntryType;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.VanillaLootTableType;
import net.minecraft.core.Registry;

public class RogueLootTableEntryComponentTypes {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("add_experience_level"), AddExperienceLevelType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("add_experience_point"), AddExperiencePointType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("advancement_criterion"), AdvancementCriterionType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("banish_rogue_loot_table_entries"), BanishRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("deal_damage"), DealDamageType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("heal_to_x_percent"), HealToXPercentType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("heal_x_amount"), HealXAmountType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("item_stack"), ItemStackType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("mob_effect_instance"), MobEffectInstanceType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("modify_equipment"), ModifyEquipmentType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("play_sound"), PlaySoundType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("random_components"), RandomComponentsType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("reserve_rogue_loot_table_entries"), ReserveRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("set_experience_level"), SetExperienceLevelType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("set_experience_point"), SetExperiencePointType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("unbanish_rogue_loot_table_entries"), UnbanishRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("unreserve_rogue_loot_table_entries"), UnreserveRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("unuse_rogue_loot_table_entries"), UnuseRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("use_rogue_loot_table_entries"), UseRogueLootTableEntryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("vanilla_loot_table"), VanillaLootTableType.MAP_CODEC);
	}

}
