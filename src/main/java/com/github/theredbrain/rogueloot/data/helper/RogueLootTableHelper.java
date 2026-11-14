package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacket;
import com.github.theredbrain.rogueloot.registry.CustomDynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(final Identifier identifier, final ServerLevel serverLevel, final ServerPlayer serverPlayer, final Vec3 pos, final int rolls, final int minChoices, final int maxChoices, @Nullable final ItemStack itemStack) {
		RogueLootTable rogueLootTable = getRogueLootTable(serverLevel.registryAccess(), identifier);
		if (rogueLootTable == null) {
			return;
		}
		Identifier localIdentifier = getLocalRogueLootTableId(serverLevel, identifier, pos, rolls, minChoices, maxChoices);
		GeneratedRogueLoot generatedRogueLoot = DataAttachmentHelper.getGeneratedRogueLootForPlayer(serverLevel, localIdentifier, serverPlayer);
		if (generatedRogueLoot == null) {
			generatedRogueLoot = generateRogueLoot(serverLevel, rogueLootTable, serverPlayer, rolls, minChoices, maxChoices);
		}
		if (generatedRogueLoot != null) {
			DataAttachmentHelper.setGeneratedRogueLootForPlayer(serverLevel, generatedRogueLoot, localIdentifier, serverPlayer);
			int banishingAttempts = calculateBanishingAttempts(serverPlayer, generatedRogueLoot);
			ServerPlayNetworking.send(serverPlayer, new OpenRogueLootScreenPacket(generatedRogueLoot, localIdentifier, banishingAttempts));
		}

	}

	public static void removeRogueLootTableUses(final ServerLevel serverLevel, final Vec3 pos, final String identifierString, final int rolls, final int minChoices, final int maxChoices, @Nullable final ServerPlayer serverPlayer) {
		Identifier identifier = Identifier.tryParse(identifierString);
		if (identifier != null) {
			removeRogueLootTableUses(serverLevel, pos, identifier, rolls, minChoices, maxChoices, serverPlayer);
		}
	}

	public static void removeRogueLootTableUses(final ServerLevel serverLevel, final Vec3 pos, final Identifier identifier, final int rolls, final int minChoices, final int maxChoices, @Nullable final ServerPlayer serverPlayer) {
		Identifier localIdentifier = getLocalRogueLootTableId(serverLevel, identifier, pos, rolls, minChoices, maxChoices);
		DataAttachmentHelper.removeGeneratedRogueLoot(serverLevel, localIdentifier, serverPlayer);
	}

	private static int calculateBanishingAttempts(ServerPlayer serverPlayer, GeneratedRogueLoot generatedRogueLoot) {

		int banishingAttempts = -1;
		if (generatedRogueLoot.enableEntryBanishing() && generatedRogueLoot.banishingCost().isPresent()) {
			ItemStack banishingCostStack = generatedRogueLoot.banishingCost().get().create();

			if (!banishingCostStack.isEmpty()) {
				banishingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, banishingCostStack);
			}
		}
		return banishingAttempts;
	}

	@Nullable
	public static GeneratedRogueLoot generateRogueLoot(final ServerLevel serverLevel, final RogueLootTable rogueLootTable, final ServerPlayer serverPlayer, final int rolls, final int minChoices, final int maxChoices) {

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

		List<MutablePair<Integer, GeneratedRogueLoot.VisibleRogueLootTableEntry>> tempList = new ArrayList<>();
		int totalWeight = 0;
		for (Holder<RogueLootTableEntry> entry : rogueLootTable.rogueLootTableEntries()) {
			RogueLootTableEntry rogueLootTableEntry = entry.value();
			Optional<ResourceKey<RogueLootTableEntry>> optionalResourceKey = entry.unwrapKey();
			if (optionalResourceKey.isEmpty()) {
				continue;
			}
			Identifier entryIdentifier = optionalResourceKey.get().identifier();
			if (DataAttachmentHelper.isRogueLootEntryBanished(serverPlayer, entryIdentifier)) {
				continue;
			}

			Optional<ContextAwarePredicate> optionalUnlockPredicate = rogueLootTableEntry.unlockPredicate();
			if (optionalUnlockPredicate.isPresent() && !optionalUnlockPredicate.get().matches(EntityPredicate.createContext(serverPlayer, serverPlayer))) {
				continue;
			}

			Identifier rarityIdentifier = Identifier.tryParse(rogueLootTableEntry.rarityIdString());
			Identifier iconTextureIdentifier = Identifier.tryParse(rogueLootTableEntry.iconTexturePath());
			Optional<Identifier> optionalIconTextureIdentifier = Optional.empty();
			if (!rogueLootTableEntry.iconTexturePath().isEmpty() && iconTextureIdentifier != null) {
				optionalIconTextureIdentifier = Optional.of(iconTextureIdentifier);
			}
			String assetPathAddition = "";
			int weight = 1;
			List<ChatFormatting> labelFormatting = List.of();
			if (!rogueLootTableEntry.rarityIdString().isEmpty() && rarityIdentifier != null) {
				RogueLootTableEntryRarity rarity = getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityIdentifier);
				if (rarity != null) {
					if (!rarity.assetPathAddition().isEmpty()) {
						assetPathAddition = rarity.assetPathAddition() + "/";
					}
					weight = rogueLootTable.rarityOverrides().getOrDefault(rarityIdentifier, rarity.defaultWeight());
					labelFormatting = new ArrayList<>(rarity.labelFormatting());
				}
			}
			tempList.add(new MutablePair<>(weight, new GeneratedRogueLoot.VisibleRogueLootTableEntry(
					entriesAssetPathAddition + assetPathAddition,
					rogueLootTableEntry.displayStack(),
					optionalIconTextureIdentifier,
					rogueLootTableEntry.labelText(),
					labelFormatting,
					rogueLootTableEntry.allowBanishing(),
					entryIdentifier
			)));
			totalWeight = totalWeight + weight;
		}

		if (tempList.size() < rolls) {
			RogueLoot.info("generated rogue loot table has fewer unlocked entries than rolls requested");
		}

		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntries = new ArrayList<>();

		int chosenWeight;
		int index;
		int weightCounter;
		for (int i = 0; i < Math.min(tempList.size(), rolls); i++) {
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
				visibleRogueLootTableEntries.add(tempList.get(index).right);
				totalWeight -= tempList.get(index).left;
				tempList.remove(index);
			}
		}

		return new GeneratedRogueLoot(
				backgroundTexturePath,
				requirementsNotMetFormatting,
				requirementsMetFormatting,
				visibleRogueLootTableEntries,
				minChoices,
				maxChoices,
				rogueLootTable.enableEntryBanishing(),
				rogueLootTable.banishingCost()
		);
	}

	public static Identifier getLocalRogueLootTableId(final ServerLevel serverLevel, final Identifier identifier, final Vec3 pos, final int rolls, final int minChoices, final int maxChoices) {
		return RogueLoot.identifier(serverLevel.dimension().identifier().toLanguageKey() + "_" + identifier.toLanguageKey() + "_" + pos.x() + "_" + pos.y() + "_" + pos.z() + "_" + rolls + "_" + minChoices + "_" + maxChoices);
	}

	@Nullable
	public static RogueLootTable getRogueLootTable(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTable rogueLootTable = null;
		Optional<Registry<RogueLootTable>> optionalRogueLootTableRegistry = registryAccess.lookup(CustomDynamicRegistries.ROGUE_LOOT_TABLE_REGISTRY_KEY);
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
		Optional<Registry<RogueLootTableEntry>> optionalRogueLootTableEntryRegistry = registryAccess.lookup(CustomDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY);
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
		Optional<Registry<RogueLootTableEntryRarity>> optionalRogueLootTableEntryRarityRegistry = registryAccess.lookup(CustomDynamicRegistries.ROGUE_LOOT_TABLE_ENTRY_RARITY_REGISTRY_KEY);
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
		Optional<Registry<RogueLootGuiSettings>> optionalRogueLootGuiSettingsRegistry = registryAccess.lookup(CustomDynamicRegistries.ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY);
		if (optionalRogueLootGuiSettingsRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootGuiSettings>> optionalRogueLootGuiSettingsReference = optionalRogueLootGuiSettingsRegistry.get().get(identifier);
			if (optionalRogueLootGuiSettingsReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootGuiSettingsReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
