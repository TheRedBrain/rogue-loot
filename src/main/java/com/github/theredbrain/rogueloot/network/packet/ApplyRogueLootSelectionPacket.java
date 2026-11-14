package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ApplyRogueLootSelectionPacket(
		List<Identifier> chosenRogueLootEntries,
		Identifier localRogueLootTableIdentifier
) implements CustomPacketPayload {
	public static final Type<ApplyRogueLootSelectionPacket> PACKET_ID = new Type<>(RogueLoot.identifier("apply_rogue_loot_selection"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ApplyRogueLootSelectionPacket> PACKET_CODEC = StreamCodec.ofMember(ApplyRogueLootSelectionPacket::write, ApplyRogueLootSelectionPacket::new);

	public ApplyRogueLootSelectionPacket(RegistryFriendlyByteBuf registryByteBuf) {
		this(
				registryByteBuf.readList(Identifier.STREAM_CODEC),
				registryByteBuf.readIdentifier()
		);
	}

	private void write(RegistryFriendlyByteBuf registryByteBuf) {
		registryByteBuf.writeCollection(this.chosenRogueLootEntries, Identifier.STREAM_CODEC);
		registryByteBuf.writeIdentifier(this.localRogueLootTableIdentifier);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
