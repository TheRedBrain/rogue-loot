package com.github.theredbrain.rogueloot.data;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(Identifier identifier, ServerLevel world, ServerPlayer serverPlayerEntity, Vec3 pos, int rolls, int choices, @Nullable ItemStack itemStack) {
//		if (isLootablesLoaded) {
//			LootablesCompat.supplyLootableLoot(identifier, world, serverPlayerEntity, pos, rolls, choices, withChoice, itemStack);
//		} else {
//			info("Tried to supply loot via Lootables, but the mod is not installed!");
//		}
	}

	public static void removeRogueLootTableUses(ServerLevel world, Vec3 pos, String identifierString, @Nullable ServerPlayer serverPlayerEntity) {
//		if (isLootablesLoaded) {
//			LootablesCompat.removeLootableUses(world, pos, identifierString, serverPlayerEntity);
//		}
	}

	public static void removeRogueLootTableUses(ServerLevel world, Vec3 pos, Identifier identifier, @Nullable ServerPlayer serverPlayerEntity) {
//		if (isLootablesLoaded) {
//			LootablesCompat.removeLootableUses(world, pos, identifier, serverPlayerEntity);
//		}
	}

}
