package com.github.theredbrain.rogueloot;

import com.github.theredbrain.rogueloot.compat.LootablesCompat;
import com.github.theredbrain.rogueloot.registry.BlockRegistry;
import com.github.theredbrain.rogueloot.registry.CustomDynamicRegistries;
import com.github.theredbrain.rogueloot.registry.EntityRegistry;
import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import com.github.theredbrain.rogueloot.registry.RogueLootMenuTypes;
import com.github.theredbrain.rogueloot.registry.ServerPacketRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RogueLoot implements ModInitializer {
	public static final String MOD_ID = "rogueloot";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final boolean isLootablesLoaded = FabricLoader.getInstance().isModLoaded("lootables");

	public static void supplyLootableLoot(Identifier identifier, ServerLevel world, ServerPlayer serverPlayerEntity, Vec3 pos, int rolls, int choices, boolean withChoice, @Nullable ItemStack itemStack) {
		if (isLootablesLoaded) {
			LootablesCompat.supplyLootableLoot(identifier, world, serverPlayerEntity, pos, rolls, choices, withChoice, itemStack);
		} else {
			info("Tried to supply loot via Lootables, but the mod is not installed!");
		}
	}

	public static void removeLootableUses(ServerLevel world, Vec3 pos, String identifierString, @Nullable ServerPlayer serverPlayerEntity) {
		if (isLootablesLoaded) {
			LootablesCompat.removeLootableUses(world, pos, identifierString, serverPlayerEntity);
		}
	}

	public static void removeLootableUses(ServerLevel world, Vec3 pos, Identifier identifier, @Nullable ServerPlayer serverPlayerEntity) {
		if (isLootablesLoaded) {
			LootablesCompat.removeLootableUses(world, pos, identifier, serverPlayerEntity);
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Lootables supplied by blocks!");

		// Packets
		ServerPacketRegistry.init();

		// Registry
		RogueLootDataAttachments.bootstrap();
		BlockRegistry.init();
		CustomDynamicRegistries.init();
		EntityRegistry.init();
		RogueLootMenuTypes.init();
	}

	public static Identifier identifier(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void info(String message) {
		LOGGER.info("[" + MOD_ID + "] [info]: " + message);
	}

}