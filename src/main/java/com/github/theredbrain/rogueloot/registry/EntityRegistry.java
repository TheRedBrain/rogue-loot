package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.block.entity.RogueLootVaultBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EntityRegistry {

	public static final BlockEntityType<RogueLootVaultBlockEntity> ROGUE_LOOT_VAULT_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			RogueLoot.identifier("rogue_loot_vault_block"),
			FabricBlockEntityTypeBuilder.create(RogueLootVaultBlockEntity::new, BlockRegistry.ROGUE_LOOT_VAULT_BLOCK, BlockRegistry.ROGUE_LOOT_FROZEN_VAULT_BLOCK).build());
	public static final BlockEntityType<InteractiveRogueLootBlockEntity> INTERACTIVE_ROGUE_LOOT_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			RogueLoot.identifier("interactive_rogue_loot_block"),
			FabricBlockEntityTypeBuilder.create(InteractiveRogueLootBlockEntity::new, BlockRegistry.INTERACTIVE_ROGUE_LOOT_BLOCK).build());

	public static void init() {
	}
}
