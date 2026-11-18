package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class LockRogueLootEntryPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<LockRogueLootEntryPacket> {

	@Override
	public void receive(final LockRogueLootEntryPacket payload, final ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier = payload.generatedRogueLootTableIdentifier();
		Optional<ItemStack> itemStack = payload.itemStack();
		Identifier lockedRogueLootEntry = payload.lockedRogueLootEntry();
		Optional<ItemStack> lockingCost = payload.lockingCost();

		if (lockingCost.isEmpty() || PlayerHelper.payItemCost(serverPlayer, lockingCost.get())) {
			DataAttachmentHelper.lockRogueLootEntry(serverPlayer, lockedRogueLootEntry);

			RogueLootTableHelper.supplyRogueLootTable(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, itemStack);
		}
	}
}
