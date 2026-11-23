package com.github.theredbrain.rogueloot.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record GeneratedRogueLootData(
		Identifier rogueLootTableIdentifier,
		Identifier savingToken,
		int defaultRolls,
		int modifiedRolls,
		int defaultMinChoices,
		int modifiedMinChoices,
		int defaultMaxChoices,
		int modifiedMaxChoices,
		Optional<BlockPos> optionalBlockPos,
		Optional<ItemStack> optionalItemStack,
		Optional<InteractionHand> optionalInteractionHand
) {

	public static final StreamCodec<RegistryFriendlyByteBuf, GeneratedRogueLootData> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC,
			GeneratedRogueLootData::rogueLootTableIdentifier,
			Identifier.STREAM_CODEC,
			GeneratedRogueLootData::savingToken,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::defaultRolls,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::modifiedRolls,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::defaultMinChoices,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::modifiedMinChoices,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::defaultMaxChoices,
			ByteBufCodecs.INT,
			GeneratedRogueLootData::modifiedMaxChoices,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
			GeneratedRogueLootData::optionalBlockPos,
			ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
			GeneratedRogueLootData::optionalItemStack,
			ByteBufCodecs.optional(InteractionHand.STREAM_CODEC),
			GeneratedRogueLootData::optionalInteractionHand,
			GeneratedRogueLootData::new
	);

	public static GeneratedRogueLootData.Builder builder(GeneratedRogueLootData generatedRogueLootData) {
		return new GeneratedRogueLootData.Builder(generatedRogueLootData);
	}

	public static class Builder {
		Identifier rogueLootTableIdentifier;
		Identifier savingToken;
		int defaultRolls;
		int modifiedRolls;
		int defaultMinChoices;
		int modifiedMinChoices;
		int defaultMaxChoices;
		int modifiedMaxChoices;
		Optional<BlockPos> optionalBlockPos;
		Optional<ItemStack> optionalItemStack;
		Optional<InteractionHand> optionalInteractionHand;

		public Builder(GeneratedRogueLootData base) {
			this.rogueLootTableIdentifier = base.rogueLootTableIdentifier();
			this.savingToken = base.savingToken();
			this.defaultRolls = base.defaultRolls();
			this.modifiedRolls = base.modifiedRolls();
			this.defaultMinChoices = base.defaultMinChoices();
			this.modifiedMinChoices = base.modifiedMinChoices();
			this.defaultMaxChoices = base.defaultMaxChoices();
			this.modifiedMaxChoices = base.modifiedMaxChoices();
			this.optionalBlockPos = base.optionalBlockPos();
			this.optionalItemStack = base.optionalItemStack();
			this.optionalInteractionHand = base.optionalInteractionHand();
		}

		public GeneratedRogueLootData.Builder withModifiedRolls(int modifiedRolls) {
			this.modifiedRolls = modifiedRolls;
			return this;
		}

		public GeneratedRogueLootData.Builder withModifiedMinChoices(int modifiedMinChoices) {
			this.modifiedMinChoices = modifiedMinChoices;
			return this;
		}

		public GeneratedRogueLootData.Builder withModifiedMaxChoices(int modifiedMaxChoices) {
			this.modifiedMaxChoices = modifiedMaxChoices;
			return this;
		}

		public GeneratedRogueLootData build() {
			return new GeneratedRogueLootData(
					this.rogueLootTableIdentifier,
					this.savingToken,
					this.defaultRolls,
					this.modifiedRolls,
					this.defaultMinChoices,
					this.modifiedMinChoices,
					this.defaultMaxChoices,
					this.modifiedMaxChoices,
					this.optionalBlockPos,
					this.optionalItemStack,
					this.optionalInteractionHand
			);
		}
	}

}
