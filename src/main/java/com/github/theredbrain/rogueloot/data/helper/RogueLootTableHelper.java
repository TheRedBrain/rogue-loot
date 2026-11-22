package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.SuppliesRogueLootTable;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, final Optional<ItemStack> itemStack, final Optional<InteractionHand> interactionHand) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), generatedRogueLootTableIdentifier.rogueLootTableIdentifier());
		if (rogueLootTable == null) {
			return;
		}
		Optional<BlockPos> optionalBlockPos = generatedRogueLootTableIdentifier.optionalBlockPos();
		List<Identifier> generatedRogueLootEntries;
		if (optionalBlockPos.isPresent()) {
			BlockEntity blockEntity = serverLevel.getBlockEntity(optionalBlockPos.get());
			if (blockEntity instanceof SuppliesRogueLootTable suppliesRogueLootTable) {
				generatedRogueLootEntries = new ArrayList<>(suppliesRogueLootTable.getGeneratedRogueLootEntries(serverPlayer));
				if (generatedRogueLootEntries.isEmpty()) {
					generatedRogueLootEntries = generateRogueLootEntries(serverLevel, rogueLootTable, serverPlayer, generatedRogueLootTableIdentifier.rolls());
				}
				if (!generatedRogueLootEntries.isEmpty()) {
					suppliesRogueLootTable.setGeneratedRogueLootEntries(generatedRogueLootEntries, serverPlayer);
					openRogueLootScreen(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, generatedRogueLootEntries, itemStack, interactionHand);
				}
				return;
			}
		}
		generatedRogueLootEntries = new ArrayList<>(DataAttachmentHelper.getGeneratedRogueLootEntries(serverPlayer, generatedRogueLootTableIdentifier.toIdentifier()));
		if (generatedRogueLootEntries.isEmpty()) {
			generatedRogueLootEntries = generateRogueLootEntries(serverLevel, rogueLootTable, serverPlayer, generatedRogueLootTableIdentifier.rolls());
		}
		if (!generatedRogueLootEntries.isEmpty()) {
			DataAttachmentHelper.setGeneratedRogueLootEntries(serverPlayer, generatedRogueLootTableIdentifier.toIdentifier(), generatedRogueLootEntries);
			openRogueLootScreen(serverLevel, serverPlayer, generatedRogueLootTableIdentifier, generatedRogueLootEntries, itemStack, interactionHand);
		}

	}

	private static void openRogueLootScreen(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, List<Identifier> generatedRogueLootEntries, final Optional<ItemStack> itemStack, final Optional<InteractionHand> interactionHand) {
		RogueLootTable rogueLootTable = RogueLootTable.getRogueLootTable(serverLevel.registryAccess(), generatedRogueLootTableIdentifier.rogueLootTableIdentifier());
		if (rogueLootTable == null) {
			return;
		}
		ServerPlayNetworking.send(serverPlayer, new OpenRogueLootScreenPacket(
				generateRogueLoot(serverLevel, serverPlayer, rogueLootTable, generatedRogueLootEntries, generatedRogueLootTableIdentifier),
				generatedRogueLootTableIdentifier,
				calculateRerollAttempts(serverPlayer, rogueLootTable),
				calculateBanishingAttempts(serverPlayer, rogueLootTable),
				calculateReservingAttempts(serverPlayer, rogueLootTable),
				itemStack,
				interactionHand
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
			DataAttachmentHelper.removeGeneratedRogueLootEntries(serverPlayer, generatedRogueLootTableIdentifier.toIdentifier());
		}
	}

	private static int calculateRerollAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int rerollAttempts = -1;
		if (rogueLootTable.enableRerolling() && rogueLootTable.rerollCost().isPresent()) {
			ItemStack rerollCostStack = rogueLootTable.rerollCost().get().create();

			if (!rerollCostStack.isEmpty()) {
				rerollAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, rerollCostStack);
			}
		}
		return rerollAttempts;
	}

	private static int calculateBanishingAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int banishingAttempts = -1;
		if (rogueLootTable.enableEntryBanishing() && rogueLootTable.banishingCost().isPresent()) {
			ItemStack banishingCostStack = rogueLootTable.banishingCost().get().create();

			if (!banishingCostStack.isEmpty()) {
				banishingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, banishingCostStack);
			}
		}
		return banishingAttempts;
	}

	private static int calculateReservingAttempts(final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable) {

		int lockingAttempts = -1;
		if (rogueLootTable.enableEntryReserving() && rogueLootTable.reservingCost().isPresent()) {
			ItemStack lockingCostStack = rogueLootTable.reservingCost().get().create();

			if (!lockingCostStack.isEmpty()) {
				lockingAttempts = PlayerHelper.countAffordableTransactions(serverPlayer, lockingCostStack);
			}
		}
		return lockingAttempts;
	}

	public static List<Identifier> generateRogueLootEntries(final ServerLevel serverLevel, final RogueLootTable rogueLootTable, final ServerPlayer serverPlayer, final int rolls) {

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

			Optional<ContextAwarePredicate> optionalUnlockPredicate = rogueLootTableEntry.unlockPredicate();
			if (optionalUnlockPredicate.isPresent() && !optionalUnlockPredicate.get().matches(EntityPredicate.createContext(serverPlayer, serverPlayer))) {
				continue;
			}

			if (DataAttachmentHelper.isRogueLootEntryReserved(serverPlayer, Optional.of(entryIdentifier))) {
				generatedRogueLootEntries.add(entryIdentifier);
				DataAttachmentHelper.unreserveRogueLootEntry(serverPlayer, entryIdentifier);
				continue;
			}

			if (rogueLootTableEntry.isPermanent()) {
				generatedRogueLootEntries.add(entryIdentifier);
				continue;
			}

			Identifier rarityIdentifier = Identifier.tryParse(rogueLootTableEntry.rarityIdString());
			int weight = 1;
			if (!rogueLootTableEntry.rarityIdString().isEmpty() && rarityIdentifier != null) {
				RogueLootTableEntryRarity rarity = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityIdentifier);
				if (rarity != null) {
					Identifier rarityOverrideIdentifier = rogueLootTable.rarityOverrides().get(rarityIdentifier);
					if (rarityOverrideIdentifier != null) {
						RogueLootTableEntryRarity rarityOverride = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityOverrideIdentifier);
						if (rarityOverride != null) {
							rarity = rarityOverride;
						}
					}
					weight = rarity.defaultWeight();
					if (rarity.weightInfluencingAttribute().isPresent()) {
						Optional<Holder.Reference<Attribute>> optionalAttributeReference = BuiltInRegistries.ATTRIBUTE.get(ResourceKey.create(Registries.ATTRIBUTE, rarity.weightInfluencingAttribute().get()));
						if (optionalAttributeReference.isPresent()) {
							double influencingAttributeValue = serverPlayer.getAttributeValue(optionalAttributeReference.get());
							weight = (int) Math.clamp(Math.floor(rarity.defaultWeight() + (influencingAttributeValue * rarity.influenceMultiplier())), rarity.minWeight(), rarity.maxWeight());
						}
					}
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

	public static GeneratedRogueLoot generateRogueLoot(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final RogueLootTable rogueLootTable, final List<Identifier> unlockedEntries, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier) {

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

			Optional<Identifier> optionalIconTextureIdentifier = Optional.empty();
			Identifier iconTextureIdentifier = Identifier.tryParse(rogueLootTableEntry.iconTexturePath());
			if (!rogueLootTableEntry.iconTexturePath().isEmpty() && iconTextureIdentifier != null) {
				optionalIconTextureIdentifier = Optional.of(iconTextureIdentifier);
			}

			RogueLootTableEntryRarity rogueLootTableEntryRarity = RogueLootTableEntryRarity.DEFAULT;
			Identifier rarityIdentifier = Identifier.tryParse(rogueLootTableEntry.rarityIdString());
			if (!rogueLootTableEntry.rarityIdString().isEmpty() && rarityIdentifier != null) {
				RogueLootTableEntryRarity rogueLootTableEntryRarity1 = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityIdentifier);
				if (rogueLootTableEntryRarity1 != null) {
					Identifier rarityOverrideIdentifier = rogueLootTable.rarityOverrides().get(rarityIdentifier);
					if (rarityOverrideIdentifier != null) {
						RogueLootTableEntryRarity rarityOverride = RogueLootTableEntryRarity.getRogueLootTableEntryRarity(serverLevel.registryAccess(), rarityOverrideIdentifier);
						if (rarityOverride != null) {
							rogueLootTableEntryRarity1 = rarityOverride;
						}
					}
					rogueLootTableEntryRarity = rogueLootTableEntryRarity1;
				}
			}

			int usesLeft = 0;
			if (rogueLootTableEntry.useLimit() > 0) {
				usesLeft = rogueLootTableEntry.useLimit() - DataAttachmentHelper.getTimesRogueLootEntryWasUsed(serverPlayer, Optional.of(entryIdentifier));
			}

			visibleRogueLootTableEntries.add(new GeneratedRogueLoot.VisibleRogueLootTableEntry(
					rogueLootTableEntry.displayStack(),
					optionalIconTextureIdentifier,
					rogueLootTableEntry.labelText(),
					rogueLootTableEntry.tooltipStack(),
					rogueLootTableEntry.tooltipLabelLines(),
					usesLeft,
					rogueLootTableEntry.allowBanishing(),
					rogueLootTableEntry.allowReserving(),
					rogueLootTableEntryRarity,
					Optional.of(entryIdentifier)
			));
		}

		return new GeneratedRogueLoot(
				rogueLootGuiSettings,
				visibleRogueLootTableEntries,
				generatedRogueLootTableIdentifier.minChoices(),
				generatedRogueLootTableIdentifier.maxChoices(),
				rogueLootTable.enableRerolling(),
				rogueLootTable.rerollCost(),
				rogueLootTable.enableEntryBanishing(),
				rogueLootTable.banishingCost(),
				rogueLootTable.enableEntryReserving(),
				rogueLootTable.reservingCost()
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
