package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record RerollRogueLootScreenPacket(
		GeneratedRogueLootData generatedRogueLootData,
		Optional<ItemStack> rerollCost
) implements CustomPacketPayload {
	public static final Type<RerollRogueLootScreenPacket> PACKET_ID = new Type<>(RogueLoot.identifier("reroll_rogue_loot_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RerollRogueLootScreenPacket> PACKET_CODEC = StreamCodec.ofMember(RerollRogueLootScreenPacket::write, RerollRogueLootScreenPacket::new);

	public RerollRogueLootScreenPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLootData.STREAM_CODEC.decode(registryByteBuf),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLootData.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLootData);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.rerollCost);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
