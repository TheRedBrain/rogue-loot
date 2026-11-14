package com.github.theredbrain.rogueloot.gui.screen.ingame;

import com.github.theredbrain.rogueloot.config.ClientConfig;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.gui.components.LootOptionButton;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.registry.RogueLootConfigs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Environment(value = EnvType.CLIENT)
public class RogueLootGUIScreen extends Screen {

	private static final Component BANISHING_LABEL_TEXT = Component.translatable("gui.rogue_loot_screen.banishing_label");
	private static final int MAX_OPTIONS_AMOUNT = 6;
	private final GeneratedRogueLoot generatedRogueLoot;
	private final Identifier localRogueLootTableIdentifier;
	private final int banishingAttempts;

	private Button selectLootOption0Button;
	private Button selectLootOption1Button;
	private Button selectLootOption2Button;
	private Button selectLootOption3Button;
	private Button selectLootOption4Button;
	private Button selectLootOption5Button;
	private Button confirmButton;
	private CycleButton<Boolean> toggleBanishingButton;

	private final boolean[] selectedOptions = new boolean[MAX_OPTIONS_AMOUNT];
	private final boolean[] banishedOptions = new boolean[MAX_OPTIONS_AMOUNT];

	private int selectedOptionsAmount;
	private int banishedOptionsAmount;
	private boolean banishingEnabled = false;
	private MutableComponent currentChoicesLabel = Component.empty();
	private MutableComponent currentBanishingAttemptsLabel = Component.empty();
	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[]{};
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[]{};
	private int currentChoicesLabelX = 0;
	private int currentChoicesLabelY = 0;
	private int currentBanishingAttemptsLabelX = 0;
	private int currentBanishingAttemptsLabelY = 0;

	public RogueLootGUIScreen(final GeneratedRogueLoot generatedRogueLoot, final Identifier localRogueLootTableIdentifier, int banishingAttempts) {
		super(GameNarrator.NO_TITLE);
		this.generatedRogueLoot = generatedRogueLoot;
		this.localRogueLootTableIdentifier = localRogueLootTableIdentifier;
		this.banishingAttempts = banishingAttempts;
		Arrays.fill(this.selectedOptions, false);
		Arrays.fill(this.banishedOptions, false);

	}

	private void selectLootOption(final Button button, final int index) {
		if (index >= MAX_OPTIONS_AMOUNT || index >= this.generatedRogueLoot.entryList().size()) {
			return;
		}
		if (this.banishingEnabled && this.generatedRogueLoot.enableEntryBanishing()) { // TODO keep redundant check?
			if (this.banishingAttempts < 0 || (this.banishingAttempts > 0 && this.banishedOptionsAmount <= this.banishingAttempts)) {
				if (((LootOptionButton) button).isSelected()) { // TODO can selected options be banished?
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.banishingEnabled = false;
				this.toggleBanishingButton.setValue(false);
				button.active = false;
				button.visible = RogueLootConfigs.CLIENT_CONFIG.show_banished_loot_options.get();
				this.banishedOptions[index] = true;
				this.banishEntry(index);
				this.updateWidgets();
			}
		} else {
			if (((LootOptionButton) button).isSelected()) {
				((LootOptionButton) button).setSelected(false);
				this.selectedOptions[index] = false;
				this.updateWidgets();
			} else {
				if (this.selectedOptionsAmount < this.generatedRogueLoot.maxChoices()) {
					((LootOptionButton) button).setSelected(true);
					this.selectedOptions[index] = true;
					this.updateWidgets();
				}
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

	private int countBanishedOptions() {
		int amount = 0;
		for (int i = 0; i < MAX_OPTIONS_AMOUNT; i++) {
			if (this.banishedOptions[i]) {
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

		this.selectLootOption0Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 81, 50, 50, 50, Component.empty(), button -> this.selectLootOption(button, 0)));
		this.selectLootOption1Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 25, 50, 50, 50, Component.empty(), button -> this.selectLootOption(button, 1)));
		this.selectLootOption2Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 + 31, 50, 50, 50, Component.empty(), button -> this.selectLootOption(button, 2)));
		this.selectLootOption3Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 81, 106, 50, 50, Component.empty(), button -> this.selectLootOption(button, 3)));
		this.selectLootOption4Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 25, 106, 50, 50, Component.empty(), button -> this.selectLootOption(button, 4)));
		this.selectLootOption5Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 + 31, 106, 50, 50, Component.empty(), button -> this.selectLootOption(button, 5)));

		this.confirmButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.rogue_loot_screen.confirm"), button -> this.done()).bounds(this.width / 2 - 75, 210, 150, 20).build());

		int labelWidth = 10 + this.font.width(BANISHING_LABEL_TEXT) + 4;
		this.toggleBanishingButton = this.addRenderableWidget(CycleButton.onOffBuilder(this.banishingEnabled).displayOnlyValue().create(this.width / 2 + 79 + labelWidth, 210, 150 - labelWidth, 20, Component.empty(), (button, banishingEnabled) -> {
			this.banishingEnabled = banishingEnabled;
			this.updateWidgets();
		}));


		// 7 + 50 + 6 + 50 + 6 + 50 + 7
		// 7 + 50 + 6 + + 25 + 25 + 6 + 50 + 7
		// 35 + 50 + 3 + 3 + 50 + 35

		// -81 -25 +31
		// -53 +3
		// -25

		// 21 + 50 + 3 + 3 + 50
		this.setInitialFocus(this.selectLootOption0Button);
		this.initWidgets();
		this.updateWidgets();
	}

	private void initWidgets() {

		ClientConfig clientConfig = RogueLootConfigs.CLIENT_CONFIG;

		this.requirementsNotMetLabelFormatting = new ChatFormatting[this.generatedRogueLoot.requirementsNotMetFormatting().size()];
		for (int i = 0; i < this.generatedRogueLoot.requirementsNotMetFormatting().size(); i++) {
			this.requirementsNotMetLabelFormatting[i] = this.generatedRogueLoot.requirementsNotMetFormatting().get(i);
		}
		this.requirementsMetLabelFormatting = new ChatFormatting[this.generatedRogueLoot.requirementsMetFormatting().size()];
		for (int i = 0; i < this.generatedRogueLoot.requirementsMetFormatting().size(); i++) {
			this.requirementsMetLabelFormatting[i] = this.generatedRogueLoot.requirementsMetFormatting().get(i);
		}

		this.toggleBanishingButton.visible = this.generatedRogueLoot.enableEntryBanishing();
		this.toggleBanishingButton.active = this.banishingAttempts != 0;

		int visibleOptionsAmount = Math.min(this.generatedRogueLoot.entryList().size(), MAX_OPTIONS_AMOUNT);

		int[][] buttonPositions = getButtonPositions(visibleOptionsAmount);

		this.selectLootOption0Button.setPosition(buttonPositions[0][0], buttonPositions[0][1]);
		this.selectLootOption1Button.setPosition(buttonPositions[1][0], buttonPositions[1][1]);
		this.selectLootOption2Button.setPosition(buttonPositions[2][0], buttonPositions[2][1]);
		this.selectLootOption3Button.setPosition(buttonPositions[3][0], buttonPositions[3][1]);
		this.selectLootOption4Button.setPosition(buttonPositions[4][0], buttonPositions[4][1]);
		this.selectLootOption5Button.setPosition(buttonPositions[5][0], buttonPositions[5][1]);

		this.selectLootOption0Button.visible = false;
		this.selectLootOption1Button.visible = false;
		this.selectLootOption2Button.visible = false;
		this.selectLootOption3Button.visible = false;
		this.selectLootOption4Button.visible = false;
		this.selectLootOption5Button.visible = false;

		if (visibleOptionsAmount >= 1) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry0 = this.generatedRogueLoot.entryList().get(0);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry0.parentRogueLootTableEntryId())) {
				this.selectLootOption0Button.visible = true;
			} else {
				this.selectLootOption0Button.active = false;
				this.selectLootOption0Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
		if (visibleOptionsAmount >= 2) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry1 = this.generatedRogueLoot.entryList().get(1);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry1.parentRogueLootTableEntryId())) {
				this.selectLootOption1Button.visible = true;
			} else {
				this.selectLootOption1Button.active = false;
				this.selectLootOption1Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
		if (visibleOptionsAmount >= 3) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry2 = this.generatedRogueLoot.entryList().get(2);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry2.parentRogueLootTableEntryId())) {
				this.selectLootOption2Button.visible = true;
			} else {
				this.selectLootOption2Button.active = false;
				this.selectLootOption2Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
		if (visibleOptionsAmount >= 4) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry3 = this.generatedRogueLoot.entryList().get(3);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry3.parentRogueLootTableEntryId())) {
				this.selectLootOption3Button.visible = true;
			} else {
				this.selectLootOption3Button.active = false;
				this.selectLootOption3Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
		if (visibleOptionsAmount >= 5) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry4 = this.generatedRogueLoot.entryList().get(4);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry4.parentRogueLootTableEntryId())) {
				this.selectLootOption4Button.visible = true;
			} else {
				this.selectLootOption4Button.active = false;
				this.selectLootOption4Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
		if (visibleOptionsAmount >= 6) {

			GeneratedRogueLoot.VisibleRogueLootTableEntry entry5 = this.generatedRogueLoot.entryList().get(5);

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

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, entry5.parentRogueLootTableEntryId())) {
				this.selectLootOption5Button.visible = true;
			} else {
				this.selectLootOption5Button.active = false;
				this.selectLootOption5Button.visible = clientConfig.show_banished_loot_options.get();
			}
		}
	}

	private int[][] getButtonPositions(final int visibleOptionsAmount) {
		return switch (visibleOptionsAmount) {
			case 1 -> new int[][]{{this.width / 2 - 25, 78}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
			case 2 -> new int[][]{{this.width / 2 - 53, 78}, {this.width / 2 + 3, 78}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
			case 3 ->
					new int[][]{{this.width / 2 - 81, 78}, {this.width / 2 - 25, 78}, {this.width / 2 + 31, 78}, {0, 0}, {0, 0}, {0, 0}};
			case 4 ->
					new int[][]{{this.width / 2 - 53, 50}, {this.width / 2 + 3, 50}, {this.width / 2 - 53, 106}, {this.width / 2 + 3, 106}, {0, 0}, {0, 0}};
			case 5 ->
					new int[][]{{this.width / 2 - 81, 50}, {this.width / 2 - 25, 50}, {this.width / 2 + 31, 50}, {this.width / 2 - 53, 106}, {this.width / 2 + 3, 106}, {0, 0}};
			default ->
					new int[][]{{this.width / 2 - 81, 50}, {this.width / 2 - 25, 50}, {this.width / 2 + 31, 50}, {this.width / 2 - 81, 106}, {this.width / 2 - 25, 106}, {this.width / 2 + 31, 106}};
		};
	}

	private void updateWidgets() {

		this.selectedOptionsAmount = countSelectedOptions();

		if (this.banishingAttempts >= 0) {
			this.banishedOptionsAmount = countBanishedOptions();
			this.currentBanishingAttemptsLabel = Component.translatable(this.banishingAttempts == 1 ? "gui.rogue_loot_screen.banishing_attempts.singular" : "gui.rogue_loot_screen.banishing_attempts.plural", this.banishedOptionsAmount, this.banishingAttempts);
		} else {
			this.banishedOptionsAmount = -1;
			this.currentBanishingAttemptsLabel = Component.empty();
		}

		this.currentBanishingAttemptsLabelX = this.width / 2 + 154 - this.font.width(this.currentBanishingAttemptsLabel) / 2;

		int minChoices = this.generatedRogueLoot.minChoices();
		int maxChoices = this.generatedRogueLoot.maxChoices();
		if (minChoices < maxChoices) {
			this.currentChoicesLabel = Component.translatable("gui.rogue_loot_screen.choices.range", this.selectedOptionsAmount, minChoices, maxChoices);
		} else if (minChoices == maxChoices) {
			this.currentChoicesLabel = Component.translatable(minChoices == 1 ? "gui.rogue_loot_screen.choices.simple.singular" : "gui.rogue_loot_screen.choices.simple.plural", this.selectedOptionsAmount, minChoices);
		}

		if (this.selectedOptionsAmount < minChoices) {
			this.currentChoicesLabel.withStyle(this.requirementsNotMetLabelFormatting);
		} else {
			this.currentChoicesLabel.withStyle(this.requirementsMetLabelFormatting);
		}

		this.currentChoicesLabelX = (this.width - this.font.width(this.currentChoicesLabel)) / 2;

		this.confirmButton.active = this.selectedOptionsAmount <= maxChoices && this.selectedOptionsAmount >= minChoices && !this.banishingEnabled;

	}

	@Override
	public void resize(final int width, final int height) {
		boolean selected0 = ((LootOptionButton) this.selectLootOption0Button).isSelected();
		boolean selected1 = ((LootOptionButton) this.selectLootOption1Button).isSelected();
		boolean selected2 = ((LootOptionButton) this.selectLootOption2Button).isSelected();
		boolean selected3 = ((LootOptionButton) this.selectLootOption3Button).isSelected();
		boolean selected4 = ((LootOptionButton) this.selectLootOption4Button).isSelected();
		boolean selected5 = ((LootOptionButton) this.selectLootOption5Button).isSelected();
		boolean banishingEnabled = this.banishingEnabled;
		this.init(width, height);
		((LootOptionButton) this.selectLootOption0Button).setSelected(selected0);
		((LootOptionButton) this.selectLootOption1Button).setSelected(selected1);
		((LootOptionButton) this.selectLootOption2Button).setSelected(selected2);
		((LootOptionButton) this.selectLootOption3Button).setSelected(selected3);
		((LootOptionButton) this.selectLootOption4Button).setSelected(selected4);
		((LootOptionButton) this.selectLootOption5Button).setSelected(selected5);
		this.banishingEnabled = banishingEnabled;
		this.toggleBanishingButton.setValue(this.banishingEnabled);
	}

	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {

		super.extractBackground(graphics, mouseX, mouseY, a);

		Identifier identifier = Identifier.tryParse(this.generatedRogueLoot.backgroundTexturePath());
		if (!this.generatedRogueLoot.backgroundTexturePath().isEmpty() && identifier != null) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, identifier, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
		}

	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		if (!this.currentBanishingAttemptsLabel.equals(Component.empty())) {
			graphics.textRenderer().accept(this.currentBanishingAttemptsLabelX, 200, this.currentBanishingAttemptsLabel);
		}
		if (!this.currentChoicesLabel.equals(Component.empty())) {
			graphics.textRenderer().accept(this.currentChoicesLabelX, 200, this.currentChoicesLabel);
		}
		if (this.generatedRogueLoot.enableEntryBanishing()) {
			graphics.textRenderer().accept(this.width / 2 + 79 + 10, 216, BANISHING_LABEL_TEXT);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean isInGameUi() {
		return true;
	}

	private void banishEntry(final int index) {
		if (this.generatedRogueLoot.enableEntryBanishing() && index < this.generatedRogueLoot.entryList().size()) {
			Optional<ItemStack> optionalBanishingCost = Optional.empty();
			if (this.generatedRogueLoot.banishingCost().isPresent()) {
				optionalBanishingCost = Optional.of(this.generatedRogueLoot.banishingCost().get().create());
			}
			ClientPlayNetworking.send(new BanishRogueLootEntryPacket(
					this.generatedRogueLoot.entryList().get(index).parentRogueLootTableEntryId(),
					optionalBanishingCost
			));
		}
	}

	private void applySelection() {
		ClientPlayNetworking.send(new ApplyRogueLootSelectionPacket(
				getChosenRogueLootEntries(),
				this.localRogueLootTableIdentifier
		));
	}

	private List<Identifier> getChosenRogueLootEntries() {
		List<Identifier> list = new ArrayList<>();
		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntryList = this.generatedRogueLoot.entryList();

		for (int i = 0; i < Math.min(6, visibleRogueLootTableEntryList.size()); i++) {
			if (this.selectedOptions[i]) {
				list.add(visibleRogueLootTableEntryList.get(i).parentRogueLootTableEntryId());
			}
		}
		return list;
	}
}
