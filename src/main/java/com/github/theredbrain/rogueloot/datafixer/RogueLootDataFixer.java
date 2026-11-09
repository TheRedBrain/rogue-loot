package com.github.theredbrain.rogueloot.datafixer;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.mojang.datafixers.schemas.Schema;
import me.voxelbill.easy_data_fix.common.api.DataFixerAPI;
import me.voxelbill.easy_data_fix.common.api.DataFixerRegistry;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.schemas.V4771;

public class RogueLootDataFixer implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		RogueLoot.info("Running DataFixer!");
		DataFixerRegistry.addDataFix("Change Rogue Loot Block IDs", builder -> {
			// dataVersion = The Minecraft data version used in the version your upgrading to.
			// SchemaVersion = The builtin schema class that goes with the data version.
			// Example: (4773, V4771::new) = 26.1 full release
			Schema schema = builder.addSchema(4773, V4771::new);
			builder.addFixer(new RogueLootBlockEntityIdFix(schema, false));
			builder.addFixer(BlockRenameFix.create(schema, "Vault Block Block", DataFixerAPI.createRenamer("lootableblocks:lootable_vault_block", "rogueloot:rogue_loot_vault_block")));
			builder.addFixer(ItemRenameFix.create(schema, "Vault Block Item", DataFixerAPI.createRenamer("lootableblocks:lootable_vault_block", "rogueloot:rogue_loot_vault_block")));
			builder.addFixer(BlockRenameFix.create(schema, "Frozen Vault Block Block", DataFixerAPI.createRenamer("lootableblocks:lootable_frozen_vault_block", "rogueloot:rogue_loot_frozen_vault_block")));
			builder.addFixer(ItemRenameFix.create(schema, "Frozen Vault Block Item", DataFixerAPI.createRenamer("lootableblocks:lootable_frozen_vault_block", "rogueloot:rogue_loot_frozen_vault_block")));
			builder.addFixer(BlockRenameFix.create(schema, "Interactive Loot Block Block", DataFixerAPI.createRenamer("lootableblocks:interactive_loot_block", "rogueloot:interactive_rogue_loot_block")));
			builder.addFixer(ItemRenameFix.create(schema, "Interactive Loot Block Item", DataFixerAPI.createRenamer("lootableblocks:interactive_loot_block", "rogueloot:interactive_rogue_loot_block")));
		});
	}
}
