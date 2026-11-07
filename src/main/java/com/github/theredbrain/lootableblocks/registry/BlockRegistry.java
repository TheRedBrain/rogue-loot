package com.github.theredbrain.lootableblocks.registry;

import com.github.theredbrain.lootableblocks.LootableBlocks;
import com.github.theredbrain.lootableblocks.block.InteractiveLootBlock;
import com.github.theredbrain.lootableblocks.block.LootableVaultBlock;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultState;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockRegistry {

	public static final Block LOOTABLE_VAULT_BLOCK = registerBlock("lootable_vault_block", new LootableVaultBlock(Block.Settings.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(50.0F).noOcclusion().sound(SoundType.VAULT).lightLevel(state -> ((LootableVaultState) state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE)).getLuminance()).isViewBlocking(Blocks::never).noLootTable()), CreativeModeTabs.FUNCTIONAL_BLOCKS);
	public static final Block LOOTABLE_FROZEN_VAULT_BLOCK = registerBlock("lootable_frozen_vault_block", new LootableVaultBlock(Block.Settings.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(50.0F).noOcclusion().sound(SoundType.VAULT).lightLevel(state -> ((LootableVaultState) state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE)).getLuminance()).isViewBlocking(Blocks::never).noLootTable()), CreativeModeTabs.FUNCTIONAL_BLOCKS);
	public static final Block INTERACTIVE_LOOT_BLOCK = registerBlock("interactive_loot_block", new InteractiveLootBlock(Block.Settings.of().mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0f, 3600000.0f).noLootTable()), CreativeModeTabs.OP_BLOCKS);

	private static Block registerBlock(String name, Block block, ResourceKey<CreativeModeTab> itemGroup) {
		Registry.register(BuiltInRegistries.ITEM, LootableBlocks.identifier(name), new BlockItem(block, new Item.Properties()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(content -> content.accept(block));
		return Registry.register(BuiltInRegistries.BLOCK, LootableBlocks.identifier(name), block);
	}

	public static void init() {
	}
}
