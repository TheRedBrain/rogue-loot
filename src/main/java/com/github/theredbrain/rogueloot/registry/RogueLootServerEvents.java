package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;

public class RogueLootServerEvents {
	public static void initializeEvents() {
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (entity instanceof ServerPlayer serverPlayer) {
				DataAttachmentHelper.unbanishRogueLootEntry(serverPlayer, null);
			}
		});
	}
}
