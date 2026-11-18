package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.LockedRogueLootEntriesAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public class RogueLootDataAttachments {

	private static final AttachmentType<BanishedRogueLootEntriesAttachedData> BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA;
	private static final AttachmentType<LockedRogueLootEntriesAttachedData> LOCKED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA;

	public static void bootstrap() {
	}

	// region getter/setter
	public static BanishedRogueLootEntriesAttachedData getBanishedRogueLootEntriesAttachedData(final Player player) {
		return player.getAttachedOrCreate(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA);
	}

	public static void setBanishedRogueLootEntriesAttachedData(final Player player, final BanishedRogueLootEntriesAttachedData banishedRogueLootEntriesAttachedData) {
		player.setAttached(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, banishedRogueLootEntriesAttachedData);
	}

	public static LockedRogueLootEntriesAttachedData getLockedRogueLootEntriesAttachedData(final Player player) {
		return player.getAttachedOrCreate(LOCKED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA);
	}

	public static void setLockedRogueLootEntriesAttachedData(final Player player, final LockedRogueLootEntriesAttachedData lockedRogueLootEntriesAttachedData) {
		player.setAttached(LOCKED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, lockedRogueLootEntriesAttachedData);
	}
	// endregion getter/setter

	static {
		BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("banished_rogue_loot_entries_attached_data"), builder -> builder
				.initializer(() -> BanishedRogueLootEntriesAttachedData.DEFAULT)
				.persistent(BanishedRogueLootEntriesAttachedData.CODEC)
				.copyOnDeath()
				.syncWith(BanishedRogueLootEntriesAttachedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
		LOCKED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("locked_rogue_loot_entries_attached_data"), builder -> builder
				.initializer(() -> LockedRogueLootEntriesAttachedData.DEFAULT)
				.persistent(LockedRogueLootEntriesAttachedData.CODEC)
				.copyOnDeath()
				.syncWith(LockedRogueLootEntriesAttachedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
	}
}
