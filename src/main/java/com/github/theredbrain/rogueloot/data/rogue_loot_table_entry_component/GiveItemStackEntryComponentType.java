package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

public record GiveItemStackEntryComponentType(
		ItemStackTemplate itemStackTemplate
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<GiveItemStackEntryComponentType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							ItemStackTemplate.CODEC.fieldOf("item_stack").forGetter(GiveItemStackEntryComponentType::itemStackTemplate)
					)
					.apply(i, GiveItemStackEntryComponentType::new)
	);

	@Override
	public MapCodec<GiveItemStackEntryComponentType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		serverPlayer.getInventory().placeItemBackInInventory(this.itemStackTemplate.create());
	}
}
