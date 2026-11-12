package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataAttachmentHelper {

	// region getter/setter
	public static GeneratedRogueLootAttachedData getGeneratedRogueLootAttachedData(ServerLevel serverLevel) {
		return serverLevel.getAttachedOrElse(RogueLootDataAttachments.GENERATED_ROGUE_LOOT_ATTACHED_DATA, GeneratedRogueLootAttachedData.DEFAULT);
	}

	public static void setGeneratedRogueLootAttachedData(ServerLevel serverLevel, GeneratedRogueLootAttachedData generatedRogueLootAttachedData) {
		serverLevel.setAttached(RogueLootDataAttachments.GENERATED_ROGUE_LOOT_ATTACHED_DATA, generatedRogueLootAttachedData);
	}
	// endregion getter/setter

	// region helper
	@Nullable
	public static GeneratedRogueLoot getGeneratedRogueLootForPlayer(ServerLevel serverLevel, Identifier localIdentifier, ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData generatedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(getGeneratedRogueLootAttachedData(serverLevel));
		if (generatedRogueLootAttachedData.generatedRogueLootData().containsKey(localIdentifier)) {
			Map<UUID, GeneratedRogueLoot> map = generatedRogueLootAttachedData.generatedRogueLootData().get(localIdentifier);
			if (map.containsKey(serverPlayer.getUUID())) {
				return map.get(serverPlayer.getUUID());
			}
		}
		return null;
	}

	public static void setGeneratedRogueLootForPlayer(ServerLevel serverLevel, GeneratedRogueLoot generatedRogueLoot, Identifier localIdentifier, ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(getGeneratedRogueLootAttachedData(serverLevel));
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> newGeneratedRogueLootData = newGeneratedRogueLootAttachedData.generatedRogueLootData();

		if (newGeneratedRogueLootData.containsKey(localIdentifier)) {
			Map<UUID, GeneratedRogueLoot> map = newGeneratedRogueLootData.get(localIdentifier);
			map.put(serverPlayer.getUUID(), generatedRogueLoot);
		} else {
			Map<UUID, GeneratedRogueLoot> map = new HashMap<>();
			map.put(serverPlayer.getUUID(), generatedRogueLoot);
			newGeneratedRogueLootData.put(localIdentifier, map);
		}

		setGeneratedRogueLootAttachedData(serverLevel, new GeneratedRogueLootAttachedData(newGeneratedRogueLootData));
	}

	public static void removeGeneratedRogueLoot(ServerLevel serverLevel, Identifier localIdentifier, @Nullable ServerPlayer serverPlayer) {
		GeneratedRogueLootAttachedData newGeneratedRogueLootAttachedData = GeneratedRogueLootAttachedData.copy(getGeneratedRogueLootAttachedData(serverLevel));
		Map<Identifier, Map<UUID, GeneratedRogueLoot>> newGeneratedRogueLootData = newGeneratedRogueLootAttachedData.generatedRogueLootData();

		if (serverPlayer != null) {
			Map<UUID, GeneratedRogueLoot> map = newGeneratedRogueLootData.get(localIdentifier);
			map.remove(serverPlayer.getUUID());
		} else {
			newGeneratedRogueLootData.remove(localIdentifier);
		}

		setGeneratedRogueLootAttachedData(serverLevel, new GeneratedRogueLootAttachedData(newGeneratedRogueLootData));
	}
	// endregion helper

}
