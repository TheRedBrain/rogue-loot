package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.SuppliesRogueLootTable;
import com.github.theredbrain.rogueloot.data.CommonDataStructures;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.OpensRogueLootDisplayScreen;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.rogueloot.entity.player.PlayerHelper;
import com.github.theredbrain.rogueloot.network.packet.OpenRogueLootScreenPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(final ServerLevel serverLevel, final ServerPlayer serverPlayer, GeneratedRogueLootData rogueLootTableIdentifier) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), rogueLootTableIdentifier.rogueLootTableIdentifier());
		if (rogueLootTable == null) {
			return;
		}
		List<Identifier> generatedRogueLootEntries;
		if (rogueLootTableIdentifier.optionalBlockPos().isPresent()) {
			BlockEntity blockEntity = serverLevel.getBlockEntity(rogueLootTableIdentifier.optionalBlockPos().get());
			if (blockEntity instanceof SuppliesRogueLootTable suppliesRogueLootTable) {
				generatedRogueLootEntries = new ArrayList<>(suppliesRogueLootTable.getGeneratedRogueLootEntries(serverPlayer));
				if (generatedRogueLootEntries.isEmpty()) {
					rogueLootTableIdentifier = GeneratedRogueLootData.builder(rogueLootTableIdentifier)
							.withModifiedRolls(calculateModifiedRolls(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultRolls()))
							.withModifiedMinChoices(calculateModifiedMinChoices(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultMinChoices()))
							.withModifiedMaxChoices(calculateModifiedMaxChoices(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultMaxChoices()))
							.build();
					generatedRogueLootEntries = generateRogueLootEntries(serverLevel, serverPlayer, rogueLootTable, rogueLootTableIdentifier.modifiedRolls());
				}
				if (!generatedRogueLootEntries.isEmpty()) {
					suppliesRogueLootTable.setGeneratedRogueLootEntries(generatedRogueLootEntries, serverPlayer);
					openRogueLootScreen(serverLevel, serverPlayer, rogueLootTable, rogueLootTableIdentifier, generatedRogueLootEntries);
				}
				return;
			}
		}
		generatedRogueLootEntries = new ArrayList<>(DataAttachmentHelper.getGeneratedRogueLootEntries(serverPlayer, rogueLootTableIdentifier.savingToken()));
		if (generatedRogueLootEntries.isEmpty()) {
			rogueLootTableIdentifier = GeneratedRogueLootData.builder(rogueLootTableIdentifier)
					.withModifiedRolls(calculateModifiedRolls(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultRolls()))
					.withModifiedMinChoices(calculateModifiedMinChoices(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultMinChoices()))
					.withModifiedMaxChoices(calculateModifiedMaxChoices(serverLevel, serverPlayer, rogueLootTableIdentifier.rogueLootTableIdentifier(), rogueLootTableIdentifier.defaultMaxChoices()))
					.build();
			generatedRogueLootEntries = generateRogueLootEntries(serverLevel, serverPlayer, rogueLootTable, rogueLootTableIdentifier.modifiedRolls());
		}
		if (!generatedRogueLootEntries.isEmpty()) {
			DataAttachmentHelper.setGeneratedRogueLootEntries(serverPlayer, rogueLootTableIdentifier.savingToken(), generatedRogueLootEntries);
			openRogueLootScreen(serverLevel, serverPlayer, rogueLootTable, rogueLootTableIdentifier, generatedRogueLootEntries);
		}

	}

	private static void openRogueLootScreen(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable, final GeneratedRogueLootData generatedRogueLootData, List<Identifier> generatedRogueLootEntries) {
		ServerPlayNetworking.send(serverPlayer, new OpenRogueLootScreenPacket(
				generateRogueLoot(serverLevel, serverPlayer, rogueLootTable, generatedRogueLootEntries),
				generatedRogueLootData,
				calculateRerollAttempts(serverPlayer, rogueLootTable),
				calculateBanishingAttempts(serverPlayer, rogueLootTable),
				calculateReservingAttempts(serverPlayer, rogueLootTable)
		));
	}

	public static void removeGeneratedRogueLootEntries(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootData generatedRogueLootData) {

		if (generatedRogueLootData.optionalBlockPos().isPresent()) {
			serverLevel.getBlockEntity(generatedRogueLootData.optionalBlockPos().get());
			BlockEntity blockEntity = serverLevel.getBlockEntity(generatedRogueLootData.optionalBlockPos().get());
			if (blockEntity instanceof SuppliesRogueLootTable suppliesRogueLootTable) {
				suppliesRogueLootTable.removeGeneratedRogueLootEntries(serverPlayer);
			}
		} else {
			DataAttachmentHelper.removeGeneratedRogueLootEntry(serverPlayer, Optional.of(generatedRogueLootData.savingToken()));
		}
	}

	public static int calculateModifiedRolls(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final Identifier rogueLootTableIdentifier, final int defaultRolls) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), rogueLootTableIdentifier);
		if (rogueLootTable == null) {
			return defaultRolls;
		}

		return CommonDataStructures.AttributeInfluencedIntegerModifier.calculateAttributeInfluencedIntegerValue(serverPlayer, rogueLootTable.rollAmountModifier(), defaultRolls);
	}

	public static int calculateModifiedMinChoices(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final Identifier rogueLootTableIdentifier, final int defaultMinChoices) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), rogueLootTableIdentifier);
		if (rogueLootTable == null) {
			return defaultMinChoices;
		}

		return CommonDataStructures.AttributeInfluencedIntegerModifier.calculateAttributeInfluencedIntegerValue(serverPlayer, rogueLootTable.minChoicesModifier(), defaultMinChoices);
	}

	public static int calculateModifiedMaxChoices(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final Identifier rogueLootTableIdentifier, final int defaultMaxChoices) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), rogueLootTableIdentifier);
		if (rogueLootTable == null) {
			return defaultMaxChoices;
		}

		return CommonDataStructures.AttributeInfluencedIntegerModifier.calculateAttributeInfluencedIntegerValue(serverPlayer, rogueLootTable.maxChoicesModifier(), defaultMaxChoices);
	}

	private static int calculateRerollAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int rerollAttempts = -1;
		if (rogueLootTable.rerolling().isPresent() && rogueLootTable.rerolling().get().optionalCost().isPresent()) {
			ItemStack rerollCostStack = rogueLootTable.rerolling().get().optionalCost().get().create();

			if (!rerollCostStack.isEmpty()) {
				rerollAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, rerollCostStack);
			}
		}
		return rerollAttempts;
	}

	private static int calculateBanishingAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int banishingAttempts = -1;
		if (rogueLootTable.banishing().isPresent() && rogueLootTable.banishing().get().optionalCost().isPresent()) {
			ItemStack banishingCostStack = rogueLootTable.banishing().get().optionalCost().get().create();

			if (!banishingCostStack.isEmpty()) {
				banishingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, banishingCostStack);
			}
		}
		return banishingAttempts;
	}

	private static int calculateReservingAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int lockingAttempts = -1;
		if (rogueLootTable.reserving().isPresent() && rogueLootTable.reserving().get().optionalCost().isPresent()) {
			ItemStack lockingCostStack = rogueLootTable.reserving().get().optionalCost().get().create();

			if (!lockingCostStack.isEmpty()) {
				lockingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, lockingCostStack);
			}
		}
		return lockingAttempts;
	}

	public static List<Identifier> generateRogueLootEntries(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable, final int rolls) {

		List<Identifier> generatedRogueLootEntries = new ArrayList<>();

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

			if (rogueLootTableEntry.useLimit() > 0 && DataAttachmentHelper.getTimesRogueLootEntryWasUsed(serverPlayer, Optional.of(entryIdentifier)) >= rogueLootTableEntry.useLimit()) {
				continue;
			}

			Optional<LootItemCondition> optionalUnlockPredicate = rogueLootTableEntry.unlockPredicate();
			if (optionalUnlockPredicate.isPresent() && !optionalUnlockPredicate.get().test(EntityPredicate.createContext(serverPlayer, serverPlayer))) {
				continue;
			}

			if (DataAttachmentHelper.isRogueLootEntryReserved(serverPlayer, Optional.of(entryIdentifier))) {
				generatedRogueLootEntries.add(entryIdentifier);
				DataAttachmentHelper.unreserveRogueLootEntry(serverPlayer, Optional.of(entryIdentifier));
				continue;
			}

			if (rogueLootTableEntry.isPermanent()) {
				generatedRogueLootEntries.add(entryIdentifier);
				continue;
			}

			int weight = 1;
			if (rogueLootTableEntry.rarityIdentifier().isPresent()) {
				RogueLootTableEntryRarity rarity = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rogueLootTableEntry.rarityIdentifier().get());
				if (rarity != null) {
					Identifier rarityOverrideIdentifier = rogueLootTable.rarityOverrides().get(rogueLootTableEntry.rarityIdentifier().get());
					if (rarityOverrideIdentifier != null) {
						RogueLootTableEntryRarity rarityOverride = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityOverrideIdentifier);
						if (rarityOverride != null) {
							rarity = rarityOverride;
						}
					}
					weight = CommonDataStructures.AttributeInfluencedIntegerModifier.calculateAttributeInfluencedIntegerValue(serverPlayer, rarity.optionalWeightModifier(), rarity.weight());
				}
			}
			tempList.add(new MutablePair<>(weight, entryIdentifier));
			totalWeight = totalWeight + weight;
		}

		if (tempList.size() < rolls) {
			RogueLoot.info("generated rogue loot table has fewer unlocked entries than rolls requested");
		}

		LootContext context = new LootContext.Builder(
				new LootParams.Builder(serverLevel)
						.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
						.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
						.create(LootContextParamSets.CHEST)
		).create(rogueLootTable.randomSequence());
		int chosenWeight;
		int index;
		int weightCounter;
		int initialTempListSize = tempList.size();
		for (int i = 0; i < Math.min(initialTempListSize, rolls); i++) {
			chosenWeight = context.getRandom().nextInt(totalWeight);
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

	private static GeneratedRogueLoot generateRogueLoot(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable, final List<Identifier> unlockedEntries) {

		RogueLootGuiSettings rogueLootGuiSettings = RogueLootGuiSettings.DEFAULT;

		Identifier rogueLootGuiSettingsIdentifier = Identifier.tryParse(rogueLootTable.rogueLootGuiSettingsIdString());
		if (!rogueLootTable.rogueLootGuiSettingsIdString().isEmpty() && rogueLootGuiSettingsIdentifier != null) {
			RogueLootGuiSettings rogueLootGuiSettings1 = RogueLootGuiSettings.getRogueLootGuiSettings(serverLevel.registryAccess(), rogueLootGuiSettingsIdentifier);
			if (rogueLootGuiSettings1 != null) {
				rogueLootGuiSettings = RogueLootGuiSettings.copy(rogueLootGuiSettings1);
			}
		}

		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntries = new ArrayList<>();

		for (Identifier entryIdentifier : unlockedEntries) {
			RogueLootTableEntry rogueLootTableEntry = RogueLootTableEntry.getRogueLootTableEntry(serverLevel.registryAccess(), entryIdentifier);
			if (rogueLootTableEntry == null) {
				continue;
			}

			Optional<RogueLootTableEntryRarity> optionalRarity = Optional.empty();
			if (rogueLootTableEntry.rarityIdentifier().isPresent()) {
				RogueLootTableEntryRarity rogueLootTableEntryRarity1 = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rogueLootTableEntry.rarityIdentifier().get());
				if (rogueLootTableEntryRarity1 != null) {
					Identifier rarityOverrideIdentifier = rogueLootTable.rarityOverrides().get(rogueLootTableEntry.rarityIdentifier().get());
					if (rarityOverrideIdentifier != null) {
						RogueLootTableEntryRarity rarityOverride = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityOverrideIdentifier);
						if (rarityOverride != null) {
							rogueLootTableEntryRarity1 = rarityOverride;
						}
					}
					optionalRarity = Optional.of(rogueLootTableEntryRarity1);
				}
			}

			int usesLeft = 0;
			if (rogueLootTableEntry.useLimit() > 0) {
				usesLeft = rogueLootTableEntry.useLimit() - DataAttachmentHelper.getTimesRogueLootEntryWasUsed(serverPlayer, Optional.of(entryIdentifier));
			}

			visibleRogueLootTableEntries.add(new GeneratedRogueLoot.VisibleRogueLootTableEntry(
					rogueLootTableEntry.display(),
					rogueLootTableEntry.tooltip(),
					usesLeft,
					rogueLootTableEntry.allowBanishing(),
					rogueLootTableEntry.allowReserving(),
					optionalRarity,
					Optional.of(entryIdentifier)
			));
		}

		return new GeneratedRogueLoot(
				rogueLootGuiSettings,
				visibleRogueLootTableEntries,
				rogueLootTable.rerolling(),
				rogueLootTable.banishing(),
				rogueLootTable.reserving()
		);
	}

	public static void applyRogueLootTableEntries(ServerLevel serverLevel, ServerPlayer serverPlayer, List<Identifier> identifierList) {
		List<Identifier> remainingEntryIdentifiers = new ArrayList<>(identifierList);

		if (!identifierList.isEmpty()) {
			Identifier currentEntryIdentifier = identifierList.getFirst();
			remainingEntryIdentifiers.removeFirst();
			RogueLootTableEntry rogueLootTableEntry = RogueLootTableEntry.getRogueLootTableEntry(serverLevel.registryAccess(), currentEntryIdentifier);

			if (rogueLootTableEntry != null) {
				if (rogueLootTableEntry.useLimit() > 0) {
					DataAttachmentHelper.useRogueLootEntry(serverPlayer, currentEntryIdentifier);
				}
				RogueLootTableEntryComponentType rogueLootTableEntryComponentType = rogueLootTableEntry.rogueLootTableEntryComponentType();
				if (rogueLootTableEntryComponentType instanceof OpensRogueLootDisplayScreen opensRogueLootDisplayScreen) {
					opensRogueLootDisplayScreen.openRogueLootDisplayScreen(serverLevel, serverPlayer, remainingEntryIdentifiers);
				} else {
					rogueLootTableEntryComponentType.apply(serverLevel, serverPlayer);
					if (!remainingEntryIdentifiers.isEmpty()) {
						applyRogueLootTableEntries(serverLevel, serverPlayer, remainingEntryIdentifiers);
					}
				}
			}
		}
	}
}
