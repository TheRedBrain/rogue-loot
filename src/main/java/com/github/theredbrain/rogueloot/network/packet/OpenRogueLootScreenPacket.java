package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenRogueLootScreenPacket(
		GeneratedRogueLoot generatedRogueLoot,
		GeneratedRogueLootData generatedRogueLootData,
		int rerollAttempts,
		int banishingAttempts,
		int reservingAttempts
) implements CustomPacketPayload {
	public static final Type<OpenRogueLootScreenPacket> PACKET_ID = new Type<>(RogueLoot.identifier("open_rogue_loot_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenRogueLootScreenPacket> PACKET_CODEC = StreamCodec.ofMember(OpenRogueLootScreenPacket::write, OpenRogueLootScreenPacket::new);

	public OpenRogueLootScreenPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLoot.STREAM_CODEC.decode(registryByteBuf),
				GeneratedRogueLootData.STREAM_CODEC.decode(registryByteBuf),
				registryByteBuf.readInt(),
				registryByteBuf.readInt(),
				registryByteBuf.readInt()
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLoot.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLoot);
		GeneratedRogueLootData.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLootData);
		registryByteBuf.writeInt(this.rerollAttempts);
		registryByteBuf.writeInt(this.banishingAttempts);
		registryByteBuf.writeInt(this.reservingAttempts);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
