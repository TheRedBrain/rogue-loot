package com.github.theredbrain.rogueloot.compatibility;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.AddExperienceLevelType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.AddExperienceType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.AddPointsType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.LockCategoryType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.LockSkillType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.SetExperienceLevelType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.SetExperienceType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.SetPointsType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.UnlockCategoryType;
import com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills.UnlockSkillType;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.puffish.skillsmod.SkillsMod;

public class PufferfishsSkillsCompat {

	public static void init() {
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/add_experience_level"), AddExperienceLevelType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/add_experience"), AddExperienceType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/add_points"), AddPointsType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/lock_category"), LockCategoryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/lock_skill"), LockSkillType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/set_experience_level"), SetExperienceLevelType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/set_experience"), SetExperienceType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/set_points"), SetPointsType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/unlock_category"), UnlockCategoryType.MAP_CODEC);
		Registry.register(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_COMPONENT_TYPE_REGISTRY, RogueLoot.identifier("compat/puffish_skills/unlock_skill"), UnlockSkillType.MAP_CODEC);
	}

	public static void addPoints(ServerPlayer player, Identifier categoryId, Identifier source, int count, boolean isSilent) {
		SkillsMod.getInstance().addPoints(player, categoryId, source, count, isSilent);
	}

	public static void setPoints(ServerPlayer player, Identifier categoryId, Identifier source, int count, boolean isSilent) {
		SkillsMod.getInstance().setPoints(player, categoryId, source, count, isSilent);
	}

	public static void addExperience(ServerPlayer player, Identifier categoryId, int count) {
		SkillsMod.getInstance().addExperience(player, categoryId, count);
	}

	public static void setExperience(ServerPlayer player, Identifier categoryId, int count) {
		SkillsMod.getInstance().setExperience(player, categoryId, count);
	}

	public static void addExperienceLevel(ServerPlayer player, Identifier categoryId, int count) {
		SkillsMod.getInstance().addExperienceLevel(player, categoryId, count);
	}

	public static void setExperienceLevel(ServerPlayer player, Identifier categoryId, int count) {
		SkillsMod.getInstance().setExperienceLevel(player, categoryId, count);
	}

	public static void unlockSkill(ServerPlayer player, Identifier categoryId, String skillId) {
		SkillsMod.getInstance().unlockSkill(player, categoryId, skillId);
	}

	public static void lockSkill(ServerPlayer player, Identifier categoryId, String skillId) {
		SkillsMod.getInstance().lockSkill(player, categoryId, skillId);
	}

	public static void unlockCategory(ServerPlayer player, Identifier categoryId) {
		SkillsMod.getInstance().unlockCategory(player, categoryId);
	}

	public static void lockCategory(ServerPlayer player, Identifier categoryId) {
		SkillsMod.getInstance().lockCategory(player, categoryId);
	}
}
