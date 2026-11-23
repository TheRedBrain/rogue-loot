package com.github.theredbrain.rogueloot;

import com.github.theredbrain.rogueloot.compatibility.LootablesCompat;
import com.github.theredbrain.rogueloot.compatibility.ManaAttributesCompat;
import com.github.theredbrain.rogueloot.compatibility.StaminaAttributesCompat;
import com.github.theredbrain.rogueloot.registry.RogueLootCommands;
import com.github.theredbrain.rogueloot.registry.RogueLootBlocks;
import com.github.theredbrain.rogueloot.registry.RogueLootConfigs;
import com.github.theredbrain.rogueloot.registry.RogueLootDataAttachments;
import com.github.theredbrain.rogueloot.registry.RogueLootDataComponents;
import com.github.theredbrain.rogueloot.registry.RogueLootEntities;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.github.theredbrain.rogueloot.registry.RogueLootServerEvents;
import com.github.theredbrain.rogueloot.registry.RogueLootServerPackets;
import com.github.theredbrain.rogueloot.registry.RogueLootTableEntryComponentTypes;
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
	public static final boolean isManaAttributesLoaded = FabricLoader.getInstance().isModLoaded("manaattributes");
	public static final boolean isOverhauledDamageLoaded = FabricLoader.getInstance().isModLoaded("overhauleddamage");
	public static final boolean isStaminaAttributesLoaded = FabricLoader.getInstance().isModLoaded("staminaattributes");

	private static void initCompatibilityRogueLootTableEntryComponentTypes() {
		if (isManaAttributesLoaded) {
			ManaAttributesCompat.init();
		}
		if (isStaminaAttributesLoaded) {
			StaminaAttributesCompat.init();
		}
	}

	public static void supplyLootableLoot(final Identifier identifier, final ServerLevel serverLevel, final ServerPlayer serverPlayer, final Vec3 pos, final int rolls, final int choices, final boolean withChoice, @Nullable final ItemStack itemStack) {
		if (isLootablesLoaded) {
			LootablesCompat.supplyLootableLoot(identifier, serverLevel, serverPlayer, pos, rolls, choices, withChoice, itemStack);
		} else {
			info("Tried to supply loot via Lootables, but the mod is not installed!");
		}
	}

	public static void removeLootableUses(final ServerLevel serverLevel, final Vec3 pos, final String identifierString, @Nullable final ServerPlayer serverPlayer) {
		if (isLootablesLoaded) {
			LootablesCompat.removeLootableUses(serverLevel, pos, identifierString, serverPlayer);
		}
	}

	public static void removeLootableUses(final ServerLevel serverLevel, final Vec3 pos, final Identifier identifier, @Nullable final ServerPlayer serverPlayer) {
		if (isLootablesLoaded) {
			LootablesCompat.removeLootableUses(serverLevel, pos, identifier, serverPlayer);
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Rogue Like Loot!");

		RogueLootRegistries.init();
		RogueLootTableEntryComponentTypes.init();
		initCompatibilityRogueLootTableEntryComponentTypes();
		RogueLootServerEvents.initializeEvents();
		RogueLootCommands.init();
		RogueLootServerPackets.init();

		RogueLootBlocks.bootstrap();
		RogueLootConfigs.bootstrap();
		RogueLootDataAttachments.bootstrap();
		RogueLootDataComponents.bootstrap();
		RogueLootEntities.bootstrap();
	}

	public static Identifier identifier(final String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void info(final String message) {
		LOGGER.info("[" + MOD_ID + "] [info]: {}", message);
	}

	public static void error(final String message) {
		LOGGER.error("[" + MOD_ID + "] [info]: {}", message);
	}

}