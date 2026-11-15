package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record BanishRogueLootEntryPacket(
		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier,
		Optional<ItemStack> itemStack,
		Identifier banishedRogueLootEntry,
		Optional<ItemStack> banishingCost
) implements CustomPacketPayload {
	public static final Type<BanishRogueLootEntryPacket> PACKET_ID = new Type<>(RogueLoot.identifier("banish_rogue_loot_entry"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BanishRogueLootEntryPacket> PACKET_CODEC = StreamCodec.ofMember(BanishRogueLootEntryPacket::write, BanishRogueLootEntryPacket::new);

	public BanishRogueLootEntryPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLootTableIdentifier.STREAM_CODEC.decode(registryByteBuf),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf),
				registryByteBuf.readIdentifier(),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLootTableIdentifier.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLootTableIdentifier);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.itemStack);
		registryByteBuf.writeIdentifier(this.banishedRogueLootEntry);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.banishingCost);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
