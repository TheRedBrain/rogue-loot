package com.github.theredbrain.rogueloot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public record LootableVaultConfig(
		String lootableIdentifier,
		int rolls,
		int choices,
		boolean withChoice,
		double activationRange,
		double deactivationRange,
		ItemStackTemplate keyItem,
		String displayLootTableIdentifier
) {
	public static LootableVaultConfig DEFAULT = new LootableVaultConfig();
	public static final Codec<LootableVaultConfig> CODEC = RecordCodecBuilder.<LootableVaultConfig>create(
					instance -> instance.group(
									Codec.STRING.fieldOf("lootable_identifier").forGetter(LootableVaultConfig::lootableIdentifier),
									Codec.INT.fieldOf("rolls").forGetter(LootableVaultConfig::rolls),
									Codec.INT.fieldOf("choices").forGetter(LootableVaultConfig::choices),
									Codec.BOOL.fieldOf("with_choice").forGetter(LootableVaultConfig::withChoice),
									Codec.DOUBLE.fieldOf("activation_range").forGetter(LootableVaultConfig::activationRange),
									Codec.DOUBLE.fieldOf("deactivation_range").forGetter(LootableVaultConfig::deactivationRange),
									ItemStackTemplate.CODEC.fieldOf("key_item").forGetter(LootableVaultConfig::keyItem),
									Codec.STRING.fieldOf("display_loot_table_identifier").forGetter(LootableVaultConfig::displayLootTableIdentifier)
							)
							.apply(instance, LootableVaultConfig::new)
			)
			.validate(LootableVaultConfig::validate);

	private LootableVaultConfig() {
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

	private DataResult<LootableVaultConfig> validate() {
		return this.activationRange > this.deactivationRange
				? DataResult.error(() -> "Activation range (" + this.activationRange + ") must be less or equal to deactivation range (" + this.deactivationRange + ")")
				: DataResult.success(this);
	}
}
