package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.InteractiveLootBlockEntity;
import com.github.theredbrain.rogueloot.block.entity.LootableVaultBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EntityRegistry {

	public static final BlockEntityType<LootableVaultBlockEntity> LOOTABLE_VAULT_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			RogueLoot.identifier("lootable_vault_block"),
			FabricBlockEntityTypeBuilder.create(LootableVaultBlockEntity::new, BlockRegistry.LOOTABLE_VAULT_BLOCK, BlockRegistry.LOOTABLE_FROZEN_VAULT_BLOCK).build());
	public static final BlockEntityType<InteractiveLootBlockEntity> INTERACTIVE_LOOT_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			RogueLoot.identifier("interactive_loot_block"),
			FabricBlockEntityTypeBuilder.create(InteractiveLootBlockEntity::new, BlockRegistry.INTERACTIVE_LOOT_BLOCK).build());

	public static void init() {
	}
}
