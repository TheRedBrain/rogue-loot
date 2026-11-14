package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record BanishRogueLootEntryPacket(
		Identifier banishedRogueLootEntry,
		Optional<ItemStack> banishingCost
) implements CustomPacketPayload {
	public static final Type<BanishRogueLootEntryPacket> PACKET_ID = new Type<>(RogueLoot.identifier("banish_rogue_loot_entry"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BanishRogueLootEntryPacket> PACKET_CODEC = StreamCodec.ofMember(BanishRogueLootEntryPacket::write, BanishRogueLootEntryPacket::new);

	public BanishRogueLootEntryPacket(RegistryFriendlyByteBuf registryByteBuf) {
		this(
				registryByteBuf.readIdentifier(),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(RegistryFriendlyByteBuf registryByteBuf) {
		registryByteBuf.writeIdentifier(this.banishedRogueLootEntry);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.banishingCost);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
