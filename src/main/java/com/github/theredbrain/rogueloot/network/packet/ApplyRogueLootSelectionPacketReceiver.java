package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.block.InteractiveRogueLootBlock;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.block.entity.RogueLootVaultBlockEntity;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Optional;

public class ApplyRogueLootSelectionPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<ApplyRogueLootSelectionPacket> {

	@Override
	public void receive(final ApplyRogueLootSelectionPacket payload, final ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		List<Identifier> chosenRogueLootEntries = payload.chosenRogueLootEntries();
		GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier = payload.generatedRogueLootTableIdentifier();
		Optional<ItemStack> itemStack = payload.itemStack();

		RogueLootTableHelper.removeGeneratedRogueLootEntries(serverLevel, serverPlayer, generatedRogueLootTableIdentifier);

		if (generatedRogueLootTableIdentifier.optionalBlockPos().isPresent()) {
			BlockEntity blockEntity = serverLevel.getBlockEntity(generatedRogueLootTableIdentifier.optionalBlockPos().get());
			if (blockEntity instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
				InteractiveRogueLootBlock.lootWasSupplied(serverPlayer, interactiveRogueLootBlockEntity);
			}
			if (blockEntity instanceof RogueLootVaultBlockEntity rogueLootVaultBlockEntity) {
				rogueLootVaultBlockEntity.markAsRewarded(serverPlayer, itemStack.orElse(ItemStack.EMPTY));
			}
		}

		for (Identifier identifier : chosenRogueLootEntries) {
			RogueLootTableEntry rogueLootTableEntry = RogueLootTableEntry.getRogueLootTableEntry(serverLevel.registryAccess(), identifier);

			if (rogueLootTableEntry != null) {
				for (RogueLootTableEntryComponentType component : rogueLootTableEntry.rogueLootTableEntryComponentTypes()) {
					component.apply(serverLevel, serverPlayer);
				}
			}
		}
	}
}
