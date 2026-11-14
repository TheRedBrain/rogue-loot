package com.github.theredbrain.rogueloot.block;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.entity.player.DuckPlayerMixin;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InteractiveRogueLootBlock extends BaseEntityBlock {
	public static final MapCodec<InteractiveRogueLootBlock> CODEC = simpleCodec(InteractiveRogueLootBlock::new);

	public InteractiveRogueLootBlock(final Properties settings) {
		super(settings);
	}

	public MapCodec<InteractiveRogueLootBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return new InteractiveRogueLootBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(final BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hit) {
		if (state.is(this)) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {

				if (player.canUseGameMasterBlocks()) {
					((DuckPlayerMixin) player).rogueloot$openInteractiveRogueLootBlockScreen(interactiveRogueLootBlockEntity);
					return InteractionResult.SUCCESS;
				} else if (level instanceof ServerLevel serverLevel && !serverLevel.isClientSide() && player instanceof ServerPlayer serverPlayer) {
					if (interactiveRogueLootBlockEntity.isPlayerInSet(serverPlayer)) {
						String alreadyLootedSoundId = interactiveRogueLootBlockEntity.getAlreadyLootedSoundId();
						if (!alreadyLootedSoundId.isEmpty()) {
							SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse(alreadyLootedSoundId));
							serverPlayer.playSound(soundEvent, 1.0F, 1.0F);
						}
						String alreadyLootedMessage = interactiveRogueLootBlockEntity.getAlreadyLootedMessage();
						if (!alreadyLootedMessage.isEmpty()) {
							serverPlayer.sendSystemMessage(Component.translatable(alreadyLootedMessage), true);
						}
					} else {
						Identifier identifier = Identifier.tryParse(interactiveRogueLootBlockEntity.getLootTableIdentifierString());
						if (!interactiveRogueLootBlockEntity.getLootTableIdentifierString().isEmpty() && identifier != null) {
							Vec3 lootPos = Vec3.atLowerCornerOf(interactiveRogueLootBlockEntity.getBlockPos());
							if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_CHOICE) {
								RogueLoot.supplyLootableLoot(identifier, serverLevel, serverPlayer, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getMinChoices(), true, null);
							} else if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_RANDOM) {
								RogueLoot.supplyLootableLoot(identifier, serverLevel, serverPlayer, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getMinChoices(), false, null);
							} else if (interactiveRogueLootBlockEntity.getMode() == InteractiveRogueLootBlockEntity.Mode.ROGUE_LOOT) {
								RogueLootTableHelper.supplyRogueLootTable(identifier, serverLevel, serverPlayer, lootPos, interactiveRogueLootBlockEntity.getRolls(), interactiveRogueLootBlockEntity.getMinChoices(), interactiveRogueLootBlockEntity.getMaxChoices(), null);
							} else {
								List<ItemStack> lootStacks = getLootItems(serverLevel, pos, player, interactiveRogueLootBlockEntity);
								for (ItemStack itemStack : lootStacks) {
									player.getInventory().placeItemBackInInventory(itemStack);
								}
								lootWasSupplied(serverPlayer, interactiveRogueLootBlockEntity);
							}
						}
					}
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
		if (!level.isClientSide()) {
			if (level.hasNeighborSignal(pos) && level.getBlockEntity(pos) instanceof InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
				interactiveRogueLootBlockEntity.reset();
			}
		}
	}

	public static void lootWasSupplied(final ServerPlayer serverPlayer, final InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
		if (interactiveRogueLootBlockEntity.trackPlayers()) {
			interactiveRogueLootBlockEntity.addPlayerToSet(serverPlayer);
		}
		String lootAcquiredSoundId = interactiveRogueLootBlockEntity.getLootAcquiredSoundId();
		if (!lootAcquiredSoundId.isEmpty()) {
			SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse(lootAcquiredSoundId));
			serverPlayer.playSound(soundEvent, 1.0F, 1.0F);
		}
		String lootAcquiredMessage = interactiveRogueLootBlockEntity.getLootAcquiredMessage();
		if (!lootAcquiredMessage.isEmpty()) {
			serverPlayer.sendSystemMessage(Component.translatable(lootAcquiredMessage), true);
		}
	}

	private static List<ItemStack> getLootItems(final ServerLevel serverLevel, final BlockPos pos, final Player player, final InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
		List<ItemStack> list = new ArrayList<>();
		Identifier lootTableIdentifier = Identifier.tryParse(interactiveRogueLootBlockEntity.getLootTableIdentifierString());
		if (lootTableIdentifier != null) {
			LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableIdentifier));
			LootParams params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.THIS_ENTITY, player).create(LootContextParamSets.VAULT);
			list = lootTable.getRandomItems(params);
		}
		return list;
	}
}
