package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public record RogueLootVaultConfig(
		String lootableIdentifier,
		int rolls,
		int choices,
		boolean withChoice,
		double activationRange,
		double deactivationRange,
		ItemStackTemplate keyItem,
		String displayLootTableIdentifier
) {
	public static RogueLootVaultConfig DEFAULT = new RogueLootVaultConfig();
	public static final Codec<RogueLootVaultConfig> CODEC = RecordCodecBuilder.<RogueLootVaultConfig>create(
					instance -> instance.group(
									Codec.STRING.fieldOf("lootable_identifier").forGetter(RogueLootVaultConfig::lootableIdentifier),
									Codec.INT.fieldOf("rolls").forGetter(RogueLootVaultConfig::rolls),
									Codec.INT.fieldOf("choices").forGetter(RogueLootVaultConfig::choices),
									Codec.BOOL.fieldOf("with_choice").forGetter(RogueLootVaultConfig::withChoice),
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
				3,
				1,
				true,
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
}
