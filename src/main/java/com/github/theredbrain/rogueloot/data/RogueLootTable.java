package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record RogueLootTable(
		HolderSet<RogueLootTableEntry> rogueLootTableEntries,
		Map<Identifier, Identifier> rarityOverrides,
		String rogueLootGuiSettingsIdString,
		Optional<CommonDataStructures.AttributeInfluencedIntegerModifier> rollAmountModifier,
		Optional<CommonDataStructures.AttributeInfluencedIntegerModifier> minChoicesModifier,
		Optional<CommonDataStructures.AttributeInfluencedIntegerModifier> maxChoicesModifier,
		Optional<CommonDataStructures.RogueLootTableInteraction> rerolling,
		Optional<CommonDataStructures.RogueLootTableInteraction> banishing,
		Optional<CommonDataStructures.RogueLootTableInteraction> reserving,
		Optional<Identifier> randomSequence
) {
	public static final Codec<RogueLootTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryCodecs.homogeneousList(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY).fieldOf("rogue_loot_table_entries").forGetter(x -> x.rogueLootTableEntries),
			Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).optionalFieldOf("rarity_overrides", Map.of()).forGetter(x -> x.rarityOverrides),
			Codec.STRING.optionalFieldOf("gui_settings_id", "").forGetter(x -> x.rogueLootGuiSettingsIdString),
			CommonDataStructures.AttributeInfluencedIntegerModifier.CODEC.optionalFieldOf("roll_amount_modifier").forGetter(x -> x.rollAmountModifier),
			CommonDataStructures.AttributeInfluencedIntegerModifier.CODEC.optionalFieldOf("min_choices_modifier").forGetter(x -> x.minChoicesModifier),
			CommonDataStructures.AttributeInfluencedIntegerModifier.CODEC.optionalFieldOf("max_choices_modifier").forGetter(x -> x.maxChoicesModifier),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("rerolling").forGetter(x -> x.rerolling),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("banishing").forGetter(x -> x.banishing),
			CommonDataStructures.RogueLootTableInteraction.CODEC.optionalFieldOf("reserving").forGetter(x -> x.reserving),
			Identifier.CODEC.optionalFieldOf("random_sequence").forGetter(x -> x.randomSequence)
	).apply(instance, RogueLootTable::new));

	@Nullable
	public static RogueLootTable getRogueLootTable(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTable rogueLootTable = null;
		Optional<Registry<RogueLootTable>> optionalRogueLootTableRegistry = registryAccess.lookup(RogueLootRegistries.ROGUE_LOOT_TABLE_REGISTRY_KEY);
		if (optionalRogueLootTableRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTable>> optionalRogueLootTableReference = optionalRogueLootTableRegistry.get().get(identifier);
			if (optionalRogueLootTableReference.isPresent()) {
				rogueLootTable = optionalRogueLootTableReference.get().value();
			}
		}
		return rogueLootTable;
	}

}
