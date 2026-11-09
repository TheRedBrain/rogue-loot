package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.InteractiveLootBlock;
import com.github.theredbrain.rogueloot.block.LootableVaultBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class BlockRegistry {

	public static ResourceKey<Block> LOOTABLE_VAULT_BLOCK_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, RogueLoot.identifier("lootable_vault_block"));
	public static ResourceKey<Item> LOOTABLE_VAULT_BLOCK_ITEM_KEY = ResourceKey.create(Registries.ITEM, RogueLoot.identifier("lootable_vault_block"));
	public static final Block LOOTABLE_VAULT_BLOCK = registerBlock(LOOTABLE_VAULT_BLOCK_BLOCK_KEY, LOOTABLE_VAULT_BLOCK_ITEM_KEY, new LootableVaultBlock(BlockBehaviour.Properties.of().setId(LOOTABLE_VAULT_BLOCK_BLOCK_KEY).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(50.0F).noOcclusion().sound(SoundType.VAULT).lightLevel(state -> state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE).getLuminance()).isViewBlocking(Blocks::never).noLootTable()), List.of(CreativeModeTabs.FUNCTIONAL_BLOCKS));
	public static ResourceKey<Block> LOOTABLE_FROZEN_VAULT_BLOCK_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, RogueLoot.identifier("lootable_frozen_vault_block"));
	public static ResourceKey<Item> LOOTABLE_FROZEN_VAULT_BLOCK_ITEM_KEY = ResourceKey.create(Registries.ITEM, RogueLoot.identifier("lootable_frozen_vault_block"));
	public static final Block LOOTABLE_FROZEN_VAULT_BLOCK = registerBlock(LOOTABLE_FROZEN_VAULT_BLOCK_BLOCK_KEY, LOOTABLE_FROZEN_VAULT_BLOCK_ITEM_KEY, new LootableVaultBlock(BlockBehaviour.Properties.of().setId(LOOTABLE_VAULT_BLOCK_BLOCK_KEY).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(50.0F).noOcclusion().sound(SoundType.VAULT).lightLevel(state -> state.getValue(LootableVaultBlock.LOOTABLE_VAULT_STATE).getLuminance()).isViewBlocking(Blocks::never).noLootTable()), List.of(CreativeModeTabs.FUNCTIONAL_BLOCKS));
	public static ResourceKey<Block> INTERACTIVE_LOOT_BLOCK_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, RogueLoot.identifier("interactive_loot_block"));
	public static ResourceKey<Item> INTERACTIVE_LOOT_BLOCK_ITEM_KEY = ResourceKey.create(Registries.ITEM, RogueLoot.identifier("interactive_loot_block"));
	public static final Block INTERACTIVE_LOOT_BLOCK = registerBlock(INTERACTIVE_LOOT_BLOCK_BLOCK_KEY, INTERACTIVE_LOOT_BLOCK_ITEM_KEY, new InteractiveLootBlock(BlockBehaviour.Properties.of().setId(INTERACTIVE_LOOT_BLOCK_BLOCK_KEY).mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0f, 3600000.0f).noLootTable()), List.of(CreativeModeTabs.OP_BLOCKS));

	private static Block registerBlock(ResourceKey<Block> block_key, ResourceKey<Item> item_key, Block block, List<ResourceKey<CreativeModeTab>> creativeModeTabList, Block... alternatives) {
		Item blockItem = Registry.register(BuiltInRegistries.ITEM, item_key, new BlockItem(block, new Item.Properties().setId(item_key)));
		for (ResourceKey<CreativeModeTab> creativeModeTab : creativeModeTabList) {
			CreativeModeTabEvents.modifyOutputEvent(creativeModeTab).register(content -> content.accept(block));
		}
		for (Block alternative : alternatives) {
			Item.BY_BLOCK.put(alternative, blockItem);
		}
		return Registry.register(BuiltInRegistries.BLOCK, block_key, block);
	}

	public static void init() {
	}
}
