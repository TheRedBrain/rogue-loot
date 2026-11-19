package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.ReservedRogueLootEntriesAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public class RogueLootDataAttachments {

	private static final AttachmentType<BanishedRogueLootEntriesAttachedData> BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA;
	private static final AttachmentType<ReservedRogueLootEntriesAttachedData> RESERVED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA;

	public static void bootstrap() {
	}

	// region getter/setter
	public static BanishedRogueLootEntriesAttachedData getBanishedRogueLootEntriesAttachedData(final Player player) {
		return player.getAttachedOrCreate(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA);
	}

	public static void setBanishedRogueLootEntriesAttachedData(final Player player, final BanishedRogueLootEntriesAttachedData banishedRogueLootEntriesAttachedData) {
		player.setAttached(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, banishedRogueLootEntriesAttachedData);
	}

	public static ReservedRogueLootEntriesAttachedData getReservedRogueLootEntriesAttachedData(final Player player) {
		return player.getAttachedOrCreate(RESERVED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA);
	}

	public static void setReservedRogueLootEntriesAttachedData(final Player player, final ReservedRogueLootEntriesAttachedData reservedRogueLootEntriesAttachedData) {
		player.setAttached(RESERVED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, reservedRogueLootEntriesAttachedData);
	}
	// endregion getter/setter

	static {
		BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("banished_rogue_loot_entries_attached_data"), builder -> builder
				.initializer(() -> BanishedRogueLootEntriesAttachedData.DEFAULT)
				.persistent(BanishedRogueLootEntriesAttachedData.CODEC)
				.copyOnDeath()
				.syncWith(BanishedRogueLootEntriesAttachedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
		RESERVED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("reserved_rogue_loot_entries_attached_data"), builder -> builder
				.initializer(() -> ReservedRogueLootEntriesAttachedData.DEFAULT)
				.persistent(ReservedRogueLootEntriesAttachedData.CODEC)
				.copyOnDeath()
				.syncWith(ReservedRogueLootEntriesAttachedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
	}
}
