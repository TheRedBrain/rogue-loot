package com.github.theredbrain.rogueloot.client.gui.screen.ingame;

import com.github.theredbrain.rogueloot.client.gui.components.LootOptionButton;
import com.github.theredbrain.rogueloot.config.ClientConfig;
import com.github.theredbrain.rogueloot.data.CommonDataStructures;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import com.github.theredbrain.rogueloot.data.RogueLootGuiSettings;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntryRarity;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.network.packet.RerollRogueLootScreenPacket;
import com.github.theredbrain.rogueloot.network.packet.ReserveRogueLootEntryPacket;
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

	private static final int MAX_OPTIONS_AMOUNT = 12;
	private final GeneratedRogueLoot generatedRogueLoot;
	private final GeneratedRogueLootData generatedRogueLootData;
	private final RogueLootGuiSettings rogueLootGuiSettings;
	private final int banishingAttempts;
	private final int rerollAttempts;
	private final int reservingAttempts;

	private Button selectLootOption0Button;
	private Button selectLootOption1Button;
	private Button selectLootOption2Button;
	private Button selectLootOption3Button;
	private Button selectLootOption4Button;
	private Button selectLootOption5Button;
	private Button selectLootOption6Button;
	private Button selectLootOption7Button;
	private Button selectLootOption8Button;
	private Button selectLootOption9Button;
	private Button selectLootOption10Button;
	private Button selectLootOption11Button;
	private Button rerollButton;
	private Button confirmButton;
	private CycleButton<Boolean> toggleBanishingButton;
	private CycleButton<Boolean> toggleReservingButton;

	private final boolean[] selectedOptions = new boolean[MAX_OPTIONS_AMOUNT];

	private int selectedOptionsAmount;
	private boolean banishingActive = false;
	private boolean reservingActive = false;

	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[0];
	private boolean requirementsNotMetLabelHasShadow;
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[0];
	private boolean requirementsMetLabelHasShadow;

	public RogueLootGUIScreen(final GeneratedRogueLoot generatedRogueLoot, final GeneratedRogueLootData generatedRogueLootData, final int rerollAttempts, final int banishingAttempts, final int reservingAttempts) {
		super(GameNarrator.NO_TITLE);
		this.generatedRogueLoot = generatedRogueLoot;
		this.rogueLootGuiSettings = this.generatedRogueLoot.rogueLootGuiSettings();
		this.generatedRogueLootData = generatedRogueLootData;
		this.rerollAttempts = rerollAttempts;
		this.banishingAttempts = banishingAttempts;
		this.reservingAttempts = reservingAttempts;
		Arrays.fill(this.selectedOptions, false);

	}

	private void selectLootOption(final Button button, final int index) {
		if (index >= MAX_OPTIONS_AMOUNT || index >= this.generatedRogueLoot.entryList().size()) {
			return;
		}
		ClientConfig clientConfig = RogueLootConfigs.CLIENT_CONFIG;
		if (this.banishingActive && this.generatedRogueLoot.banishing().isPresent() && this.generatedRogueLoot.banishing().get().enabled()) {
			if (this.generatedRogueLoot.entryList().get(index).allowBanishing() && this.banishingAttempts != 0) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_banishing_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.banishingActive = false;
				this.toggleBanishingButton.setValue(false);
				button.active = false;
				button.visible = clientConfig.show_banished_loot_options.get();
				this.banishEntry(index);
				// TODO play sound?
				this.updateWidgets();
			} else {
				// TODO play sound?
			}
		} else if (this.reservingActive && this.generatedRogueLoot.reserving().isPresent() && this.generatedRogueLoot.reserving().get().enabled()) {
			if (this.generatedRogueLoot.entryList().get(index).allowReserving() && this.reservingAttempts != 0) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_reserving_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.reservingActive = false;
				this.toggleReservingButton.setValue(false);
				button.active = false;
				button.visible = clientConfig.show_reserved_loot_options.get();
				this.reserveEntry(index);
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
				if (this.selectedOptionsAmount < this.generatedRogueLootData.modifiedMaxChoices()) {
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

	@Override
	protected void init() {

		super.init();

		int soloButtonX = 10;
		int firstButtonX = 10;
		int secondButtonX = this.width / 2 + 5;

		int firstRowY = 10;
		int secondRowY = this.height - 30;

		int soloButtonWidth = this.width - 20;
		int dualButtonWidth = (this.width - 30) / 2;

		// region reserving
		MutableComponent disableReservingButtonLabelComponent = Component.translatable("gui.rogue_loot_screen.reserve.stop");
		ChatFormatting[] disableReservingButtonLabelChatFormatting = new ChatFormatting[0];
		boolean disableReservingButtonLabelHasShadow = true;

		Optional<CommonDataStructures.LabelSettings> disableReserving = this.rogueLootGuiSettings.disableReserving();
		if (disableReserving.isPresent()) {
			disableReservingButtonLabelChatFormatting = new ChatFormatting[disableReserving.get().formatting().size()];
			for (int i = 0; i < disableReserving.get().formatting().size(); i++) {
				disableReservingButtonLabelChatFormatting[i] = disableReserving.get().formatting().get(i);
			}
			disableReservingButtonLabelHasShadow = disableReserving.get().hasShadow();
		}
		disableReservingButtonLabelComponent.withStyle(disableReservingButtonLabelChatFormatting);
		if (!disableReservingButtonLabelHasShadow) {
			disableReservingButtonLabelComponent.withoutShadow();
		}

		MutableComponent enableReservingButtonLabelComponent = this.reservingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.reserve.simple") : this.reservingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.reserve.singular", this.reservingAttempts) : Component.translatable("gui.rogue_loot_screen.reserve.plural", this.reservingAttempts);
		ChatFormatting[] enableReservingButtonLabelChatFormatting = new ChatFormatting[0];
		boolean enableReservingButtonLabelHasShadow = true;

		if (this.reservingAttempts == 0) {
			Optional<CommonDataStructures.LabelSettings> enableReservingRequirementsNotMet = this.rogueLootGuiSettings.enableReservingRequirementsNotMet();
			if (enableReservingRequirementsNotMet.isPresent()) {
				enableReservingButtonLabelChatFormatting = new ChatFormatting[enableReservingRequirementsNotMet.get().formatting().size()];
				for (int i = 0; i < enableReservingRequirementsNotMet.get().formatting().size(); i++) {
					enableReservingButtonLabelChatFormatting[i] = enableReservingRequirementsNotMet.get().formatting().get(i);
				}
				enableReservingButtonLabelHasShadow = enableReservingRequirementsNotMet.get().hasShadow();
			}
		} else {
			Optional<CommonDataStructures.LabelSettings> enableReservingRequirementsMet = this.rogueLootGuiSettings.enableReservingRequirementsMet();
			if (enableReservingRequirementsMet.isPresent()) {
				enableReservingButtonLabelChatFormatting = new ChatFormatting[enableReservingRequirementsMet.get().formatting().size()];
				for (int i = 0; i < enableReservingRequirementsMet.get().formatting().size(); i++) {
					enableReservingButtonLabelChatFormatting[i] = enableReservingRequirementsMet.get().formatting().get(i);
				}
				enableReservingButtonLabelHasShadow = enableReservingRequirementsMet.get().hasShadow();
			}
		}
		enableReservingButtonLabelComponent.withStyle(enableReservingButtonLabelChatFormatting);
		if (!enableReservingButtonLabelHasShadow) {
			enableReservingButtonLabelComponent.withoutShadow();
		}

		boolean banishingEnabled = this.generatedRogueLoot.banishing().isPresent() && this.generatedRogueLoot.banishing().get().enabled();
		this.toggleReservingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								disableReservingButtonLabelComponent,
								enableReservingButtonLabelComponent,
								this.reservingActive
						)
						.displayOnlyValue()
						.create(
								banishingEnabled ? firstButtonX : soloButtonX,
								firstRowY,
								banishingEnabled ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, reservingActive) -> {
									this.reservingActive = reservingActive;
									this.updateWidgets();
								}
						)
		);
		// endregion reserving

		// region banishing
		MutableComponent disableBanishingButtonLabelComponent = Component.translatable("gui.rogue_loot_screen.banish.stop");
		ChatFormatting[] disableBanishingButtonLabelChatFormatting = new ChatFormatting[0];
		boolean disableBanishingButtonLabelHasShadow = true;

		Optional<CommonDataStructures.LabelSettings> disableBanishing = this.rogueLootGuiSettings.disableBanishing();
		if (disableBanishing.isPresent()) {
			disableBanishingButtonLabelChatFormatting = new ChatFormatting[disableBanishing.get().formatting().size()];
			for (int i = 0; i < disableBanishing.get().formatting().size(); i++) {
				disableBanishingButtonLabelChatFormatting[i] = disableBanishing.get().formatting().get(i);
			}
			disableBanishingButtonLabelHasShadow = disableBanishing.get().hasShadow();
		}
		disableBanishingButtonLabelComponent.withStyle(disableBanishingButtonLabelChatFormatting);
		if (!disableBanishingButtonLabelHasShadow) {
			disableBanishingButtonLabelComponent.withoutShadow();
		}

		MutableComponent enableBanishingButtonLabelComponent = this.banishingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.banish.simple") : this.banishingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.banish.singular", this.banishingAttempts) : Component.translatable("gui.rogue_loot_screen.banish.plural", this.banishingAttempts);
		ChatFormatting[] enableBanishingButtonLabelChatFormatting = new ChatFormatting[0];
		boolean enableBanishingButtonLabelHasShadow = true;

		if (this.banishingAttempts == 0) {
			Optional<CommonDataStructures.LabelSettings> enableBanishingRequirementsNotMet = this.rogueLootGuiSettings.enableBanishingRequirementsNotMet();
			if (enableBanishingRequirementsNotMet.isPresent()) {
				enableBanishingButtonLabelChatFormatting = new ChatFormatting[enableBanishingRequirementsNotMet.get().formatting().size()];
				for (int i = 0; i < enableBanishingRequirementsNotMet.get().formatting().size(); i++) {
					enableBanishingButtonLabelChatFormatting[i] = enableBanishingRequirementsNotMet.get().formatting().get(i);
				}
				enableBanishingButtonLabelHasShadow = enableBanishingRequirementsNotMet.get().hasShadow();
			}
		} else {
			Optional<CommonDataStructures.LabelSettings> enableBanishingRequirementsMet = this.rogueLootGuiSettings.enableBanishingRequirementsMet();
			if (enableBanishingRequirementsMet.isPresent()) {
				enableBanishingButtonLabelChatFormatting = new ChatFormatting[enableBanishingRequirementsMet.get().formatting().size()];
				for (int i = 0; i < enableBanishingRequirementsMet.get().formatting().size(); i++) {
					enableBanishingButtonLabelChatFormatting[i] = enableBanishingRequirementsMet.get().formatting().get(i);
				}
				enableBanishingButtonLabelHasShadow = enableBanishingRequirementsMet.get().hasShadow();
			}
		}
		enableBanishingButtonLabelComponent.withStyle(enableBanishingButtonLabelChatFormatting);
		if (!enableBanishingButtonLabelHasShadow) {
			enableBanishingButtonLabelComponent.withoutShadow();
		}

		boolean reservingEnabled = this.generatedRogueLoot.reserving().isPresent() && this.generatedRogueLoot.reserving().get().enabled();
		this.toggleBanishingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								disableBanishingButtonLabelComponent,
								enableBanishingButtonLabelComponent,
								this.banishingActive
						)
						.displayOnlyValue()
						.create(
								reservingEnabled ? secondButtonX : soloButtonX,
								firstRowY,
								reservingEnabled ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, banishingActive) -> {
									this.banishingActive = banishingActive;
									this.updateWidgets();
								}
						)
		);
		// endregion banishing

		this.selectLootOption0Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 0)));
		this.selectLootOption1Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 1)));
		this.selectLootOption2Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 2)));
		this.selectLootOption3Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 3)));
		this.selectLootOption4Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 4)));
		this.selectLootOption5Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 5)));
		this.selectLootOption6Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 6)));
		this.selectLootOption7Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 7)));
		this.selectLootOption8Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 8)));
		this.selectLootOption9Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 9)));
		this.selectLootOption10Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 10)));
		this.selectLootOption11Button = this.addRenderableWidget(new LootOptionButton(this.font, 0, 0, 50, 50, button -> this.selectLootOption(button, 11)));

		// region rerolling
		MutableComponent rerollButtonLabelComponent = this.rerollAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.reroll.simple") : this.rerollAttempts == 0 ? Component.translatable("gui.rogue_loot_screen.reroll.plural", this.rerollAttempts) : this.rerollAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.reroll.singular", this.rerollAttempts) : Component.translatable("gui.rogue_loot_screen.reroll.plural", this.rerollAttempts);
		ChatFormatting[] rerollButtonLabelChatFormatting = new ChatFormatting[0];
		boolean rerollButtonLabelHasShadow = true;

		if (this.rerollAttempts == 0) {
			Optional<CommonDataStructures.LabelSettings> rerollRequirementsNotMet = this.rogueLootGuiSettings.rerollRequirementsNotMet();
			if (rerollRequirementsNotMet.isPresent()) {
				rerollButtonLabelChatFormatting = new ChatFormatting[rerollRequirementsNotMet.get().formatting().size()];
				for (int i = 0; i < rerollRequirementsNotMet.get().formatting().size(); i++) {
					rerollButtonLabelChatFormatting[i] = rerollRequirementsNotMet.get().formatting().get(i);
				}
				rerollButtonLabelHasShadow = rerollRequirementsNotMet.get().hasShadow();
			}
		} else {
			Optional<CommonDataStructures.LabelSettings> rerollRequirementsMet = this.rogueLootGuiSettings.rerollRequirementsMet();
			if (rerollRequirementsMet.isPresent()) {
				rerollButtonLabelChatFormatting = new ChatFormatting[rerollRequirementsMet.get().formatting().size()];
				for (int i = 0; i < rerollRequirementsMet.get().formatting().size(); i++) {
					rerollButtonLabelChatFormatting[i] = rerollRequirementsMet.get().formatting().get(i);
				}
				rerollButtonLabelHasShadow = rerollRequirementsMet.get().hasShadow();
			}
		}
		rerollButtonLabelComponent.withStyle(rerollButtonLabelChatFormatting);
		if (!rerollButtonLabelHasShadow) {
			rerollButtonLabelComponent.withoutShadow();
		}

		this.rerollButton = this.addRenderableWidget(
				Button.builder(rerollButtonLabelComponent, button -> this.reroll())
						.bounds(
								firstButtonX,
								secondRowY,
								dualButtonWidth,
								20
						)
						.build()
		);
		// endregion rerolling

		boolean rerollingEnabled = this.generatedRogueLoot.rerolling().isPresent() && this.generatedRogueLoot.rerolling().get().enabled();
		this.confirmButton = this.addRenderableWidget(
				Button.builder(Component.empty(), button -> this.confirmSelection())
						.bounds(
								rerollingEnabled ? secondButtonX : soloButtonX,
								secondRowY,
								rerollingEnabled ? dualButtonWidth : soloButtonWidth,
								20)
						.build()
		);

		this.initWidgets();
		this.updateWidgets();

		if (this.selectLootOption0Button.visible) {
			this.setInitialFocus(this.selectLootOption0Button);
		} else {
			super.setInitialFocus();
		}
	}

	private void initWidgets() {

		ClientConfig clientConfig = RogueLootConfigs.CLIENT_CONFIG;

		this.requirementsNotMetLabelFormatting = new ChatFormatting[0];
		Optional<CommonDataStructures.LabelSettings> confirmationRequirementsNotMet = this.rogueLootGuiSettings.confirmationRequirementsNotMet();
		if (confirmationRequirementsNotMet.isPresent()) {
			this.requirementsNotMetLabelFormatting = new ChatFormatting[confirmationRequirementsNotMet.get().formatting().size()];
			for (int i = 0; i < confirmationRequirementsNotMet.get().formatting().size(); i++) {
				this.requirementsNotMetLabelFormatting[i] = confirmationRequirementsNotMet.get().formatting().get(i);
			}
			this.requirementsNotMetLabelHasShadow = confirmationRequirementsNotMet.get().hasShadow();
		}

		this.requirementsMetLabelFormatting = new ChatFormatting[0];
		Optional<CommonDataStructures.LabelSettings> confirmationRequirementsMet = this.rogueLootGuiSettings.confirmationRequirementsMet();
		if (confirmationRequirementsMet.isPresent()) {
			this.requirementsMetLabelFormatting = new ChatFormatting[confirmationRequirementsMet.get().formatting().size()];
			for (int i = 0; i < confirmationRequirementsMet.get().formatting().size(); i++) {
				this.requirementsMetLabelFormatting[i] = confirmationRequirementsMet.get().formatting().get(i);
			}
			this.requirementsMetLabelHasShadow = confirmationRequirementsMet.get().hasShadow();
		}

		String entriesAssetPathAddition = "";
		if (!this.rogueLootGuiSettings.entriesAssetPathAddition().isEmpty()) {
			entriesAssetPathAddition = this.rogueLootGuiSettings.entriesAssetPathAddition() + "/";
		}

		this.rerollButton.visible = this.generatedRogueLoot.rerolling().isPresent() && this.generatedRogueLoot.rerolling().get().enabled();
		this.rerollButton.active = this.rerollAttempts != 0;

		this.toggleBanishingButton.visible = this.generatedRogueLoot.banishing().isPresent() && this.generatedRogueLoot.banishing().get().enabled();
		this.toggleBanishingButton.active = this.banishingAttempts != 0;

		this.toggleReservingButton.visible = this.generatedRogueLoot.reserving().isPresent() && this.generatedRogueLoot.reserving().get().enabled();
		this.toggleReservingButton.active = this.reservingAttempts != 0;

		int visibleOptionsAmount = Math.min(this.generatedRogueLoot.entryList().size(), MAX_OPTIONS_AMOUNT);

		int[][] buttonPositions = getButtonPositions(visibleOptionsAmount);

		for (int i = 0; i < MAX_OPTIONS_AMOUNT; i++) {
			Button button = this.getLootOptionButton(i);

			if (i < buttonPositions.length) {
				button.setPosition(buttonPositions[i][0], buttonPositions[i][1]);
			} else {
				button.setPosition(0, 0);
			}
			button.visible = false;

			if (visibleOptionsAmount >= i + 1) {
				this.initLootOptionButton(this.getLootOptionButton(i), this.generatedRogueLoot.entryList().get(i), entriesAssetPathAddition, clientConfig.show_reserved_loot_options.get(), clientConfig.show_banished_loot_options.get());
			}
		}
	}

	private Button getLootOptionButton(int index) {
		return switch (index) {
			case 1 -> this.selectLootOption1Button;
			case 2 -> this.selectLootOption2Button;
			case 3 -> this.selectLootOption3Button;
			case 4 -> this.selectLootOption4Button;
			case 5 -> this.selectLootOption5Button;
			case 6 -> this.selectLootOption6Button;
			case 7 -> this.selectLootOption7Button;
			case 8 -> this.selectLootOption8Button;
			case 9 -> this.selectLootOption9Button;
			case 10 -> this.selectLootOption10Button;
			case 11 -> this.selectLootOption11Button;
			default -> this.selectLootOption0Button;
		};
	}

	private void initLootOptionButton(Button lootoptionButton, GeneratedRogueLoot.VisibleRogueLootTableEntry visibleRogueLootTableEntry, String entriesAssetPathAddition, boolean showReservedLootOptions, boolean showBanishedLootOptions) {

		Optional<RogueLootTableEntryRarity> optionalRarity = visibleRogueLootTableEntry.rogueLootTableEntryRarity();

		String assetPathAddition0 = "";
		if (optionalRarity.isPresent() && !optionalRarity.get().assetPathAddition().isEmpty()) {
			assetPathAddition0 = optionalRarity.get().assetPathAddition() + "/";
		}
		((LootOptionButton) lootoptionButton).setTextureIds(entriesAssetPathAddition + assetPathAddition0);
		if (visibleRogueLootTableEntry.display().isPresent()) {
			((LootOptionButton) lootoptionButton).setDisplayStack(visibleRogueLootTableEntry.display().get().displayStack());
			((LootOptionButton) lootoptionButton).setIconTexture(visibleRogueLootTableEntry.display().get().iconTextureIdentifier());
			String labelText = visibleRogueLootTableEntry.display().get().labelText();
			if (!labelText.isEmpty() && optionalRarity.isPresent()) {

				MutableComponent reservedMessage = Component.translatable(labelText);
				Optional<CommonDataStructures.LabelSettings> reservedLabel = optionalRarity.get().reservedLabel();
				if (reservedLabel.isPresent()) {
					ChatFormatting[] reservedLabelFormatting = new ChatFormatting[reservedLabel.get().formatting().size()];
					for (int i = 0; i < reservedLabel.get().formatting().size(); i++) {
						reservedLabelFormatting[i] = reservedLabel.get().formatting().get(i);
					}
					reservedMessage.withStyle(reservedLabelFormatting);
					if (!reservedLabel.get().hasShadow()) {
						reservedMessage.withoutShadow();
					}
				}
				((LootOptionButton) lootoptionButton).setReservedMessage(reservedMessage);

				MutableComponent banishedMessage = Component.translatable(labelText);
				Optional<CommonDataStructures.LabelSettings> banishedLabelSettings = optionalRarity.get().banishedLabel();
				if (banishedLabelSettings.isPresent()) {
					ChatFormatting[] banishedLabelFormatting = new ChatFormatting[banishedLabelSettings.get().formatting().size()];
					for (int i = 0; i < banishedLabelSettings.get().formatting().size(); i++) {
						banishedLabelFormatting[i] = banishedLabelSettings.get().formatting().get(i);
					}
					banishedMessage.withStyle(banishedLabelFormatting);
					if (!banishedLabelSettings.get().hasShadow()) {
						banishedMessage.withoutShadow();
					}
				}
				((LootOptionButton) lootoptionButton).setBanishedMessage(banishedMessage);

				MutableComponent defaultMessage = Component.translatable(labelText);
				Optional<CommonDataStructures.LabelSettings> defaultLabelSettings = optionalRarity.get().defaultLabel();
				if (defaultLabelSettings.isPresent()) {
					ChatFormatting[] defaultLabelFormatting = new ChatFormatting[defaultLabelSettings.get().formatting().size()];
					for (int i = 0; i < defaultLabelSettings.get().formatting().size(); i++) {
						defaultLabelFormatting[i] = defaultLabelSettings.get().formatting().get(i);
					}
					defaultMessage.withStyle(defaultLabelFormatting);
					if (!defaultLabelSettings.get().hasShadow()) {
						defaultMessage.withoutShadow();
					}
				}
				((LootOptionButton) lootoptionButton).setDefaultMessage(defaultMessage);

				MutableComponent selectedMessage = Component.translatable(labelText);
				Optional<CommonDataStructures.LabelSettings> selectedLabelSettings = optionalRarity.get().selectedLabel();
				if (selectedLabelSettings.isPresent()) {
					ChatFormatting[] selectedLabelFormatting = new ChatFormatting[selectedLabelSettings.get().formatting().size()];
					for (int i = 0; i < selectedLabelSettings.get().formatting().size(); i++) {
						selectedLabelFormatting[i] = selectedLabelSettings.get().formatting().get(i);
					}
					selectedMessage.withStyle(selectedLabelFormatting);
					if (!selectedLabelSettings.get().hasShadow()) {
						selectedMessage.withoutShadow();
					}
				}
				((LootOptionButton) lootoptionButton).setSelectedMessage(selectedMessage);
			}
		}

		boolean entryIsReserved = DataAttachmentHelper.isRogueLootEntryReserved(this.minecraft.player, visibleRogueLootTableEntry.parentRogueLootTableEntryId());
		boolean entryIsBanished = DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, visibleRogueLootTableEntry.parentRogueLootTableEntryId());
		((LootOptionButton) lootoptionButton).setReserved(entryIsReserved);
		if (!entryIsReserved && !entryIsBanished) {
			lootoptionButton.visible = true;
		} else {
			lootoptionButton.active = false;
			lootoptionButton.visible = (entryIsReserved && showReservedLootOptions) || (entryIsBanished && showBanishedLootOptions);
		}

		List<Component> list = new ArrayList<>();

		ItemStack tooltipStack = ItemStack.EMPTY;
		Optional<CommonDataStructures.RogueLootTableEntryTooltip> optionalRogueLootTableEntryTooltip = visibleRogueLootTableEntry.tooltip();
		if (optionalRogueLootTableEntryTooltip.isPresent()) {
			if (optionalRogueLootTableEntryTooltip.get().tooltipStack().isPresent()) {
				tooltipStack = optionalRogueLootTableEntryTooltip.get().tooltipStack().get().create();
			}
			if (!tooltipStack.isEmpty()) {
				list.addAll(Screen.getTooltipFromItem(this.minecraft, tooltipStack));
			}

			if (!optionalRogueLootTableEntryTooltip.get().tooltipLabelLines().isEmpty()) {
				for (String string : optionalRogueLootTableEntryTooltip.get().tooltipLabelLines()) {
					list.add(Component.translatable(string));
				}
			}
		}

		if (visibleRogueLootTableEntry.usesLeft() > 0) {
			if (!list.isEmpty()) {
				list.add(Component.empty());
			}
			if (visibleRogueLootTableEntry.usesLeft() > 1) {
				list.add(Component.translatable("gui.rogue_loot_screen.tooltip.uses_left.plural", visibleRogueLootTableEntry.usesLeft()));
			} else {
				list.add(Component.translatable("gui.rogue_loot_screen.tooltip.uses_left.singular", visibleRogueLootTableEntry.usesLeft()));
			}
		}
		if (!list.isEmpty()) {
			((LootOptionButton) lootoptionButton).setTooltipLines(list);
		}
		if (optionalRarity.isPresent() && optionalRarity.get().tooltipStyle().isPresent()) {
			((LootOptionButton) lootoptionButton).setTooltipStyle(optionalRarity.get().tooltipStyle().get());
		}
	}

	private int[][] getButtonPositions(final int visibleOptionsAmount) {

		// x
		int odd_columns_1 = this.width / 2 - 193;
		int odd_columns_2 = this.width / 2 - 137;
		int odd_columns_3 = this.width / 2 - 81;
		int odd_columns_4 = this.width / 2 - 25;
		int odd_columns_5 = this.width / 2 + 31;
		int odd_columns_6 = this.width / 2 + 87;
		int odd_columns_7 = this.width / 2 + 143;

		int even_columns_1 = this.width / 2 - 165;
		int even_columns_2 = this.width / 2 - 109;
		int even_columns_3 = this.width / 2 - 53;
		int even_columns_4 = this.width / 2 + 3;
		int even_columns_5 = this.width / 2 + 59;
		int even_columns_6 = this.width / 2 + 115;

		// y
		int odd_rows_1 = this.height / 2 - 81;
		int odd_rows_2 = this.height / 2 - 25;
		int odd_rows_3 = this.height / 2 + 31;

		int even_rows_1 = this.height / 2 - 53;
		int even_rows_2 = this.height / 2 + 3;

		return switch (visibleOptionsAmount) {
			case 1 -> new int[][]{
					{odd_columns_4, odd_rows_2}
			};
			case 2 -> new int[][]{
					{even_columns_3, odd_rows_2}, {even_columns_4, odd_rows_2}
			};
			case 3 -> new int[][]{
					{odd_columns_3, odd_rows_2}, {odd_columns_4, odd_rows_2}, {odd_columns_5, odd_rows_2}
			};
			case 4 -> new int[][]{
					{even_columns_3, even_rows_1}, {even_columns_4, even_rows_1},
					{even_columns_3, even_rows_2}, {even_columns_4, even_rows_2}
			};
			case 5 -> new int[][]{
					{odd_columns_3, even_rows_1}, {odd_columns_4, even_rows_1}, {odd_columns_5, even_rows_1},
					{even_columns_3, even_rows_2}, {even_columns_4, even_rows_2}
			};
			case 6 -> new int[][]{
					{odd_columns_3, even_rows_1}, {odd_columns_4, even_rows_1}, {odd_columns_5, even_rows_1},
					{odd_columns_3, even_rows_2}, {odd_columns_4, even_rows_2}, {odd_columns_5, even_rows_2}
			};
			case 7 -> new int[][]{
					{odd_columns_3, even_rows_1}, {odd_columns_4, even_rows_1}, {odd_columns_5, even_rows_1},
					{even_columns_2, even_rows_2}, {even_columns_3, even_rows_2}, {even_columns_4, even_rows_2}, {even_columns_5, even_rows_2}
			};
			case 8 -> new int[][]{
					{even_columns_2, even_rows_1}, {even_columns_3, even_rows_1}, {even_columns_4, even_rows_1}, {even_columns_5, even_rows_1},
					{even_columns_2, even_rows_2}, {even_columns_3, even_rows_2}, {even_columns_4, even_rows_2}, {even_columns_5, even_rows_2}
			};
			case 9 -> new int[][]{
					{odd_columns_3, odd_rows_1}, {odd_columns_4, odd_rows_1}, {odd_columns_5, odd_rows_1},
					{odd_columns_3, odd_rows_2}, {odd_columns_4, odd_rows_2}, {odd_columns_5, odd_rows_2},
					{odd_columns_3, odd_rows_3}, {odd_columns_4, odd_rows_3}, {odd_columns_5, odd_rows_3}
			};
			case 10 -> new int[][]{
					{odd_columns_3, odd_rows_1}, {odd_columns_4, odd_rows_1}, {odd_columns_5, odd_rows_1},
					{even_columns_2, odd_rows_2}, {even_columns_3, odd_rows_2}, {even_columns_4, odd_rows_2}, {even_columns_5, odd_rows_2},
					{odd_columns_3, odd_rows_3}, {odd_columns_4, odd_rows_3}, {odd_columns_5, odd_rows_3}
			};
			case 11 -> new int[][]{
					{even_columns_2, odd_rows_1}, {even_columns_3, odd_rows_1}, {even_columns_4, odd_rows_1}, {even_columns_5, odd_rows_1},
					{odd_columns_3, odd_rows_2}, {odd_columns_4, odd_rows_2}, {odd_columns_5, odd_rows_2},
					{even_columns_2, odd_rows_3}, {even_columns_3, odd_rows_3}, {even_columns_4, odd_rows_3}, {even_columns_5, odd_rows_3}
			};
			case 12 -> new int[][]{
					{even_columns_2, odd_rows_1}, {even_columns_3, odd_rows_1}, {even_columns_4, odd_rows_1}, {even_columns_5, odd_rows_1},
					{even_columns_2, odd_rows_2}, {even_columns_3, odd_rows_2}, {even_columns_4, odd_rows_2}, {even_columns_5, odd_rows_2},
					{even_columns_2, odd_rows_3}, {even_columns_3, odd_rows_3}, {even_columns_4, odd_rows_3}, {even_columns_5, odd_rows_3}
			};
			default -> new int[][]{};
		};
	}

	private void updateWidgets() {

		this.selectedOptionsAmount = countSelectedOptions();

		this.confirmButton.setMessage(getConfirmChoicesButtonLabel());

		this.confirmButton.active = this.selectedOptionsAmount <= this.generatedRogueLootData.modifiedMaxChoices() && this.selectedOptionsAmount >= this.generatedRogueLootData.modifiedMinChoices() && !this.banishingActive && !this.reservingActive;

	}

	private Component getConfirmChoicesButtonLabel() {
		MutableComponent confirmChoicesButtonLabel = Component.empty();

		int minChoices = this.generatedRogueLootData.modifiedMinChoices();
		int maxChoices = this.generatedRogueLootData.modifiedMaxChoices();
		if (minChoices > maxChoices) {
			return confirmChoicesButtonLabel;
		}
		confirmChoicesButtonLabel = minChoices < maxChoices ? Component.translatable("gui.rogue_loot_screen.confirm_choices.range", this.selectedOptionsAmount, minChoices, maxChoices) :
				Component.translatable(minChoices == 1 ? "gui.rogue_loot_screen.confirm_choices.singular" : "gui.rogue_loot_screen.confirm_choices.plural", this.selectedOptionsAmount, minChoices);

		if (this.selectedOptionsAmount < minChoices) {
			confirmChoicesButtonLabel.withStyle(this.requirementsNotMetLabelFormatting);
			if (!this.requirementsNotMetLabelHasShadow) {
				confirmChoicesButtonLabel.withoutShadow();
			}
		} else {
			confirmChoicesButtonLabel.withStyle(this.requirementsMetLabelFormatting);
			if (!this.requirementsMetLabelHasShadow) {
				confirmChoicesButtonLabel.withoutShadow();
			}
		}

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
		boolean banishingActive = this.banishingActive;
		boolean reservingActive = this.reservingActive;
		this.init(width, height);
		((LootOptionButton) this.selectLootOption0Button).setSelected(selected0);
		((LootOptionButton) this.selectLootOption1Button).setSelected(selected1);
		((LootOptionButton) this.selectLootOption2Button).setSelected(selected2);
		((LootOptionButton) this.selectLootOption3Button).setSelected(selected3);
		((LootOptionButton) this.selectLootOption4Button).setSelected(selected4);
		((LootOptionButton) this.selectLootOption5Button).setSelected(selected5);
		this.banishingActive = banishingActive;
		this.toggleBanishingButton.setValue(this.banishingActive);
		this.reservingActive = reservingActive;
		this.toggleReservingButton.setValue(this.reservingActive);
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
		if (this.generatedRogueLoot.banishing().isPresent() && this.generatedRogueLoot.banishing().get().enabled() && index < this.generatedRogueLoot.entryList().size()) {
			Optional<ItemStack> optionalBanishingCost = Optional.empty();
			if (this.generatedRogueLoot.banishing().get().optionalCost().isPresent()) {
				optionalBanishingCost = Optional.of(this.generatedRogueLoot.banishing().get().optionalCost().get().create());
			}
			Optional<Identifier> optionalIdentifier = this.generatedRogueLoot.entryList().get(index).parentRogueLootTableEntryId();
			if (optionalIdentifier.isPresent()) {
				ClientPlayNetworking.send(new BanishRogueLootEntryPacket(
						this.generatedRogueLootData,
						optionalIdentifier.get(),
						optionalBanishingCost
				));
			}
		}
	}

	private void reserveEntry(final int index) {
		if (this.generatedRogueLoot.reserving().isPresent() && this.generatedRogueLoot.reserving().get().enabled() && index < this.generatedRogueLoot.entryList().size()) {
			Optional<ItemStack> optionalReservingCost = Optional.empty();
			if (this.generatedRogueLoot.reserving().get().optionalCost().isPresent()) {
				optionalReservingCost = Optional.of(this.generatedRogueLoot.reserving().get().optionalCost().get().create());
			}
			Optional<Identifier> optionalIdentifier = this.generatedRogueLoot.entryList().get(index).parentRogueLootTableEntryId();
			if (optionalIdentifier.isPresent()) {
				ClientPlayNetworking.send(new ReserveRogueLootEntryPacket(
						this.generatedRogueLootData,
						optionalIdentifier.get(),
						optionalReservingCost
				));
			}
		}
	}

	private void reroll() {
		if (this.generatedRogueLoot.rerolling().isPresent() && this.generatedRogueLoot.rerolling().get().enabled() && this.rerollAttempts != 0) {
			// TODO play sound?
			Optional<ItemStack> optionalRerollCost = Optional.empty();
			if (this.generatedRogueLoot.rerolling().get().optionalCost().isPresent()) {
				optionalRerollCost = Optional.of(this.generatedRogueLoot.rerolling().get().optionalCost().get().create());
			}
			ClientPlayNetworking.send(new RerollRogueLootScreenPacket(
					this.generatedRogueLootData,
					optionalRerollCost
			));
		}
	}

	private void confirmSelection() {
		ClientPlayNetworking.send(new ApplyRogueLootSelectionPacket(
				getChosenRogueLootEntries(),
				this.generatedRogueLootData
		));
		this.onClose();
	}

	private List<Identifier> getChosenRogueLootEntries() {
		List<Identifier> list = new ArrayList<>();
		List<GeneratedRogueLoot.VisibleRogueLootTableEntry> visibleRogueLootTableEntryList = this.generatedRogueLoot.entryList();

		for (int i = 0; i < Math.min(MAX_OPTIONS_AMOUNT, visibleRogueLootTableEntryList.size()); i++) {
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
