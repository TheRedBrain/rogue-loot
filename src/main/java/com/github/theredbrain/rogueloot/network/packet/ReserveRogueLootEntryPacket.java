package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ReserveRogueLootEntryPacket(
		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier,
		Optional<ItemStack> itemStack,
		Optional<InteractionHand> interactionHand,
		Identifier reservedRogueLootEntry,
		Optional<ItemStack> reservingCost
) implements CustomPacketPayload {
	public static final Type<ReserveRogueLootEntryPacket> PACKET_ID = new Type<>(RogueLoot.identifier("reserve_rogue_loot_entry"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ReserveRogueLootEntryPacket> PACKET_CODEC = StreamCodec.ofMember(ReserveRogueLootEntryPacket::write, ReserveRogueLootEntryPacket::new);

	public ReserveRogueLootEntryPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLootTableIdentifier.STREAM_CODEC.decode(registryByteBuf),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(InteractionHand.STREAM_CODEC).decode(registryByteBuf),
				registryByteBuf.readIdentifier(),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLootTableIdentifier.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLootTableIdentifier);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.itemStack);
		ByteBufCodecs.optional(InteractionHand.STREAM_CODEC).encode(registryByteBuf, this.interactionHand);
		registryByteBuf.writeIdentifier(this.reservedRogueLootEntry);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.reservingCost);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
