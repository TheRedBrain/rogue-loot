package com.github.theredbrain.rogueloot.block;

import com.github.theredbrain.rogueloot.block.entity.RogueLootVaultBlockEntity;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultState;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class RogueLootVaultBlock extends BaseEntityBlock {
	public static final MapCodec<RogueLootVaultBlock> CODEC = simpleCodec(RogueLootVaultBlock::new);
	public static final Property<RogueLootVaultState> ROGUE_LOOT_VAULT_STATE = EnumProperty.create("rogue_loot_vault_state", RogueLootVaultState.class);

	@Override
	public MapCodec<RogueLootVaultBlock> codec() {
		return CODEC;
	}

	public RogueLootVaultBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(
				this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH).setValue(ROGUE_LOOT_VAULT_STATE, RogueLootVaultState.INACTIVE).setValue(BlockStateProperties.OMINOUS, Boolean.FALSE)
		);
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (stack.isEmpty() || state.getValue(ROGUE_LOOT_VAULT_STATE) != RogueLootVaultState.ACTIVE) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		} else if (world instanceof ServerLevel serverWorld) {
			if (serverWorld.getBlockEntity(pos) instanceof RogueLootVaultBlockEntity rogueLootVaultBlockEntity) {
				RogueLootVaultBlockEntity.Server.tryInsertKey(
						serverWorld, pos, state, rogueLootVaultBlockEntity.getConfig(world), rogueLootVaultBlockEntity.getServerData(), rogueLootVaultBlockEntity.getSharedData(), player, stack
				);
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
		} else {
			return InteractionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RogueLootVaultBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HorizontalDirectionalBlock.FACING, ROGUE_LOOT_VAULT_STATE, BlockStateProperties.OMINOUS);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world instanceof ServerLevel serverWorld
				? createTickerHelper(
				type,
				EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY,
				(worldx, pos, statex, blockEntity) -> RogueLootVaultBlockEntity.Server.tick(
						serverWorld, pos, statex, blockEntity.getConfig(world), blockEntity.getServerData(), blockEntity.getSharedData()
				)
		)
				: createTickerHelper(
				type,
				EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY,
				(worldx, pos, statex, blockEntity) -> RogueLootVaultBlockEntity.Client.tick(worldx, pos, statex, blockEntity.getClientData(), blockEntity.getSharedData())
		);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
