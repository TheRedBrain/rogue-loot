package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenRogueLootScreenPacket(
		GeneratedRogueLoot generatedRogueLoot,
		Identifier localRogueLootTableIdentifier,
		int banishingAttempts
) implements CustomPacketPayload {
	public static final Type<OpenRogueLootScreenPacket> PACKET_ID = new Type<>(RogueLoot.identifier("open_rogue_loot_screen"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenRogueLootScreenPacket> PACKET_CODEC = StreamCodec.ofMember(OpenRogueLootScreenPacket::write, OpenRogueLootScreenPacket::new);

	public OpenRogueLootScreenPacket(RegistryFriendlyByteBuf registryByteBuf) {
		this(
				GeneratedRogueLoot.STREAM_CODEC.decode(registryByteBuf),
				registryByteBuf.readIdentifier(),
				registryByteBuf.readInt()
		);
	}

	private void write(RegistryFriendlyByteBuf registryByteBuf) {
		GeneratedRogueLoot.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLoot);
		registryByteBuf.writeIdentifier(this.localRogueLootTableIdentifier);
		registryByteBuf.writeInt(this.banishingAttempts);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
