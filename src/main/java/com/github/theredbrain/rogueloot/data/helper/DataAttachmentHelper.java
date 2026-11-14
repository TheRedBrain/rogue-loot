package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.GeneratedRogueLootAttachedData;
import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataAttachmentHelper {

	@Nullable
	public static GeneratedRogueLoot getGeneratedRogueLootForPlayer(final ServerLevel serverLevel, final Identifier localIdentifier, final ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData generatedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverLevel));
		if (generatedRogueLootAttachedData.generatedRogueLootData().containsKey(localIdentifier)) {
			Map<UUID, GeneratedRogueLoot> map = generatedRogueLootAttachedData.generatedRogueLootData().get(localIdentifier);
			if (map.containsKey(serverPlayer.getUUID())) {
				return map.get(serverPlayer.getUUID());
			}
		}
		return null;
	}

	public static void setGeneratedRogueLootForPlayer(final ServerLevel serverLevel, final GeneratedRogueLoot generatedRogueLoot, final Identifier localIdentifier, final ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverLevel));
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> newGeneratedRogueLootData = newGeneratedRogueLootAttachedData.generatedRogueLootData();

		if (newGeneratedRogueLootData.containsKey(localIdentifier)) {
			Map<UUID, GeneratedRogueLoot> map = newGeneratedRogueLootData.get(localIdentifier);
			map.put(serverPlayer.getUUID(), generatedRogueLoot);
		} else {
			Map<UUID, GeneratedRogueLoot> map = new HashMap<>();
			map.put(serverPlayer.getUUID(), generatedRogueLoot);
			newGeneratedRogueLootData.put(localIdentifier, map);
		}

		RogueLootDataAttachments.setGeneratedRogueLootAttachedData(serverLevel, new GeneratedRogueLootAttachedData(newGeneratedRogueLootData));
	}

	public static void removeGeneratedRogueLoot(final ServerLevel serverLevel, final Identifier localIdentifier, @Nullable final ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverLevel));
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> newGeneratedRogueLootData = newGeneratedRogueLootAttachedData.generatedRogueLootData();

		if (serverPlayer != null) {
			Map<UUID, GeneratedRogueLoot> map = newGeneratedRogueLootData.get(localIdentifier);
			map.remove(serverPlayer.getUUID());
		} else {
			newGeneratedRogueLootData.remove(localIdentifier);
		}

		RogueLootDataAttachments.setGeneratedRogueLootAttachedData(serverLevel, new GeneratedRogueLootAttachedData(newGeneratedRogueLootData));
	}

	public static boolean isRogueLootEntryBanished(Player player, final Identifier entryIdentifier) {
		return RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(player).banishedRogueLootEntries().contains(entryIdentifier);
	}

	public static void banishRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		BanishedRogueLootEntriesAttachedData newBanishedRogueLootEntriesAttachedData = BanishedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newBanishedRogueLootEntrySet = new HashSet<>(newBanishedRogueLootEntriesAttachedData.banishedRogueLootEntries());

		newBanishedRogueLootEntrySet.addAll(entryIdentifierList);

		RogueLootDataAttachments.setBanishedRogueLootEntriesAttachedData(serverPlayer, new BanishedRogueLootEntriesAttachedData(new ArrayList<>(newBanishedRogueLootEntrySet)));
	}

	public static void banishRogueLootEntry(final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		BanishedRogueLootEntriesAttachedData newBanishedRogueLootEntriesAttachedData = BanishedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newBanishedRogueLootEntrySet = new HashSet<>(newBanishedRogueLootEntriesAttachedData.banishedRogueLootEntries());

		newBanishedRogueLootEntrySet.add(entryIdentifier);

		RogueLootDataAttachments.setBanishedRogueLootEntriesAttachedData(serverPlayer, new BanishedRogueLootEntriesAttachedData(new ArrayList<>(newBanishedRogueLootEntrySet)));
	}

	public static void unbanishRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		BanishedRogueLootEntriesAttachedData newBanishedRogueLootEntriesAttachedData = BanishedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newBanishedRogueLootEntrySet = new HashSet<>(newBanishedRogueLootEntriesAttachedData.banishedRogueLootEntries());

		for (Identifier identifier : entryIdentifierList) {
			newBanishedRogueLootEntrySet.remove(identifier);
		}

		RogueLootDataAttachments.setBanishedRogueLootEntriesAttachedData(serverPlayer, new BanishedRogueLootEntriesAttachedData(new ArrayList<>(newBanishedRogueLootEntrySet)));
	}

	/**
	 * Unbanishes all entries when entryIdentifier is null
	 */
	public static void unbanishRogueLootEntry(final ServerPlayer serverPlayer, @Nullable final Identifier entryIdentifier) {
		if (entryIdentifier != null) {
			BanishedRogueLootEntriesAttachedData newBanishedRogueLootEntriesAttachedData = BanishedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(serverPlayer));
			Set<Identifier> newBanishedRogueLootEntrySet = new HashSet<>(newBanishedRogueLootEntriesAttachedData.banishedRogueLootEntries());

			newBanishedRogueLootEntrySet.remove(entryIdentifier);

			RogueLootDataAttachments.setBanishedRogueLootEntriesAttachedData(serverPlayer, new BanishedRogueLootEntriesAttachedData(new ArrayList<>(newBanishedRogueLootEntrySet)));
		} else {
			RogueLootDataAttachments.setBanishedRogueLootEntriesAttachedData(serverPlayer, BanishedRogueLootEntriesAttachedData.DEFAULT);
		}
	}

}
