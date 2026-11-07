package com.github.theredbrain.lootableblocks.block.entity;

import com.github.theredbrain.lootableblocks.LootableBlocks;
import com.github.theredbrain.lootableblocks.registry.EntityRegistry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class InteractiveLootBlockEntity extends BlockEntity {
	private final Set<UUID> playerSet = new HashSet<>();
	private String lootTableIdentifierString = "";
	private Mode mode = Mode.VANILLA;
	private int rolls = 3;
	private int choices = 1;
	private boolean trackPlayers = false;
	private String lootAcquiredMessage = "gui.interactive_loot_block.loot_acquired";
	private String lootAcquiredSoundId = "";
	private String alreadyLootedMessage = "gui.interactive_loot_block.already_looted";
	private String alreadyLootedSoundId = "block.chest.locked";

	public InteractiveLootBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.INTERACTIVE_LOOT_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {

		List<UUID> list = this.playerSet.stream().toList();
		int listSize = list.size();
		nbt.putInt("listSize", listSize);
		for (int i = 0; i < listSize; i++) {
			nbt.putUuid("listEntry_" + i, list.get(i));
		}

		if (!this.lootTableIdentifierString.isEmpty()) {
			nbt.putString("lootTableIdentifierString", this.lootTableIdentifierString);
		} else {
			nbt.remove("lootTableIdentifierString");
		}

		if (this.mode != Mode.VANILLA) {
			nbt.putString("mode", this.mode.getSerializedName());
		} else {
			nbt.remove("mode");
		}

		if (this.rolls != 3) {
			nbt.putInt("rolls", this.rolls);
		} else {
			nbt.remove("rolls");
		}

		if (this.choices != 1) {
			nbt.putInt("choices", this.choices);
		} else {
			nbt.remove("choices");
		}

		if (this.trackPlayers) {
			nbt.putBoolean("trackPlayers", true);
		} else {
			nbt.remove("trackPlayers");
		}

		if (!this.lootAcquiredMessage.isEmpty()) {
			nbt.putString("lootAcquiredMessage", this.lootAcquiredMessage);
		} else {
			nbt.remove("lootAcquiredMessage");
		}

		if (!this.lootAcquiredSoundId.isEmpty()) {
			nbt.putString("lootAcquiredSoundId", this.lootAcquiredSoundId);
		} else {
			nbt.remove("lootAcquiredSoundId");
		}

		if (!this.alreadyLootedMessage.isEmpty()) {
			nbt.putString("alreadyLootedMessage", this.alreadyLootedMessage);
		} else {
			nbt.remove("alreadyLootedMessage");
		}

		if (!this.alreadyLootedSoundId.isEmpty()) {
			nbt.putString("alreadyLootedSoundId", this.alreadyLootedSoundId);
		} else {
			nbt.remove("alreadyLootedSoundId");
		}

		super.writeNbt(nbt, registryLookup);
	}

	@Override
	protected void readNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {

		this.playerSet.clear();
		int listSize = nbt.getInt("listSize");
		for (int i = 0; i < listSize; i++) {
			if (nbt.containsUuid("listEntry_" + i)) {
				this.playerSet.add(nbt.getUuid("listEntry_" + i));
			}
		}

		if (nbt.contains("lootTableIdentifierString")) {
			this.lootTableIdentifierString = nbt.getString("lootTableIdentifierString");
		} else {
			this.lootTableIdentifierString = "";
		}

		this.mode = Mode.byName(nbt.getString("mode")).orElse(Mode.VANILLA);

		if (nbt.contains("rolls")) {
			this.rolls = nbt.getInt("rolls");
		} else {
			this.rolls = 3;
		}

		if (nbt.contains("choices")) {
			this.choices = nbt.getInt("choices");
		} else {
			this.choices = 1;
		}

		this.trackPlayers = nbt.contains("trackPlayers");

		if (nbt.contains("lootAcquiredMessage")) {
			this.lootAcquiredMessage = nbt.getString("lootAcquiredMessage");
		} else {
			this.lootAcquiredMessage = "";
		}

		if (nbt.contains("lootAcquiredSoundId")) {
			this.lootAcquiredSoundId = nbt.getString("lootAcquiredSoundId");
		} else {
			this.lootAcquiredSoundId = "";
		}

		if (nbt.contains("alreadyLootedMessage")) {
			this.alreadyLootedMessage = nbt.getString("alreadyLootedMessage");
		} else {
			this.alreadyLootedMessage = "";
		}

		if (nbt.contains("alreadyLootedSoundId")) {
			this.alreadyLootedSoundId = nbt.getString("alreadyLootedSoundId");
		} else {
			this.alreadyLootedSoundId = "";
		}

		super.readNbt(nbt, registryLookup);
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
		return this.saveCustomOnly(registryLookup);
	}

	@Override
	public void setRemoved() {
		if (this.level instanceof ServerLevel serverWorld) {
			LootableBlocks.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), this.lootTableIdentifierString, null);
		}
		super.setRemoved();
	}

	public String getLootTableIdentifierString() {
		return this.lootTableIdentifierString;
	}

	public void setLootTableIdentifierString(String lootTableIdentifierString) {
		this.lootTableIdentifierString = lootTableIdentifierString;
	}

	public Mode getMode() {
		return this.mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public int getRolls() {
		return this.rolls;
	}

	public void setRolls(int rolls) {
		this.rolls = rolls;
	}

	public int getChoices() {
		return this.choices;
	}

	public void setChoices(int choices) {
		this.choices = choices;
	}

	public boolean trackPlayers() {
		return this.trackPlayers;
	}

	public void setTrackPlayers(boolean trackPlayers) {
		this.trackPlayers = trackPlayers;
	}

	public String getLootAcquiredMessage() {
		return this.lootAcquiredMessage;
	}

	public void setLootAcquiredMessage(String lootAcquiredMessage) {
		this.lootAcquiredMessage = lootAcquiredMessage;
	}

	public String getLootAcquiredSoundId() {
		return this.lootAcquiredSoundId;
	}

	public void setLootAcquiredSoundId(String lootAcquiredSoundId) {
		this.lootAcquiredSoundId = lootAcquiredSoundId;
	}

	public String getAlreadyLootedMessage() {
		return this.alreadyLootedMessage;
	}

	public void setAlreadyLootedMessage(String alreadyLootedMessage) {
		this.alreadyLootedMessage = alreadyLootedMessage;
	}

	public String getAlreadyLootedSoundId() {
		return this.alreadyLootedSoundId;
	}

	public void setAlreadyLootedSoundId(String alreadyLootedSoundId) {
		this.alreadyLootedSoundId = alreadyLootedSoundId;
	}

	public boolean isPlayerInSet(Player playerEntity) {
		return this.playerSet.contains(playerEntity.getUUID());
	}

	public void addPlayerToSet(Player playerEntity) {
		this.playerSet.add(playerEntity.getUUID());
	}

	public void removePlayerFromSet(Player playerEntity) {
		this.playerSet.remove(playerEntity.getUUID());
	}

	public void reset() {
		this.playerSet.clear();
		if (this.level instanceof ServerLevel serverWorld) {
			LootableBlocks.removeLootableUses(serverWorld, Vec3.atLowerCornerOf(this.worldPosition), this.lootTableIdentifierString, null);
		}
	}

	public static enum Mode implements StringRepresentable {
		CHOICE("choice"),
		RANDOM("random"),
		VANILLA("vanilla");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public static Optional<Mode> byName(String name) {
			return Arrays.stream(Mode.values()).filter(mode -> mode.getSerializedName().equals(name)).findFirst();
		}

		public Component asText() {
			return Component.translatable("gui.interactive_loot_block.mode." + this.name);
		}
	}
}
