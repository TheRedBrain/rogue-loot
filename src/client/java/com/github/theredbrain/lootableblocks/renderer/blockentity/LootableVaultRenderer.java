package com.github.theredbrain.lootableblocks.renderer.blockentity;

import com.github.theredbrain.lootableblocks.block.entity.LootableVaultBlockEntity;
import com.github.theredbrain.lootableblocks.block.lootable_vault.LootableVaultClientData;
import com.github.theredbrain.lootableblocks.renderer.blockentity.state.LootableVaultRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LootableVaultRenderer implements BlockEntityRenderer<LootableVaultBlockEntity, LootableVaultRenderState> {
	private final ItemModelResolver itemModelResolver;
	private final RandomSource random = RandomSource.create();

	public LootableVaultRenderer(final BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	public LootableVaultRenderState createRenderState() {
		return new LootableVaultRenderState();
	}

	public void extractRenderState(
			final LootableVaultBlockEntity blockEntity,
			final LootableVaultRenderState state,
			final float partialTicks,
			final Vec3 cameraPosition,
			final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		ItemStack displayItem = blockEntity.getSharedData().getDisplayItem();
		if (LootableVaultBlockEntity.Client.shouldDisplayActiveEffects(blockEntity.getSharedData()) && !displayItem.isEmpty() && blockEntity.getLevel() != null) {
			state.displayItem = new ItemClusterRenderState();
			this.itemModelResolver.updateForTopItem(state.displayItem.item, displayItem, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
			state.displayItem.count = ItemClusterRenderState.getRenderedAmount(displayItem.getCount());
			state.displayItem.seed = ItemClusterRenderState.getSeedForItemStack(displayItem);
			LootableVaultClientData clientData = blockEntity.getClientData();
			state.spin = Mth.rotLerp(partialTicks, clientData.getPreviousDisplayRotation(), clientData.getDisplayRotation());
		}
	}

	public void submit(final LootableVaultRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
		if (state.displayItem != null) {
			poseStack.pushPose();
			poseStack.translate(0.5F, 0.4F, 0.5F);
			poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
			ItemEntityRenderer.renderMultipleFromCount(poseStack, submitNodeCollector, state.lightCoords, state.displayItem, this.random);
			poseStack.popPose();
		}
	}
}
