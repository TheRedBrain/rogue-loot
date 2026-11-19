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
		LabelSettings rerollRequirementsNotMet,
		LabelSettings rerollRequirementsMet,
		LabelSettings enableBanishingRequirementsNotMet,
		LabelSettings enableBanishingRequirementsMet,
		LabelSettings disableBanishing,
		LabelSettings enableReservingRequirementsNotMet,
		LabelSettings enableReservingRequirementsMet,
		LabelSettings disableReserving,
		LabelSettings confirmationRequirementsNotMet,
		LabelSettings confirmationRequirementsMet
) {
	public static final RogueLootGuiSettings DEFAULT = new RogueLootGuiSettings("", "", LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT, LabelSettings.DEFAULT);
	public static final Codec<RogueLootGuiSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("background_texture_path", "").forGetter(x -> x.backgroundTexturePath),
			Codec.STRING.optionalFieldOf("entries_asset_path_addition", "").forGetter(x -> x.entriesAssetPathAddition),
			LabelSettings.CODEC.optionalFieldOf("reroll_requirements_not_met", LabelSettings.DEFAULT).forGetter(x -> x.rerollRequirementsNotMet),
			LabelSettings.CODEC.optionalFieldOf("reroll_requirements_met", LabelSettings.DEFAULT).forGetter(x -> x.rerollRequirementsMet),
			LabelSettings.CODEC.optionalFieldOf("enable_banishing_requirements_not_met", LabelSettings.DEFAULT).forGetter(x -> x.enableBanishingRequirementsNotMet),
			LabelSettings.CODEC.optionalFieldOf("enable_banishing_requirements_met", LabelSettings.DEFAULT).forGetter(x -> x.enableBanishingRequirementsMet),
			LabelSettings.CODEC.optionalFieldOf("disable_banishing", LabelSettings.DEFAULT).forGetter(x -> x.disableBanishing),
			LabelSettings.CODEC.optionalFieldOf("enable_reserving_requirements_not_met", LabelSettings.DEFAULT).forGetter(x -> x.enableReservingRequirementsNotMet),
			LabelSettings.CODEC.optionalFieldOf("enable_reserving_requirements_met", LabelSettings.DEFAULT).forGetter(x -> x.enableReservingRequirementsMet),
			LabelSettings.CODEC.optionalFieldOf("disable_reserving", LabelSettings.DEFAULT).forGetter(x -> x.disableReserving),
			LabelSettings.CODEC.optionalFieldOf("confirmation_requirements_not_met", LabelSettings.DEFAULT).forGetter(x -> x.confirmationRequirementsNotMet),
			LabelSettings.CODEC.optionalFieldOf("confirmation_requirements_met", LabelSettings.DEFAULT).forGetter(x -> x.confirmationRequirementsMet)
	).apply(instance, RogueLootGuiSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootGuiSettings> STREAM_CODEC = StreamCodec.ofMember(RogueLootGuiSettings::write, RogueLootGuiSettings::new);

	public RogueLootGuiSettings(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf),
				LabelSettings.STREAM_CODEC.decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.backgroundTexturePath);
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.entriesAssetPathAddition);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.rerollRequirementsNotMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.rerollRequirementsMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableBanishingRequirementsNotMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableBanishingRequirementsMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.disableBanishing);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableReservingRequirementsNotMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.enableReservingRequirementsMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.disableReserving);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.confirmationRequirementsNotMet);
		LabelSettings.STREAM_CODEC.encode((RegistryFriendlyByteBuf) registryByteBuf, this.confirmationRequirementsMet);
	}

	public static RogueLootGuiSettings copy(final RogueLootGuiSettings rogueLootGuiSettings) {
		return new RogueLootGuiSettings(
				rogueLootGuiSettings.backgroundTexturePath,
				rogueLootGuiSettings.entriesAssetPathAddition,
				LabelSettings.copy(rogueLootGuiSettings.rerollRequirementsNotMet),
				LabelSettings.copy(rogueLootGuiSettings.rerollRequirementsMet),
				LabelSettings.copy(rogueLootGuiSettings.enableBanishingRequirementsNotMet),
				LabelSettings.copy(rogueLootGuiSettings.enableBanishingRequirementsMet),
				LabelSettings.copy(rogueLootGuiSettings.disableBanishing),
				LabelSettings.copy(rogueLootGuiSettings.enableReservingRequirementsNotMet),
				LabelSettings.copy(rogueLootGuiSettings.enableReservingRequirementsMet),
				LabelSettings.copy(rogueLootGuiSettings.disableReserving),
				LabelSettings.copy(rogueLootGuiSettings.confirmationRequirementsNotMet),
				LabelSettings.copy(rogueLootGuiSettings.confirmationRequirementsMet)
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
