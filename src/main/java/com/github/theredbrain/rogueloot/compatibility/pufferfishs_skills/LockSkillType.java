package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record LockSkillType(
		Identifier categoryId,
		String skillId
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<LockSkillType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(LockSkillType::categoryId),
							Codec.STRING.fieldOf("skill_id").forGetter(LockSkillType::skillId)
					)
					.apply(i, LockSkillType::new)
	);

	@Override
	public MapCodec<LockSkillType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.lockSkill(serverPlayer, this.categoryId, this.skillId);
	}
}
