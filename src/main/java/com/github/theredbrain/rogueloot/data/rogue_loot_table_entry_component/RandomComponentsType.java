package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record RandomComponentsType(
		List<ComponentTypeWithWeight> weightedComponents
) implements RogueLootTableEntryComponentType, OpensRogueLootDisplayScreen {
	public static final MapCodec<RandomComponentsType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							ComponentTypeWithWeight.CODEC.listOf().fieldOf("weighted_components").forGetter(x -> x.weightedComponents)
					)
					.apply(instance, RandomComponentsType::new)
	);

	@Override
	public MapCodec<RandomComponentsType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void openRogueLootDisplayScreen(final ServerLevel serverLevel, final ServerPlayer serverPlayer, final List<Identifier> additionalEntryIdentifiers) {
		apply(serverLevel, serverPlayer);
		if (!additionalEntryIdentifiers.isEmpty()) {
			RogueLootTableHelper.applyRogueLootTableEntries(serverLevel, serverPlayer, additionalEntryIdentifiers);
		}
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		int totalWeight = 0;
		for (ComponentTypeWithWeight entry : this.weightedComponents) {
			totalWeight += entry.weight;
		}
		int randomInt = serverPlayer.getRandom().nextInt(totalWeight);
		int discardedWeight = 0;
		for (ComponentTypeWithWeight entry : this.weightedComponents) {

			if (randomInt < discardedWeight + entry.weight) {
				entry.component.apply(serverLevel, serverPlayer);
				break;
			} else {
				discardedWeight += entry.weight;
			}
		}
	}

	public record ComponentTypeWithWeight(
			RogueLootTableEntryComponentType component,
			int weight
	) {
		public static final MapCodec<ComponentTypeWithWeight> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								RogueLootTableEntryComponentType.CODEC.validate(ComponentTypeWithWeight::validate).fieldOf("component").forGetter(x -> x.component),
								Codec.INT.validate(value -> value <= 0 ? DataResult.error(() -> "Weight can not be zero or smaller, but was " + value) : DataResult.success(value)).optionalFieldOf("weight", 1).forGetter(x -> x.weight)
						)
						.apply(instance, ComponentTypeWithWeight::new)
		);
		public static final Codec<ComponentTypeWithWeight> CODEC = MAP_CODEC.codec();

		private static DataResult<RogueLootTableEntryComponentType> validate(RogueLootTableEntryComponentType componentType) {
			if (componentType instanceof OpensRogueLootDisplayScreen) {
				return DataResult.error(() -> componentType + " is instance of OpensRogueLootDisplayScreen");
			}
			return DataResult.success(componentType);
		}

	}
}
