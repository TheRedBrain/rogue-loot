package com.github.theredbrain.rogueloot.gui.components;

import com.github.theredbrain.rogueloot.RogueLoot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Optional;

public class LootOptionButton extends Button {

	private Identifier selectedHighlightedTextureId;
	private Identifier selectedTextureId;
	private Identifier unselectedHighlightedTextureId;
	private Identifier unselectedTextureId;
	private Optional<Identifier> iconTexture = Optional.empty();
	private Optional<ItemStackTemplate> displayStack = Optional.empty();
	private boolean selected;

	public LootOptionButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
	}

	public void setTextureIds(String string) {
		this.selectedHighlightedTextureId = RogueLoot.identifier("widget/loot_option_button/" + string + "selected_highlighted");
		this.selectedTextureId = RogueLoot.identifier("widget/loot_option_button/" + string + "selected");
		this.unselectedHighlightedTextureId = RogueLoot.identifier("widget/loot_option_button/" + string + "unselected_highlighted");
		this.unselectedTextureId = RogueLoot.identifier("widget/loot_option_button/" + string + "unselected");
	}

	public void setIconTexture(Optional<Identifier> iconTexture) {
		this.iconTexture = iconTexture;
	}

	public void setDisplayStack(Optional<ItemStackTemplate> displayStack) {
		this.displayStack = displayStack;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

		Identifier textureId;
		if (this.isHoveredOrFocused()) {
			textureId = this.selected ? this.selectedHighlightedTextureId : this.unselectedHighlightedTextureId;
		} else {
			textureId = this.selected ? this.selectedTextureId : this.unselectedTextureId;
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, textureId, this.getX(), this.getY(), this.width, this.height);

		boolean hasMessage = !this.message.equals(Component.empty());
		if (this.displayStack.isPresent()) {
			if (hasMessage) {
				graphics.item(this.displayStack.get().create(), this.getX() + 17, this.getY() + 7);
				// TODO wrap message
				graphics.textRenderer().accept(this.getX() + 7, this.getY() + 27, this.message);
			} else {
				graphics.item(this.displayStack.get().create(), this.getX() + 17, this.getY() + 17);
			}
		} else if (this.iconTexture.isPresent()) {
			if (hasMessage) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, this.iconTexture.get(), this.getX() + 17, this.getY() + 7, 0.0F, 0.0F, 16, 16, 16, 16);
				// TODO wrap message
				graphics.textRenderer().accept(this.getX() + 7, this.getY() + 27, this.message);
			} else {
				graphics.blit(RenderPipelines.GUI_TEXTURED, this.iconTexture.get(), this.getX() + 9, this.getY() + 9, 0.0F, 0.0F, 32, 32, 32, 32);
			}
		} else if (hasMessage) {
			// TODO wrap message
			graphics.textRenderer().accept(this.getX() + 7, this.getY() + 20, this.message);
		}

	}
}
