package com.github.theredbrain.rogueloot.block.entity;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.RogueLootVaultBlock;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultClientData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultServerData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultSharedData;
import com.github.theredbrain.rogueloot.block.rogue_loot_vault.RogueLootVaultState;
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
import org.jetbrains.annotations.Nullable;
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

	public RogueLootVaultBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.ROGUE_LOOT_VAULT_BLOCK_ENTITY, pos, state);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return Util.make(new CompoundTag(), tag -> tag.store("shared_data", RogueLootVaultSharedData.CODEC, registries.createSerializationContext(NbtOps.INSTANCE), this.sharedData));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {

		output.putString("config_identifier", this.lootableVaultConfigIdentifier);

		output.store("shared_data", RogueLootVaultSharedData.CODEC, this.sharedData);
		output.store("server_data", RogueLootVaultServerData.CODEC, this.serverData);

		super.saveAdditional(output);
	}

	@Override
	protected void loadAdditional(ValueInput input) {

		input.read("shared_data", RogueLootVaultSharedData.CODEC).ifPresent(this.sharedData::set);

		input.read("server_data", RogueLootVaultServerData.CODEC).ifPresent(this.serverData::set);

		this.lootableVaultConfigIdentifier = input.getStringOr("config_identifier", "");
		this.config = null;

		super.loadAdditional(input);
	}

	@Override
	public void setRemoved() {
		if (this.level instanceof ServerLevel serverWorld) {
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), null);
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

	public void markAsRewarded(ServerPlayer serverPlayerEntity, ItemStack itemStack) {
		if (this.level instanceof ServerLevel serverWorld) {
			RogueLootVaultServerData serverData = this.getServerData();
			RogueLootVaultSharedData sharedData = this.getSharedData();
			RogueLootVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverPlayerEntity.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				itemStack.consume(config.keyItem().create().getCount(), serverPlayerEntity);
				Server.unlock(serverWorld, serverWorld.getBlockState(this.worldPosition), this.worldPosition, config, serverData, sharedData);
				serverData.markPlayerAsRewarded(serverPlayerEntity);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivationRange());
			}
		}
	}

	public void unmarkAsRewarded(ServerPlayer serverPlayerEntity) {
		if (this.level instanceof ServerLevel serverWorld) {
			RogueLootVaultServerData serverData = this.getServerData();
			RogueLootVaultSharedData sharedData = this.getSharedData();
			RogueLootVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverData.unmarkPlayerAsRewarded(serverPlayerEntity);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivationRange());
			}
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), serverPlayerEntity);
		}
	}

	public RogueLootVaultConfig getConfig(Level world) {
		if (this.config != null) {
			return this.config;
		}
		Identifier identifier = Identifier.tryParse(this.lootableVaultConfigIdentifier);
		Optional<Holder.Reference<Registry<RogueLootVaultConfig>>> optionalLootableVaultConfigRegistryReference = world.registryAccess().get(CustomDynamicRegistries.ROGUE_LOOT_VAULT_CONFIG_REGISTRY_KEY);
		if (optionalLootableVaultConfigRegistryReference.isPresent()) {
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

	private void removeLootableUses(ServerLevel serverWorld, Vec3 pos, @Nullable ServerPlayer serverPlayerEntity) {
		RogueLoot.removeLootableUses(serverWorld, pos, this.getConfig(serverWorld).lootableIdentifier(), serverPlayerEntity);
	}

	public static final class Client {

		public static void tick(Level world, BlockPos pos, BlockState state, RogueLootVaultClientData clientData, RogueLootVaultSharedData sharedData) {
			clientData.rotateDisplay();
			if (world.getGameTime() % 20L == 0L) {
				emitConnectionParticlesForNearbyPlayers(world, pos, state, sharedData);
			}

			emitIdleParticles(world, pos, sharedData, state.getValue(VaultBlock.OMINOUS) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
			playIdleSounds(world, pos, sharedData);
		}

		public static void emitActivationParticles(Level world, BlockPos pos, BlockState state, RogueLootVaultSharedData sharedData, ParticleOptions particle) {
			emitConnectionParticlesForNearbyPlayers(world, pos, state, sharedData);
			RandomSource random = world.getRandom();

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = randomPosInsideCage(pos, random);
				world.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
			}
		}

		public static void emitDeactivationParticles(Level world, BlockPos pos, ParticleOptions particle) {
			RandomSource random = world.getRandom();

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = randomPosCenterOfCage(pos, random);
				Vec3 vec3d2 = new Vec3(random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
				world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void emitIdleParticles(Level world, BlockPos pos, RogueLootVaultSharedData sharedData, ParticleOptions particle) {
			RandomSource random = world.getRandom();
			if (random.nextFloat() <= 0.5F) {
				Vec3 vec3d = randomPosInsideCage(pos, random);
				world.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				if (shouldDisplayActiveEffects(sharedData)) {
					world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				}
			}
		}

		private static void emitConnectionParticlesForPlayer(Level world, Vec3 pos, Player player) {
			RandomSource random = world.getRandom();
			Vec3 vec3d = pos.vectorTo(player.position().add(0.0, (double) (player.getBbHeight() / 2.0F), 0.0));
			int i = Mth.nextInt(random, 2, 5);

			for (int j = 0; j < i; j++) {
				Vec3 vec3d2 = vec3d.offsetRandom(random, 1.0F);
				world.addParticle(ParticleTypes.VAULT_CONNECTION, pos.x(), pos.y(), pos.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void emitConnectionParticlesForNearbyPlayers(Level world, BlockPos pos, BlockState state, RogueLootVaultSharedData sharedData) {
			Set<UUID> set = sharedData.getConnectedPlayers();
			if (!set.isEmpty()) {
				Vec3 vec3d = keyholePos(pos, state.getValue(VaultBlock.FACING));

				for (UUID uUID : set) {
					Player playerEntity = world.getPlayerByUUID(uUID);
					if (playerEntity != null && isWithinConnectionRange(pos, sharedData, playerEntity)) {
						emitConnectionParticlesForPlayer(world, vec3d, playerEntity);
					}
				}
			}
		}

		private static boolean isWithinConnectionRange(BlockPos pos, RogueLootVaultSharedData sharedData, Player player) {
			return player.blockPosition().distSqr(pos) <= Mth.square(sharedData.getConnectedParticlesRange());
		}

		private static void playIdleSounds(Level world, BlockPos pos, RogueLootVaultSharedData sharedData) {
			if (shouldDisplayActiveEffects(sharedData)) {
				RandomSource random = world.getRandom();
				if (random.nextFloat() <= 0.02F) {
					world.playLocalSound(
							pos, SoundEvents.VAULT_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false
					);
				}
			}
		}

		public static boolean shouldDisplayActiveEffects(RogueLootVaultSharedData sharedData) {
			return sharedData.hasDisplayItem();
		}

		private static Vec3 randomPosCenterOfCage(BlockPos pos, RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6));
		}

		private static Vec3 randomPosInsideCage(BlockPos pos, RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
		}

		private static Vec3 keyholePos(BlockPos pos, Direction direction) {
			return Vec3.atBottomCenterOf(pos).add((double) direction.getStepX() * 0.5, 1.75, (double) direction.getStepZ() * 0.5);
		}
	}

	public static final class Server {

		public static void tick(ServerLevel world, BlockPos pos, BlockState state, RogueLootVaultConfig config, RogueLootVaultServerData serverData, RogueLootVaultSharedData sharedData) {
			RogueLootVaultState vaultState = state.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			if (shouldCycleDisplayItem(world.getGameTime(), vaultState)) {
				cycleDisplayItemFromLootTable(world, vaultState, config, sharedData, pos);
			}

			BlockState blockState = state;
			if (world.getGameTime() >= serverData.getStateUpdatingResumeTime()) {
				blockState = state.setValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE, vaultState.update(world, pos, config, serverData, sharedData));
				if (!state.equals(blockState)) {
					setVaultState(world, pos, state, blockState, config, sharedData);
				}
			}

			if (serverData.isDirty() || sharedData.isDirty()) {
				RogueLootVaultBlockEntity.setChanged(world, pos, state);
				if (sharedData.isDirty()) {
					world.sendBlockUpdated(pos, state, blockState, Block.UPDATE_CLIENTS);
				}

				serverData.markClean();
				sharedData.markClean();
			}
		}

		public static void tryInsertKey(
				ServerLevel world,
				BlockPos pos,
				BlockState state,
				RogueLootVaultConfig config,
				RogueLootVaultServerData serverData,
				RogueLootVaultSharedData sharedData,
				Player player,
				ItemStack stack
		) {
			RogueLootVaultState vaultState = state.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			if (canEjectReward(config, vaultState)) {
				if (!isValidToInsert(config, stack)) {
					RogueLoot.info("invalid key");
					playInsertFailSound(world, serverData, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
				} else if (serverData.hasRewardedPlayer(player)) {
					RogueLoot.info("player already rewarded");
					playInsertFailSound(world, serverData, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER);
				} else if (player instanceof ServerPlayer serverPlayerEntity) {
					// TODO loot
//					Vec3d lootPos = new Vec3d(this.getPos().getX(), interactiveLootBlockEntity.getPos().getY(), interactiveLootBlockEntity.getPos().getZ());
//					boolean lootSupplied = ScriptBlocks.supplyLootableLoot(Identifier.of(config.lootableIdentifier()), serverPlayerEntity, Vec3d.of(pos), config.rolls(), config.choices(), config.withChoice());

//					List<ItemStack> list = generateLoot(world, config, pos, player);
					/*if (*/
					RogueLoot.supplyLootableLoot(Identifier.parse(config.lootableIdentifier()), world, serverPlayerEntity, Vec3.atLowerCornerOf(pos), config.rolls(), config.choices(), config.withChoice(), stack);/*) {
						ScriptBlocks.info("loot supplied");
						player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
						stack.decrementUnlessCreative(config.keyItem().getCount(), player);
						unlock(world, state, pos, config, serverData, sharedData);
					}*/
					serverData.markPlayerAsRewarded(player);
					sharedData.updateConnectedPlayers(world, pos, serverData, config, config.deactivationRange());
				}
			}
		}

		static void setVaultState(ServerLevel world, BlockPos pos, BlockState oldState, BlockState newState, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData) {
			RogueLootVaultState vaultState = oldState.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			RogueLootVaultState vaultState2 = newState.getValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE);
			world.setBlock(pos, newState, Block.UPDATE_ALL);
			vaultState.onStateChange(world, pos, vaultState2, config, sharedData, (Boolean) newState.getValue(VaultBlock.OMINOUS));
		}

		public static void cycleDisplayItemFromLootTable(ServerLevel world, RogueLootVaultState state, RogueLootVaultConfig config, RogueLootVaultSharedData sharedData, BlockPos pos) {
			if (!canEjectReward(config, state)) {
				sharedData.setDisplayItem(ItemStack.EMPTY);
			} else {
				ItemStack itemStack = ItemStack.EMPTY;
				if (!config.displayLootTableIdentifier().isEmpty()) {
					itemStack = getRandomDisplayItemFromLootTable(world, pos, ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(config.displayLootTableIdentifier())));
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
				ServerLevel world, BlockState state, BlockPos pos, RogueLootVaultConfig config, RogueLootVaultServerData serverData, RogueLootVaultSharedData sharedData
		) {
//			serverData.setItemsToEject(itemsToEject);
			sharedData.setDisplayItem(ItemStack.EMPTY);
			serverData.setStateUpdatingResumeTime(world.getGameTime() + 14L);
			setVaultState(world, pos, state, state.setValue(RogueLootVaultBlock.ROGUE_LOOT_VAULT_STATE, RogueLootVaultState.UNLOCKING), config, sharedData);
		}

//		private static List<ItemStack> generateLoot(ServerWorld world, LootableVaultConfig config, BlockPos pos, PlayerEntity player) {
//			LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(config.lootTable());
//			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
//					.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
//					.luck(player.getLuck())
//					.add(LootContextParameters.THIS_ENTITY, player)
//					.build(LootContextTypes.VAULT);
//			return lootTable.generateLoot(lootContextParameterSet);
//		}

		private static boolean canEjectReward(RogueLootVaultConfig config, RogueLootVaultState state) {
			return !Objects.equals(config.lootableIdentifier(), "") && !config.keyItem().create().isEmpty() && state != RogueLootVaultState.INACTIVE;
		}

		private static boolean isValidToInsert(RogueLootVaultConfig config, ItemStack stack) {
			return ItemStack.isSameItemSameComponents(stack, config.keyItem().create()) && stack.getCount() >= config.keyItem().create().getCount();
		}

		private static boolean shouldCycleDisplayItem(long time, RogueLootVaultState state) {
			return time % 20L == 0L && state == RogueLootVaultState.ACTIVE;
		}

		private static void playInsertFailSound(ServerLevel world, RogueLootVaultServerData serverData, BlockPos pos, SoundEvent sound) {
			if (world.getGameTime() >= serverData.getLastFailedUnlockTime() + 15L) {
				world.playSound(null, pos, sound, SoundSource.BLOCKS);
				serverData.setLastFailedUnlockTime(world.getGameTime());
			}
		}
	}
}
