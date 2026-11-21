package com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public record ModifyEquipmentType(
		EquipmentSlotGroup equipmentSlotGroup,
		Optional<ItemPredicate> condition,
		List<LootItemFunction> lootItemFunctions,
		Optional<Identifier> randomSequence
) implements RogueLootTableEntryComponentType {
	public static final MapCodec<ModifyEquipmentType> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							EquipmentSlotGroup.CODEC.fieldOf("equipment_slot_group").forGetter(x -> x.equipmentSlotGroup),
							ItemPredicate.CODEC.optionalFieldOf("condition").forGetter(p -> p.condition),
							LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(x -> x.lootItemFunctions),
							Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
					)
					.apply(instance, ModifyEquipmentType::new)
	);

	@Override
	public MapCodec<ModifyEquipmentType> codec() {
		return MAP_CODEC;
	}

	@Override
	public void apply(final ServerLevel serverLevel, final ServerPlayer serverPlayer) {
		LootContext context = new LootContext.Builder(
				new LootParams.Builder(serverLevel)
						.withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
						.withParameter(LootContextParams.ORIGIN, serverPlayer.position())
						.create(LootContextParamSets.ADVANCEMENT_REWARD)
		).create(this.randomSequence);
		boolean bl = false;
		for (EquipmentSlot equipmentSlot : this.equipmentSlotGroup.slots()) {
			ItemStack itemStack = serverPlayer.getItemBySlot(equipmentSlot).copy();
			if (this.condition().isEmpty() || this.condition().get().test(itemStack)) {
				for (LootItemFunction lootItemFunction : this.lootItemFunctions) {
					itemStack = lootItemFunction.apply(
							itemStack,
							context
					);
				}
				serverPlayer.setItemSlot(equipmentSlot, itemStack.copy());
				bl = true;
			}
		}
		if (bl) {
			serverPlayer.containerMenu.broadcastChanges();
		}
	}
}
