package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record LockCategoryType(
		Identifier categoryId
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<LockCategoryType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(x -> x.categoryId)
					)
					.apply(instance, LockCategoryType::new)
	);

	@Override
	public MapCodec<LockCategoryType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.lockCategory(
				serverPlayer,
				this.categoryId
		);
	}
}
