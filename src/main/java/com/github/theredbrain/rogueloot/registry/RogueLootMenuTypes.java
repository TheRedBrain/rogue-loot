package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.world.inventory.RogueLootGuiMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class RogueLootMenuTypes {
	public static final MenuType<RogueLootGuiMenu> ROGUE_LOOT_GUI_MENU_TYPE = new ExtendedMenuType<>(RogueLootGuiMenu::new, RogueLootGuiMenu.RogueLootGuiData.STREAM_CODEC);

	public static void init() {
		Registry.register(BuiltInRegistries.MENU, RogueLoot.identifier("rogue_loot_gui"), ROGUE_LOOT_GUI_MENU_TYPE);
	}
}
