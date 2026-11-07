package com.github.theredbrain.lootableblocks.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class LootablesCompat {

	public static void supplyLootableLoot(Identifier identifier, ServerWorld world, ServerPlayerEntity serverPlayerEntity, Vec3d pos, int rolls, int choices, boolean withChoice, @Nullable ItemStack itemStack) {
//		if (withChoice) {
//			LootablesApi.supplyLootWithChoices(
//					identifier,
//					serverPlayerEntity,
//					pos,
//					(serverPlayerEntity1, vec3d) -> {
//
//						BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
//						BlockEntity blockEntity = world.getBlockEntity(blockPos);
//						if (blockEntity instanceof InteractiveLootBlockEntity interactiveLootBlockEntity) {
//							InteractiveLootBlock.lootWasSupplied(serverPlayerEntity, interactiveLootBlockEntity);
//						}
//						if (blockEntity instanceof LootableVaultBlockEntity lootableVaultBlockEntity) {
//							lootableVaultBlockEntity.markAsRewarded(serverPlayerEntity, itemStack);
//						}
//					},
//					(serverPlayerEntity2, vec3d) -> {
//						// gets called when player leaves choices screen without making a choice
//						BlockEntity blockEntity = world.getBlockEntity(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z));
//						if (blockEntity instanceof InteractiveLootBlockEntity interactiveLootBlockEntity) {
//							interactiveLootBlockEntity.removePlayerFromSet(serverPlayerEntity);
//						}
//						if (blockEntity instanceof LootableVaultBlockEntity lootableVaultBlockEntity) {
//							lootableVaultBlockEntity.unmarkAsRewarded(serverPlayerEntity);
//						}
//					},
//					getIdKeyForIdentifier(world, pos, identifier),
//					rolls,
//					choices
//			);
//		} else {
//			LootablesApi.supplyLootRandomly(
//					identifier,
//					serverPlayerEntity,
//					pos,
//					null,
//					rolls
//			);
//			BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
//			BlockEntity blockEntity = world.getBlockEntity(blockPos);
//			if (blockEntity instanceof InteractiveLootBlockEntity interactiveLootBlockEntity) {
//				InteractiveLootBlock.lootWasSupplied(serverPlayerEntity, interactiveLootBlockEntity);
//			}
//			if (blockEntity instanceof LootableVaultBlockEntity lootableVaultBlockEntity) {
//				lootableVaultBlockEntity.markAsRewarded(serverPlayerEntity, itemStack);
//			}
//		}
	}

	public static void removeLootableUses(ServerWorld world, Vec3d pos, String identifierString, @Nullable ServerPlayerEntity serverPlayerEntity) {
		removeLootableUses(world, pos, Identifier.of(identifierString), serverPlayerEntity);
	}

	public static void removeLootableUses(ServerWorld world, Vec3d pos, Identifier identifier, @Nullable ServerPlayerEntity serverPlayerEntity) {
//		if (serverPlayerEntity != null) {
//			LootablesApi.resetKey(getIdKeyForIdentifier(world, pos, identifier), serverPlayerEntity);
//		} else {
//			LootablesApi.resetKey(getIdKeyForIdentifier(world, pos, identifier), world.getServer());
//		}
	}

//	private static IdKey getIdKeyForIdentifier(ServerWorld world, Vec3d pos, Identifier identifier) {
//		return new IdKey(LootableBlocks.identifier(world.getRegistryKey().getRegistry().toTranslationKey() + "_" + world.getRegistryKey().getValue().toTranslationKey() + "_" + identifier.toTranslationKey() + "_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ()));
//	}

}
