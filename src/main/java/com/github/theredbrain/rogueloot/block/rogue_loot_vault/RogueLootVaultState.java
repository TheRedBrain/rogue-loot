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
		protected void onChangedTo(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
			sharedData.setDisplayItem(ItemStack.EMPTY);
			serverLevel.levelEvent(LevelEvent.ANIMATION_VAULT_DEACTIVATE, pos, ominous ? 1 : 0);
		}
	},
	ACTIVE("active", Light.LIT) {
		@Override
		protected void onChangedTo(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
			if (!sharedData.hasDisplayItem()) {
				RogueLootVaultBlockEntity.Server.cycleDisplayItemFromLootTable(serverLevel, this, config, sharedData, pos);
			}

			serverLevel.levelEvent(LevelEvent.ANIMATION_VAULT_ACTIVATE, pos, ominous ? 1 : 0);
		}
	},
	UNLOCKING("unlocking", Light.LIT) {
		@Override
		protected void onChangedTo(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
			serverLevel.playSound(null, pos, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS);
		}
	},
	EJECTING("ejecting", Light.LIT) {
		@Override
		protected void onChangedTo(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
			serverLevel.playSound(null, pos, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS);
		}

		@Override
		protected void onChangedFrom(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData) {
			serverLevel.playSound(null, pos, SoundEvents.VAULT_CLOSE_SHUTTER, SoundSource.BLOCKS);
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

	public RogueLootVaultState update(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultServerData serverData, final RogueLootVaultSharedData sharedData) {
		return switch (this) {
			case INACTIVE -> updateActiveState(serverLevel, pos, config, serverData, sharedData, config.activationRange());
			case ACTIVE -> updateActiveState(serverLevel, pos, config, serverData, sharedData, config.deactivationRange());
			case UNLOCKING -> {
				serverData.setStateUpdatingResumeTime(serverLevel.getGameTime() + 20L);
				yield EJECTING;
			}
			case EJECTING -> {
				this.ejectReward(serverLevel, pos);
				serverData.setStateUpdatingResumeTime(serverLevel.getGameTime() + 20L);
				yield updateActiveState(serverLevel, pos, config, serverData, sharedData, config.deactivationRange());
			}
		};
	}

	private static RogueLootVaultState updateActiveState(
			final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultServerData serverData, final RogueLootVaultSharedData sharedData, final double radius
	) {
		sharedData.updateConnectedPlayers(serverLevel, pos, serverData, config, radius);
		serverData.setStateUpdatingResumeTime(serverLevel.getGameTime() + 20L);
		return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
	}

	public void onStateChange(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultState newState, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
		this.onChangedFrom(serverLevel, pos, config, sharedData);
		newState.onChangedTo(serverLevel, pos, config, sharedData, ominous);
	}

	protected void onChangedTo(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final boolean ominous) {
	}

	protected void onChangedFrom(final ServerLevel serverLevel, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData) {
	}

	private void ejectReward(final ServerLevel serverLevel, final BlockPos pos) {
		serverLevel.levelEvent(LevelEvent.ANIMATION_VAULT_EJECT_ITEM, pos, 0);
		serverLevel.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.4F);
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
