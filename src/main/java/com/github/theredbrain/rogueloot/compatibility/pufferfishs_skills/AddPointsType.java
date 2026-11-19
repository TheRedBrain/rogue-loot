package com.github.theredbrain.rogueloot.compatibility.pufferfishs_skills;

import com.github.theredbrain.rogueloot.compatibility.PufferfishsSkillsCompat;
import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record AddPointsType(
		Identifier categoryId,
		int amount,
		Identifier pointSource,
		boolean isSilent
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AddPointsType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Identifier.CODEC.fieldOf("category_id").forGetter(AddPointsType::categoryId),
							Codec.INT.optionalFieldOf("amount", 1).forGetter(AddPointsType::amount),
							Identifier.CODEC.fieldOf("point_source").forGetter(AddPointsType::pointSource),
							Codec.BOOL.fieldOf("is_silent").forGetter(AddPointsType::isSilent)
					)
					.apply(i, AddPointsType::new)
	);

	@Override
	public MapCodec<AddPointsType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		PufferfishsSkillsCompat.addPoints(serverPlayer, this.categoryId, this.pointSource, this.amount, this.isSilent);
	}
}
