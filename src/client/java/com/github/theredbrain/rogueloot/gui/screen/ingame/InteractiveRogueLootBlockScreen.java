package com.github.theredbrain.rogueloot.gui.screen.ingame;

import com.github.theredbrain.rogueloot.block.entity.InteractiveRogueLootBlockEntity;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@Environment(value = EnvType.CLIENT)
public class InteractiveRogueLootBlockScreen extends Screen {
	private static final Component LOOT_TABLE_IDENTIFIER_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.loot_table_identifier_label");
	private static final Component LOOTABLE_IDENTIFIER_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.loot_table_identifier_label");
	private static final Component ROLLS_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.rolls_label");
	private static final Component CHOICES_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.choices_label");
	private static final Component MIN_CHOICES_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.min_choices_label");
	private static final Component MAX_CHOICES_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.max_choices_label");
	private static final Component TRACK_PLAYERS_LABEL_TEXT = Component.translatable("gui.interactive_rogue_loot_block.track_players_label");
	private static final Component LOOT_ACQUIRED_MESSAGE_PLACEHOLDER_TEXT = Component.translatable("gui.interactive_rogue_loot_block.loot_acquired_message_placeholder");
	private static final Component LOOT_ACQUIRED_SOUND_ID_PLACEHOLDER_TEXT = Component.translatable("gui.interactive_rogue_loot_block.loot_acquired_sound_id_placeholder");
	private static final Component ALREADY_LOOTED_MESSAGE_PLACEHOLDER_TEXT = Component.translatable("gui.interactive_rogue_loot_block.already_looted_message_placeholder");
	private static final Component ALREADY_LOOTED_SOUND_ID_PLACEHOLDER_TEXT = Component.translatable("gui.interactive_rogue_loot_block.already_looted_sound_id_placeholder");
	private final InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity;
	private EditBox lootTableIdentifierStringField;
	private InteractiveRogueLootBlockEntity.Mode lootBlockMode;
	private CycleButton<InteractiveRogueLootBlockEntity.Mode> lootBlockModeCycleButton;
	private EditBox rollsField;
	private EditBox minChoicesField;
	private EditBox maxChoicesField;
	private boolean trackPlayers;
	private CycleButton<Boolean> trackPlayersCycleButton;
	private EditBox lootAcquiredMessageField;
	private EditBox lootAcquiredSoundIdField;
	private EditBox alreadyLootedMessageField;
	private EditBox alreadyLootedSoundIdField;

	public InteractiveRogueLootBlockScreen(InteractiveRogueLootBlockEntity interactiveRogueLootBlockEntity) {
		super(GameNarrator.NO_TITLE);
		this.interactiveRogueLootBlockEntity = interactiveRogueLootBlockEntity;
	}

	private void done() {
		this.updateInteractiveLootBlock();
		this.onClose();
	}

	private void cancel() {
		this.onClose();
	}

	@Override
	protected void init() {
		this.lootTableIdentifierStringField = new EditBox(this.font, this.width / 2 - 154, 30, 308, 20, Component.translatable(""));
		this.lootTableIdentifierStringField.setMaxLength(128);
		this.lootTableIdentifierStringField.setValue(this.interactiveRogueLootBlockEntity.getLootTableIdentifierString());
		this.addWidget(this.lootTableIdentifierStringField);

		this.lootBlockMode = this.interactiveRogueLootBlockEntity.getMode();

		this.lootBlockModeCycleButton = this.addRenderableWidget(
				CycleButton.builder(InteractiveRogueLootBlockEntity.Mode::asText, this.lootBlockMode)
						.withValues(InteractiveRogueLootBlockEntity.Mode.values())
						.displayOnlyValue()
						.create(this.width / 2 - 154, 54, 308, 20, Component.empty(), (button, lootBlockMode) -> {
									this.lootBlockMode = lootBlockMode;
									this.updateWidgets();
								}
						)
		);

		this.rollsField = new EditBox(this.font, this.width / 2 - 154, 89, 74, 20, Component.empty());
		this.rollsField.setMaxLength(128);
		this.rollsField.setValue(Integer.toString(this.interactiveRogueLootBlockEntity.getRolls()));
		this.addWidget(this.rollsField);

		this.minChoicesField = new EditBox(this.font, this.width / 2 - 76, 89, 74, 20, Component.empty());
		this.minChoicesField.setMaxLength(128);
		this.minChoicesField.setValue(Integer.toString(this.interactiveRogueLootBlockEntity.getMinChoices()));
		this.addWidget(this.minChoicesField);

		this.maxChoicesField = new EditBox(this.font, this.width / 2 + 2, 89, 74, 20, Component.empty());
		this.maxChoicesField.setMaxLength(128);
		this.maxChoicesField.setValue(Integer.toString(this.interactiveRogueLootBlockEntity.getMaxChoices()));
		this.addWidget(this.maxChoicesField);

		this.trackPlayers = this.interactiveRogueLootBlockEntity.trackPlayers();

		this.trackPlayersCycleButton = this.addRenderableWidget(
				CycleButton.onOffBuilder(this.trackPlayers)
						.displayOnlyValue()
						.create(this.width / 2 + 80, 89, 74, 20, Component.empty(), (button, trackPlayers) -> {
							this.trackPlayers = trackPlayers;
						})
		);

		this.lootAcquiredMessageField = new EditBox(this.font, this.width / 2 - 154, 113, 308, 20, Component.translatable(""));
		this.lootAcquiredMessageField.setMaxLength(128);
		this.lootAcquiredMessageField.setValue(this.interactiveRogueLootBlockEntity.getLootAcquiredMessage());
		this.lootAcquiredMessageField.setHint(LOOT_ACQUIRED_MESSAGE_PLACEHOLDER_TEXT);
		this.addWidget(this.lootAcquiredMessageField);

		this.lootAcquiredSoundIdField = new EditBox(this.font, this.width / 2 - 154, 137, 308, 20, Component.translatable(""));
		this.lootAcquiredSoundIdField.setMaxLength(128);
		this.lootAcquiredSoundIdField.setValue(this.interactiveRogueLootBlockEntity.getLootAcquiredSoundId());
		this.lootAcquiredSoundIdField.setHint(LOOT_ACQUIRED_SOUND_ID_PLACEHOLDER_TEXT);
		this.addWidget(this.lootAcquiredSoundIdField);

		this.alreadyLootedMessageField = new EditBox(this.font, this.width / 2 - 154, 161, 308, 20, Component.translatable(""));
		this.alreadyLootedMessageField.setMaxLength(128);
		this.alreadyLootedMessageField.setValue(this.interactiveRogueLootBlockEntity.getAlreadyLootedMessage());
		this.alreadyLootedMessageField.setHint(ALREADY_LOOTED_MESSAGE_PLACEHOLDER_TEXT);
		this.addWidget(this.alreadyLootedMessageField);

		this.alreadyLootedSoundIdField = new EditBox(this.font, this.width / 2 - 154, 185, 308, 20, Component.translatable(""));
		this.alreadyLootedSoundIdField.setMaxLength(128);
		this.alreadyLootedSoundIdField.setValue(this.interactiveRogueLootBlockEntity.getAlreadyLootedSoundId());
		this.alreadyLootedSoundIdField.setHint(ALREADY_LOOTED_SOUND_ID_PLACEHOLDER_TEXT);
		this.addWidget(this.alreadyLootedSoundIdField);

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.done()).bounds(this.width / 2 - 4 - 150, 209, 152, 20).build());
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.cancel()).bounds(this.width / 2 + 2, 209, 152, 20).build());
		this.setInitialFocus(this.lootTableIdentifierStringField);

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.rollsField.setVisible(false);
		this.minChoicesField.setVisible(false);
		this.maxChoicesField.setVisible(false);

		if (this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.ROGUE_LOOT) {

			this.rollsField.setVisible(true);
			this.minChoicesField.setVisible(true);
			this.maxChoicesField.setVisible(true);

		} else if (this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_CHOICE) {

			this.rollsField.setVisible(true);
			this.minChoicesField.setVisible(true);

		} else if (this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_RANDOM) {

			this.rollsField.setVisible(true);

		}

	}

	@Override
	public void resize(final int width, final int height) {
		boolean bool = this.trackPlayers;
		String string = this.lootTableIdentifierStringField.getValue();
		String string1 = this.rollsField.getValue();
		String string2 = this.minChoicesField.getValue();
		String string3 = this.maxChoicesField.getValue();
		String string4 = this.lootAcquiredMessageField.getValue();
		String string5 = this.lootAcquiredSoundIdField.getValue();
		String string6 = this.alreadyLootedMessageField.getValue();
		String string7 = this.alreadyLootedSoundIdField.getValue();
		InteractiveRogueLootBlockEntity.Mode var = this.lootBlockMode;
		this.init(width, height);
		this.trackPlayers = bool;
		this.lootTableIdentifierStringField.setValue(string);
		this.rollsField.setValue(string1);
		this.minChoicesField.setValue(string2);
		this.maxChoicesField.setValue(string3);
		this.lootAcquiredMessageField.setValue(string4);
		this.lootAcquiredSoundIdField.setValue(string5);
		this.alreadyLootedMessageField.setValue(string6);
		this.alreadyLootedSoundIdField.setValue(string7);
		this.lootBlockMode = var;
		this.lootBlockModeCycleButton.setValue(this.lootBlockMode);
		this.trackPlayersCycleButton.setValue(this.trackPlayers);
		this.updateWidgets();
	}

	@Override
	public boolean keyPressed(final KeyEvent event) {
		if (event.isConfirmation()) {
			this.done();
			return true;
		}
		return super.keyPressed(event);
	}

	private void updateInteractiveLootBlock() {
		ClientPlayNetworking.send(new UpdateInteractiveRogueLootBlockPacket(
				this.interactiveRogueLootBlockEntity.getBlockPos(),
				this.lootTableIdentifierStringField.getValue(),
				this.lootBlockMode,
				parseInt(this.rollsField.getValue()),
				parseInt(this.minChoicesField.getValue()),
				parseInt(this.maxChoicesField.getValue()),
				this.trackPlayers,
				this.lootAcquiredMessageField.getValue(),
				this.lootAcquiredSoundIdField.getValue(),
				this.alreadyLootedMessageField.getValue(),
				this.alreadyLootedSoundIdField.getValue()
		));
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {

		super.extractRenderState(graphics, mouseX, mouseY, a);

		if (this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.VANILLA) {
			graphics.text(this.font, LOOT_TABLE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 20, -6250336);
		} else {
			graphics.text(this.font, LOOTABLE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 20, -6250336);
		}
		this.lootTableIdentifierStringField.extractRenderState(graphics, mouseX, mouseY, a);

		if (this.lootBlockMode != InteractiveRogueLootBlockEntity.Mode.VANILLA) {
			graphics.text(this.font, ROLLS_LABEL_TEXT, this.width / 2 - 153, 79, -6250336);
			this.rollsField.extractRenderState(graphics, mouseX, mouseY, a);
		}

		boolean isRogueLootMode = this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.ROGUE_LOOT;

		if (this.lootBlockMode == InteractiveRogueLootBlockEntity.Mode.LOOTABLE_CHOICE || isRogueLootMode) {
			graphics.text(this.font, isRogueLootMode ? MIN_CHOICES_LABEL_TEXT : CHOICES_LABEL_TEXT, this.width / 2 - 75, 79, -6250336);
			this.minChoicesField.extractRenderState(graphics, mouseX, mouseY, a);
		}

		if (isRogueLootMode) {
			graphics.text(this.font, MAX_CHOICES_LABEL_TEXT, this.width / 2 + 3, 79, -6250336);
			this.maxChoicesField.extractRenderState(graphics, mouseX, mouseY, a);
		}
		graphics.text(this.font, TRACK_PLAYERS_LABEL_TEXT, this.width / 2 + 81, 79, -6250336);
		this.lootAcquiredMessageField.extractRenderState(graphics, mouseX, mouseY, a);
		this.lootAcquiredSoundIdField.extractRenderState(graphics, mouseX, mouseY, a);
		this.alreadyLootedMessageField.extractRenderState(graphics, mouseX, mouseY, a);
		this.alreadyLootedSoundIdField.extractRenderState(graphics, mouseX, mouseY, a);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean isInGameUi() {
		return true;
	}

	public static int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException numberFormatException) {
			return 0;
		}
	}

}
