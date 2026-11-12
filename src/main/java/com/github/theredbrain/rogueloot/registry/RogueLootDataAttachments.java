package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootAttachedData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class RogueLootDataAttachments {

	public static AttachmentType<GeneratedRogueLootAttachedData> GENERATED_ROGUE_LOOT_ATTACHED_DATA;

	public static void bootstrap() {
	}

	static {
		GENERATED_ROGUE_LOOT_ATTACHED_DATA = AttachmentRegistry.create(RogueLoot.identifier("generated_rogue_loot_attached_data"), builder -> builder
						.persistent(GeneratedRogueLootAttachedData.CODEC)
//				.syncWith(GeneratedRogueLootAttachedData.STREAM_CODEC, AttachmentSyncPredicate.all())
		);
	}
}
