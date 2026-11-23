package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonDataStructures {

	public record LabelSettings(
			List<ChatFormatting> formatting,
			boolean hasShadow
	) {

		public static final LabelSettings DEFAULT = new LabelSettings(List.of(), true);
		public static final Codec<LabelSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ChatFormatting.CODEC.listOf().optionalFieldOf("formatting", List.of()).forGetter(x -> x.formatting),
				Codec.BOOL.optionalFieldOf("has_shadow", true).forGetter(x -> x.hasShadow)
		).apply(instance, LabelSettings::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, LabelSettings> STREAM_CODEC = StreamCodec.ofMember(LabelSettings::write, LabelSettings::new);

		public LabelSettings(final RegistryFriendlyByteBuf registryByteBuf) {
			this(
					registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
					ByteBufCodecs.BOOL.decode(registryByteBuf)
			);
		}

		private void write(final FriendlyByteBuf registryByteBuf) {
			registryByteBuf.writeCollection(this.formatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
			ByteBufCodecs.BOOL.encode(registryByteBuf, this.hasShadow);
		}

		public static LabelSettings copy(final LabelSettings labelSettings) {
			return new LabelSettings(
					new ArrayList<>(labelSettings.formatting()),
					labelSettings.hasShadow()
			);
		}

	}

	public record AttributeInfluencedIntegerModifier(
			int defaultValue,
			int minValue,
			int maxValue,
			Optional<Identifier> influencingAttributeIdentifier,
			double influenceMultiplier
	) {

		public static final AttributeInfluencedIntegerModifier DEFAULT = new AttributeInfluencedIntegerModifier(1, 1, 1, Optional.empty(), 1.0);
		public static final Codec<AttributeInfluencedIntegerModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.optionalFieldOf("default_value", 1).forGetter(x -> x.defaultValue),
				Codec.INT.optionalFieldOf("min_value", 1).forGetter(x -> x.minValue),
				Codec.INT.optionalFieldOf("max_value", 1).forGetter(x -> x.maxValue),
				Identifier.CODEC.optionalFieldOf("influencing_attribute_identifier").forGetter(x -> x.influencingAttributeIdentifier),
				Codec.DOUBLE.optionalFieldOf("influence_multiplier", 1.0).forGetter(x -> x.influenceMultiplier)
				).apply(instance, AttributeInfluencedIntegerModifier::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, AttributeInfluencedIntegerModifier> STREAM_CODEC = StreamCodec.ofMember(AttributeInfluencedIntegerModifier::write, AttributeInfluencedIntegerModifier::new);

		public AttributeInfluencedIntegerModifier(final RegistryFriendlyByteBuf registryByteBuf) {
			this(
					ByteBufCodecs.INT.decode(registryByteBuf),
					ByteBufCodecs.INT.decode(registryByteBuf),
					ByteBufCodecs.INT.decode(registryByteBuf),
					ByteBufCodecs.optional(Identifier.STREAM_CODEC).decode(registryByteBuf),
					ByteBufCodecs.DOUBLE.decode(registryByteBuf)
			);
		}

		private void write(final FriendlyByteBuf registryByteBuf) {
			ByteBufCodecs.INT.encode(registryByteBuf, this.defaultValue);
			ByteBufCodecs.INT.encode(registryByteBuf, this.minValue);
			ByteBufCodecs.INT.encode(registryByteBuf, this.maxValue);
			ByteBufCodecs.optional(Identifier.STREAM_CODEC).encode(registryByteBuf, this.influencingAttributeIdentifier);
			ByteBufCodecs.DOUBLE.encode(registryByteBuf, this.influenceMultiplier);
		}

		public static int calculateAttributeInfluencedIntegerValue(ServerPlayer serverPlayer, Optional<AttributeInfluencedIntegerModifier> optionalAttributeInfluencedIntegerModifier, int defaultValue) {
			int attributeInfluencedIntegerValue = defaultValue;
			if (optionalAttributeInfluencedIntegerModifier.isPresent() && optionalAttributeInfluencedIntegerModifier.get().influencingAttributeIdentifier().isPresent()) {
				Optional<Holder.Reference<Attribute>> optionalAttributeReference = BuiltInRegistries.ATTRIBUTE.get(ResourceKey.create(Registries.ATTRIBUTE, optionalAttributeInfluencedIntegerModifier.get().influencingAttributeIdentifier().get()));
				if (optionalAttributeReference.isPresent()) {
					double influencingAttributeValue = serverPlayer.getAttributeValue(optionalAttributeReference.get());
					attributeInfluencedIntegerValue = (int) Math.clamp(Math.floor(defaultValue + (influencingAttributeValue * optionalAttributeInfluencedIntegerModifier.get().influenceMultiplier())), optionalAttributeInfluencedIntegerModifier.get().minValue(), optionalAttributeInfluencedIntegerModifier.get().maxValue());
				}
			}
			return attributeInfluencedIntegerValue;
		}

//		public static LabelSettings copy(final LabelSettings labelSettings) {
//			return new LabelSettings(
//					new ArrayList<>(labelSettings.formatting()),
//					labelSettings.hasShadow()
//			);
//		}

	}

	public record RogueLootTableInteraction(
			boolean enabled,
			Optional<ItemStackTemplate> optionalCost
	) {
		public static final Codec<RogueLootTableInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("enabled", false).forGetter(x -> x.enabled),
				ItemStackTemplate.CODEC.optionalFieldOf("cost").forGetter(x -> x.optionalCost)
		).apply(instance, RogueLootTableInteraction::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootTableInteraction> STREAM_CODEC = StreamCodec.ofMember(RogueLootTableInteraction::write, RogueLootTableInteraction::new);

		public RogueLootTableInteraction(final RegistryFriendlyByteBuf registryByteBuf) {
			this(
					ByteBufCodecs.BOOL.decode(registryByteBuf),
					ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(registryByteBuf)
			);
		}

		private void write(final RegistryFriendlyByteBuf registryByteBuf) {
			ByteBufCodecs.BOOL.encode(registryByteBuf, this.enabled);
			ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode(registryByteBuf, this.optionalCost);
		}

		public static RogueLootTableInteraction copy(final RogueLootTableInteraction rogueLootTableInteraction) {
			return new RogueLootTableInteraction(
					rogueLootTableInteraction.enabled(),
					rogueLootTableInteraction.optionalCost().isPresent() ? Optional.of(ItemStackTemplate.fromNonEmptyStack(rogueLootTableInteraction.optionalCost().get().create())) : Optional.empty()
			);
		}

	}

}
