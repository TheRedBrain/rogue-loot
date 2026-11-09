package com.github.theredbrain.rogueloot.renderer.blockentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import org.jspecify.annotations.Nullable;

public class RogueLootVaultRenderState extends BlockEntityRenderState {
	public @Nullable ItemClusterRenderState displayItem;
	public float spin;
}
