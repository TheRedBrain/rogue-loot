package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

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