package com.github.theredbrain.rogueloot.block.entity;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class InteractiveRogueLootBlockEntity extends BlockEntity {
	private final Set<UUID> playerSet = new HashSet<>();
	private String lootTableIdentifierString = "";
	private Mode mode = Mode.VANILLA;
	private int rolls = 3;
	private int minChoices = 1;
	private int maxChoices = 1;
	private boolean trackPlayers = false;
	private String lootAcquiredMessage = "gui.interactive_loot_block.loot_acquired";
	private String lootAcquiredSoundId = "";
	private String alreadyLootedMessage = "gui.interactive_loot_block.already_looted";
	private String alreadyLootedSoundId = "block.chest.locked";

	public InteractiveRogueLootBlockEntity(final BlockPos pos, final BlockState state) {
		super(EntityRegistry.INTERACTIVE_ROGUE_LOOT_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void saveAdditional(final ValueOutput output) {

		super.saveAdditional(output);

		ValueOutput.TypedOutputList<UUID> listAppender = output.list("uuid_list", UUIDUtil.AUTHLIB_CODEC);
		for (UUID uuid : this.playerSet) {
			listAppender.add(uuid);
		}

		output.putString("loot_table_identifier_string", this.lootTableIdentifierString);

		output.store("mode", Mode.CODEC, this.mode);

		output.putInt("rolls", this.rolls);

		output.putInt("choices", this.minChoices);

		output.putInt("max_choices", this.maxChoices);

		output.putBoolean("track_players", true);

		output.putString("loot_acquired_message", this.lootAcquiredMessage);

		output.putString("loot_acquired_sound_id", this.lootAcquiredSoundId);

		output.putString("already_looted_message", this.alreadyLootedMessage);

		output.putString("already_looted_sound_id", this.alreadyLootedSoundId);
	}

	@Override
	protected void loadAdditional(final ValueInput input) {

		super.loadAdditional(input);

		this.playerSet.clear();
		if (input.contains("uuidList")) {
			for (UUID uuid : input.listOrEmpty("uuidList", UUIDUtil.AUTHLIB_CODEC)) {
				this.playerSet.add(uuid);
			}
		} else {
			for (UUID uuid : input.listOrEmpty("uuid_list", UUIDUtil.AUTHLIB_CODEC)) {
				this.playerSet.add(uuid);
			}
		}

		this.lootTableIdentifierString = input.getStringOr("loot_table_identifier_string", input.getStringOr("lootTableIdentifierString", ""));

		this.mode = input.read("mode", Mode.CODEC).orElse(Mode.byName(input.getStringOr("mode", "")).orElse(Mode.VANILLA));

		this.rolls = input.getIntOr("rolls", 3);

		this.minChoices = input.getIntOr("min_choices", input.getIntOr("choices", 1));

		this.maxChoices = input.getIntOr("max_choices", 1);

		if (this.minChoices > this.maxChoices) {
			if (this.getLevel() != null) {
				RogueLoot.info("min_choices > max_choices for Interactive Rogue Loot Block at position: " + this.getBlockPos().toShortString() + " in " + this.getLevel().dimension() + ". This has been corrected.");
			}
			this.maxChoices = this.minChoices;
		}

		this.trackPlayers = input.getBooleanOr("track_players", input.getBooleanOr("trackPlayers", false));

		this.lootAcquiredMessage = input.getStringOr("loot_acquired_message", input.getStringOr("lootAcquiredMessage", ""));

		this.lootAcquiredSoundId = input.getStringOr("loot_acquired_sound_id", input.getStringOr("lootAcquiredSoundId", ""));

		this.alreadyLootedMessage = input.getStringOr("already_looted_message", input.getStringOr("alreadyLootedMessage", ""));

		this.alreadyLootedSoundId = input.getStringOr("already_looted_sound_id", input.getStringOr("alreadyLootedSoundId", ""));
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(final HolderLookup.Provider registryLookup) {
		return this.saveCustomOnly(registryLookup);
	}

	@Override
	public void setRemoved() {
		if (this.level instanceof ServerLevel serverWorld) {
			Vec3 pos = Vec3.atLowerCornerOf(this.worldPosition);
			if (this.mode == Mode.ROGUE_LOOT) {
				RogueLootTableHelper.removeRogueLootTableUses(serverWorld, pos, this.lootTableIdentifierString, this.rolls, this.minChoices, this.maxChoices, null);
			} else if (this.mode == Mode.LOOTABLE_CHOICE || this.mode == Mode.LOOTABLE_RANDOM) {
				RogueLoot.removeLootableUses(serverWorld, pos, this.lootTableIdentifierString, null);
			}
		}
		super.setRemoved();
	}

	public String getLootTableIdentifierString() {
		return this.lootTableIdentifierString;
	}

	public void setLootTableIdentifierString(final String lootTableIdentifierString) {
		this.lootTableIdentifierString = lootTableIdentifierString;
	}

	public Mode getMode() {
		return this.mode;
	}

	public void setMode(final Mode mode) {
		this.mode = mode;
	}

	public int getRolls() {
		return this.rolls;
	}

	public void setRolls(final int rolls) {
		this.rolls = rolls;
	}

	public int getMinChoices() {
		return this.minChoices;
	}

	public void setMinChoices(final int minChoices) {
		this.minChoices = minChoices;
	}

	public int getMaxChoices() {
		return this.maxChoices;
	}

	public void setMaxChoices(final int maxChoices) {
		this.maxChoices = maxChoices;
	}

	public boolean trackPlayers() {
		return this.trackPlayers;
	}

	public void setTrackPlayers(final boolean trackPlayers) {
		this.trackPlayers = trackPlayers;
	}

	public String getLootAcquiredMessage() {
		return this.lootAcquiredMessage;
	}

	public void setLootAcquiredMessage(final String lootAcquiredMessage) {
		this.lootAcquiredMessage = lootAcquiredMessage;
	}

	public String getLootAcquiredSoundId() {
		return this.lootAcquiredSoundId;
	}

	public void setLootAcquiredSoundId(final String lootAcquiredSoundId) {
		this.lootAcquiredSoundId = lootAcquiredSoundId;
	}

	public String getAlreadyLootedMessage() {
		return this.alreadyLootedMessage;
	}

	public void setAlreadyLootedMessage(final String alreadyLootedMessage) {
		this.alreadyLootedMessage = alreadyLootedMessage;
	}

	public String getAlreadyLootedSoundId() {
		return this.alreadyLootedSoundId;
	}

	public void setAlreadyLootedSoundId(final String alreadyLootedSoundId) {
		this.alreadyLootedSoundId = alreadyLootedSoundId;
	}

	public boolean isPlayerInSet(final Player playerEntity) {
		return this.playerSet.contains(playerEntity.getUUID());
	}

	public void addPlayerToSet(final Player playerEntity) {
		this.playerSet.add(playerEntity.getUUID());
	}

	public void removePlayerFromSet(final Player playerEntity) {
		this.playerSet.remove(playerEntity.getUUID());
	}

	public void reset() {
		this.playerSet.clear();
		if (this.level instanceof ServerLevel serverWorld) {
			Vec3 pos = Vec3.atLowerCornerOf(this.worldPosition);
			if (this.mode == Mode.ROGUE_LOOT) {
				RogueLootTableHelper.removeRogueLootTableUses(serverWorld, pos, this.lootTableIdentifierString, this.rolls, this.minChoices, this.maxChoices, null);
			} else if (this.mode == Mode.LOOTABLE_CHOICE || this.mode == Mode.LOOTABLE_RANDOM) {
				RogueLoot.removeLootableUses(serverWorld, pos, this.lootTableIdentifierString, null);
			}
		}
	}

	public enum Mode implements StringRepresentable {
		VANILLA("vanilla"),
		ROGUE_LOOT("rogue_loot"),
		LOOTABLE_CHOICE("lootable_choice"),
		LOOTABLE_RANDOM("lootable_random");

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		private final String name;

		Mode(final String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		@Deprecated
		public static Optional<Mode> byName(final String name) {
			return Arrays.stream(Mode.values()).filter(mode -> mode.getSerializedName().equals(name)).findFirst();
		}

		public Component asText() {
			return Component.translatable("gui.interactive_rogue_loot_block.mode." + this.name);
		}
	}
}
