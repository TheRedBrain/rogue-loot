package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record VanillaLootTableType(
		Holder<LootTable> lootTableHolder
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<VanillaLootTableType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							LootTable.CODEC.fieldOf("loot_table").forGetter(VanillaLootTableType::lootTableHolder)
					)
					.apply(i, VanillaLootTableType::new)
	);

	@Override
	public MapCodec<VanillaLootTableType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		LootParams params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.THIS_ENTITY, serverPlayer).withParameter(LootContextParams.ORIGIN, serverPlayer.position()).create(LootContextParamSets.ADVANCEMENT_REWARD);
		boolean bl = false;
		for (ItemStack itemStack : this.lootTableHolder.value().getRandomItems(params)) {

			if (serverPlayer.addItem(itemStack)) {
				bl = true;
				continue;
			}
			ItemEntity itemEntity = serverPlayer.drop(itemStack, false);

			if (itemEntity != null) {
				itemEntity.setNoPickUpDelay();
				itemEntity.setTarget(serverPlayer.getUUID());
			}
		}
		if (bl) {
			serverPlayer.containerMenu.broadcastChanges();
		}
	}
}

