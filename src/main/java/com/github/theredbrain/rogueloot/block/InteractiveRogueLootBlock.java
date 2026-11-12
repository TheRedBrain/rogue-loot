package com.github.theredbrain.rogueloot.block;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.data.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.entity.player.DuckPlayerEntityMixin;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InteractiveRogueLootBlock extends BaseEntityBlock {
	public static final MapCodec<InteractiveRogueLootBlock> CODEC = simpleCodec(InteractiveRogueLootBlock::new);

	public InteractiveRogueLootBlock(Properties settings) {
		super(settings);
	}

	public MapCodec<InteractiveRogueLootBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new InteractiveRogueLootBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (state.is(this)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {

				if (player.canUseGameMasterBlocks()) {
					((DuckPlayerEntityMixin) player).rogueloot$openInteractiveRogueLootBlockScreen(interactiveRogueLootBlockEntity);
					return InteractionResult.SUCCESS;
				} else if (world instanceof ServerLevel serverWorld && !serverWorld.isClientSide() && player instanceof ServerPlayer serverPlayerEntity) {
					if (interactiveRogueLootBlockEntity.isPlayerInSet(serverPlayerEntity)) {
						String alreadyLootedSoundId = interactiveRogueLootBlockEntity.getAlreadyLootedSoundId();
						if (!alreadyLootedSoundId.isEmpty()) {
							SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse(alreadyLootedSoundId));
							serverPlayerEntity.playSound(soundEvent, 1.0F, 1.0F);
						}
						String alreadyLootedMessage = interactiveRogueLootBlockEntity.getAlreadyLootedMessage();
						if (!alreadyLootedMessage.isEmpty()) {
							serverPlayerEntity.sendSystemMessage(Component.translatable(alreadyLootedMessage), true);
						}
					} else {
						Identifier identifier = Identifier.tryParse(interactiveRogueLootBlockEntity.getLootTableIdentifierString());
						if (!interactiveRogueLootBlockEntity.getLootTableIdentifierString().isEmpty() && identifier != null) {
							Vec3 lootPos = new Vec3(interactiveRogueLootBlockEntity.getBlockPos().getX(), interactiveRogueLootBlockEntity.getBlockPos().getY(), interactiveRogueLootBlockEntity.getBlockPos().getZ());
							if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_CHOICE) {
								RogueLoot.supplyLootableLoot(identifier, serverWorld, serverPlayerEntity, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getChoices(), true, null);
							} else if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_RANDOM) {
								RogueLoot.supplyLootableLoot(identifier, serverWorld, serverPlayerEntity, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getChoices(), false, null);
							} else if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.ROGUE_LOOT) {
								RogueLootTableHelper.supplyRogueLootTable(identifier, serverWorld, serverPlayerEntity, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getChoices(), null);
							} else {
								List<ItemStack> lootStacks = getLootItems(serverWorld, pos, player, interactiveRogueLootBlockEntity);
								for (ItemStack itemStack : lootStacks) {
									player.getInventory().placeItemBackInInventory(itemStack);
								}
								lootWasSupplied(serverPlayerEntity, interactiveRogueLootBlockEntity);
							}
							return InteractionResult.SUCCESS;
						}
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	protected void neighborUpdate(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClientSide()) {
			if (world.hasNeighborSignal(pos) && world.getBlockEntity(pos) instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
				interactiveRogueLootBlockEntity.reset();
			}
		}
	}

	public static void lootWasSupplied(ServerPlayer serverPlayerEntity, InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
		if (interactiveRogueLootBlockEntity.trackPlayers()) {
			interactiveRogueLootBlockEntity.addPlayerToSet(serverPlayerEntity);
		}
		String lootAcquiredSoundId = interactiveRogueLootBlockEntity.getLootAcquiredSoundId();
		if (!lootAcquiredSoundId.isEmpty()) {
			SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse(lootAcquiredSoundId));
			serverPlayerEntity.playSound(soundEvent, 1.0F, 1.0F);
		}
		String lootAcquiredMessage = interactiveRogueLootBlockEntity.getLootAcquiredMessage();
		if (!lootAcquiredMessage.isEmpty()) {
			serverPlayerEntity.sendSystemMessage(Component.translatable(lootAcquiredMessage), true);
		}
	}

	private static List<ItemStack> getLootItems(ServerLevel serverWorld, BlockPos pos, Player player, InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
		List<ItemStack> list = new ArrayList<>();
		Identifier lootTableIdentifier = Identifier.tryParse(interactiveRogueLootBlockEntity.getLootTableIdentifierString());
		if (lootTableIdentifier != null) {
			LootTable lootTable = serverWorld.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableIdentifier));
			LootParams params = new LootParams.Builder(serverWorld).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.THIS_ENTITY, player).create(LootContextParamSets.VAULT);
			list = lootTable.getRandomItems(params);
		}
		return list;
	}
}
