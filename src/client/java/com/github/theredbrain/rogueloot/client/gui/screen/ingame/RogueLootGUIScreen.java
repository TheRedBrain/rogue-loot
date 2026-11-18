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
import com.github.theredbrain.rogueloot.network.packet.LockRogueLootEntryPacket;
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

	private static final int MAX_OPTIONS_AMOUNT = 12;
	private final GeneratedRogueLoot generatedRogueLoot;
	private final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier;
	private final RogueLootGuiSettings rogueLootGuiSettings;
	private final int banishingAttempts;
	private final int rerollAttempts;
	private final int lockingAttempts;
	private final Optional<ItemStack> itemStack;

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
	private CycleButton<Boolean> toggleLockingButton;

	private final boolean[] selectedOptions = new boolean[MAX_OPTIONS_AMOUNT];

	private int selectedOptionsAmount;
	private boolean banishingEnabled = false;
	private boolean lockingEnabled = false;

	private ChatFormatting[] requirementsNotMetLabelFormatting = new ChatFormatting[]{};
	private ChatFormatting[] requirementsMetLabelFormatting = new ChatFormatting[]{};

	public RogueLootGUIScreen(final GeneratedRogueLoot generatedRogueLoot, final GeneratedRogueLootTableIdentifier generatedRogueLootTableIdentifier, final int rerollAttempts, final int banishingAttempts, final int lockingAttempts, final Optional<ItemStack> itemStack) {
		super(GameNarrator.NO_TITLE);
		this.generatedRogueLoot = generatedRogueLoot;
		this.rogueLootGuiSettings = this.generatedRogueLoot.rogueLootGuiSettings();
		this.generatedRogueLootTableIdentifier = generatedRogueLootTableIdentifier;
		this.rerollAttempts = rerollAttempts;
		this.banishingAttempts = banishingAttempts;
		this.lockingAttempts = lockingAttempts;
		this.itemStack = itemStack;
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
		} else if (this.lockingEnabled && this.generatedRogueLoot.enableEntryLocking()) {
			if (this.generatedRogueLoot.entryList().get(index).allowLocking() && this.lockingAttempts != 0) {
				if (((LootOptionButton) button).isSelected() && clientConfig.enable_locking_selected_options.get()) {
					((LootOptionButton) button).setSelected(false);
					this.selectedOptions[index] = false;
				}
				this.lockingEnabled = false;
				this.toggleLockingButton.setValue(false);
				button.active = false;
				button.visible = clientConfig.show_banished_loot_options.get();
				this.lockEntry(index);
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

		// region lock
		MutableComponent disableLockingButtonLabelComponent = Component.translatable("gui.rogue_loot_screen.lock.stop");
		ChatFormatting[] disableLockingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.disableLocking().formatting().size()];
		for (int i = 0; i < this.rogueLootGuiSettings.disableLocking().formatting().size(); i++) {
			disableLockingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.disableLocking().formatting().get(i);
		}
		boolean disableLockingButtonLabelHasShadow = this.rogueLootGuiSettings.disableLocking().hasShadow();
		disableLockingButtonLabelComponent.withStyle(disableLockingButtonLabelChatFormatting);
		if (!disableLockingButtonLabelHasShadow) {
			disableLockingButtonLabelComponent.withoutShadow();
		}

		MutableComponent enableLockingButtonLabelComponent = this.lockingAttempts < 0 ? Component.translatable("gui.rogue_loot_screen.lock.simple") : this.lockingAttempts == 1 ? Component.translatable("gui.rogue_loot_screen.lock.singular", this.lockingAttempts) : Component.translatable("gui.rogue_loot_screen.lock.plural", this.lockingAttempts);
		ChatFormatting[] enableLockingButtonLabelChatFormatting;
		boolean enableLockingButtonLabelHasShadow;

		if (this.lockingAttempts == 0) {
			enableLockingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.lockingRequirementsNotMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.lockingRequirementsNotMet().formatting().size(); i++) {
				enableLockingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.lockingRequirementsNotMet().formatting().get(i);
			}
			enableLockingButtonLabelHasShadow = this.rogueLootGuiSettings.lockingRequirementsNotMet().hasShadow();
		} else {
			enableLockingButtonLabelChatFormatting = new ChatFormatting[this.rogueLootGuiSettings.lockingRequirementsMet().formatting().size()];
			for (int i = 0; i < this.rogueLootGuiSettings.lockingRequirementsMet().formatting().size(); i++) {
				enableLockingButtonLabelChatFormatting[i] = this.rogueLootGuiSettings.lockingRequirementsMet().formatting().get(i);
			}
			enableLockingButtonLabelHasShadow = this.rogueLootGuiSettings.lockingRequirementsMet().hasShadow();
		}
		enableLockingButtonLabelComponent.withStyle(enableLockingButtonLabelChatFormatting);
		if (!enableLockingButtonLabelHasShadow) {
			enableLockingButtonLabelComponent.withoutShadow();
		}

		this.toggleLockingButton = this.addRenderableWidget(
				CycleButton.booleanBuilder(
								disableLockingButtonLabelComponent,
								enableLockingButtonLabelComponent,
								this.lockingEnabled
						)
						.displayOnlyValue()
						.create(
								this.generatedRogueLoot.enableEntryBanishing() ? firstButtonX : soloButtonX,
								firstRowY,
								this.generatedRogueLoot.enableEntryBanishing() ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, lockingEnabled) -> {
									this.lockingEnabled = lockingEnabled;
									this.updateWidgets();
								}
						)
		);
		// endregion lock

		// region banish
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
								this.generatedRogueLoot.enableEntryLocking() ? secondButtonX : soloButtonX,
								firstRowY,
								this.generatedRogueLoot.enableEntryLocking() ? dualButtonWidth : soloButtonWidth,
								20,
								Component.empty(),
								(button, banishingEnabled) -> {
									this.banishingEnabled = banishingEnabled;
									this.updateWidgets();
								}
						)
		);
		// endregion banish

		this.selectLootOption0Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 0)));
		this.selectLootOption1Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 1)));
		this.selectLootOption2Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 2)));
		this.selectLootOption3Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 3)));
		this.selectLootOption4Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 4)));
		this.selectLootOption5Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 5)));
		this.selectLootOption6Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 6)));
		this.selectLootOption7Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 7)));
		this.selectLootOption8Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 8)));
		this.selectLootOption9Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 9)));
		this.selectLootOption10Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 10)));
		this.selectLootOption11Button = this.addRenderableWidget(new LootOptionButton(0, 0, 50, 50, button -> this.selectLootOption(button, 11)));

		// region reroll
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
		// endregion reroll

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

		this.toggleLockingButton.visible = this.generatedRogueLoot.enableEntryLocking();
		this.toggleLockingButton.active = this.lockingAttempts != 0;

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
				this.initLootOptionButton(this.getLootOptionButton(i), this.generatedRogueLoot.entryList().get(i), entriesAssetPathAddition, clientConfig.show_locked_loot_options.get(), clientConfig.show_banished_loot_options.get());
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

	private void initLootOptionButton(Button lootoptionButton, GeneratedRogueLoot.VisibleRogueLootTableEntry visibleRogueLootTableEntry, String entriesAssetPathAddition, boolean showLockedLootOptions, boolean showBanishedLootOptions) {

		RogueLootTableEntryRarity rogueLootTableEntryRarity0 = visibleRogueLootTableEntry.rogueLootTableEntryRarity();

		String assetPathAddition0 = "";
		if (!rogueLootTableEntryRarity0.assetPathAddition().isEmpty()) {
			assetPathAddition0 = rogueLootTableEntryRarity0.assetPathAddition() + "/";
		}
		((LootOptionButton) lootoptionButton).setTextureIds(entriesAssetPathAddition + assetPathAddition0);
		((LootOptionButton) lootoptionButton).setDisplayStack(visibleRogueLootTableEntry.displayStack());
		((LootOptionButton) lootoptionButton).setIconTexture(visibleRogueLootTableEntry.iconTextureIdentifier());
		if (!visibleRogueLootTableEntry.labelText().isEmpty()) {
			ChatFormatting[] lockedLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.lockedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity0.lockedLabel().formatting().size(); i++) {
				lockedLabelFormatting0[i] = rogueLootTableEntryRarity0.lockedLabel().formatting().get(i);
			}
			MutableComponent lockedMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(lockedLabelFormatting0);
			if (!rogueLootTableEntryRarity0.lockedLabel().hasShadow()) {
				lockedMessage0.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setLockedMessage(lockedMessage0);

			ChatFormatting[] banishedLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.banishedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity0.banishedLabel().formatting().size(); i++) {
				banishedLabelFormatting0[i] = rogueLootTableEntryRarity0.banishedLabel().formatting().get(i);
			}
			MutableComponent banishedMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(banishedLabelFormatting0);
			if (!rogueLootTableEntryRarity0.banishedLabel().hasShadow()) {
				banishedMessage0.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setBanishedMessage(banishedMessage0);

			ChatFormatting[] defaultLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.defaultLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity0.defaultLabel().formatting().size(); i++) {
				defaultLabelFormatting0[i] = rogueLootTableEntryRarity0.defaultLabel().formatting().get(i);
			}
			MutableComponent defaultMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(defaultLabelFormatting0);
			if (!rogueLootTableEntryRarity0.defaultLabel().hasShadow()) {
				defaultMessage0.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setDefaultMessage(defaultMessage0);

			ChatFormatting[] selectedLabelFormatting0 = new ChatFormatting[rogueLootTableEntryRarity0.selectedLabel().formatting().size()];
			for (int i = 0; i < rogueLootTableEntryRarity0.selectedLabel().formatting().size(); i++) {
				selectedLabelFormatting0[i] = rogueLootTableEntryRarity0.selectedLabel().formatting().get(i);
			}
			MutableComponent selectedMessage0 = Component.translatable(visibleRogueLootTableEntry.labelText()).withStyle(selectedLabelFormatting0);
			if (!rogueLootTableEntryRarity0.selectedLabel().hasShadow()) {
				selectedMessage0.withoutShadow();
			}
			((LootOptionButton) lootoptionButton).setSelectedMessage(selectedMessage0);
		}

		boolean entryIsLocked = DataAttachmentHelper.isRogueLootEntryLocked(this.minecraft.player, visibleRogueLootTableEntry.parentRogueLootTableEntryId());
		boolean entryIsBanished = DataAttachmentHelper.isRogueLootEntryBanished(this.minecraft.player, visibleRogueLootTableEntry.parentRogueLootTableEntryId());
		if (!entryIsLocked && !entryIsBanished) {
			lootoptionButton.visible = true;
		} else {
			lootoptionButton.active = false;
			lootoptionButton.visible = (entryIsLocked && showLockedLootOptions) || (entryIsBanished && showBanishedLootOptions);
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

		this.confirmButton.active = this.selectedOptionsAmount <= this.generatedRogueLoot.maxChoices() && this.selectedOptionsAmount >= this.generatedRogueLoot.minChoices() && !this.banishingEnabled && !this.lockingEnabled;

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
		boolean lockingEnabled = this.lockingEnabled;
		this.init(width, height);
		((LootOptionButton) this.selectLootOption0Button).setSelected(selected0);
		((LootOptionButton) this.selectLootOption1Button).setSelected(selected1);
		((LootOptionButton) this.selectLootOption2Button).setSelected(selected2);
		((LootOptionButton) this.selectLootOption3Button).setSelected(selected3);
		((LootOptionButton) this.selectLootOption4Button).setSelected(selected4);
		((LootOptionButton) this.selectLootOption5Button).setSelected(selected5);
		this.banishingEnabled = banishingEnabled;
		this.toggleBanishingButton.setValue(this.banishingEnabled);
		this.lockingEnabled = lockingEnabled;
		this.toggleLockingButton.setValue(this.lockingEnabled);
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

	private void lockEntry(final int index) {
		if (this.generatedRogueLoot.enableEntryLocking() && index < this.generatedRogueLoot.entryList().size()) {
			Optional<ItemStack> optionalLockingCost = Optional.empty();
			if (this.generatedRogueLoot.lockingCost().isPresent()) {
				optionalLockingCost = Optional.of(this.generatedRogueLoot.lockingCost().get().create());
			}
			Optional<Identifier> optionalIdentifier = this.generatedRogueLoot.entryList().get(index).parentRogueLootTableEntryId();
			if (optionalIdentifier.isPresent()) {
				ClientPlayNetworking.send(new LockRogueLootEntryPacket(
						this.generatedRogueLootTableIdentifier,
						this.itemStack,
						optionalIdentifier.get(),
						optionalLockingCost
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

	private void confirmSelection() {
		ClientPlayNetworking.send(new ApplyRogueLootSelectionPacket(
				getChosenRogueLootEntries(),
				this.generatedRogueLootTableIdentifier,
				this.itemStack
		));
		this.onClose();
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
