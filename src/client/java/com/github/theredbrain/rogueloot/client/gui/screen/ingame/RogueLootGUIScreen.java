package com.github.theredbrain.rogueloot.client.gui.screen.ingame;

import com.github.theredbrain.rogueloot.client.gui.components.LootOptionButton;
import com.github.theredbrain.rogueloot.config.ClientConfig;
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
	private boolean banishingEnabled = false;
	private boolean reservingEnabled = false;

	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[]{};
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[]{};

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
		if (this.banishingEnabled && this.generatedRogueLoot.enableEntryBanishing()) {
			if (this.generatedRogueLoot.entryList().get(index).allowBanishing() && this.banishingAttempts != 0) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_banishing_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.banishingEnabled = false;
				this.toggleBanishingButton.setValue(false);
				button.active = false;
				button.visible = clientConfig.show_banished_loot_options.get();
				this.banishEntry(index);
				// TODO play sound?
				this.updateWidgets();
			} else {
				// TODO play sound?
			}
		} else if (this.reservingEnabled && this.generatedRogueLoot.enableEntryReserving()) {
			if (this.generatedRogueLoot.entryList().get(index).allowReserving() && this.reservingAttempts != 0) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_reserving_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.reservingEnabled = false;
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
		ChatFormatting[] disableReservingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.disableReserving().formatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.disableReserving().formatting().size(); i++) {
			disableReservingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.disableReserving().formatting().get(i);
		}
		boolean disableReservingButtonLabelHasShadow = this.rogueLootGuiSettings.disableReserving().hasShadow();
		disableReservingButtonLabelComponent.withStyle(disableReservingButtonLabelChatFormatting);
		if (!disableReservingButtonLabelHasShadow) {
			disableReservingButtonLabelComponent.withoutShadow();
		}

		MutableComponent enableReservingButtonLabelComponent = this.reservingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.reserve.simple") : this.reservingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.reserve.singular", this.reservingAttempts) : Component.translatable("gui.rogue_loot_screen.reserve.plural", this.reservingAttempts);
		ChatFormatting[] enableReservingButtonLabelChatFormatting;
		boolean enableReservingButtonLabelHasShadow;

		if (this.reservingAttempts == 0) {
			enableReservingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.enableReservingRequirementsNotMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.enableReservingRequirementsNotMet().formatting().size(); i++) {
				enableReservingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.enableReservingRequirementsNotMet().formatting().get(i);
			}
			enableReservingButtonLabelHasShadow = this.rogueLootGuiSettings.enableReservingRequirementsNotMet().hasShadow();
		} else {
			enableReservingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.enableReservingRequirementsMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.enableReservingRequirementsMet().formatting().size(); i++) {
				enableReservingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.enableReservingRequirementsMet().formatting().get(i);
			}
			enableReservingButtonLabelHasShadow = this.rogueLootGuiSettings.enableReservingRequirementsMet().hasShadow();
		}
		enableReservingButtonLabelComponent.withStyle(enableReservingButtonLabelChatFormatting);
		if (!enableReservingButtonLabelHasShadow) {
			enableReservingButtonLabelComponent.withoutShadow();
		}

		this.toggleReservingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								disableReservingButtonLabelComponent,
								enableReservingButtonLabelComponent,
								this.reservingEnabled
						)
						.displayOnlyValue()
						.create(
								this.generatedRogueLoot.enableEntryBanishing() ? firstButtonX : soloButtonX,
								firstRowY,
								this.generatedRogueLoot.enableEntryBanishing() ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, reservingEnabled) -> {
									this.reservingEnabled = reservingEnabled;
									this.updateWidgets();
								}
						)
		);
		// endregion reserving

		// region banishing
		MutableComponent disableBanishingButtonLabelComponent = Component.translatable("gui.rogue_loot_screen.banish.stop");
		ChatFormatting[] disableBanishingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.disableBanishing().formatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.disableBanishing().formatting().size(); i++) {
			disableBanishingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.disableBanishing().formatting().get(i);
		}
		boolean disableBanishingButtonLabelHasShadow = this.rogueLootGuiSettings.disableBanishing().hasShadow();
		disableBanishingButtonLabelComponent.withStyle(disableBanishingButtonLabelChatFormatting);
		if (!disableBanishingButtonLabelHasShadow) {
			disableBanishingButtonLabelComponent.withoutShadow();
		}

		MutableComponent enableBanishingButtonLabelComponent = this.banishingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.banish.simple") : this.banishingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.banish.singular", this.banishingAttempts) : Component.translatable("gui.rogue_loot_screen.banish.plural", this.banishingAttempts);
		ChatFormatting[] enableBanishingButtonLabelChatFormatting;
		boolean enableBanishingButtonLabelHasShadow;

		if (this.banishingAttempts == 0) {
			enableBanishingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.enableBanishingRequirementsNotMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.enableBanishingRequirementsNotMet().formatting().size(); i++) {
				enableBanishingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.enableBanishingRequirementsNotMet().formatting().get(i);
			}
			enableBanishingButtonLabelHasShadow = this.rogueLootGuiSettings.enableBanishingRequirementsNotMet().hasShadow();
		} else {
			enableBanishingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.enableBanishingRequirementsMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.enableBanishingRequirementsMet().formatting().size(); i++) {
				enableBanishingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.enableBanishingRequirementsMet().formatting().get(i);
			}
			enableBanishingButtonLabelHasShadow = this.rogueLootGuiSettings.enableBanishingRequirementsMet().hasShadow();
		}
		enableBanishingButtonLabelComponent.withStyle(enableBanishingButtonLabelChatFormatting);
		if (!enableBanishingButtonLabelHasShadow) {
			enableBanishingButtonLabelComponent.withoutShadow();
		}

		this.toggleBanishingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								disableBanishingButtonLabelComponent,
								enableBanishingButtonLabelComponent,
								this.banishingEnabled
						)
						.displayOnlyValue()
						.create(
								this.generatedRogueLoot.enableEntryReserving() ? secondButtonX : soloButtonX,
								firstRowY,
								this.generatedRogueLoot.enableEntryReserving() ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, banishingEnabled) -> {
									this.banishingEnabled = banishingEnabled;
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
		ChatFormatting[] rerollButtonLabelChatFormatting;
		boolean rerollButtonLabelHasShadow;

		if (this.rerollAttempts == 0) {
			rerollButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.rerollRequirementsNotMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.rerollRequirementsNotMet().formatting().size(); i++) {
				rerollButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.rerollRequirementsNotMet().formatting().get(i);
			}
			rerollButtonLabelHasShadow = this.rogueLootGuiSettings.rerollRequirementsNotMet().hasShadow();
		} else {
			rerollButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.rerollRequirementsMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.rerollRequirementsMet().formatting().size(); i++) {
				rerollButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.rerollRequirementsMet().formatting().get(i);
			}
			rerollButtonLabelHasShadow = this.rogueLootGuiSettings.rerollRequirementsMet().hasShadow();
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

		this.confirmButton = this.addRenderableWidget(
				Button.builder(Component.empty(), button -> this.confirmSelection())
						.bounds(
								this.generatedRogueLoot.enableRerolling() ? secondButtonX : soloButtonX,
								secondRowY,
								this.generatedRogueLoot.enableRerolling() ? dualButtonWidth : soloButtonWidth,
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

		this.requirementsNotMetLabelFormatting = new ChatFormatting[this.rogueLootGuiSettings.confirmationRequirementsNotMet().formatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.confirmationRequirementsNotMet().formatting().size(); i++) {
			this.requirementsNotMetLabelFormatting[i] = this.rogueLootGuiSettings.confirmationRequirementsNotMet().formatting().get(i);
		}
		this.requirementsMetLabelFormatting = new ChatFormatting[this.rogueLootGuiSettings.confirmationRequirementsMet().formatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.confirmationRequirementsMet().formatting().size(); i++) {
			this.requirementsMetLabelFormatting[i] = this.rogueLootGuiSettings.confirmationRequirementsMet().formatting().get(i);
		}

		String entriesAssetPathAddition = "";
		if (!this.rogueLootGuiSettings.entriesAssetPathAddition().isEmpty()) {
			entriesAssetPathAddition = this.rogueLootGuiSettings.entriesAssetPathAddition() + "/";
		}

		this.rerollButton.visible = this.generatedRogueLoot.enableRerolling();
		this.rerollButton.active = this.rerollAttempts != 0;

		this.toggleBanishingButton.visible = this.generatedRogueLoot.enableEntryBanishing();
		this.toggleBanishingButton.active = this.banishingAttempts != 0;

		this.toggleReservingButton.visible = this.generatedRogueLoot.enableEntryReserving();
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

		RogueLootTableEntryRarity rogueLootTableEntryRarity = visibleRogueLootTableEntry.rogueLootTableEntryRarity();

		String assetPathAddition0 = "";
		if (!rogueLootTableEntryRarity.assetPathAddition().isEmpty()) {
			assetPathAddition0 = rogueLootTableEntryRarity.assetPathAddition() + "/";
		}
		((LootOptionButton) lootoptionButton).setTextureIds(entriesAssetPathAddition + assetPathAddition0);
		((LootOptionButton) lootoptionButton).setDisplayStack(visibleRogueLootTableEntry.displayStack());
		((LootOptionButton) lootoptionButton).setIconTexture(visibleRogueLootTableEntry.iconTextureIdentifier());
		if (!visibleRogueLootTableEntry.labelText().isEmpty()) {
			ChatFormatting[] reservedLabelFormatting = new ChatFormatting[rogueLootTableEntryRarity.reservedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity.reservedLabel().formatting().size(); i++) {
				reservedLabelFormatting[i] = rogueLootTableEntryRarity.reservedLabel().formatting().get(i);
			}
			MutableComponent reservedMessage = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(reservedLabelFormatting);
			if (!rogueLootTableEntryRarity.reservedLabel().hasShadow()) {
				reservedMessage.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setReservedMessage(reservedMessage);

			ChatFormatting[] banishedLabelFormatting = new ChatFormatting[rogueLootTableEntryRarity.banishedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity.banishedLabel().formatting().size(); i++) {
				banishedLabelFormatting[i] = rogueLootTableEntryRarity.banishedLabel().formatting().get(i);
			}
			MutableComponent banishedMessage = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(banishedLabelFormatting);
			if (!rogueLootTableEntryRarity.banishedLabel().hasShadow()) {
				banishedMessage.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setBanishedMessage(banishedMessage);

			ChatFormatting[] defaultLabelFormatting = new ChatFormatting[rogueLootTableEntryRarity.defaultLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity.defaultLabel().formatting().size(); i++) {
				defaultLabelFormatting[i] = rogueLootTableEntryRarity.defaultLabel().formatting().get(i);
			}
			MutableComponent defaultMessage = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(defaultLabelFormatting);
			if (!rogueLootTableEntryRarity.defaultLabel().hasShadow()) {
				defaultMessage.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setDefaultMessage(defaultMessage);

			ChatFormatting[] selectedLabelFormatting = new ChatFormatting[rogueLootTableEntryRarity.selectedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity.selectedLabel().formatting().size(); i++) {
				selectedLabelFormatting[i] = rogueLootTableEntryRarity.selectedLabel().formatting().get(i);
			}
			MutableComponent selectedMessage = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(selectedLabelFormatting);
			if (!rogueLootTableEntryRarity.selectedLabel().hasShadow()) {
				selectedMessage.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setSelectedMessage(selectedMessage);
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
		if (visibleRogueLootTableEntry.tooltipStack().isPresent()) {
			tooltipStack = visibleRogueLootTableEntry.tooltipStack().get().create();
		}
		if (!tooltipStack.isEmpty()) {
			list.addAll(Screen.getTooltipFromItem(this.minecraft, tooltipStack));
		}

		if (!visibleRogueLootTableEntry.tooltipLabelLines().isEmpty()) {
			for (String string : visibleRogueLootTableEntry.tooltipLabelLines()) {
				list.add(Component.translatable(string));
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
		if (rogueLootTableEntryRarity.tooltipStyle().isPresent()) {
			((LootOptionButton) lootoptionButton).setTooltipStyle(rogueLootTableEntryRarity.tooltipStyle().get());
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

		this.confirmButton.setMessage(getConfirmChoicesButonLabel());

		this.confirmButton.active = this.selectedOptionsAmount <= this.generatedRogueLootData.modifiedMaxChoices() && this.selectedOptionsAmount >= this.generatedRogueLootData.modifiedMinChoices() && !this.banishingEnabled && !this.reservingEnabled;

	}

	private Component getConfirmChoicesButonLabel() {
		MutableComponent confirmChoicesButtonLabel = Component.empty();

		int minChoices = this.generatedRogueLootData.modifiedMinChoices();
		int maxChoices = this.generatedRogueLootData.modifiedMaxChoices();
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
		boolean reservingEnabled = this.reservingEnabled;
		this.init(width, height);
		((LootOptionButton) this.selectLootOption0Button).setSelected(selected0);
		((LootOptionButton) this.selectLootOption1Button).setSelected(selected1);
		((LootOptionButton) this.selectLootOption2Button).setSelected(selected2);
		((LootOptionButton) this.selectLootOption3Button).setSelected(selected3);
		((LootOptionButton) this.selectLootOption4Button).setSelected(selected4);
		((LootOptionButton) this.selectLootOption5Button).setSelected(selected5);
		this.banishingEnabled = banishingEnabled;
		this.toggleBanishingButton.setValue(this.banishingEnabled);
		this.reservingEnabled = reservingEnabled;
		this.toggleReservingButton.setValue(this.reservingEnabled);
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
						this.generatedRogueLootData,
						optionalIdentifier.get(),
						optionalBanishingCost
				));
			}
		}
	}

	private void reserveEntry(final int index) {
		if (this.generatedRogueLoot.enableEntryReserving() && index < this.generatedRogueLoot.entryList().size()) {
			Optional<ItemStack> optionalReservingCost = Optional.empty();
			if (this.generatedRogueLoot.reservingCost().isPresent()) {
				optionalReservingCost = Optional.of(this.generatedRogueLoot.reservingCost().get().create());
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
		if (this.generatedRogueLoot.enableRerolling() && this.rerollAttempts != 0) {
			// TODO play sound?
			Optional<ItemStack> optionalRerollCost = Optional.empty();
			if (this.generatedRogueLoot.rerollCost().isPresent()) {
				optionalRerollCost = Optional.of(this.generatedRogueLoot.rerollCost().get().create());
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
