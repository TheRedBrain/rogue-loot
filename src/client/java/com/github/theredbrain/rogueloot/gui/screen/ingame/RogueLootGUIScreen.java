package com.github.theredbrain.rogueloot.gui.screen.ingame;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.gui.components.LootOptionButton;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.world.inventory.RogueLootGuiMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class RogueLootGUIScreen extends AbstractContainerScreen<RogueLootGuiMenu> {

	private static final int MAX_OPTIONS_AMOUNT = 6;

	private Button selectLootOption0Button;
	private Button selectLootOption1Button;
	private Button selectLootOption2Button;
	private Button selectLootOption3Button;
	private Button selectLootOption4Button;
	private Button selectLootOption5Button;
	private Button confirmButton;

	private final boolean[] selectedOptions = new boolean[MAX_OPTIONS_AMOUNT];

	private int selectedOptionsAmount;
	private MutableComponent currentChoicesLabel = Component.empty();
	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[]{};
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[]{};
	private int currentChoicesLabelX = 0;

	public RogueLootGUIScreen(RogueLootGuiMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 176);
		Arrays.fill(this.selectedOptions, false);

	}

	private void selectLootOption(Button button, int index) {
		if (((LootOptionButton) button).isSelected()) {
			((LootOptionButton) button).setSelected(false);
			this.selectedOptions[index] = false;
			this.updateWidgets();
		} else {
			if (this.selectedOptionsAmount < this.menu.getGeneratedRogueLoot().maxChoices()) {
				((LootOptionButton) button).setSelected(true);
				this.selectedOptions[index] = true;
				this.updateWidgets();
			}
		}
	}

	private int countSelectedOptions() {
		int amount = 0;
		for (int i = 0; i < MAX_OPTIONS_AMOUNT; i++) {
			if (this.selectedOptions[i]) {
				amount++;
			}
		}
		return amount;
	}

	private void done() {
		this.applySelection();
		this.onClose();
	}

	@Override
	protected void init() {

		super.init();

		this.selectLootOption0Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 7, this.topPos + 21, 50, 50, Component.empty(), button -> this.selectLootOption(button, 0)));
		this.selectLootOption1Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 63, this.topPos + 21, 50, 50, Component.empty(), button -> this.selectLootOption(button, 1)));
		this.selectLootOption2Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 119, this.topPos + 21, 50, 50, Component.empty(), button -> this.selectLootOption(button, 2)));
		this.selectLootOption3Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 7, this.topPos + 77, 50, 50, Component.empty(), button -> this.selectLootOption(button, 3)));
		this.selectLootOption4Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 63, this.topPos + 77, 50, 50, Component.empty(), button -> this.selectLootOption(button, 4)));
		this.selectLootOption5Button = this.addRenderableWidget(new LootOptionButton(this.leftPos + 119, this.topPos + 77, 50, 50, Component.empty(), button -> this.selectLootOption(button, 5)));

		this.confirmButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.rogue_loot_screen.confirm"), button -> this.done()).bounds(this.leftPos + 7, this.topPos + this.imageHeight - 27, this.imageWidth - 14, 20).build());


		// 7 + 50 + 6 + 50 + 6 + 50 + 7
		// 7 + 50 + 6 + + 25 + 25 + 6 + 50 + 7
		// 35 + 50 + 3 + 3 + 50 + 35

		// 21 + 50 + 3 + 3 + 50
		this.setInitialFocus(this.selectLootOption0Button);
		this.initWidgets();
		this.updateWidgets();
	}

	private void initWidgets() {

		GeneratedRogueLoot generatedRogueLoot = this.menu.getGeneratedRogueLoot();

		this.requirementsNotMetLabelFormatting = new ChatFormatting[generatedRogueLoot.requirementsNotMetFormatting().size()];
		for (int i = 0; i < generatedRogueLoot.requirementsNotMetFormatting().size(); i++) {
			this.requirementsNotMetLabelFormatting[i] = generatedRogueLoot.requirementsNotMetFormatting().get(i);
		}
		this.requirementsMetLabelFormatting = new ChatFormatting[generatedRogueLoot.requirementsMetFormatting().size()];
		for (int i = 0; i < generatedRogueLoot.requirementsMetFormatting().size(); i++) {
			this.requirementsMetLabelFormatting[i] = generatedRogueLoot.requirementsMetFormatting().get(i);
		}

		this.selectLootOption0Button.visible = false;
		this.selectLootOption1Button.visible = false;
		this.selectLootOption2Button.visible = false;
		this.selectLootOption3Button.visible = false;
		this.selectLootOption4Button.visible = false;
		this.selectLootOption5Button.visible = false;

		int visibleOptionsAmount = Math.min(generatedRogueLoot.entryList().size(), MAX_OPTIONS_AMOUNT);

		int[][] buttonPositions = getButtonPositions(visibleOptionsAmount);

		this.selectLootOption0Button.setPosition(buttonPositions[0][0], buttonPositions[0][1]);
		this.selectLootOption1Button.setPosition(buttonPositions[1][0], buttonPositions[1][1]);
		this.selectLootOption2Button.setPosition(buttonPositions[2][0], buttonPositions[2][1]);
		this.selectLootOption3Button.setPosition(buttonPositions[3][0], buttonPositions[3][1]);
		this.selectLootOption4Button.setPosition(buttonPositions[4][0], buttonPositions[4][1]);
		this.selectLootOption5Button.setPosition(buttonPositions[5][0], buttonPositions[5][1]);

		if (visibleOptionsAmount >= 1) {
			this.selectLootOption0Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry0 = generatedRogueLoot.entryList().get(0);

			((LootOptionButton) this.selectLootOption0Button).setTextureIds(entry0.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption0Button).setDisplayStack(entry0.displayStack());
			((LootOptionButton) this.selectLootOption0Button).setIconTexture(entry0.iconTextureIdentifier());
			if (!entry0.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting0 = new ChatFormatting[entry0.labelFormatting().size()];
				for (int i = 0; i < entry0.labelFormatting().size(); i++) {
					labelFormatting0[i] = entry0.labelFormatting().get(i);
				}
				this.selectLootOption0Button.setMessage(Component.translatable(entry0.labelText()).withStyle(labelFormatting0));
			}

		}
		if (visibleOptionsAmount >= 2) {

			this.selectLootOption1Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry1 = generatedRogueLoot.entryList().get(1);

			((LootOptionButton) this.selectLootOption1Button).setTextureIds(entry1.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption1Button).setDisplayStack(entry1.displayStack());
			((LootOptionButton) this.selectLootOption1Button).setIconTexture(entry1.iconTextureIdentifier());
			if (!entry1.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting1 = new ChatFormatting[entry1.labelFormatting().size()];
				for (int i = 0; i < entry1.labelFormatting().size(); i++) {
					labelFormatting1[i] = entry1.labelFormatting().get(i);
				}
				this.selectLootOption1Button.setMessage(Component.translatable(entry1.labelText()).withStyle(labelFormatting1));
			}

		}
		if (visibleOptionsAmount >= 3) {

			this.selectLootOption2Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry2 = generatedRogueLoot.entryList().get(2);

			((LootOptionButton) this.selectLootOption2Button).setTextureIds(entry2.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption2Button).setDisplayStack(entry2.displayStack());
			((LootOptionButton) this.selectLootOption2Button).setIconTexture(entry2.iconTextureIdentifier());
			if (!entry2.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting2 = new ChatFormatting[entry2.labelFormatting().size()];
				for (int i = 0; i < entry2.labelFormatting().size(); i++) {
					labelFormatting2[i] = entry2.labelFormatting().get(i);
				}
				this.selectLootOption2Button.setMessage(Component.translatable(entry2.labelText()).withStyle(labelFormatting2));
			}

		}
		if (visibleOptionsAmount >= 4) {

			this.selectLootOption3Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry3 = generatedRogueLoot.entryList().get(3);

			((LootOptionButton) this.selectLootOption3Button).setTextureIds(entry3.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption3Button).setDisplayStack(entry3.displayStack());
			((LootOptionButton) this.selectLootOption3Button).setIconTexture(entry3.iconTextureIdentifier());
			if (!entry3.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting3 = new ChatFormatting[entry3.labelFormatting().size()];
				for (int i = 0; i < entry3.labelFormatting().size(); i++) {
					labelFormatting3[i] = entry3.labelFormatting().get(i);
				}
				this.selectLootOption3Button.setMessage(Component.translatable(entry3.labelText()).withStyle(labelFormatting3));
			}

		}
		if (visibleOptionsAmount >= 5) {

			this.selectLootOption4Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry4 = generatedRogueLoot.entryList().get(4);

			((LootOptionButton) this.selectLootOption4Button).setTextureIds(entry4.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption4Button).setDisplayStack(entry4.displayStack());
			((LootOptionButton) this.selectLootOption4Button).setIconTexture(entry4.iconTextureIdentifier());
			if (!entry4.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting4 = new ChatFormatting[entry4.labelFormatting().size()];
				for (int i = 0; i < entry4.labelFormatting().size(); i++) {
					labelFormatting4[i] = entry4.labelFormatting().get(i);
				}
				this.selectLootOption4Button.setMessage(Component.translatable(entry4.labelText()).withStyle(labelFormatting4));
			}

		}
		if (visibleOptionsAmount >= 6) {

			this.selectLootOption5Button.visible = true;

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry5 = generatedRogueLoot.entryList().get(5);

			((LootOptionButton) this.selectLootOption5Button).setTextureIds(entry5.entryBackgroundTexturePathAddition());
			((LootOptionButton) this.selectLootOption5Button).setDisplayStack(entry5.displayStack());
			((LootOptionButton) this.selectLootOption5Button).setIconTexture(entry5.iconTextureIdentifier());
			if (!entry5.labelText().isEmpty()) {
				ChatFormatting[] labelFormatting5 = new ChatFormatting[entry5.labelFormatting().size()];
				for (int i = 0; i < entry5.labelFormatting().size(); i++) {
					labelFormatting5[i] = entry5.labelFormatting().get(i);
				}
				this.selectLootOption5Button.setMessage(Component.translatable(entry5.labelText()).withStyle(labelFormatting5));
			}

		}
	}

	private int[][] getButtonPositions(int visibleOptionsAmount) {
		return switch (visibleOptionsAmount) {
			case 1 -> new int[][]{{this.leftPos + 63, this.topPos + 49}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
			case 2 ->
					new int[][]{{this.leftPos + 35, this.topPos + 49}, {this.leftPos + 91, this.topPos + 49}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
			case 3 ->
					new int[][]{{this.leftPos + 7, this.topPos + 49}, {this.leftPos + 63, this.topPos + 49}, {this.leftPos + 119, this.topPos + 49}, {0, 0}, {0, 0}, {0, 0}};
			case 4 ->
					new int[][]{{this.leftPos + 35, this.topPos + 21}, {this.leftPos + 91, this.topPos + 21}, {this.leftPos + 35, this.topPos + 77}, {this.leftPos + 91, this.topPos + 77}, {0, 0}, {0, 0}};
			case 5 ->
					new int[][]{{this.leftPos + 7, this.topPos + 21}, {this.leftPos + 63, this.topPos + 21}, {this.leftPos + 119, this.topPos + 21}, {this.leftPos + 35, this.topPos + 77}, {this.leftPos + 91, this.topPos + 77}, {0, 0}};
			default ->
					new int[][]{{this.leftPos + 7, this.topPos + 21}, {this.leftPos + 63, this.topPos + 21}, {this.leftPos + 119, this.topPos + 21}, {this.leftPos + 7, this.topPos + 77}, {this.leftPos + 63, this.topPos + 77}, {this.leftPos + 119, this.topPos + 77}};
		};
	}

	private void updateWidgets() {

		this.selectedOptionsAmount = countSelectedOptions();

		GeneratedRogueLoot generatedRogueLoot = this.menu.getGeneratedRogueLoot();

		int minChoices = generatedRogueLoot.minChoices();
		int maxChoices = generatedRogueLoot.maxChoices();
		if (minChoices < maxChoices) {
			this.currentChoicesLabel = Component.translatable("gui.rogue_loot_screen.choices.range", this.selectedOptionsAmount, minChoices, maxChoices);
		} else if (minChoices == maxChoices) {
			this.currentChoicesLabel = Component.translatable("gui.rogue_loot_screen.choices.simple", this.selectedOptionsAmount, minChoices);
		}

		if (this.selectedOptionsAmount < minChoices) {
			this.currentChoicesLabel.withStyle(this.requirementsNotMetLabelFormatting);
		} else {
			this.currentChoicesLabel.withStyle(this.requirementsMetLabelFormatting);
		}

		this.currentChoicesLabelX = (this.imageWidth - this.font.width(this.currentChoicesLabel)) / 2;

		this.confirmButton.active = this.selectedOptionsAmount <= maxChoices && this.selectedOptionsAmount >= minChoices;

	}

	@Override
	public void resize(final int width, final int height) {
		boolean bool = ((LootOptionButton) this.selectLootOption0Button).isSelected();
		boolean bool1 = ((LootOptionButton) this.selectLootOption1Button).isSelected();
		boolean bool2 = ((LootOptionButton) this.selectLootOption2Button).isSelected();
		boolean bool3 = ((LootOptionButton) this.selectLootOption3Button).isSelected();
		boolean bool4 = ((LootOptionButton) this.selectLootOption4Button).isSelected();
		boolean bool5 = ((LootOptionButton) this.selectLootOption5Button).isSelected();
		this.init(width, height);
		((LootOptionButton) this.selectLootOption0Button).setSelected(bool);
		((LootOptionButton) this.selectLootOption1Button).setSelected(bool1);
		((LootOptionButton) this.selectLootOption2Button).setSelected(bool2);
		((LootOptionButton) this.selectLootOption3Button).setSelected(bool3);
		((LootOptionButton) this.selectLootOption4Button).setSelected(bool4);
		((LootOptionButton) this.selectLootOption5Button).setSelected(bool5);
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {

		super.extractBackground(graphics, mouseX, mouseY, a);

		int x = this.leftPos;
		int y = this.topPos;
		Identifier identifier = Identifier.tryParse(this.menu.getGeneratedRogueLoot().backgroundTexturePath());
		if (!this.menu.getGeneratedRogueLoot().backgroundTexturePath().isEmpty() && identifier != null) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, identifier, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		}

	}

	@Override
	protected void extractLabels(final GuiGraphicsExtractor graphics, final int xm, final int ym) {
		if (!this.currentChoicesLabel.equals(Component.empty())) {
			graphics.textRenderer().accept(this.currentChoicesLabelX, this.imageHeight - 40, this.currentChoicesLabel);
		}
	}

	private void applySelection() {
		ClientPlayNetworking.send(new ApplyRogueLootSelectionPacket(
				getChosenRogueLootEntries(),
				this.menu.getLocalRogueLootTableIdentifier()
		));
	}

	private List<String> getChosenRogueLootEntries() {
		List<String> list = new ArrayList<>();
		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntryList = this.menu.getGeneratedRogueLoot().entryList();

		for (int i = 0; i < Math.min(6, visibleRogueLootTableEntryList.size()); i++) {
			if (this.selectedOptions[i]) {
				list.add(visibleRogueLootTableEntryList.get(i).parentRogueLootTableEntryId());
			}
		}
		return list;
	}
}
