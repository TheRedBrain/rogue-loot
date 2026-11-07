package com.github.theredbrain.lootableblocks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record LootableVaultConfig(
		String lootable_identifier,
		int rolls,
		int choices,
		boolean with_choice,
		double activation_range,
		double deactivation_range,
		ItemStack key_item,
		String display_loot_table_identifier
) {
	public static LootableVaultConfig DEFAULT = new LootableVaultConfig();
	public static final Codec<LootableVaultConfig> CODEC = RecordCodecBuilder.<LootableVaultConfig>create(
					instance -> instance.group(
									Codec.STRING.fieldOf("lootable_identifier").forGetter(LootableVaultConfig::lootable_identifier),
									Codec.INT.fieldOf("rolls").forGetter(LootableVaultConfig::rolls),
									Codec.INT.fieldOf("choices").forGetter(LootableVaultConfig::choices),
									Codec.BOOL.fieldOf("with_choice").forGetter(LootableVaultConfig::with_choice),
									Codec.DOUBLE.fieldOf("activation_range").forGetter(LootableVaultConfig::activation_range),
									Codec.DOUBLE.fieldOf("deactivation_range").forGetter(LootableVaultConfig::deactivation_range),
									ItemStack.STRICT_CODEC.fieldOf("key_item").forGetter(LootableVaultConfig::key_item),
									Codec.STRING.fieldOf("display_loot_table_identifier").forGetter(LootableVaultConfig::lootable_identifier)
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
				new ItemStack(Items.TRIAL_KEY),
				""
		);
	}

	private DataResult<LootableVaultConfig> validate() {
		return this.activation_range > this.deactivation_range
				? DataResult.error(() -> "Activation range (" + this.activation_range + ") must be less or equal to deactivation range (" + this.deactivation_range + ")")
				: DataResult.success(this);
	}
}
