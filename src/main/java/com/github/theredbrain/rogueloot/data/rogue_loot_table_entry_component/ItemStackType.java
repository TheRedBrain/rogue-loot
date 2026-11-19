package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

public record ItemStackType(
		ItemStackTemplate itemStackTemplate
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<ItemStackType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							ItemStackTemplate.CODEC.fieldOf("item_stack").forGetter(ItemStackType::itemStackTemplate)
					)
					.apply(i, ItemStackType::new)
	);

	@Override
	public MapCodec<ItemStackType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		serverPlayer.getInventory().placeItemBackInInventory(this.itemStackTemplate.create());
	}
}
