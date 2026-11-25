package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record RogueLootGuiSettings(
		String backgroundTexturePath,
		String entriesAssetPathAddition,
		Optional<CommonDataStructures.LabelSettings> rerollRequirementsNotMet,
		Optional<CommonDataStructures.LabelSettings> rerollRequirementsMet,
		Optional<CommonDataStructures.LabelSettings> enableBanishingRequirementsNotMet,
		Optional<CommonDataStructures.LabelSettings> enableBanishingRequirementsMet,
		Optional<CommonDataStructures.LabelSettings> disableBanishing,
		Optional<CommonDataStructures.LabelSettings> enableReservingRequirementsNotMet,
		Optional<CommonDataStructures.LabelSettings> enableReservingRequirementsMet,
		Optional<CommonDataStructures.LabelSettings> disableReserving,
		Optional<CommonDataStructures.LabelSettings> confirmationRequirementsNotMet,
		Optional<CommonDataStructures.LabelSettings> confirmationRequirementsMet
) {
	public static final RogueLootGuiSettings DEFAULT = new RogueLootGuiSettings("", "", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	public static final Codec<RogueLootGuiSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("background_texture_path", "").forGetter(x -> x.backgroundTexturePath),
			Codec.STRING.optionalFieldOf("entries_asset_path_addition", "").forGetter(x -> x.entriesAssetPathAddition),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("reroll_requirements_not_met").forGetter(x -> x.rerollRequirementsNotMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("reroll_requirements_met").forGetter(x -> x.rerollRequirementsMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("enable_banishing_requirements_not_met").forGetter(x -> x.enableBanishingRequirementsNotMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("enable_banishing_requirements_met").forGetter(x -> x.enableBanishingRequirementsMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("disable_banishing").forGetter(x -> x.disableBanishing),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("enable_reserving_requirements_not_met").forGetter(x -> x.enableReservingRequirementsNotMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("enable_reserving_requirements_met").forGetter(x -> x.enableReservingRequirementsMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("disable_reserving").forGetter(x -> x.disableReserving),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("confirmation_requirements_not_met").forGetter(x -> x.confirmationRequirementsNotMet),
			CommonDataStructures.LabelSettings.CODEC.optionalFieldOf("confirmation_requirements_met").forGetter(x -> x.confirmationRequirementsMet)
	).apply(instance, RogueLootGuiSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootGuiSettings> STREAM_CODEC = StreamCodec.ofMember(RogueLootGuiSettings::write, RogueLootGuiSettings::new);

	public RogueLootGuiSettings(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf),
				ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.backgroundTexturePath);
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.entriesAssetPathAddition);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.rerollRequirementsNotMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.rerollRequirementsMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableBanishingRequirementsNotMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableBanishingRequirementsMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.disableBanishing);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableReservingRequirementsNotMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableReservingRequirementsMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.disableReserving);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.confirmationRequirementsNotMet);
		ByteBufCodecs.optional(CommonDataStructures.LabelSettings.STREAM_CODEC).encode((RegistryFriendlyByteBuf) registryByteBuf, this.confirmationRequirementsMet);
	}

	public static RogueLootGuiSettings copy(final RogueLootGuiSettings rogueLootGuiSettings) {
		return new RogueLootGuiSettings(
				rogueLootGuiSettings.backgroundTexturePath,
				rogueLootGuiSettings.entriesAssetPathAddition,
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.rerollRequirementsNotMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.rerollRequirementsMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.enableBanishingRequirementsNotMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.enableBanishingRequirementsMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.disableBanishing)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.enableReservingRequirementsNotMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.enableReservingRequirementsMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.disableReserving)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.confirmationRequirementsNotMet)),
				Optional.of(CommonDataStructures.LabelSettings.copy(rogueLootGuiSettings.confirmationRequirementsMet))
		);
	}

	@Nullable
	public static RogueLootGuiSettings getRogueLootGuiSettings(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootGuiSettings rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootGuiSettings>> optionalRogueLootGuiSettingsRegistry = registryAccess.lookup(RogueLootRegistries.ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY);
		if (optionalRogueLootGuiSettingsRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootGuiSettings>> optionalRogueLootGuiSettingsReference = optionalRogueLootGuiSettingsRegistry.get().get(identifier);
			if (optionalRogueLootGuiSettingsReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootGuiSettingsReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}
}
