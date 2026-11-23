package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.world.item.component.SupplyRogueLootOnUseDataComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RogueLootServerEvents {
	public static void initializeEvents() {
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (entity instanceof ServerPlayer serverPlayer) {
				DataAttachmentHelper.unbanishRogueLootEntry(serverPlayer, null);
			}
		});
		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (!player.isSpectator()) {
				ItemStack itemStack = player.getItemInHand(hand);
				SupplyRogueLootOnUseDataComponent supplyRogueLootOnUseDataComponent = itemStack.get(RogueLootDataComponents.SUPPLY_ROGUE_LOOT_ON_USE);
				if (supplyRogueLootOnUseDataComponent != null) {
					if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel && supplyRogueLootOnUseDataComponent.rogueLootTableIdentifier().isPresent()) {
						Identifier rogueLootTableIdentifier = supplyRogueLootOnUseDataComponent.rogueLootTableIdentifier().get();
						int modifiedRolls = RogueLootTableHelper.calculateModifiedRolls(serverLevel, serverPlayer, rogueLootTableIdentifier, supplyRogueLootOnUseDataComponent.rolls());
						int modifiedMinChoices = RogueLootTableHelper.calculateModifiedMinChoices(serverLevel, serverPlayer, rogueLootTableIdentifier, supplyRogueLootOnUseDataComponent.minChoices());
						int modifiedMaxChoices = RogueLootTableHelper.calculateModifiedMaxChoices(serverLevel, serverPlayer, rogueLootTableIdentifier, supplyRogueLootOnUseDataComponent.maxChoices());
						RogueLootTableHelper.supplyRogueLootTable(serverLevel, serverPlayer, new GeneratedRogueLootData(rogueLootTableIdentifier, Identifier.fromNamespaceAndPath(rogueLootTableIdentifier.getNamespace(), rogueLootTableIdentifier.getPath() + "_" + supplyRogueLootOnUseDataComponent.uniqueSavingToken()), supplyRogueLootOnUseDataComponent.rolls(), modifiedRolls, supplyRogueLootOnUseDataComponent.minChoices(), modifiedMinChoices, supplyRogueLootOnUseDataComponent.maxChoices(), modifiedMaxChoices, Optional.empty(), Optional.of(itemStack), Optional.of(hand)));
						return InteractionResult.SUCCESS;
					}
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
	}
}
