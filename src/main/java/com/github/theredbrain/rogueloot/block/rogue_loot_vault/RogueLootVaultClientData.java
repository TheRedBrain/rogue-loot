package com.github.theredbrain.rogueloot.block.rogue_loot_vault;

import net.minecraft.util.Mth;

public class RogueLootVaultClientData {
	private float displayRotation;
	private float prevDisplayRotation;

	public RogueLootVaultClientData() {
	}

	public float getDisplayRotation() {
		return this.displayRotation;
	}

	public float getPreviousDisplayRotation() {
		return this.prevDisplayRotation;
	}

	public void rotateDisplay() {
		this.prevDisplayRotation = this.displayRotation;
		this.displayRotation = Mth.wrapDegrees(this.displayRotation + 10.0F);
	}
}
