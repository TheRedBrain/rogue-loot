package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.attached.BanishedRogueLootEntriesAttachedData;
import com.github.theredbrain.rogueloot.data.attached.GeneratedRogueLootAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RogueLootDataAttachments {

	// serverLevel
	private static final AttachmentType<GeneratedRogueLootAttachedData> GENERATED_ROGUE_LOOT_ATTACHED_DATA;
	private static final AttachmentType<BanishedRogueLootEntriesAttachedData> BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA;

	public static void bootstrap() {
	}

	// region getter/setter
	public static GeneratedRogueLootAttachedData getGeneratedRogueLootAttachedData(final ServerLevel serverLevel) {
		return serverLevel.getAttachedOrElse(GENERATED_ROGUE_LOOT_ATTACHED_DATA, GeneratedRogueLootAttachedData.DEFAULT);
	}

	public static void setGeneratedRogueLootAttachedData(final ServerLevel serverLevel, final GeneratedRogueLootAttachedData generatedRogueLootAttachedData) {
		serverLevel.setAttached(GENERATED_ROGUE_LOOT_ATTACHED_DATA, generatedRogueLootAttachedData);
	}

	public static BanishedRogueLootEntriesAttachedData getBanishedRogueLootEntriesAttachedData(final Player player) {
		return player.getAttachedOrElse(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, BanishedRogueLootEntriesAttachedData.DEFAULT);
	}

	public static void setBanishedRogueLootEntriesAttachedData(final Player player, final BanishedRogueLootEntriesAttachedData banishedRogueLootEntriesAttachedData) {
		player.setAttached(BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA, banishedRogueLootEntriesAttachedData);
	}
	// endregion getter/setter

	static {
		GENERATED_ROGUE_LOOT_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("generated_rogue_loot_attached_data"), builder -> builder
						.persistent(GeneratedRogueLootAttachedData.CODEC)
//				.syncWith(GeneratedRogueLootAttachedData.STREAM_CODEC, AttachmentSyncPredicate.all())
		);
		BANISHED_ROGUE_LOOT_ENTRIES_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("banished_rogue_loot_entries_attached_data"), builder -> builder
				.persistent(BanishedRogueLootEntriesAttachedData.CODEC)
				.copyOnDeath()
				.syncWith(BanishedRogueLootEntriesAttachedData.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
		);
	}
}
