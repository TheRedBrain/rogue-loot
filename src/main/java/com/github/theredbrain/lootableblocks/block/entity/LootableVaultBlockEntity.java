package com.github.theredbrain.lootableblocks.block.entity;

import com.github.theredbrain.lootableblocks.LootableBlocks;
import com.github.theredbrain.lootableblocks.block.LootableVaultBlock;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultClientData;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultServerData;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultSharedData;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultState;
import com.github.theredbrain.lootableblocks.data.LootableVaultConfig;
import com.github.theredbrain.lootableblocks.registry.CustomDynamicRegistries;
import com.github.theredbrain.lootableblocks.registry.EntityRegistry;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class LootableVaultBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final LootableVaultServerData serverData = new LootableVaultServerData();
	private final LootableVaultSharedData sharedData = new LootableVaultSharedData();
	private final LootableVaultClientData clientData = new LootableVaultClientData();
	private String lootableVaultConfigIdentifier = "";

	public LootableVaultBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.LOOTABLE_VAULT_BLOCK_ENTITY, pos, state);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
		return Util.make(new CompoundTag(), nbt -> nbt.put("shared_data", encodeValue(LootableVaultSharedData.CODEC, this.sharedData, registryLookup)));
	}

	@Override
	protected void writeNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		nbt.putString("config_identifier", this.lootableVaultConfigIdentifier);

		nbt.put("shared_data", encodeValue(LootableVaultSharedData.CODEC, this.sharedData, registryLookup));
		nbt.put("server_data", encodeValue(LootableVaultServerData.CODEC, this.serverData, registryLookup));

		super.writeNbt(nbt, registryLookup);
	}

	private static <T> Tag encodeValue(Codec<T> codec, T value, HolderLookup.Provider registries) {
		return codec.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), value).getOrThrow();
	}

	@Override
	protected void readNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		DynamicOps<Tag> dynamicOps = registryLookup.createSerializationContext(NbtOps.INSTANCE);
		if (nbt.contains("server_data")) {
			LootableVaultServerData.CODEC.parse(dynamicOps, nbt.get("server_data")).resultOrPartial(LOGGER::error).ifPresent(this.serverData::copyFrom);
		}

		this.lootableVaultConfigIdentifier = nbt.getString("config_identifier");

		if (nbt.contains("shared_data")) {
			LootableVaultSharedData.CODEC.parse(dynamicOps, nbt.get("shared_data")).resultOrPartial(LOGGER::error).ifPresent(this.sharedData::copyFrom);
		}

		super.readNbt(nbt, registryLookup);
	}

	@Override
	public void setRemoved() {
		if (this.level instanceof ServerLevel serverWorld) {
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), null);
		}
		super.setRemoved();
	}

	@Nullable
	public LootableVaultServerData getServerData() {
		return this.level != null && !this.level.isClientSide ? this.serverData : null;
	}

	public LootableVaultSharedData getSharedData() {
		return this.sharedData;
	}

	public LootableVaultClientData getClientData() {
		return this.clientData;
	}

	public void markAsRewarded(ServerPlayer serverPlayerEntity, ItemStack itemStack) {
		if (this.level instanceof ServerLevel serverWorld) {
			LootableVaultServerData serverData = this.getServerData();
			LootableVaultSharedData sharedData = this.getSharedData();
			LootableVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverPlayerEntity.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
				itemStack.consume(config.key_item().getCount(), serverPlayerEntity);
				Server.unlock(serverWorld, serverWorld.getBlockState(this.worldPosition), this.worldPosition, config, serverData, sharedData);
				serverData.markPlayerAsRewarded(serverPlayerEntity);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivation_range());
			}
		}
	}

	public void unmarkAsRewarded(ServerPlayer serverPlayerEntity) {
		if (this.level instanceof ServerLevel serverWorld) {
			LootableVaultServerData serverData = this.getServerData();
			LootableVaultSharedData sharedData = this.getSharedData();
			LootableVaultConfig config = this.getConfig(serverWorld);
			if (serverData != null && sharedData != null) {
				serverData.unmarkPlayerAsRewarded(serverPlayerEntity);
				sharedData.updateConnectedPlayers(serverWorld, this.worldPosition, serverData, config, config.deactivation_range());
			}
			this.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), serverPlayerEntity);
		}
	}

	public LootableVaultConfig getConfig(Level world) {
		LootableVaultConfig lootableVaultConfig = world.registryAccess().get(CustomDynamicRegistries.LOOTABLE_VAULT_CONFIG_REGISTRY_KEY).get(Identifier.parse(this.lootableVaultConfigIdentifier));
		if (lootableVaultConfig != null) {
			return lootableVaultConfig;
		}
		LootableBlocks.info("Config not valid: " + this.lootableVaultConfigIdentifier);
		return LootableVaultConfig.DEFAULT;
	}

	private void removeLootableUses(ServerLevel serverWorld, Vec3 pos, @Nullable ServerPlayer serverPlayerEntity) {
		LootableBlocks.removeLootableUses(serverWorld, pos, this.getConfig(serverWorld).lootable_identifier(), serverPlayerEntity);
	}

	public static final class Client {

		public static void tick(Level world, BlockPos pos, BlockState state, LootableVaultClientData clientData, LootableVaultSharedData sharedData) {
			clientData.rotateDisplay();
			if (world.getGameTime() % 20L == 0L) {
				spawnConnectedParticles(world, pos, state, sharedData);
			}

			spawnAmbientParticles(world, pos, sharedData, state.getValue(VaultBlock.OMINOUS) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
			playAmbientSound(world, pos, sharedData);
		}

		// TODO research custom world events
		public static void spawnActivateParticles(Level world, BlockPos pos, BlockState state, LootableVaultSharedData sharedData, ParticleOptions particle) {
			spawnConnectedParticles(world, pos, state, sharedData);
			RandomSource random = world.random;

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = getRegularParticlesPos(pos, random);
				world.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
			}
		}

		public static void spawnDeactivateParticles(Level world, BlockPos pos, ParticleOptions particle) {
			RandomSource random = world.random;

			for (int i = 0; i < 20; i++) {
				Vec3 vec3d = getDeactivateParticlesPos(pos, random);
				Vec3 vec3d2 = new Vec3(random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
				world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void spawnAmbientParticles(Level world, BlockPos pos, LootableVaultSharedData sharedData, ParticleOptions particle) {
			RandomSource random = world.getRandom();
			if (random.nextFloat() <= 0.5F) {
				Vec3 vec3d = getRegularParticlesPos(pos, random);
				world.addParticle(ParticleTypes.SMOKE, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				if (hasDisplayItem(sharedData)) {
					world.addParticle(particle, vec3d.x(), vec3d.y(), vec3d.z(), 0.0, 0.0, 0.0);
				}
			}
		}

		private static void spawnConnectedParticlesFor(Level world, Vec3 pos, Player player) {
			RandomSource random = world.random;
			Vec3 vec3d = pos.vectorTo(player.getPos().add(0.0, (double) (player.getBbHeight() / 2.0F), 0.0));
			int i = Mth.nextInt(random, 2, 5);

			for (int j = 0; j < i; j++) {
				Vec3 vec3d2 = vec3d.offsetRandom(random, 1.0F);
				world.addParticle(ParticleTypes.VAULT_CONNECTION, pos.x(), pos.y(), pos.z(), vec3d2.x(), vec3d2.y(), vec3d2.z());
			}
		}

		private static void spawnConnectedParticles(Level world, BlockPos pos, BlockState state, LootableVaultSharedData sharedData) {
			Set<UUID> set = sharedData.getConnectedPlayers();
			if (!set.isEmpty()) {
				Vec3 vec3d = getConnectedParticlesOrigin(pos, state.getValue(VaultBlock.FACING));

				for (UUID uUID : set) {
					Player playerEntity = world.getPlayerByUUID(uUID);
					if (playerEntity != null && isPlayerWithinConnectedParticlesRange(pos, sharedData, playerEntity)) {
						spawnConnectedParticlesFor(world, vec3d, playerEntity);
					}
				}
			}
		}

		private static boolean isPlayerWithinConnectedParticlesRange(BlockPos pos, LootableVaultSharedData sharedData, Player player) {
			return player.blockPosition().distSqr(pos) <= Mth.square(sharedData.getConnectedParticlesRange());
		}

		private static void playAmbientSound(Level world, BlockPos pos, LootableVaultSharedData sharedData) {
			if (hasDisplayItem(sharedData)) {
				RandomSource random = world.getRandom();
				if (random.nextFloat() <= 0.02F) {
					world.playSoundAtBlockCenter(
							pos, SoundEvents.VAULT_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false
					);
				}
			}
		}

		public static boolean hasDisplayItem(LootableVaultSharedData sharedData) {
			return sharedData.hasDisplayItem();
		}

		private static Vec3 getDeactivateParticlesPos(BlockPos pos, RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6), Mth.nextDouble(random, 0.4, 0.6));
		}

		private static Vec3 getRegularParticlesPos(BlockPos pos, RandomSource random) {
			return Vec3.atLowerCornerOf(pos).add(Mth.nextDouble(random, 0.1, 0.9), Mth.nextDouble(random, 0.25, 0.75), Mth.nextDouble(random, 0.1, 0.9));
		}

		private static Vec3 getConnectedParticlesOrigin(BlockPos pos, Direction direction) {
			return Vec3.atBottomCenterOf(pos).add((double) direction.getStepX() * 0.5, 1.75, (double) direction.getStepZ() * 0.5);
		}
	}

	public static final class Server {

		public static void tick(ServerLevel world, BlockPos pos, BlockState state, LootableVaultConfig config, LootableVaultServerData serverData, LootableVaultSharedData sharedData) {
			LootableVaultState vaultState = state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE);
			if (shouldUpdateDisplayItem(world.getGameTime(), vaultState)) {
				updateDisplayItem(world, vaultState, config, sharedData, pos);
			}

			BlockState blockState = state;
			if (world.getGameTime() >= serverData.getStateUpdatingResumeTime()) {
				blockState = state.setValue(LootableVaultBlock.LOOTABLE_VAULT_STATE, vaultState.update(world, pos, config, serverData, sharedData));
				if (!state.equals(blockState)) {
					changeVaultState(world, pos, state, blockState, config, sharedData);
				}
			}

			if (serverData.isDirty() || sharedData.isDirty()) {
				LootableVaultBlockEntity.setChanged(world, pos, state);
				if (sharedData.isDirty()) {
					world.sendBlockUpdated(pos, state, blockState, Block.UPDATE_CLIENTS);
				}

				serverData.markClean();
				sharedData.markClean();
			}
		}

		public static void tryUnlock(
				ServerLevel world,
				BlockPos pos,
				BlockState state,
				LootableVaultConfig config,
				LootableVaultServerData serverData,
				LootableVaultSharedData sharedData,
				Player player,
				ItemStack stack
		) {
			LootableVaultState vaultState = state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE);
			if (canBeUnlocked(config, vaultState)) {
				if (!isValidKey(config, stack)) {
					playFailedUnlockSound(world, serverData, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
				} else if (serverData.hasRewardedPlayer(player)) {
					playFailedUnlockSound(world, serverData, pos, SoundEvents.VAULT_REJECT_REWARDED_PLAYER);
				} else if (player instanceof ServerPlayer serverPlayerEntity) {
					LootableBlocks.supplyLootableLoot(Identifier.parse(config.lootable_identifier()), world, serverPlayerEntity, Vec3.atLowerCornerOf(pos), config.rolls(), config.choices(), config.with_choice(), stack);
				}
			}
		}

		static void changeVaultState(ServerLevel world, BlockPos pos, BlockState oldState, BlockState newState, LootableVaultConfig config, LootableVaultSharedData sharedData) {
			LootableVaultState vaultState = oldState.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE);
			LootableVaultState vaultState2 = newState.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE);
			world.setBlock(pos, newState, Block.UPDATE_ALL);
			vaultState.onStateChange(world, pos, vaultState2, config, sharedData, (Boolean) newState.getValue(VaultBlock.OMINOUS));
		}

		public static void updateDisplayItem(ServerLevel world, LootableVaultState state, LootableVaultConfig config, LootableVaultSharedData sharedData, BlockPos pos) {
			if (!canBeUnlocked(config, state)) {
				sharedData.setDisplayItem(ItemStack.EMPTY);
			} else {
				ItemStack itemStack = ItemStack.EMPTY;
				if (!config.display_loot_table_identifier().isEmpty()) {
					itemStack = generateDisplayItem(world, pos, world.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(config.display_loot_table_identifier()))));
				}
				sharedData.setDisplayItem(itemStack);
			}
		}

		private static ItemStack generateDisplayItem(ServerLevel world, BlockPos pos, LootTable lootTable) {
			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
					.add(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
					.build(LootContextParamSets.VAULT);
			List<ItemStack> list = lootTable.getRandomItems(lootContextParameterSet, world.getRandom());
			return list.isEmpty() ? ItemStack.EMPTY : Util.getRandom(list, world.getRandom());
		}

		public static void unlock(
				ServerLevel world, BlockState state, BlockPos pos, LootableVaultConfig config, LootableVaultServerData serverData, LootableVaultSharedData sharedData
		) {
			sharedData.setDisplayItem(ItemStack.EMPTY);
			serverData.setStateUpdatingResumeTime(world.getGameTime() + 14L);
			changeVaultState(world, pos, state, state.setValue(LootableVaultBlock.LOOTABLE_VAULT_STATE, LootableVaultState.UNLOCKING), config, sharedData);
		}

		private static boolean canBeUnlocked(LootableVaultConfig config, LootableVaultState state) {
			return !Objects.equals(config.lootable_identifier(), "") && !config.key_item().isEmpty() && state != LootableVaultState.INACTIVE;
		}

		private static boolean isValidKey(LootableVaultConfig config, ItemStack stack) {
			return ItemStack.isSameItemSameComponents(stack, config.key_item()) && stack.getCount() >= config.key_item().getCount();
		}

		private static boolean shouldUpdateDisplayItem(long time, LootableVaultState state) {
			return time % 20L == 0L && state == LootableVaultState.ACTIVE;
		}

		private static void playFailedUnlockSound(ServerLevel world, LootableVaultServerData serverData, BlockPos pos, SoundEvent sound) {
			if (world.getGameTime() >= serverData.getLastFailedUnlockTime() + 15L) {
				world.playSound(null, pos, sound, SoundSource.BLOCKS);
				serverData.setLastFailedUnlockTime(world.getGameTime());
			}
		}
	}
}
