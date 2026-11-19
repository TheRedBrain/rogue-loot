package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.ReservedRogueLootEntriesAttachedData;
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

	public static boolean isRogueLootEntryReserved(@Nullable Player player, final Optional<Identifier> entryIdentifier) {
		return player != null && entryIdentifier.isPresent() && RogueLootDataAttachments.getReservedRogueLootEntriesAttachedData(player).reservedRogueLootEntries().contains(entryIdentifier.get());
	}

	public static void reserveRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		ReservedRogueLootEntriesAttachedData newReservedRogueLootEntriesAttachedData = ReservedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getReservedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newReservedRogueLootEntriesAttachedData.reservedRogueLootEntries());

		newLockedRogueLootEntrySet.addAll(entryIdentifierList);

		RogueLootDataAttachments.setReservedRogueLootEntriesAttachedData(serverPlayer, new ReservedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	public static void reserveRogueLootEntry(final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		ReservedRogueLootEntriesAttachedData newReservedRogueLootEntriesAttachedData = ReservedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getReservedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newReservedRogueLootEntriesAttachedData.reservedRogueLootEntries());

		newLockedRogueLootEntrySet.add(entryIdentifier);

		RogueLootDataAttachments.setReservedRogueLootEntriesAttachedData(serverPlayer, new ReservedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	public static void unreserveRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		ReservedRogueLootEntriesAttachedData newReservedRogueLootEntriesAttachedData = ReservedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getReservedRogueLootEntriesAttachedData(serverPlayer));
		Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newReservedRogueLootEntriesAttachedData.reservedRogueLootEntries());

		for (Identifier identifier : entryIdentifierList) {
			newLockedRogueLootEntrySet.remove(identifier);
		}

		RogueLootDataAttachments.setReservedRogueLootEntriesAttachedData(serverPlayer, new ReservedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
	}

	/**
	 * Unreserves all entries when entryIdentifier is null
	 */
	public static void unreserveRogueLootEntry(final ServerPlayer serverPlayer, @Nullable final Identifier entryIdentifier) {
		if (entryIdentifier != null) {
			ReservedRogueLootEntriesAttachedData newReservedRogueLootEntriesAttachedData = ReservedRogueLootEntriesAttachedData.copy(RogueLootDataAttachments.getReservedRogueLootEntriesAttachedData(serverPlayer));
			Set<Identifier> newLockedRogueLootEntrySet = new HashSet<>(newReservedRogueLootEntriesAttachedData.reservedRogueLootEntries());

			newLockedRogueLootEntrySet.remove(entryIdentifier);

			RogueLootDataAttachments.setReservedRogueLootEntriesAttachedData(serverPlayer, new ReservedRogueLootEntriesAttachedData(new ArrayList<>(newLockedRogueLootEntrySet)));
		} else {
			RogueLootDataAttachments.setReservedRogueLootEntriesAttachedData(serverPlayer, ReservedRogueLootEntriesAttachedData.DEFAULT);
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
