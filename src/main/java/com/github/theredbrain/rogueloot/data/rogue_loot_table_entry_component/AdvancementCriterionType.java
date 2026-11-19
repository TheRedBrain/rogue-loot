package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record AdvancementCriterionType(
		Identifier advancementIdentifier,
		String criterion
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<AdvancementCriterionType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							Identifier.CODEC.fieldOf("advancement_identifier").forGetter(x -> x.advancementIdentifier),
							Codec.STRING.fieldOf("criterion").forGetter(x -> x.criterion)
					)
					.apply(instance, AdvancementCriterionType::new)
	);

	@Override
	public MapCodec<AdvancementCriterionType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {

		AdvancementHolder advancementEntry = serverLevel.getServer().getAdvancements().get(this.advancementIdentifier);

		if (advancementEntry != null && !this.criterion.isEmpty()) {
			serverPlayer.getAdvancements().award(advancementEntry, this.criterion);
		}
	}
}
