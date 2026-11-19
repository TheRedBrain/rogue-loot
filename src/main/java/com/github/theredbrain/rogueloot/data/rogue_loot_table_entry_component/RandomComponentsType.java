package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record RandomComponentsType(
		List<ComponentTypeListWithWeight> weightedComponentLists
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<RandomComponentsType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							ComponentTypeListWithWeight.CODEC.listOf().fieldOf("weighted_component_lists").forGetter(x -> x.weightedComponentLists)
					)
					.apply(instance, RandomComponentsType::new)
	);

	@Override
	public MapCodec<RandomComponentsType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		int totalWeight = 0;
		for (ComponentTypeListWithWeight entry : this.weightedComponentLists) {
			totalWeight += entry.weight;
		}
		int randomInt = serverPlayer.getRandom().nextInt(totalWeight);
		int discardedWeight = 0;
		for (ComponentTypeListWithWeight entry : this.weightedComponentLists) {

			if (randomInt < discardedWeight + entry.weight) {
				for (RogueLootTableEntryComponentType component : entry.componentList) {
					component.apply(serverLevel, serverPlayer);
				}
				break;
			} else {
				discardedWeight += entry.weight;
			}
		}
	}

	public record ComponentTypeListWithWeight(
			List<RogueLootTableEntryComponentType> componentList,
			int weight
	) {
		public static final MapCodec<ComponentTypeListWithWeight> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								RogueLootTableEntryComponentType.CODEC.listOf().fieldOf("components").forGetter(ComponentTypeListWithWeight::componentList),
								Codec.INT.validate(value -> value <= 0 ? DataResult.error(() -> "Weight can not be zero or smaller, but was " + value) : DataResult.success(value)).optionalFieldOf("weight", 1).forGetter(ComponentTypeListWithWeight::weight)
						)
						.apply(instance, ComponentTypeListWithWeight::new)
		);
		public static final Codec<ComponentTypeListWithWeight> CODEC = MAP_CODEC.codec();
	}
}
