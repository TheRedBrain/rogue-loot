package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record RogueLootTableEntry(
		Optional<ContextAwarePredicate> unlockPredicate,
		List<RogueLootTableEntryComponent> rogueLootTableEntryComponents,
		Optional<ItemStackTemplate> displayStack,
		String iconTexturePath,
		String labelText,
		String rarity
) {
	public static final Codec<RogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("unlock_predicate").forGetter(x -> x.unlockPredicate),
			RogueLootTableEntryComponent.CODEC.listOf().fieldOf("components").forGetter(x -> x.rogueLootTableEntryComponents),
			ItemStackTemplate.CODEC.optionalFieldOf("display_stack").forGetter(x -> x.displayStack),
			Codec.STRING.optionalFieldOf("icon_texture_path", "").forGetter(x -> x.iconTexturePath),
			Codec.STRING.optionalFieldOf("label_text", "").forGetter(x -> x.labelText),
			Codec.STRING.fieldOf("rarity").forGetter(x -> x.rarity)
	).apply(instance, RogueLootTableEntry::new));

	public RogueLootTableEntry(
			Optional<ContextAwarePredicate> unlockPredicate,
			List<RogueLootTableEntryComponent> rogueLootTableEntryComponents,
			Optional<ItemStackTemplate> displayStack,
			String iconTexturePath,
			String labelText,
			String rarity
	) {
		this.unlockPredicate = unlockPredicate;
		this.rogueLootTableEntryComponents = rogueLootTableEntryComponents != null ? rogueLootTableEntryComponents : List.of();
		this.displayStack = displayStack;
		this.iconTexturePath = iconTexturePath != null ? iconTexturePath : "";
		this.labelText = labelText != null ? labelText : "";
		this.rarity = rarity;
	}

	public record RogueLootTableEntryComponent(
			RogueLootTableEntryComponent.Type type,
			Optional<String> advancement,
			Optional<ItemStackTemplate> itemStackTemplate,
			Optional<String> lootTable,
			Optional<MobEffectInstance> mobEffectInstance
	) {

		public static final Codec<RogueLootTableEntryComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
						RogueLootTableEntryComponent.Type.CODEC.fieldOf("type").forGetter(x -> x.type),
						Codec.STRING.optionalFieldOf("advancement").forGetter(x -> x.advancement),
						ItemStackTemplate.CODEC.optionalFieldOf("item_stack").forGetter(x -> x.itemStackTemplate),
						Codec.STRING.optionalFieldOf("loot_table").forGetter(x -> x.lootTable),
						MobEffectInstance.CODEC.optionalFieldOf("mob_effect").forGetter(x -> x.mobEffectInstance)
				).apply(instance, RogueLootTableEntryComponent::new)
		);

		public RogueLootTableEntryComponent(
				RogueLootTableEntryComponent.Type type,
				Optional<String> advancement,
				Optional<ItemStackTemplate> itemStackTemplate,
				Optional<String> lootTable,
				Optional<MobEffectInstance> mobEffectInstance
		) {
			this.type = type;
			this.advancement = advancement;
			this.itemStackTemplate = itemStackTemplate;
			this.lootTable = lootTable;
			this.mobEffectInstance = mobEffectInstance;
		}

		public enum Type implements StringRepresentable {
			ADVANCEMENT("advancement"),
			ITEM_STACK("item_stack"),
			LOOT_TABLE("loot_table"),
			MOB_EFFECT_INSTANCE("mob_effect_instance");

			public static final Codec<RogueLootTableEntryComponent.Type> CODEC = StringRepresentable.fromEnum(RogueLootTableEntryComponent.Type::values);

			private final String name;

			Type(String name) {
				this.name = name;
			}

			@Override
			public String getSerializedName() {
				return this.name;
			}
		}
	}

}
