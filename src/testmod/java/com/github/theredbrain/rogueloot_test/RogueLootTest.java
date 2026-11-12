package com.github.theredbrain.rogueloot_test;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RogueLootTest implements ModInitializer {
	public static final String MOD_ID = "rogueloot_test";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Testing Rogue Loot!");
	}
}
