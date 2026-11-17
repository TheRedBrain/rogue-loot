package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.SuppliesRogueLootTable;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacket;
import com.github.theredbrain.rogueloot.registry.RogueLootDynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, final Optional<ItemStack> itemStack) {
		RogueLootTable rogueLootTable = getRogueLootTable(serverLevel.registryAccess(), generatedRogueLootTableIdentifier.rogueLootTableIdentifier());
		if (rogueLootTable == null) {
			return;
		}
		Optional<BlockPos> optionalBlockPos = generatedRogueLootTableIdentifier.optionalBlockPos();
		List<Identifier> generatedRogueLootEntries = new ArrayList<>();
		if (optionalBlockPos.isPresent()) {
			BlockEntity blockEntity = serverLevel.getBlockEntity(optionalBlockPos.get());
			if (blockEntity instanceof SuppliesRogueLootTable suppliesRogueLootTable) {
				generatedRogueLootEntries = new ArrayList<>(suppliesRogueLootTable.getGeneratedRogueLootEntries(serverPlayer));
				if (generatedRogueLootEntries.isEmpty()) {
					generatedRogueLootEntries = generateRogueLootEntries(serverLevel, rogueLootTable, serverPlayer, generatedRogueLootTableIdentifier.rolls());
				}
				if (!generatedRogueLootEntries.isEmpty()) {
					suppliesRogueLootTable.setGeneratedRogueLootEntries(generatedRogueLootEntries, serverPlayer);
					openRogueLootScreen(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, generatedRogueLootEntries, itemStack);
				}
				return;
			}
		}
		// TODO save generatedRogueLootEntries to player attached data
		if (!generatedRogueLootEntries.isEmpty()) {
			openRogueLootScreen(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, generatedRogueLootEntries, itemStack);
		}

	}

	private static void openRogueLootScreen(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, List<Identifier> generatedRogueLootEntries, final Optional<ItemStack> itemStack) {
		RogueLootTable rogueLootTable = getRogueLootTable(serverLevel.registryAccess(), generatedRogueLootTableIdentifier.rogueLootTableIdentifier());
		if (rogueLootTable == null) {
			return;
		}
		ServerPlayNetworking.send(serverPlayer, new OpenRogueLootScreenPacket(
				generateRogueLoot(serverLevel, rogueLootTable, generatedRogueLootEntries, generatedRogueLootTableIdentifier),
				generatedRogueLootTableIdentifier,
				calculateRerollAttempts(serverPlayer, rogueLootTable),
				calculateBanishingAttempts(serverPlayer, rogueLootTable),
				itemStack
		));
	}

	public static void removeGeneratedRogueLootEntries(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier) {

		Optional<BlockPos> optionalBlockPos = generatedRogueLootTableIdentifier.optionalBlockPos();
		if (optionalBlockPos.isPresent()) {
			serverLevel.getBlockEntity(optionalBlockPos.get());
			BlockEntity blockEntity = serverLevel.getBlockEntity(optionalBlockPos.get());
			if (blockEntity instanceof SuppliesRogueLootTable suppliesRogueLootTable) {
				suppliesRogueLootTable.removeGeneratedRogueLootEntries(serverPlayer);
			}
		} else {
			// TODO remove entries from player attached data
		}
	}

	private static int calculateRerollAttempts(ServerPlayer serverPlayer, RogueLootTable rogueLootTable) {

		int rerollAttempts = -1;
		if (rogueLootTable.enableRerolling() && rogueLootTable.rerollCost().isPresent()) {
			ItemStack rerollCostStack = rogueLootTable.rerollCost().get().create();

			if (!rerollCostStack.isEmpty()) {
				rerollAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, rerollCostStack);
			}
		}
		return rerollAttempts;
	}

	private static int calculateBanishingAttempts(ServerPlayer serverPlayer, RogueLootTable rogueLootTable) {

		int banishingAttempts = -1;
		if (rogueLootTable.enableEntryBanishing() && rogueLootTable.banishingCost().isPresent()) {
			ItemStack banishingCostStack = rogueLootTable.banishingCost().get().create();

			if (!banishingCostStack.isEmpty()) {
				banishingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, banishingCostStack);
			}
		}
		return banishingAttempts;
	}

	public static List<Identifier> generateRogueLootEntries(final ServerLevel serverLevel, final RogueLootTable rogueLootTable, final ServerPlayer serverPlayer, final int rolls) {

		List<MutablePair<Integer, Identifier>> tempList = new ArrayList<>();
		int totalWeight = 0;
		for (Holder<RogueLootTableEntry> entry : rogueLootTable.rogueLootTableEntries()) {
			RogueLootTableEntry rogueLootTableEntry = entry.value();
			Optional<ResourceKey<RogueLootTableEntry>> optionalResourceKey = entry.unwrapKey();
			if (optionalResourceKey.isEmpty()) {
				continue;
			}
			Identifier entryIdentifier = optionalResourceKey.get().identifier();
			if (DataAttachmentHelper.isRogueLootEntryBanished(serverPlayer, Optional.of(entryIdentifier))) {
				continue;
			}

			Optional<ContextAwarePredicate> optionalUnlockPredicate = rogueLootTableEntry.unlockPredicate();
			if (optionalUnlockPredicate.isPresent() && !optionalUnlockPredicate.get().matches(EntityPredicate.createContext(serverPlayer, serverPlayer))) {
				continue;
			}

			Identifier rarityIdentifier = Identifier.tryParse(rogueLootTableEntry.rarityIdString());
			int weight = 1;
			if (!rogueLootTableEntry.rarityIdString().isEmpty() && rarityIdentifier != null) {
				RogueLootTableEntryRarity rarity = getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityIdentifier);
				if (rarity != null) {
					weight = rogueLootTable.rarityOverrides().getOrDefault(rarityIdentifier, rarity.defaultWeight());
				}
			}
			tempList.add(new MutablePair<>(weight, entryIdentifier));
			totalWeight = totalWeight + weight;
		}

		if (tempList.size() < rolls) {
			RogueLoot.info("generated rogue loot table has fewer unlocked entries than rolls requested");
		}

		List<Identifier> generatedRogueLootEntries = new ArrayList<>();

		int chosenWeight;
		int index;
		int weightCounter;
		int initialTempListSize = tempList.size();
		for (int i = 0; i < Math.min(initialTempListSize, rolls); i++) {
			chosenWeight = serverLevel.getRandom().nextInt(totalWeight);
			index = -1;
			weightCounter = 0;
			for (int j = 0; j < tempList.size(); j++) {
				weightCounter += tempList.get(j).left;
				if (chosenWeight <= weightCounter) {
					index = j;
					break;
				}
			}
			if (index >= 0) {
				generatedRogueLootEntries.add(tempList.get(index).right);
				totalWeight -= tempList.get(index).left;
				tempList.remove(index);
			}
		}
		return generatedRogueLootEntries;
	}

	public static GeneratedRogueLoot generateRogueLoot(final ServerLevel serverLevel, final RogueLootTable rogueLootTable, final List<Identifier> unlockedEntries, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier) {

		String backgroundTexturePath = "";
		String entriesAssetPathAddition = "";
		List<ChatFormatting> requirementsNotMetFormatting = List.of();
		List<ChatFormatting> requirementsMetFormatting = List.of();

		Identifier rogueLootGuiSettingsIdentifier = Identifier.tryParse(rogueLootTable.rogueLootGuiSettingsIdString());
		if (!rogueLootTable.rogueLootGuiSettingsIdString().isEmpty() && rogueLootGuiSettingsIdentifier != null) {
			RogueLootGuiSettings rogueLootGuiSettings = getRogueLootGuiSettings(serverLevel.registryAccess(), rogueLootGuiSettingsIdentifier);
			if (rogueLootGuiSettings != null) {
				backgroundTexturePath = rogueLootGuiSettings.backgroundTexturePath();
				if (!rogueLootGuiSettings.entriesAssetPathAddition().isEmpty()) {
					entriesAssetPathAddition = rogueLootGuiSettings.entriesAssetPathAddition() + "/";
				}
				requirementsNotMetFormatting = new ArrayList<>(rogueLootGuiSettings.requirementsNotMetFormatting());
				requirementsMetFormatting = new ArrayList<>(rogueLootGuiSettings.requirementsMetFormatting());
			}
		}

		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntries = new ArrayList<>();

		for (Identifier entryIdentifier : unlockedEntries) {
			RogueLootTableEntry rogueLootTableEntry = getRogueLootTableEntry(serverLevel.registryAccess(), entryIdentifier);
			if (rogueLootTableEntry == null) {
				continue;
			}

			Identifier rarityIdentifier = Identifier.tryParse(rogueLootTableEntry.rarityIdString());
			Identifier iconTextureIdentifier = Identifier.tryParse(rogueLootTableEntry.iconTexturePath());
			Optional<Identifier> optionalIconTextureIdentifier = Optional.empty();
			if (!rogueLootTableEntry.iconTexturePath().isEmpty() && iconTextureIdentifier != null) {
				optionalIconTextureIdentifier = Optional.of(iconTextureIdentifier);
			}
			String assetPathAddition = "";
			List<ChatFormatting> labelFormatting = List.of();
			if (!rogueLootTableEntry.rarityIdString().isEmpty() && rarityIdentifier != null) {
				RogueLootTableEntryRarity rarity = getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityIdentifier);
				if (rarity != null) {
					if (!rarity.assetPathAddition().isEmpty()) {
						assetPathAddition = rarity.assetPathAddition() + "/";
					}
					labelFormatting = new ArrayList<>(rarity.labelFormatting());
				}
			}
			visibleRogueLootTableEntries.add(new GeneratedRogueLoot.VisibleRogueLootTableEntry(
					entriesAssetPathAddition + assetPathAddition,
					rogueLootTableEntry.displayStack(),
					optionalIconTextureIdentifier,
					rogueLootTableEntry.labelText(),
					labelFormatting,
					rogueLootTableEntry.allowBanishing(),
					Optional.of(entryIdentifier)
			));
		}

		return new GeneratedRogueLoot(
				backgroundTexturePath,
				requirementsNotMetFormatting,
				requirementsMetFormatting,
				visibleRogueLootTableEntries,
				generatedRogueLootTableIdentifier.minChoices(),
				generatedRogueLootTableIdentifier.maxChoices(),
				rogueLootTable.enableRerolling(),
				rogueLootTable.rerollCost(),
				rogueLootTable.enableEntryBanishing(),
				rogueLootTable.banishingCost()
		);
	}

	@Nullable
	public static RogueLootTable getRogueLootTable(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTable rogueLootTable = null;
		Optional<Registry<RogueLootTable>> optionalRogueLootTableRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_TABLE_REGISTRY_KEY);
		if (optionalRogueLootTableRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTable>> optionalRogueLootTableReference = optionalRogueLootTableRegistry.get().get(identifier);
			if (optionalRogueLootTableReference.isPresent()) {
				rogueLootTable = optionalRogueLootTableReference.get().value();
			}
		}
		return rogueLootTable;
	}

	@Nullable
	public static RogueLootTableEntry getRogueLootTableEntry(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTableEntry rogueLootTableEntry = null;
		Optional<Registry<RogueLootTableEntry>> optionalRogueLootTableEntryRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY);
		if (optionalRogueLootTableEntryRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTableEntry>> optionalRogueLootTableEntryReference = optionalRogueLootTableEntryRegistry.get().get(identifier);
			if (optionalRogueLootTableEntryReference.isPresent()) {
				rogueLootTableEntry = optionalRogueLootTableEntryReference.get().value();
			}
		}
		return rogueLootTableEntry;
	}

	@Nullable
	public static RogueLootTableEntryRarity getRogueLootTableEntryRarity(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTableEntryRarity rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY);
		if (optionalRogueLootTableEntryRarityRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityReference = optionalRogueLootTableEntryRarityRegistry.get().get(identifier);
			if (optionalRogueLootTableEntryRarityReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootTableEntryRarityReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

	@Nullable
	public static RogueLootGuiSettings getRogueLootGuiSettings(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootGuiSettings rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootGuiSettings>> optionalRogueLootGuiSettingsRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY);
		if (optionalRogueLootGuiSettingsRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootGuiSettings>> optionalRogueLootGuiSettingsReference = optionalRogueLootGuiSettingsRegistry.get().get(identifier);
			if (optionalRogueLootGuiSettingsReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootGuiSettingsReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
