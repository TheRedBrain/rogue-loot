package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetExperienceType(
		Identifier categoryId,
		int amount
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetExperienceType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(SetExperienceType::categoryId),
							Codec.INT.optionalFieldOf("amount", 1).forGetter(SetExperienceType::amount)
					)
					.apply(i, SetExperienceType::new)
	);

	@Override
	public MapCodec<SetExperienceType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.setExperience(serverPlayer, this.categoryId, this.amount);
	}
}
