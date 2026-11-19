package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.data.rogue_loot_table_entry_component.RogueLootTableEntryComponentType;
import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record RogueLootTableEntry(
		Optional<ContextAwarePredicate> unlockPredicate,
		List<RogueLootTableEntryComponentType> rogueLootTableEntryComponentTypes,
		Optional<ItemStackTemplate> displayStack,
		String iconTexturePath,
		String labelText,
		String rarityIdString,
		boolean allowBanishing,
		boolean allowReserving,
		boolean isPermanent
) {
	public static final Codec<RogueLootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("unlock_predicate").forGetter(x -> x.unlockPredicate),
			RogueLootTableEntryComponentType.CODEC.listOf().fieldOf("components").forGetter(x -> x.rogueLootTableEntryComponentTypes),
			ItemStackTemplate.CODEC.optionalFieldOf("display_stack").forGetter(x -> x.displayStack),
			Codec.STRING.optionalFieldOf("icon_texture_path", "").forGetter(x -> x.iconTexturePath),
			Codec.STRING.optionalFieldOf("label_text", "").forGetter(x -> x.labelText),
			Codec.STRING.optionalFieldOf("rarity", "").forGetter(x -> x.rarityIdString),
			Codec.BOOL.optionalFieldOf("allow_banishing", true).forGetter(x -> x.allowBanishing),
			Codec.BOOL.optionalFieldOf("allow_reserving", true).forGetter(x -> x.allowReserving),
			Codec.BOOL.optionalFieldOf("is_permanent", false).forGetter(x -> x.isPermanent)
	).apply(instance, RogueLootTableEntry::new));

	@Nullable
	public static RogueLootTableEntry getRogueLootTableEntry(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootTableEntry rogueLootTableEntry = null;
		Optional<Registry<RogueLootTableEntry>> optionalRogueLootTableEntryRegistry = registryAccess.lookup(RogueLootRegistries.ROGUE_LOOT_TABLE_ENTRY_REGISTRY_KEY);
		if (optionalRogueLootTableEntryRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootTableEntry>> optionalRogueLootTableEntryReference = optionalRogueLootTableEntryRegistry.get().get(identifier);
			if (optionalRogueLootTableEntryReference.isPresent()) {
				rogueLootTableEntry = optionalRogueLootTableEntryReference.get().value();
			}
		}
		return rogueLootTableEntry;
	}
}
