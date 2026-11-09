package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public record RogueLootVaultConfig(
		String lootTableIdentifier,
		Mode lootTableMode,
		int rolls,
		int choices,
		double activationRange,
		double deactivationRange,
		ItemStackTemplate keyItem,
		String displayLootTableIdentifier
) {
	public static RogueLootVaultConfig DEFAULT = new RogueLootVaultConfig();
	public static final Codec<RogueLootVaultConfig> CODEC = RecordCodecBuilder.<RogueLootVaultConfig>create(
					instance -> instance.group(
									Codec.STRING.fieldOf("loot_table_identifier").forGetter(RogueLootVaultConfig::lootTableIdentifier),
									Mode.CODEC.optionalFieldOf("loot_table_mode", Mode.ROGUE_LOOT).forGetter(RogueLootVaultConfig::lootTableMode),
									Codec.INT.fieldOf("rolls").forGetter(RogueLootVaultConfig::rolls),
									Codec.INT.fieldOf("choices").forGetter(RogueLootVaultConfig::choices),
									Codec.DOUBLE.fieldOf("activation_range").forGetter(RogueLootVaultConfig::activationRange),
									Codec.DOUBLE.fieldOf("deactivation_range").forGetter(RogueLootVaultConfig::deactivationRange),
									ItemStackTemplate.CODEC.fieldOf("key_item").forGetter(RogueLootVaultConfig::keyItem),
									Codec.STRING.fieldOf("display_loot_table_identifier").forGetter(RogueLootVaultConfig::displayLootTableIdentifier)
							)
							.apply(instance, RogueLootVaultConfig::new)
			)
			.validate(RogueLootVaultConfig::validate);

	private RogueLootVaultConfig() {
		this(
				"",
				Mode.ROGUE_LOOT,
				3,
				1,
				4.0,
				4.5,
				new ItemStackTemplate(Items.TRIAL_KEY),
				""
		);
	}

	private DataResult<RogueLootVaultConfig> validate() {
		return this.activationRange > this.deactivationRange
				? DataResult.error(() -> "Activation range (" + this.activationRange + ") must be less or equal to deactivation range (" + this.deactivationRange + ")")
				: DataResult.success(this);
	}

	public enum Mode implements StringRepresentable {
		LOOTABLE_CHOICE("lootable_choice"),
		LOOTABLE_RANDOM("lootable_random"),
		ROGUE_LOOT("rogue_loot");

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		private final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public Component asText() {
			return Component.translatable("gui.interactive_loot_block.mode." + this.name);
		}
	}

}
