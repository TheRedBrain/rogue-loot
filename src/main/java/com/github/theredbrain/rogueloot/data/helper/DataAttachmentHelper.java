package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.LockedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DataAttachmentHelper {

	public static boolean isRogueLootEntryLocked(@Nullable Player player, final Optional<Identifier> entryIdentifier) {
		return player != null && entryIdentifier.isPresent() && RogueLootDataAttachments.getLockedRogueLootEntriesAttachedData(player).lockedRogueLootEntries().contains(entryIdentifier.get());
	}

	public static void lockRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		LockedRogueLootEntriesAttachedData newLockedRogueLootEntriesAttachedData = LockedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getLockedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newLockedRogueLootEntriesAttachedData.lockedRogueLootEntries());

		newLockedRogueLootEntrySet.addAll(entryIdentifierList);

		RogueLootDataAttachments.setLockedRogueLootEntriesAttachedData(serverPlayer, new LockedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	public static void lockRogueLootEntry(final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		LockedRogueLootEntriesAttachedData newLockedRogueLootEntriesAttachedData = LockedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getLockedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newLockedRogueLootEntriesAttachedData.lockedRogueLootEntries());

		newLockedRogueLootEntrySet.add(entryIdentifier);

		RogueLootDataAttachments.setLockedRogueLootEntriesAttachedData(serverPlayer, new LockedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	public static void unlockRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		LockedRogueLootEntriesAttachedData newLockedRogueLootEntriesAttachedData = LockedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getLockedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newLockedRogueLootEntriesAttachedData.lockedRogueLootEntries());

		for (Identifier identifier : entryIdentifierList) {
			newLockedRogueLootEntrySet.remove(identifier);
		}

		RogueLootDataAttachments.setLockedRogueLootEntriesAttachedData(serverPlayer, new LockedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	/**
	 * Unlocks all entries when entryIdentifier is null
	 */
	public static void unlockRogueLootEntry(final ServerPlayer serverPlayer, @Nullable final Identifier entryIdentifier) {
		if (entryIdentifier != null) {
			LockedRogueLootEntriesAttachedData newLockedRogueLootEntriesAttachedData = LockedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getLockedRogueLootEntriesAttachedData(serverPlayer));
			Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newLockedRogueLootEntriesAttachedData.lockedRogueLootEntries());

			newLockedRogueLootEntrySet.remove(entryIdentifier);

			RogueLootDataAttachments.setLockedRogueLootEntriesAttachedData(serverPlayer, new LockedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
		} else {
			RogueLootDataAttachments.setLockedRogueLootEntriesAttachedData(serverPlayer, LockedRogueLootEntriesAttachedData.DEFAULT);
		}
	}

	public static boolean isRogueLootEntryBanished(@Nullable Player player, final Optional<Identifier> entryIdentifier) {
		return player != null && entryIdentifier.isPresent() && RogueLootDataAttachments.getBanishedRogueLootEntriesAttachedData(player).banishedRogueLootEntries().contains(entryIdentifier.get());
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
