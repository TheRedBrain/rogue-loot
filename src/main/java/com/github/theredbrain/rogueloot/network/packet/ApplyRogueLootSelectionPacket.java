package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record ApplyRogueLootSelectionPacket(
		List<Identifier> chosenRogueLootEntries,
		Identifier localRogueLootTableIdentifier,
		Optional<BlockPos> lootProviderBlockPos,
		Optional<ItemStack> itemStack
) implements CustomPacketPayload {
	public static final Type<ApplyRogueLootSelectionPacket> PACKET_ID = new Type<>(RogueLoot.identifier("apply_rogue_loot_selection"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ApplyRogueLootSelectionPacket> PACKET_CODEC = StreamCodec.ofMember(ApplyRogueLootSelectionPacket::write, ApplyRogueLootSelectionPacket::new);

	public ApplyRogueLootSelectionPacket(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				registryByteBuf.readList(Identifier.STREAM_CODEC),
				registryByteBuf.readIdentifier(),
				ByteBufCodecs.optional(BlockPos.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final RegistryFriendlyByteBuf registryByteBuf) {
		registryByteBuf.writeCollection(this.chosenRogueLootEntries, Identifier.STREAM_CODEC);
		registryByteBuf.writeIdentifier(this.localRogueLootTableIdentifier);
		ByteBufCodecs.optional(BlockPos.STREAM_CODEC).encode(registryByteBuf, this.lootProviderBlockPos);
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(registryByteBuf, this.itemStack);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_ID;
	}
}
