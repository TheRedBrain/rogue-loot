package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SetPointsType(
		Identifier categoryId,
		int amount,
		Identifier pointSource,
		boolean isSilent
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<SetPointsType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(SetPointsType::categoryId),
							Codec.INT.optionalFieldOf("amount", 1).forGetter(SetPointsType::amount),
							Identifier.CODEC.fieldOf("point_source").forGetter(SetPointsType::pointSource),
							Codec.BOOL.fieldOf("is_silent").forGetter(SetPointsType::isSilent)
					)
					.apply(i, SetPointsType::new)
	);

	@Override
	public MapCodec<SetPointsType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.setPoints(serverPlayer, this.categoryId, this.pointSource, this.amount, this.isSilent);
	}
}
