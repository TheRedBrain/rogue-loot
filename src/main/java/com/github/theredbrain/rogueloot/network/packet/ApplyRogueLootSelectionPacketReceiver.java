package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.block.InteractiveRogueLootBlock;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.block.entity.RogueLootVaultBlockEntity;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.registry.RogueLootDataComponents;
import com.github.theredbrain.rogueloot.world.item.component.SupplyRogueLootOnUseDataComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ApplyRogueLootSelectionPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<ApplyRogueLootSelectionPacket> {

	@Override
	public void receive(final ApplyRogueLootSelectionPacket payload, final ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		List<Identifier> chosenRogueLootEntries = payload.chosenRogueLootEntries();
		GeneratedRogueLootData generatedRogueLootData = payload.generatedRogueLootData();

		RogueLootTableHelper.removeGeneratedRogueLootEntries(serverLevel, serverPlayer, generatedRogueLootData);

		if (generatedRogueLootData.optionalBlockPos().isPresent()) {
			BlockEntity blockEntity = serverLevel.getBlockEntity(generatedRogueLootData.optionalBlockPos().get());
			if (blockEntity instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
				InteractiveRogueLootBlock.lootWasSupplied(serverPlayer, interactiveRogueLootBlockEntity);
			}
			if (blockEntity instanceof RogueLootVaultBlockEntity rogueLootVaultBlockEntity) {
				rogueLootVaultBlockEntity.markAsRewarded(serverPlayer, generatedRogueLootData.optionalItemStack().orElse(ItemStack.EMPTY));
			}
		} else if (generatedRogueLootData.optionalItemStack().isPresent() && generatedRogueLootData.optionalInteractionHand().isPresent()) {
			SupplyRogueLootOnUseDataComponent supplyRogueLootOnUseDataComponent = generatedRogueLootData.optionalItemStack().get().get(RogueLootDataComponents.SUPPLY_ROGUE_LOOT_ON_USE);
			if (supplyRogueLootOnUseDataComponent != null && supplyRogueLootOnUseDataComponent.consume()) {
				generatedRogueLootData.optionalItemStack().get().consume(1, serverPlayer);
				serverPlayer.setItemInHand(generatedRogueLootData.optionalInteractionHand().get(), generatedRogueLootData.optionalItemStack().get());
			}
		}

		RogueLootTableHelper.applyRogueLootTableEntries(serverLevel, serverPlayer, chosenRogueLootEntries);
	}
}
