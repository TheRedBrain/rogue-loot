package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record OpenRogueLootScreenPacket(
		GeneratedRogueLoot generatedRogueLoot,
		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier,
		int rerollAttempts,
		int banishingAttempts,
		int reservingAttempts,
		Optional<ItemStack> itemStack,
		Optional<InteractionHand> interactionHand
) implements CustomPacketPayload {
	public static final Type<OpenRogueLootScreenPacket> PACKET_ID = new Type<>(RogueLoot.identifier("open_rogue_loot_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenRogueLootScreenPacket> PACKET_CODEC = StreamCodec.ofMember(OpenRogueLootScreenPacket::write, OpenRogueLootScreenPacket::new);

	public OpenRogueLootScreenPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLoot.STREAM_CODEC.decode(registryByteBuf),
				GeneratedRogueLootTableIdentifier.STREAM_CODEC.decode(registryByteBuf),
				registryByteBuf.readInt(),
				registryByteBuf.readInt(),
				registryByteBuf.readInt(),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(InteractionHand.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLoot.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLoot);
		GeneratedRogueLootTableIdentifier.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLootTableIdentifier);
		registryByteBuf.writeInt(this.rerollAttempts);
		registryByteBuf.writeInt(this.banishingAttempts);
		registryByteBuf.writeInt(this.reservingAttempts);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.itemStack);
		ByteBufCodecs.optional(InteractionHand.STREAM_CODEC).encode(registryByteBuf, this.interactionHand);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
