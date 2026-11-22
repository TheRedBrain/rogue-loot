package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record ListType(
		List<RogueLootTableEntryComponentType> rogueLootTableEntryComponentTypes
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<ListType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							RogueLootTableEntryComponentType.CODEC.listOf().validate(ListType::validate).fieldOf("components").forGetter(x -> x.rogueLootTableEntryComponentTypes)
					)
					.apply(instance, ListType::new)
	);

	private static DataResult<List<RogueLootTableEntryComponentType>> validate(List<RogueLootTableEntryComponentType> instance) {
		for (RogueLootTableEntryComponentType componentType : instance) {
			if (componentType instanceof OpensRogueLootDisplayScreen) {
				return DataResult.error(() -> componentType + " is instance of OpensRogueLootDisplayScreen");
			}
		}
		return DataResult.success(instance);
	}

	@Override
	public MapCodec<ListType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		for (RogueLootTableEntryComponentType component : this.rogueLootTableEntryComponentTypes) {
			if (component instanceof OpensRogueLootDisplayScreen) {
				RogueLoot.error(component + " is instance of OpensRogueLootDisplayScreen. This should not happen.");
			} else {
				component.apply(serverLevel, serverPlayer);
			}
		}
	}
}
