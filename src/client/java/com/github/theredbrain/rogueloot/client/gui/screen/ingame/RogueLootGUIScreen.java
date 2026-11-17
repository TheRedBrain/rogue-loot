package com.github.theredbrain.rogueloot.client.gui.screen.ingame;

import com.github.theredbrain.rogueloot.client.gui.components.LootOptionButton;
import com.github.theredbrain.rogueloot.config.ClientConfig;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootTableIdentifier;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.network.packet.RerollRogueLootScreenPacket;
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

	private static final int MAX_OPTIONS_AMOUNT = 6;
	private final GeneratedRogueLoot generatedRogueLoot;
	private final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier;
	private final RogueLootGuiSettings rogueLootGuiSettings;
	private final int banishingAttempts;
	private final int rerollAttempts;
	private final Optional<ItemStack> itemStack;

	private Button selectLootOption0Button;
	private Button selectLootOption1Button;
	private Button selectLootOption2Button;
	private Button selectLootOption3Button;
	private Button selectLootOption4Button;
	private Button selectLootOption5Button;
	private Button rerollButton;
	private Button confirmButton;
	private CycleButton<Boolean> toggleBanishingButton;

	private final boolean[] selectedOptions = new boolean[MAX_OPTIONS_AMOUNT];
	private final boolean[] banishedOptions = new boolean[MAX_OPTIONS_AMOUNT];

	private int selectedOptionsAmount;
	private boolean banishingEnabled = false;
	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[]{};
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[]{};

	public RogueLootGUIScreen(final GeneratedRogueLoot generatedRogueLoot, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, final int rerollAttempts, final int banishingAttempts, final Optional<ItemStack> itemStack) {
		super(GameNarrator.NO_TITLE);
		this.generatedRogueLoot = generatedRogueLoot;
		this.rogueLootGuiSettings = this.generatedRogueLoot.rogueLootGuiSettings();
		this.generatedRogueLootTableIdentifier = generatedRogueLootTableIdentifier;
		this.rerollAttempts = rerollAttempts;
		this.banishingAttempts = banishingAttempts;
		this.itemStack = itemStack;
		Arrays.fill(this.selectedOptions, false);
		Arrays.fill(this.banishedOptions, false);

	}

	private void selectLootOption(final Button button, final int index) {
		if (index >= MAX_OPTIONS_AMOUNT || index >= this.generatedRogueLoot.entryList().size()) {
			return;
		}
		ClientConfig clientConfig = RogueLootConfigs.CLIENT_CONFIG;
		if (this.banishingEnabled && this.generatedRogueLoot.enableEntryBanishing()) {
			if (this.generatedRogueLoot.entryList().get(index).allowBanishing() && this.banishingAttempts != 1) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_banishing_for_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.banishingEnabled = false;
				this.toggleBanishingButton.setValue(false);
				button.active = false;
				button.visible = clientConfig.show_banished_loot_options.get();
				this.banishedOptions[index] = true;
				this.banishEntry(index);
				// TODO play sound?
				this.updateWidgets();
			} else {
				// TODO play sound?
			}
		} else {
			if (((LootOptionButton) button).isSelected()) {
				((LootOptionButton) button).setSelected(false);
				this.selectedOptions[index] = false;
				// TODO play sound?
				this.updateWidgets();
			} else {
				if (this.selectedOptionsAmount < this.generatedRogueLoot.maxChoices()) {
					((LootOptionButton) button).setSelected(true);
					this.selectedOptions[index] = true;
					// TODO play sound?
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

	private void done() {
		this.applySelection();
		this.onClose();
	}

	@Override
	protected void init() {

		super.init();

		this.selectLootOption0Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 81, 50, 50, 50, button -> this.selectLootOption(button, 0)));
		this.selectLootOption1Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 25, 50, 50, 50, button -> this.selectLootOption(button, 1)));
		this.selectLootOption2Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 + 31, 50, 50, 50, button -> this.selectLootOption(button, 2)));
		this.selectLootOption3Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 81, 106, 50, 50, button -> this.selectLootOption(button, 3)));
		this.selectLootOption4Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 - 25, 106, 50, 50, button -> this.selectLootOption(button, 4)));
		this.selectLootOption5Button = this.addRenderableWidget(new LootOptionButton(this.width / 2 + 31, 106, 50, 50, button -> this.selectLootOption(button, 5)));

		MutableComponent rerollButtonLabelComponent = this.rerollAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.reroll.simple") : this.rerollAttempts == 0 ? Component.translatable("gui.rogue_loot_screen.reroll.plural", this.rerollAttempts) : this.rerollAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.reroll.singular", this.rerollAttempts) : Component.translatable("gui.rogue_loot_screen.reroll.plural", this.rerollAttempts);
		ChatFormatting[] rerollButtonLabelChatFormatting;
		boolean rerollButtonLabelHasShadow;

		if (this.rerollAttempts == 0) {
			rerollButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.rerollRequirementsNotMetFormatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.rerollRequirementsNotMetFormatting().size(); i++) {
				rerollButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.rerollRequirementsNotMetFormatting().get(i);
			}
			rerollButtonLabelHasShadow = this.rogueLootGuiSettings.rerollRequirementsNotMetHasShadow();
		} else {
			rerollButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.rerollRequirementsMetFormatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.rerollRequirementsMetFormatting().size(); i++) {
				rerollButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.rerollRequirementsMetFormatting().get(i);
			}
			rerollButtonLabelHasShadow = this.rogueLootGuiSettings.rerollRequirementsMetHasShadow();
		}
		rerollButtonLabelComponent.withStyle(rerollButtonLabelChatFormatting);
		if (!rerollButtonLabelHasShadow) {
			rerollButtonLabelComponent.withoutShadow();
		}

		this.rerollButton = this.addRenderableWidget(Button.builder(rerollButtonLabelComponent, button -> this.reroll()).bounds(this.width / 2 - 229, 210, 150, 20).build());
		this.confirmButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> this.done()).bounds(this.width / 2 - 75, 210, 150, 20).build());

		MutableComponent toggleBanishingButtonLabelComponentTrue = Component.translatable("gui.rogue_loot_screen.banish.stop");
		ChatFormatting[] toggleBanishingButtonLabelChatFormattingTrue = new ChatFormatting[]{}; // TODO
		boolean toggleBanishingButtonLabelHasShadowTrue = true; // TODO
		toggleBanishingButtonLabelComponentTrue.withStyle(toggleBanishingButtonLabelChatFormattingTrue);
		if (!toggleBanishingButtonLabelHasShadowTrue) {
			toggleBanishingButtonLabelComponentTrue.withoutShadow();
		}

		MutableComponent toggleBanishingButtonLabelComponentFalse = this.banishingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.banish.simple") : this.banishingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.banish.singular", this.banishingAttempts) : Component.translatable("gui.rogue_loot_screen.banish.plural", this.banishingAttempts);
		ChatFormatting[] toggleBanishingButtonLabelChatFormattingFalse;
		boolean toggleBanishingButtonLabelHasShadowFalse;

		if (this.banishingAttempts == 0) {
			toggleBanishingButtonLabelChatFormattingFalse = new ChatFormatting[this.rogueLootGuiSettings.banishingRequirementsNotMetFormatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.banishingRequirementsNotMetFormatting().size(); i++) {
				toggleBanishingButtonLabelChatFormattingFalse[i] = this.rogueLootGuiSettings.banishingRequirementsNotMetFormatting().get(i);
			}
			toggleBanishingButtonLabelHasShadowFalse = this.rogueLootGuiSettings.banishingRequirementsNotMetHasShadow();
		} else {
			toggleBanishingButtonLabelChatFormattingFalse = new ChatFormatting[this.rogueLootGuiSettings.banishingRequirementsMetFormatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.banishingRequirementsMetFormatting().size(); i++) {
				toggleBanishingButtonLabelChatFormattingFalse[i] = this.rogueLootGuiSettings.banishingRequirementsMetFormatting().get(i);
			}
			toggleBanishingButtonLabelHasShadowFalse = this.rogueLootGuiSettings.banishingRequirementsMetHasShadow();
		}
		toggleBanishingButtonLabelComponentFalse.withStyle(toggleBanishingButtonLabelChatFormattingFalse);
		if (!toggleBanishingButtonLabelHasShadowFalse) {
			toggleBanishingButtonLabelComponentFalse.withoutShadow();
		}

		this.toggleBanishingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								toggleBanishingButtonLabelComponentTrue,
								toggleBanishingButtonLabelComponentFalse,
								this.banishingEnabled
						)
						.displayOnlyValue()
						.create(this.width / 2 + 79, 210, 150, 20, Component.empty(), (button, banishingEnabled) -> {
							this.banishingEnabled = banishingEnabled;
							this.updateWidgets();
						})
		);


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

		this.requirementsNotMetLabelFormatting = new ChatFormatting[this.rogueLootGuiSettings.confirmationRequirementsNotMetFormatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.confirmationRequirementsNotMetFormatting().size(); i++) {
			this.requirementsNotMetLabelFormatting[i] = this.rogueLootGuiSettings.confirmationRequirementsNotMetFormatting().get(i);
		}
		this.requirementsMetLabelFormatting = new ChatFormatting[this.rogueLootGuiSettings.confirmationRequirementsMetFormatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.confirmationRequirementsMetFormatting().size(); i++) {
			this.requirementsMetLabelFormatting[i] = this.rogueLootGuiSettings.confirmationRequirementsMetFormatting().get(i);
		}

		String entriesAssetPathAddition = "";
		if (!this.rogueLootGuiSettings.entriesAssetPathAddition().isEmpty()) {
			entriesAssetPathAddition = this.rogueLootGuiSettings.entriesAssetPathAddition() + "/";
		}

		this.rerollButton.visible = this.generatedRogueLoot.enableRerolling();
		this.rerollButton.active = this.rerollAttempts != 0;

		this.toggleBanishingButton.visible = this.generatedRogueLoot.enableEntryBanishing();
		this.toggleBanishingButton.active = this.banishingAttempts != 0;

		int visibleOptionsAmount = Math.min(this.generatedRogueLoot.entryList().size(), MAX_OPTIONS_AMOUNT);

		int[][] buttonPositions = getButtonPositions(visibleOptionsAmount);

		for (int i = 0; i < MAX_OPTIONS_AMOUNT; i++) {
			this.initLootOptionButton(this.getLootOptionButton(i), this.generatedRogueLoot.entryList().get(i), visibleOptionsAmount >= i + 1, buttonPositions[i][0], buttonPositions[i][1], entriesAssetPathAddition, clientConfig.show_banished_loot_options.get());
		}
	}

	private Button getLootOptionButton(int index) {
		return switch (index) {
			case 0 -> this.selectLootOption0Button;
			case 1 -> this.selectLootOption1Button;
			case 2 -> this.selectLootOption2Button;
			case 3 -> this.selectLootOption3Button;
			case 4 -> this.selectLootOption4Button;
			default -> this.selectLootOption5Button;
		};
	}

	private void initLootOptionButton(Button lootoptionButton, GeneratedRogueLoot.VisibleRogueLootTableEntry visibleRogueLootTableEntry, boolean buttonIsVisible, int buttonPositionX, int buttonPositionY, String entriesAssetPathAddition, boolean showBanishedLootOptions) {

		lootoptionButton.setPosition(buttonPositionX, buttonPositionY);

		lootoptionButton.visible = false;

		if (buttonIsVisible) {

			RogueLootTableEntryRarity rogueLootTableEntryRarity0 = visibleRogueLootTableEntry.rogueLootTableEntryRarity();

			String assetPathAddition0 = "";
			if (!rogueLootTableEntryRarity0.assetPathAddition().isEmpty()) {
				assetPathAddition0 = rogueLootTableEntryRarity0.assetPathAddition() + "/";
			}
			((LootOptionButton) lootoptionButton).setTextureIds(entriesAssetPathAddition + assetPathAddition0);
			((LootOptionButton) lootoptionButton).setDisplayStack(visibleRogueLootTableEntry.displayStack());
			((LootOptionButton) lootoptionButton).setIconTexture(visibleRogueLootTableEntry.iconTextureIdentifier());
			if (!visibleRogueLootTableEntry.labelText().isEmpty()) {
				ChatFormatting[] banishedLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.banishedLabelFormatting().size()];
				for (int i = 0; i < rogueLootTableEntryRarity0.banishedLabelFormatting().size(); i++) {
					banishedLabelFormatting0[i] = rogueLootTableEntryRarity0.banishedLabelFormatting().get(i);
				}
				MutableComponent banishedMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(banishedLabelFormatting0);
				if (!rogueLootTableEntryRarity0.banishedLabelHasShadow()) {
					banishedMessage0.withoutShadow();
				}
				ChatFormatting[] defaultLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.defaultLabelFormatting().size()];
				for (int i = 0; i < rogueLootTableEntryRarity0.defaultLabelFormatting().size(); i++) {
					defaultLabelFormatting0[i] = rogueLootTableEntryRarity0.defaultLabelFormatting().get(i);
				}
				MutableComponent defaultMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(defaultLabelFormatting0);
				if (!rogueLootTableEntryRarity0.defaultLabelHasShadow()) {
					defaultMessage0.withoutShadow();
				}
				ChatFormatting[] selectedLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.selectedLabelFormatting().size()];
				for (int i = 0; i < rogueLootTableEntryRarity0.selectedLabelFormatting().size(); i++) {
					selectedLabelFormatting0[i] = rogueLootTableEntryRarity0.selectedLabelFormatting().get(i);
				}
				MutableComponent selectedMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(selectedLabelFormatting0);
				if (!rogueLootTableEntryRarity0.selectedLabelHasShadow()) {
					selectedMessage0.withoutShadow();
				}
				((LootOptionButton) lootoptionButton).setBanishedMessage(banishedMessage0);
				((LootOptionButton) lootoptionButton).setDefaultMessage(defaultMessage0);
				((LootOptionButton) lootoptionButton).setSelectedMessage(selectedMessage0);
			}

			if (!DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, visibleRogueLootTableEntry.parentRogueLootTableEntryId())) {
				lootoptionButton.visible = true;
			} else {
				lootoptionButton.active = false;
				lootoptionButton.visible = showBanishedLootOptions;
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

		this.confirmButton.setMessage(getConfirmChoicesButonLabel());

		this.confirmButton.active = this.selectedOptionsAmount <= this.generatedRogueLoot.maxChoices() && this.selectedOptionsAmount >= this.generatedRogueLoot.minChoices() && !this.banishingEnabled;

	}

	private Component getConfirmChoicesButonLabel() {
		MutableComponent confirmChoicesButtonLabel = Component.empty();

		int minChoices = this.generatedRogueLoot.minChoices();
		int maxChoices = this.generatedRogueLoot.maxChoices();
		if (minChoices > maxChoices) {
			return confirmChoicesButtonLabel;
		}
		confirmChoicesButtonLabel = minChoices < maxChoices ? Component.translatable("gui.rogue_loot_screen.confirm_choices.range", this.selectedOptionsAmount, minChoices, maxChoices) :
				Component.translatable(minChoices == 1 ? "gui.rogue_loot_screen.confirm_choices.singular" : "gui.rogue_loot_screen.confirm_choices.plural", this.selectedOptionsAmount, minChoices);

		confirmChoicesButtonLabel.withStyle(this.selectedOptionsAmount < minChoices ? this.requirementsNotMetLabelFormatting : this.requirementsMetLabelFormatting);

		return confirmChoicesButtonLabel;
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

		Identifier identifier = Identifier.tryParse(this.rogueLootGuiSettings.backgroundTexturePath());
		if (!this.rogueLootGuiSettings.backgroundTexturePath().isEmpty() && identifier != null) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, identifier, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
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
			Optional<Identifier> optionalIdentifier = this.generatedRogueLoot.entryList().get(index).parentRogueLootTableEntryId();
			if (optionalIdentifier.isPresent()) {
				ClientPlayNetworking.send(new BanishRogueLootEntryPacket(
						this.generatedRogueLootTableIdentifier,
						this.itemStack,
						optionalIdentifier.get(),
						optionalBanishingCost
				));
			}
		}
	}

	private void reroll() {
		if (this.generatedRogueLoot.enableRerolling() && this.rerollAttempts != 0) {
			// TODO play sound?
			Optional<ItemStack> optionalRerollCost = Optional.empty();
			if (this.generatedRogueLoot.rerollCost().isPresent()) {
				optionalRerollCost = Optional.of(this.generatedRogueLoot.rerollCost().get().create());
			}
			ClientPlayNetworking.send(new RerollRogueLootScreenPacket(
					this.generatedRogueLootTableIdentifier,
					this.itemStack,
					optionalRerollCost
			));
		}
	}

	private void applySelection() {
		ClientPlayNetworking.send(new ApplyRogueLootSelectionPacket(
				getChosenRogueLootEntries(),
				this.generatedRogueLootTableIdentifier,
				this.itemStack
		));
	}

	private List<Identifier> getChosenRogueLootEntries() {
		List<Identifier> list = new ArrayList<>();
		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntryList = this.generatedRogueLoot.entryList();

		for (int i = 0; i < Math.min(6, visibleRogueLootTableEntryList.size()); i++) {
			if (this.selectedOptions[i]) {
				Optional<Identifier> optionalIdentifier = visibleRogueLootTableEntryList.get(i).parentRogueLootTableEntryId();
				if (optionalIdentifier.isPresent()) {
					list.add(optionalIdentifier.get());
				}
			}
		}
		return list;
	}
}
