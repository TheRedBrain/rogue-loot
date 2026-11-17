package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RerollRogueLootScreenPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<RerollRogueLootScreenPacket> {

	@Override
	public void receive(final RerollRogueLootScreenPacket payload, final ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier = payload.generatedRogueLootTableIdentifier();
		Optional<ItemStack> itemStack = payload.itemStack();
		Optional<ItemStack> rerollCost = payload.rerollCost();

		if (rerollCost.isEmpty() || PlayerHelper.payItemCost(serverPlayer, rerollCost.get())) {
			RogueLootTableHelper.removeGeneratedRogueLootEntries(serverLevel, serverPlayer, generatedRogueLootTableIdentifier);
			RogueLootTableHelper.supplyRogueLootTable(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, itemStack);
		}
	}
}
