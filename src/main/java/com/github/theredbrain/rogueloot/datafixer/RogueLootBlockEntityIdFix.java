package com.github.theredbrain.rogueloot.datafixer;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import net.minecraft.util.datafix.fixes.References;

import java.util.Map;

public class RogueLootBlockEntityIdFix extends DataFix {
	private static final Map<String, String> RENAMED_BLOCK_ENTITIES = DataFixUtils.make(Maps.<String, String>newHashMap(), map -> {
		map.put("lootableblocks:lootable_vault_block", "rogueloot:rogue_loot_vault_block");
		map.put("lootableblocks:interactive_loot_block", "rogueloot:interactive_rogue_loot_block");
	});

	public RogueLootBlockEntityIdFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
		Type<?> type2 = this.getOutputSchema().getType(References.ITEM_STACK);
		TaggedChoice.TaggedChoiceType<String> taggedChoiceType = (TaggedChoice.TaggedChoiceType<String>) this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
		TaggedChoice.TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoice.TaggedChoiceType<String>) this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
		return TypeRewriteRule.seq(
				this.convertUnchecked("item stack block entity name hook converter", type, type2),
				this.fixTypeEverywhere(
						"RogueLootBlockEntityIdFix",
						taggedChoiceType,
						taggedChoiceType2,
						dynamicOps -> pair -> pair.mapFirst(oldName -> (String) RENAMED_BLOCK_ENTITIES.getOrDefault(oldName, oldName))
				)
		);
	}
}
