package com.github.theredbrain.rogueloot.data;

import com.github.theredbrain.rogueloot.registry.RogueLootDynamicRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RogueLootGuiSettings(
		String backgroundTexturePath,
		String entriesAssetPathAddition,
		List<ChatFormatting> rerollRequirementsNotMetFormatting,
		boolean rerollRequirementsNotMetHasShadow,
		List<ChatFormatting> rerollRequirementsMetFormatting,
		boolean rerollRequirementsMetHasShadow,
		List<ChatFormatting> banishingRequirementsNotMetFormatting,
		boolean banishingRequirementsNotMetHasShadow,
		List<ChatFormatting> banishingRequirementsMetFormatting,
		boolean banishingRequirementsMetHasShadow,
		List<ChatFormatting> confirmationRequirementsNotMetFormatting,
		boolean confirmationRequirementsNotMetHasShadow,
		List<ChatFormatting> confirmationRequirementsMetFormatting,
		boolean confirmationRequirementsMetHasShadow
) {
	public static final RogueLootGuiSettings DEFAULT = new RogueLootGuiSettings("", "", List.of(), true, List.of(), true, List.of(), true, List.of(), true, List.of(), true, List.of(), true);
	public static final Codec<RogueLootGuiSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("background_texture_path", "").forGetter(x -> x.backgroundTexturePath),
			Codec.STRING.optionalFieldOf("entries_asset_path_addition", "").forGetter(x -> x.entriesAssetPathAddition),
			ChatFormatting.CODEC.listOf().optionalFieldOf("reroll_requirements_not_met_formatting", List.of()).forGetter(x -> x.rerollRequirementsNotMetFormatting),
			Codec.BOOL.optionalFieldOf("reroll_requirements_not_met_has_shadow", true).forGetter(x -> x.rerollRequirementsNotMetHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("reroll_requirements_met_formatting", List.of()).forGetter(x -> x.rerollRequirementsMetFormatting),
			Codec.BOOL.optionalFieldOf("reroll_requirements_met_has_shadow", true).forGetter(x -> x.rerollRequirementsMetHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("banishing_requirements_not_met_formatting", List.of()).forGetter(x -> x.banishingRequirementsNotMetFormatting),
			Codec.BOOL.optionalFieldOf("banishing_requirements_not_met_has_shadow", true).forGetter(x -> x.banishingRequirementsNotMetHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("banishing_requirements_met_formatting", List.of()).forGetter(x -> x.banishingRequirementsMetFormatting),
			Codec.BOOL.optionalFieldOf("banishing_requirements_met_has_shadow", true).forGetter(x -> x.banishingRequirementsMetHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("confirmation_requirements_not_met_formatting", List.of()).forGetter(x -> x.confirmationRequirementsNotMetFormatting),
			Codec.BOOL.optionalFieldOf("confirmation_requirements_not_met_has_shadow", true).forGetter(x -> x.confirmationRequirementsNotMetHasShadow),
			ChatFormatting.CODEC.listOf().optionalFieldOf("confirmation_requirements_met_formatting", List.of()).forGetter(x -> x.confirmationRequirementsMetFormatting),
			Codec.BOOL.optionalFieldOf("confirmation_requirements_met_has_shadow", true).forGetter(x -> x.confirmationRequirementsMetHasShadow)
	).apply(instance, RogueLootGuiSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootGuiSettings> STREAM_CODEC = StreamCodec.ofMember(RogueLootGuiSettings::write, RogueLootGuiSettings::new);

	public RogueLootGuiSettings(final RegistryFriendlyByteBuf registryByteBuf) {
		this(
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				ByteBufCodecs.STRING_UTF8.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf),
				registryByteBuf.readList(ByteBufCodecs.fromCodec(ChatFormatting.CODEC)),
				ByteBufCodecs.BOOL.decode(registryByteBuf)
		);
	}

	private void write(final FriendlyByteBuf registryByteBuf) {
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.backgroundTexturePath);
		ByteBufCodecs.STRING_UTF8.encode(registryByteBuf, this.entriesAssetPathAddition);
		registryByteBuf.writeCollection(this.rerollRequirementsNotMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.rerollRequirementsNotMetHasShadow);
		registryByteBuf.writeCollection(this.rerollRequirementsMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.rerollRequirementsMetHasShadow);
		registryByteBuf.writeCollection(this.banishingRequirementsNotMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.banishingRequirementsNotMetHasShadow);
		registryByteBuf.writeCollection(this.banishingRequirementsMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.banishingRequirementsMetHasShadow);
		registryByteBuf.writeCollection(this.confirmationRequirementsNotMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.confirmationRequirementsNotMetHasShadow);
		registryByteBuf.writeCollection(this.confirmationRequirementsMetFormatting, ByteBufCodecs.fromCodec(ChatFormatting.CODEC));
		ByteBufCodecs.BOOL.encode(registryByteBuf, this.confirmationRequirementsMetHasShadow);
	}

	public static RogueLootGuiSettings copy(final RogueLootGuiSettings rogueLootGuiSettings) {
		return new RogueLootGuiSettings(
				rogueLootGuiSettings.backgroundTexturePath,
				rogueLootGuiSettings.entriesAssetPathAddition,
				new ArrayList<>(rogueLootGuiSettings.rerollRequirementsNotMetFormatting),
				rogueLootGuiSettings.rerollRequirementsNotMetHasShadow,
				new ArrayList<>(rogueLootGuiSettings.rerollRequirementsMetFormatting),
				rogueLootGuiSettings.rerollRequirementsMetHasShadow,
				new ArrayList<>(rogueLootGuiSettings.banishingRequirementsNotMetFormatting),
				rogueLootGuiSettings.banishingRequirementsNotMetHasShadow,
				new ArrayList<>(rogueLootGuiSettings.banishingRequirementsMetFormatting),
				rogueLootGuiSettings.banishingRequirementsMetHasShadow,
				new ArrayList<>(rogueLootGuiSettings.confirmationRequirementsNotMetFormatting),
				rogueLootGuiSettings.confirmationRequirementsNotMetHasShadow,
				new ArrayList<>(rogueLootGuiSettings.confirmationRequirementsMetFormatting),
				rogueLootGuiSettings.confirmationRequirementsMetHasShadow
		);
	}

	@Nullable
	public static RogueLootGuiSettings getRogueLootGuiSettings(final RegistryAccess registryAccess, final Identifier identifier) {
		RogueLootGuiSettings rogueLootTableEntryRarity = null;
		Optional<Registry<RogueLootGuiSettings>> optionalRogueLootGuiSettingsRegistry = registryAccess.lookup(RogueLootDynamicRegistries.ROGUE_LOOT_GUI_SETTINGS_REGISTRY_KEY);
		if (optionalRogueLootGuiSettingsRegistry.isPresent()) {
			Optional<Holder.Reference<RogueLootGuiSettings>> optionalRogueLootGuiSettingsReference = optionalRogueLootGuiSettingsRegistry.get().get(identifier);
			if (optionalRogueLootGuiSettingsReference.isPresent()) {
				rogueLootTableEntryRarity = optionalRogueLootGuiSettingsReference.get().value();
			}
		}
		return rogueLootTableEntryRarity;
	}

}
