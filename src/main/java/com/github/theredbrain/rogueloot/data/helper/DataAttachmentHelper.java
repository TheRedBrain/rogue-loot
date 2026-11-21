package com.github.theredbrain.rogueloot.data.helper;

import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.GeneratedRogueLootAttachedData;
import com.github.theredbrain.rogueloot.data.attached.ReservedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.RogueLootEntryUsesAttachedData;
import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	public static List<Identifier> getGeneratedRogueLootEntries(final ServerPlayer serverPlayer, final Identifier generatedRogueLootTableIdentifier) {
		return RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverPlayer).generatedRogueLootData().getOrDefault(generatedRogueLootTableIdentifier, GeneratedRogueLootAttachedData.GeneratedRogueLootEntries.DEFAULT).generatedRogueLootEntries();
	}

	public static void setGeneratedRogueLootEntries(final ServerPlayer serverPlayer, final Identifier generatedRogueLootTableIdentifier, final List<Identifier> entryIdentifierList) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverPlayer));
		Map<Identifier, GeneratedRogueLootAttachedData.GeneratedRogueLootEntries> newGeneratedRogueLoot = new HashMap<>(newGeneratedRogueLootAttachedData.generatedRogueLootData());

		newGeneratedRogueLoot.put(generatedRogueLootTableIdentifier, new GeneratedRogueLootAttachedData.GeneratedRogueLootEntries(entryIdentifierList));

		RogueLootDataAttachments.setGeneratedRogueLootAttachedData(serverPlayer, new GeneratedRogueLootAttachedData(newGeneratedRogueLoot));
	}

	public static void removeGeneratedRogueLootEntries(final ServerPlayer serverPlayer, final Identifier generatedRogueLootTableIdentifier) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(RogueLootDataAttachments.getGeneratedRogueLootAttachedData(serverPlayer));
		Map<Identifier, GeneratedRogueLootAttachedData.GeneratedRogueLootEntries> newGeneratedRogueLoot = new HashMap<>(newGeneratedRogueLootAttachedData.generatedRogueLootData());

		newGeneratedRogueLoot.remove(generatedRogueLootTableIdentifier);

		RogueLootDataAttachments.setGeneratedRogueLootAttachedData(serverPlayer, new GeneratedRogueLootAttachedData(newGeneratedRogueLoot));
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

	public static int getTimesRogueLootEntryWasUsed(@Nullable Player player, final Optional<Identifier> entryIdentifier) {
		return player != null && entryIdentifier.isPresent() ? RogueLootDataAttachments.getRogueLootEntryUsesAttachedData(player).rogueLootEntryUses().getOrDefault(entryIdentifier.get(), 0) : 0;
	}

	public static void useRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList) {
		RogueLootEntryUsesAttachedData newRogueLootEntryUsesAttachedData = RogueLootEntryUsesAttachedData.copy(RogueLootDataAttachments.getRogueLootEntryUsesAttachedData(serverPlayer));
		Map<Identifier, Integer> newRogueLootEntryUses = new HashMap<>(newRogueLootEntryUsesAttachedData.rogueLootEntryUses());

		for (Identifier identifier : entryIdentifierList) {
			int oldUse = newRogueLootEntryUsesAttachedData.rogueLootEntryUses().getOrDefault(identifier, 0);
			newRogueLootEntryUses.put(identifier, oldUse + 1);
		}

		RogueLootDataAttachments.setRogueLootEntryUsesAttachedData(serverPlayer, new RogueLootEntryUsesAttachedData(newRogueLootEntryUses));
	}

	public static void useRogueLootEntry(final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		RogueLootEntryUsesAttachedData newRogueLootEntryUsesAttachedData = RogueLootEntryUsesAttachedData.copy(RogueLootDataAttachments.getRogueLootEntryUsesAttachedData(serverPlayer));
		Map<Identifier, Integer> newRogueLootEntryUses = new HashMap<>(newRogueLootEntryUsesAttachedData.rogueLootEntryUses());

		int oldUse = newRogueLootEntryUsesAttachedData.rogueLootEntryUses().getOrDefault(entryIdentifier, 0);
		newRogueLootEntryUses.put(entryIdentifier, oldUse + 1);

		RogueLootDataAttachments.setRogueLootEntryUsesAttachedData(serverPlayer, new RogueLootEntryUsesAttachedData(newRogueLootEntryUses));
	}

	public static void unuseRogueLootEntries(final ServerPlayer serverPlayer, final List<Identifier> entryIdentifierList, boolean removeAllUses) {
		RogueLootEntryUsesAttachedData newRogueLootEntryUsesAttachedData = RogueLootEntryUsesAttachedData.copy(RogueLootDataAttachments.getRogueLootEntryUsesAttachedData(serverPlayer));
		Map<Identifier, Integer> newRogueLootEntryUses = new HashMap<>(newRogueLootEntryUsesAttachedData.rogueLootEntryUses());

		for (Identifier identifier : entryIdentifierList) {
			int oldUse = newRogueLootEntryUsesAttachedData.rogueLootEntryUses().getOrDefault(identifier, 0);
			if (oldUse > 1 && !removeAllUses) {
				newRogueLootEntryUses.put(identifier, oldUse - 1);
			} else {
				newRogueLootEntryUses.remove(identifier);
			}
		}

		RogueLootDataAttachments.setRogueLootEntryUsesAttachedData(serverPlayer, new RogueLootEntryUsesAttachedData(newRogueLootEntryUses));
	}

	/**
	 * Removes all uses of all entries when entryIdentifier is null
	 */
	public static void unuseRogueLootEntry(final ServerPlayer serverPlayer, @Nullable final Identifier entryIdentifier, boolean removeAllUses) {
		if (entryIdentifier != null) {
			RogueLootEntryUsesAttachedData newRogueLootEntryUsesAttachedData = RogueLootEntryUsesAttachedData.copy(RogueLootDataAttachments.getRogueLootEntryUsesAttachedData(serverPlayer));
			Map<Identifier, Integer> newRogueLootEntryUses = new HashMap<>(newRogueLootEntryUsesAttachedData.rogueLootEntryUses());

			int oldUse = newRogueLootEntryUsesAttachedData.rogueLootEntryUses().getOrDefault(entryIdentifier, 0);
			if (oldUse > 1 && !removeAllUses) {
				newRogueLootEntryUses.put(entryIdentifier, oldUse - 1);
			} else {
				newRogueLootEntryUses.remove(entryIdentifier);
			}

			RogueLootDataAttachments.setRogueLootEntryUsesAttachedData(serverPlayer, new RogueLootEntryUsesAttachedData(newRogueLootEntryUses));
		} else {
			RogueLootDataAttachments.setRogueLootEntryUsesAttachedData(serverPlayer, RogueLootEntryUsesAttachedData.DEFAULT);
		}
	}

}
