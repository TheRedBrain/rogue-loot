package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UpdateInteractiveRogueLootBlockPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<UpdateInteractiveRogueLootBlockPacket> {
	@Override
	public void receive(UpdateInteractiveRogueLootBlockPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayerEntity = context.player();

		if (!serverPlayerEntity.canUseGameMasterBlocks()) {
			return;
		}

		BlockPos interactiveLootBlockPosition = payload.interactiveRogueLootBlockPosition();

		String lootTableIdentifierString = payload.lootTableIdentifierString();

		InteractiveRogueLootBlockEntity.Mode mode = payload.mode();

		int rolls = payload.rolls();

		int minChoices = payload.minChoices();

		int maxChoices = payload.maxChoices();

		if (minChoices > maxChoices) {
			serverPlayerEntity.sendSystemMessage(Component.translatable("hud.message.rogue_loot.max_choices_must_not_be_smaller_than_min_choices"));
			maxChoices = minChoices;
		}

		boolean trackPlayers = payload.trackPlayers();

		String lootAcquiredMessage = payload.lootAcquiredMessage();

		String lootAcquiredSoundId = payload.lootAcquiredSoundId();

		String alreadyLootedMessage = payload.alreadyLootedMessage();

		String alreadyLootedSoundId = payload.alreadyLootedSoundId();

		ServerLevel serverLevel = serverPlayerEntity.level();

		BlockEntity blockEntity = serverLevel.getBlockEntity(interactiveLootBlockPosition);
		BlockState blockState = serverLevel.getBlockState(interactiveLootBlockPosition);

		if (blockEntity instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
			interactiveRogueLootBlockEntity.reset();
			interactiveRogueLootBlockEntity.setLootTableIdentifierString(lootTableIdentifierString);
			interactiveRogueLootBlockEntity.setMode(mode);
			interactiveRogueLootBlockEntity.setRolls(rolls);
			interactiveRogueLootBlockEntity.setMinChoices(minChoices);
			interactiveRogueLootBlockEntity.setMaxChoices(maxChoices);
			interactiveRogueLootBlockEntity.setTrackPlayers(trackPlayers);
			interactiveRogueLootBlockEntity.setLootAcquiredMessage(lootAcquiredMessage);
			interactiveRogueLootBlockEntity.setLootAcquiredSoundId(lootAcquiredSoundId);
			interactiveRogueLootBlockEntity.setAlreadyLootedMessage(alreadyLootedMessage);
			interactiveRogueLootBlockEntity.setAlreadyLootedSoundId(alreadyLootedSoundId);
			serverPlayerEntity.sendSystemMessage(Component.translatable("hud.message.rogue_loot.update_successful"), true);
			interactiveRogueLootBlockEntity.setChanged();
			serverLevel.sendBlockUpdated(interactiveLootBlockPosition, blockState, blockState, Block.UPDATE_ALL);
		}
	}
}
