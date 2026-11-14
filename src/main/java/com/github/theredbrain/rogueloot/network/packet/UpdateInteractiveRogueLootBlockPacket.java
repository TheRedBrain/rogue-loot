package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateInteractiveRogueLootBlockPacket(
		BlockPos interactiveRogueLootBlockPosition,
		String lootTableIdentifierString,
		InteractiveRogueLootBlockEntity.Mode mode,
		int rolls,
		int minChoices,
		int maxChoices,
		boolean trackPlayers,
		String lootAcquiredMessage,
		String lootAcquiredSoundId,
		String alreadyLootedMessage,
		String alreadyLootedSoundId
) implements CustomPacketPayload {
	public static final Type<UpdateInteractiveRogueLootBlockPacket> PACKET_ID = new Type<>(RogueLoot.identifier("update_interactive_rogue_loot_block"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateInteractiveRogueLootBlockPacket> PACKET_CODEC = StreamCodec.ofMember(UpdateInteractiveRogueLootBlockPacket::write, UpdateInteractiveRogueLootBlockPacket::new);

	public UpdateInteractiveRogueLootBlockPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				registryByteBuf.readBlockPos(),
				registryByteBuf.readUtf(),
				registryByteBuf.readEnum(InteractiveRogueLootBlockEntity.Mode.class),
				registryByteBuf.readInt(),
				registryByteBuf.readInt(),
				registryByteBuf.readInt(),
				registryByteBuf.readBoolean(),
				registryByteBuf.readUtf(),
				registryByteBuf.readUtf(),
				registryByteBuf.readUtf(),
				registryByteBuf.readUtf()
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		registryByteBuf.writeBlockPos(this.interactiveRogueLootBlockPosition);
		registryByteBuf.writeUtf(this.lootTableIdentifierString);
		registryByteBuf.writeEnum(this.mode);
		registryByteBuf.writeInt(this.rolls);
		registryByteBuf.writeInt(this.minChoices);
		registryByteBuf.writeInt(this.maxChoices);
		registryByteBuf.writeBoolean(this.trackPlayers);
		registryByteBuf.writeUtf(this.lootAcquiredMessage);
		registryByteBuf.writeUtf(this.lootAcquiredSoundId);
		registryByteBuf.writeUtf(this.alreadyLootedMessage);
		registryByteBuf.writeUtf(this.alreadyLootedSoundId);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
