package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.registry.CustomDynamicRegistries;
import com.github.theredbrain.rogueloot.world.inventory.RogueLootGuiMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RogueLootTableHelper {

	public static void supplyRogueLootTable(Identifier identifier, ServerLevel serverLevel, ServerPlayer serverPlayerEntity, Vec3 pos, int rolls, int minChoices, int maxChoices, @Nullable ItemStack itemStack) {
		RogueLootTable rogueLootTable = getRogueLootTable(serverLevel.registryAccess(), identifier);
		if (rogueLootTable == null) {
			return;
		}
		Identifier localIdentifier = getLocalRogueLootTableId(serverLevel, identifier, pos, rolls, minChoices, maxChoices);
		GeneratedRogueLoot generatedRogueLoot = DataAttachmentHelper.getGeneratedRogueLootForPlayer(serverLevel, localIdentifier, serverPlayerEntity);
		if (generatedRogueLoot == null) {
			generatedRogueLoot = generateRogueLoot(serverLevel, rogueLootTable, serverPlayerEntity, rolls, minChoices, maxChoices);
		}
		if (generatedRogueLoot != null) {
			DataAttachmentHelper.setGeneratedRogueLootForPlayer(serverLevel, generatedRogueLoot, localIdentifier, serverPlayerEntity);
			serverPlayerEntity.openMenu(createRogueLootMenuProvider(serverLevel, generatedRogueLoot, localIdentifier));
		}

	}

	static MenuProvider createRogueLootMenuProvider(
			Level level,
			GeneratedRogueLoot generatedRogueLoot,
			Identifier localRogueLootTableIdentifier
	) {

		return new ExtendedMenuProvider<>() {
			@Override
			public RogueLootGuiMenu.RogueLootGuiData getScreenOpeningData(ServerPlayer player) {
				return new RogueLootGuiMenu.RogueLootGuiData(
						generatedRogueLoot,
						localRogueLootTableIdentifier
				);
			}

			@Override
			public Component getDisplayName() {
				return Component.empty();
			}

			@Override
			public RogueLootGuiMenu createMenu(int syncId, Inventory playerInventory, Player player) {
				return new RogueLootGuiMenu(
						syncId,
						playerInventory,
						generatedRogueLoot,
						localRogueLootTableIdentifier
				);
			}
		};
	}

	public static void removeRogueLootTableUses(ServerLevel serverLevel, Vec3 pos, String identifierString, int rolls, int minChoices, int maxChoices, @Nullable ServerPlayer serverPlayerEntity) {
		Identifier identifier = Identifier.tryParse(identifierString);
		if (identifier != null) {
			removeRogueLootTableUses(serverLevel, pos, identifier, rolls, minChoices, maxChoices, serverPlayerEntity);
		}
	}

	public static void removeRogueLootTableUses(ServerLevel serverLevel, Vec3 pos, Identifier identifier, int rolls, int minChoices, int maxChoices, @Nullable ServerPlayer serverPlayerEntity) {
		Identifier localIdentifier = getLocalRogueLootTableId(serverLevel, identifier, pos, rolls, minChoices, maxChoices);
		DataAttachmentHelper.removeGeneratedRogueLoot(serverLevel, localIdentifier, serverPlayerEntity);
	}

	@Nullable
	public static GeneratedRogueLoot generateRogueLoot(ServerLevel serverLevel, RogueLootTable rogueLootTable, ServerPlayer serverPlayerEntity, int rolls, int minChoices, int maxChoices) {

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

			Optional<ContextAwarePredicate> optionalUnlockPredicate = rogueLootTableEntry.unlockPredicate();
			if (optionalUnlockPredicate.isPresent() && !optionalUnlockPredicate.get().matches(EntityPredicate.createContext(serverPlayerEntity, serverPlayerEntity))) {
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
					entry.getRegisteredName()
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
		for (int i = 0; i < rolls; i++) {
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
				maxChoices
		);
	}

	public static Identifier getLocalRogueLootTableId(ServerLevel serverLevel, Identifier identifier, Vec3 pos, int rolls, int minChoices, int maxChoices) {
		return RogueLoot.identifier(serverLevel.dimension().identifier().toLanguageKey() + "_" + identifier.toLanguageKey() + "_" + pos.x() + "_" + pos.y() + "_" + pos.z() + "_" + rolls + "_" + minChoices + "_" + maxChoices);
	}

	@Nullable
	public static RogueLootTable getRogueLootTable(RegistryAccess registryAccess, Identifier identifier) {
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
	public static RogueLootTableEntry getRogueLootTableEntry(RegistryAccess registryAccess, Identifier identifier) {
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
	public static RogueLootTableEntryRarity getRogueLootTableEntryRarity(RegistryAccess registryAccess, Identifier identifier) {
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
	public static RogueLootGuiSettings getRogueLootGuiSettings(RegistryAccess registryAccess, Identifier identifier) {
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
