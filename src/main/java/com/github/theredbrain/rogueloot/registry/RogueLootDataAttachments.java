package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.attached.GeneratedRogueLootAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerLevel;

public class RogueLootDataAttachments {

	// serverLevel
	private static final AttachmentType<GeneratedRogueLootAttachedData> GENERATED_ROGUE_LOOT_ATTACHED_DATA;

	public static void bootstrap() {
	}

	// region getter/setter
	public static GeneratedRogueLootAttachedData getGeneratedRogueLootAttachedData(ServerLevel serverLevel) {
		return serverLevel.getAttachedOrElse(GENERATED_ROGUE_LOOT_ATTACHED_DATA, GeneratedRogueLootAttachedData.DEFAULT);
	}

	public static void setGeneratedRogueLootAttachedData(ServerLevel serverLevel, GeneratedRogueLootAttachedData generatedRogueLootAttachedData) {
		serverLevel.setAttached(GENERATED_ROGUE_LOOT_ATTACHED_DATA, generatedRogueLootAttachedData);
	}
	// endregion getter/setter

	static {
		GENERATED_ROGUE_LOOT_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("generated_rogue_loot_attached_data"), builder -> builder
						.persistent(GeneratedRogueLootAttachedData.CODEC)
//				.syncWith(GeneratedRogueLootAttachedData.STREAM_CODEC, AttachmentSyncPredicate.all())
		);
	}
}
