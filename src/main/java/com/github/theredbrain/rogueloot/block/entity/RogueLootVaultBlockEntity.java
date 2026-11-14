package com.github.theredbrain.rogueloot.block.entity;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.RogueLootVaultBlock;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultClientData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultServerData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultSharedData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultState;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.data.RogueLootVaultConfig;
import com.github.theredbrain.rogueloot.registry.CustomDynamicRegistries;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class RogueLootVaultBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final RogueLootVaultServerData serverData = new RogueLootVaultServerData();
	private final RogueLootVaultSharedData sharedData = new RogueLootVaultSharedData();
	private final RogueLootVaultClientData clientData = new RogueLootVaultClientData();
	private RogueLootVaultConfig config = null;
	private String lootableVaultConfigIdentifier = "";

	public RogueLootVaultBlockEntity(final BlockPos pos, final BlockState state) {
		super(EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY, pos, state);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
		return Util.make(new CompoundTag(), tag -> tag.store("shared_data", RogueLootVaultSharedData.CODEC, registries.createSerializationContext(NbtOps.INSTANCE), this.sharedData));
	}

	@Override
	protected void saveAdditional(final ValueOutput output) {

		output.putString("config_identifier", this.lootableVaultConfigIdentifier);

		output.store("shared_data", RogueLootVaultSharedData.CODEC, this.sharedData);
		output.store("server_data", RogueLootVaultServerData.CODEC, this.serverData);

		super.saveAdditional(output);
	}

	@Override
	protected void loadAdditional(final ValueInput input) {

		input.read("shared_data", RogueLootVaultSharedData.CODEC).ifPresent(this.sharedData::set);

		input.read("server_data", RogueLootVaultServerData.CODEC).ifPresent(this.serverData::set);

		this.lootableVaultConfigIdentifier = input.getStringOr("config_identifier", "");
		this.config = null;

		super.loadAdditional(input);
	}

	@Override
	public void setRemoved() {
		if (this.level instanceof ServerLevel serverWorld) {
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), null, this.getConfig(serverWorld));
		}
		super.setRemoved();
	}

	@Nullable
	public RogueLootVaultServerData getServerData() {
		return this.level != null && !this.level.isClientSide() ? this.serverData : null;
	}

	public RogueLootVaultSharedData getSharedData() {
		return this.sharedData;
	}

	public RogueLootVaultClientData getClientData() {
		return this.clientData;
	}

	public void markAsRewarded(final ServerPlayer serverPlayer, final ItemStack itemStack) {
		if (this.level instanceof ServerLevel serverWorld) {
			RogueLootVaultServerData serverData = this.getServerData();
			RogueLootVaultSharedData sharedData = this.getSharedData();
			RogueLootVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverPlayer.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				itemStack.consume(config.keyItem().create().getCount(), serverPlayer);
				Server.unlock(serverWorld, serverWorld.getBlockState(this.worldPosition), this.worldPosition, config, serverData, sharedData);
				serverData.markPlayerAsRewarded(serverPlayer);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivationRange());
			}
		}
	}

	public void unmarkAsRewarded(final ServerPlayer serverPlayer) {
		if (this.level instanceof ServerLevel serverWorld) {
			RogueLootVaultServerData serverData = this.getServerData();
			RogueLootVaultSharedData sharedData = this.getSharedData();
			RogueLootVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverData.unmarkPlayerAsRewarded(serverPlayer);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivationRange());
			}
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), serverPlayer, config);
		}
	}

	public RogueLootVaultConfig getConfig(final Level level) {
		if (this.config != null) {
			return this.config;
		}
		Identifier identifier = Identifier.tryParse(this.lootableVaultConfigIdentifier);
		Optional<Holder.Reference<Registry<RogueLootVaultConfig>>> optionalLootableVaultConfigRegistryReference = level.registryAccess().get(CustomDynamicRegistries.ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY);
		if (optionalLootableVaultConfigRegistryReference.isPresent() && identifier != null) {
			Optional<Holder.Reference<RogueLootVaultConfig>> optionalLootableVaultConfigReference = optionalLootableVaultConfigRegistryReference.get().value().get(identifier);
			if (optionalLootableVaultConfigReference.isPresent()) {
				this.config = optionalLootableVaultConfigReference.get().value();
				return this.config;
			}
		}
		if (!this.lootableVaultConfigIdentifier.isEmpty()) {
			RogueLoot.info("Config not valid: " + this.lootableVaultConfigIdentifier);
		}
		this.config = RogueLootVaultConfig.DEFAULT;
		return this.config;
	}

	private void removeLootableUses(final ServerLevel serverLevel, final Vec3 pos, @Nullable final ServerPlayer serverPlayer, final RogueLootVaultConfig config) {
		if (config.lootTableMode() == RogueLootVaultConfig.Mode.ROGUE_LOOT) {
			RogueLootTableHelper.removeRogueLootTableUses(serverLevel, pos, config.lootTableIdentifier(), config.rolls(), config.minChoices(), config.maxChoices(), serverPlayer);
		} else {
			RogueLoot.removeLootableUses(serverLevel, pos, config.lootTableIdentifier(), serverPlayer);
		}
	}

	public static final class Client {

		public static void tick(final Level level, final BlockPos pos, final BlockState state, final RogueLootVaultClientData clientData, final RogueLootVaultSharedData sharedData) {
			clientData.rotateDisplay();
			if (level.getGameTime() % 20L == 0L) {
				emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
			}

			emitIdleParticles(level, pos, sharedData, state.getValue(VaultBlock.OMINOUS) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
			playIdleSounds(level, pos, sharedData);
		}

		public static void emitActivationParticles(final Level level, final BlockPos pos, final BlockState state, final RogueLootVaultSharedData sharedData, final ParticleOptions particle) {
			emitConnectionParticlesForNearbyPlayers(level, pos, state, sharedData);
			RandomSource random = level.getRandom();

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = randomPosInsideCage(pos, random);
				level.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				level.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
			}
		}

		public static void emitDeactivationParticles(final Level level, final BlockPos pos, final ParticleOptions particle) {
			RandomSource random = level.getRandom();

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = randomPosCenterOfCage(pos, random);
				Vec3 vec3d2 = new Vec3(random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
				level.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void emitIdleParticles(final Level level, final BlockPos pos, final RogueLootVaultSharedData sharedData, final ParticleOptions particle) {
			RandomSource random = level.getRandom();
			if (random.nextFloat() <= 0.5F) {
				Vec3 vec3d = randomPosInsideCage(pos, random);
				level.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				if (shouldDisplayActiveEffects(sharedData)) {
					level.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				}
			}
		}

		private static void emitConnectionParticlesForPlayer(final Level level, final Vec3 pos, final Player player) {
			RandomSource random = level.getRandom();
			Vec3 vec3d = pos.vectorTo(player.position().add(0.0, (double) (player.getBbHeight() / 2.0F), 0.0));
			int i = Mth.nextInt(random, 2, 5);

			for (int j = 0; j < i; j++) {
				Vec3 vec3d2 = vec3d.offsetRandom(random, 1.0F);
				level.addParticle(ParticleTypes.VAULT_CONNECTION, pos.x(), pos.y(), pos.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void emitConnectionParticlesForNearbyPlayers(final Level level, final BlockPos pos, final BlockState state, final RogueLootVaultSharedData sharedData) {
			Set<UUID> set = sharedData.getConnectedPlayers();
			if (!set.isEmpty()) {
				Vec3 vec3d = keyholePos(pos, state.getValue(VaultBlock.FACING));

				for (UUID uUID : set) {
					Player playerEntity = level.getPlayerByUUID(uUID);
					if (playerEntity != null && isWithinConnectionRange(pos, sharedData, playerEntity)) {
						emitConnectionParticlesForPlayer(level, vec3d, playerEntity);
					}
				}
			}
		}

		private static boolean isWithinConnectionRange(final BlockPos pos, final RogueLootVaultSharedData sharedData, final Player player) {
			return player.blockPosition().distSqr(pos) <= Mth.square(sharedData.getConnectedParticlesRange());
		}

		private static void playIdleSounds(final Level level, final BlockPos pos, final RogueLootVaultSharedData sharedData) {
			if (shouldDisplayActiveEffects(sharedData)) {
				RandomSource random = level.getRandom();
				if (random.nextFloat() <= 0.02F) {
					level.playLocalSound(
							pos, SoundEvents.VAULT_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false
					);
				}
			}
		}

		public static boolean shouldDisplayActiveEffects(final RogueLootVaultSharedData sharedData) {
			return sharedData.hasDisplayItem();
		}

		private static Vec3 randomPosCenterOfCage(final BlockPos pos, final RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6));
		}

		private static Vec3 randomPosInsideCage(final BlockPos pos, final RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
		}

		private static Vec3 keyholePos(final BlockPos pos, final Direction direction) {
			return Vec3.atBottomCenterOf(pos).add((double) direction.getStepX() * 0.5, 1.75, (double) direction.getStepZ() * 0.5);
		}
	}

	public static final class Server {

		public static void tick(final ServerLevel serverLevel, final BlockPos pos, final BlockState state, final RogueLootVaultConfig config, final RogueLootVaultServerData serverData, final RogueLootVaultSharedData sharedData) {
			RogueLootVaultState vaultState = state.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			if (shouldCycleDisplayItem(serverLevel.getGameTime(), vaultState)) {
				cycleDisplayItemFromLootTable(serverLevel, vaultState, config, sharedData, pos);
			}

			BlockState blockState = state;
			if (serverLevel.getGameTime() >= serverData.getStateUpdatingResumeTime()) {
				blockState = state.setValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE, vaultState.update(serverLevel, pos, config, serverData, sharedData));
				if (!state.equals(blockState)) {
					setVaultState(serverLevel, pos, state, blockState, config, sharedData);
				}
			}

			if (serverData.isDirty() || sharedData.isDirty()) {
				RogueLootVaultBlockEntity.setChanged(serverLevel, pos, state);
				if (sharedData.isDirty()) {
					serverLevel.sendBlockUpdated(pos, state, blockState, Block.UPDATE_CLIENTS);
				}

				serverData.markClean();
				sharedData.markClean();
			}
		}

		public static void tryInsertKey(
				final ServerLevel serverLevel,
				final BlockPos pos,
				final BlockState state,
				final RogueLootVaultConfig config,
				final RogueLootVaultServerData serverData,
				final RogueLootVaultSharedData sharedData,
				final Player player,
				final ItemStack stack
		) {
			RogueLootVaultState vaultState = state.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			if (canEjectReward(config, vaultState)) {
				if (!isValidToInsert(config, stack)) {
					RogueLoot.info("invalid key");
					playInsertFailSound(serverLevel, serverData, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
				} else if (serverData.hasRewardedPlayer(player)) {
					RogueLoot.info("player already rewarded");
					playInsertFailSound(serverLevel, serverData, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER);
				} else if (player instanceof ServerPlayer serverPlayerEntity) {
					Identifier identifier = Identifier.tryParse(config.lootTableIdentifier());
					if (identifier == null) {
						playInsertFailSound(serverLevel, serverData, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER);
						return;
					}
					if (config.lootTableMode() == RogueLootVaultConfig.Mode.ROGUE_LOOT) {
						RogueLootTableHelper.supplyRogueLootTable(identifier, serverLevel, serverPlayerEntity, Vec3.atLowerCornerOf(pos), config.rolls(), config.minChoices(), config.maxChoices(), stack);
					} else {
						RogueLoot.supplyLootableLoot(identifier, serverLevel, serverPlayerEntity, Vec3.atLowerCornerOf(pos), config.rolls(), config.minChoices(), config.lootTableMode() == RogueLootVaultConfig.Mode.LOOTABLE_CHOICE, stack);
					}
					serverData.markPlayerAsRewarded(player);
					sharedData.updateConnectedPlayers(serverLevel, pos, serverData, config, config.deactivationRange());
				}
			}
		}

		static void setVaultState(final ServerLevel serverLevel, final BlockPos pos, final BlockState oldState, final BlockState newState, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData) {
			RogueLootVaultState vaultState = oldState.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			RogueLootVaultState vaultState2 = newState.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			serverLevel.setBlock(pos, newState, Block.UPDATE_ALL);
			vaultState.onStateChange(serverLevel, pos, vaultState2, config, sharedData, (Boolean) newState.getValue(VaultBlock.OMINOUS));
		}

		public static void cycleDisplayItemFromLootTable(final ServerLevel serverLevel, final RogueLootVaultState state, final RogueLootVaultConfig config, final RogueLootVaultSharedData sharedData, final BlockPos pos) {
			if (!canEjectReward(config, state)) {
				sharedData.setDisplayItem(ItemStack.EMPTY);
			} else {
				ItemStack itemStack = ItemStack.EMPTY;
				if (!config.displayLootTableIdentifier().isEmpty()) {
					itemStack = getRandomDisplayItemFromLootTable(serverLevel, pos, ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(config.displayLootTableIdentifier())));
				}
				sharedData.setDisplayItem(itemStack);
			}
		}

		private static ItemStack getRandomDisplayItemFromLootTable(final ServerLevel serverLevel, final BlockPos pos, final ResourceKey<LootTable> lootTableId) {
			LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableId);
			LootParams params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).create(LootContextParamSets.VAULT);
			List<ItemStack> list = lootTable.getRandomItems(params);
			return list.isEmpty() ? ItemStack.EMPTY : Util.getRandom(list, serverLevel.getRandom());
		}

		public static void unlock(
				final ServerLevel serverLevel, final BlockState state, final BlockPos pos, final RogueLootVaultConfig config, final RogueLootVaultServerData serverData, final RogueLootVaultSharedData sharedData
		) {
//			serverData.setItemsToEject(itemsToEject);
			sharedData.setDisplayItem(ItemStack.EMPTY);
			serverData.setStateUpdatingResumeTime(serverLevel.getGameTime() + 14L);
			setVaultState(serverLevel, pos, state, state.setValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE, RogueLootVaultState.UNLOCKING), config, sharedData);
		}

//		private static List<ItemStack> generateLoot(ServerWorld level, LootableVaultConfig config, BlockPos pos, PlayerEntity player) {
//			LootTable lootTable = level.getServer().getReloadableRegistries().getLootTable(config.lootTable());
//			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(level)
//					.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
//					.luck(player.getLuck())
//					.add(LootContextParameters.THIS_ENTITY, player)
//					.build(LootContextTypes.VAULT);
//			return lootTable.generateLoot(lootContextParameterSet);
//		}

		private static boolean canEjectReward(final RogueLootVaultConfig config, final RogueLootVaultState state) {
			return !Objects.equals(config.lootTableIdentifier(), "") && !config.keyItem().create().isEmpty() && state != RogueLootVaultState.INACTIVE;
		}

		private static boolean isValidToInsert(final RogueLootVaultConfig config, final ItemStack stack) {
			return ItemStack.isSameItemSameComponents(stack, config.keyItem().create()) && stack.getCount() >= config.keyItem().create().getCount();
		}

		private static boolean shouldCycleDisplayItem(final long time, final RogueLootVaultState state) {
			return time % 20L == 0L && state == RogueLootVaultState.ACTIVE;
		}

		private static void playInsertFailSound(final ServerLevel serverLevel, final RogueLootVaultServerData serverData, final BlockPos pos, final SoundEvent sound) {
			if (serverLevel.getGameTime() >= serverData.getLastFailedUnlockTime() + 15L) {
				serverLevel.playSound(null, pos, sound, SoundSource.BLOCKS);
				serverData.setLastFailedUnlockTime(serverLevel.getGameTime());
			}
		}
	}
}
