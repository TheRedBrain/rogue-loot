package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.puffish.skillsmod.SkillsMod;

public record AddExperienceLevelType(
		Identifier categoryId,
		int amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddExperienceLevelType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(AddExperienceLevelType::categoryId),
							Codec.INT.optionalFieldOf("amount", 1).forGetter(AddExperienceLevelType::amount)
					)
					.apply(i, AddExperienceLevelType::new)
	);

	@Override
	public MapCodec<AddExperienceLevelType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.addExperienceLevel(serverPlayer, this.categoryId, this.amount);
	}
}
