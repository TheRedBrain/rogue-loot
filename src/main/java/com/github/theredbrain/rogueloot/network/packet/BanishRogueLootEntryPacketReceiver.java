package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class BanishRogueLootEntryPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<BanishRogueLootEntryPacket> {

	@Override
	public void receive(final BanishRogueLootEntryPacket payload, final ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier = payload.generatedRogueLootTableIdentifier();
		Optional<ItemStack> itemStack = payload.itemStack();
		Optional<InteractionHand> interactionHand = payload.interactionHand();
		Identifier banishedRogueLootEntry = payload.banishedRogueLootEntry();
		Optional<ItemStack> banishingCost = payload.banishingCost();

		if (banishingCost.isEmpty() || PlayerHelper.payItemCost(serverPlayer, banishingCost.get())) {
			DataAttachmentHelper.banishRogueLootEntry(serverPlayer, banishedRogueLootEntry);

			RogueLootTableHelper.supplyRogueLootTable(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, itemStack, interactionHand);
		}
	}
}
