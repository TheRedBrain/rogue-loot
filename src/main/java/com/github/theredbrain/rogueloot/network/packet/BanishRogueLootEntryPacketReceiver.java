package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class BanishRogueLootEntryPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<BanishRogueLootEntryPacket> {

	@Override
	public void receive(BanishRogueLootEntryPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();

		Identifier banishedRogueLootEntry = payload.banishedRogueLootEntry();
		Optional<ItemStack> banishingCost = payload.banishingCost();

		if (banishingCost.isPresent()) {
			if (PlayerHelper.payItemCost(serverPlayer, banishingCost.get())) {
				DataAttachmentHelper.banishRogueLootEntry(serverPlayer, banishedRogueLootEntry);
			}
		} else {
			DataAttachmentHelper.banishRogueLootEntry(serverPlayer, banishedRogueLootEntry);
		}
	}
}
