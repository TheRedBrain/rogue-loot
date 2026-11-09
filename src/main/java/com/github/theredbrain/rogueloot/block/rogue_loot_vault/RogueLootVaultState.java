package com.github.theredbrain.rogueloot.block.rogue_loot_vault;

import com.github.theredbrain.rogueloot.block.entity.RogueLootVaultBlockEntity;
import com.github.theredbrain.rogueloot.data.RogueLootVaultConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;

public enum RogueLootVaultState implements StringRepresentable {
	INACTIVE("inactive", Light.HALF_LIT) {
		@Override
		protected void onChangedTo(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
			sharedData.setDisplayItem(ItemStack.EMPTY);
			world.levelEvent(LevelEvent.ANIMATION_VAULT_DEACTIVATE, pos, ominous ? 1 : 0);
		}
	},
	ACTIVE("active", Light.LIT) {
		@Override
		protected void onChangedTo(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
			if (!sharedData.hasDisplayItem()) {
				RogueLootVaultBlockEntity.Server.cycleDisplayItemFromLootTable(world, this, config, sharedData, pos);
			}

			world.levelEvent(LevelEvent.ANIMATION_VAULT_ACTIVATE, pos, ominous ? 1 : 0);
		}
	},
	UNLOCKING("unlocking", Light.LIT) {
		@Override
		protected void onChangedTo(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
			world.playSound(null, pos, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS);
		}
	},
	EJECTING("ejecting", Light.LIT) {
		@Override
		protected void onChangedTo(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
			world.playSound(null, pos, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS);
		}

		@Override
		protected void onChangedFrom(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData) {
			world.playSound(null, pos, SoundEvents.VAULT_CLOSE_SHUTTER, SoundSource.BLOCKS);
		}
	};

	private final String id;
	private final Light light;

	RogueLootVaultState(final String id, final Light light) {
		this.id = id;
		this.light = light;
	}

	@Override
	public String getSerializedName() {
		return this.id;
	}

	public int getLuminance() {
		return this.light.luminance;
	}

	public RogueLootVaultState update(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultServerData serverData, RogueLootVaultSharedData sharedData) {
		return switch (this) {
			case INACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.activationRange());
			case ACTIVE -> updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
			case UNLOCKING -> {
				serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
				yield EJECTING;
			}
			case EJECTING -> {
				this.ejectReward(world, pos);
				serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
				yield updateActiveState(world, pos, config, serverData, sharedData, config.deactivationRange());
			}
		};
	}

	private static RogueLootVaultState updateActiveState(
			ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultServerData serverData, RogueLootVaultSharedData sharedData, double radius
	) {
		sharedData.updateConnectedPlayers(world, pos, serverData, config, radius);
		serverData.setStateUpdatingResumeTime(world.getGameTime() + 20L);
		return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
	}

	public void onStateChange(ServerLevel world, BlockPos pos, RogueLootVaultState newState, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
		this.onChangedFrom(world, pos, config, sharedData);
		newState.onChangedTo(world, pos, config, sharedData, ominous);
	}

	protected void onChangedTo(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, boolean ominous) {
	}

	protected void onChangedFrom(ServerLevel world, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData) {
	}

	private void ejectReward(ServerLevel world, BlockPos pos) {
		world.levelEvent(LevelEvent.ANIMATION_VAULT_EJECT_ITEM, pos, 0);
		world.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F);
	}

	enum Light {
		HALF_LIT(6),
		LIT(12);

		final int luminance;

		Light(final int luminance) {
			this.luminance = luminance;
		}
	}
}
