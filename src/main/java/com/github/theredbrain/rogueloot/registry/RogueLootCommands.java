package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLootData;
import com.github.theredbrain.rogueloot.data.RogueLootTable;
import com.github.theredbrain.rogueloot.data.helper.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.data.helper.RogueLootTableHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class RogueLootCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
				dispatcher.register(
						Commands
								.literal("rogue_loot")
								.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(
										Commands.literal("reset")
												.then(
														Commands.argument("player", EntityArgument.player())
																.then(
																		Commands.literal("banished_entries")
																				.then(
																						Commands.argument("entry_identifier", IdentifierArgument.id())
																								.executes(
																										(ctx) -> resetBanishedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.of(IdentifierArgument.getId(ctx, "entry_identifier")))
																								)
																				)
																				.executes(
																						(ctx) -> resetBanishedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.empty())
																				)
																)
																.then(
																		Commands.literal("entry_uses")
																				.then(
																						Commands.argument("entry_identifier", IdentifierArgument.id())
																								.then(
																										Commands.literal("remove_only_one_use")
																												.executes(
																														(ctx) -> resetEntryUses(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.of(IdentifierArgument.getId(ctx, "entry_identifier")), false)
																												)
																								)
																								.executes(
																										(ctx) -> resetEntryUses(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.of(IdentifierArgument.getId(ctx, "entry_identifier")), true)
																								)
																				)
																				.executes(
																						(ctx) -> resetEntryUses(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.empty(), true)
																				)
																)
																.then(
																		Commands.literal("everything")
																				.executes(
																						(ctx) -> resetEverything(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))
																				)
																)
																.then(
																		Commands.literal("generated_entries")
																				.then(
																						Commands.argument("entry_identifier", IdentifierArgument.id())
																								.executes(
																										(ctx) -> resetGeneratedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.of(IdentifierArgument.getId(ctx, "entry_identifier")))
																								)
																				)
																				.executes(
																						(ctx) -> resetGeneratedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.empty())
																				)

																)
																.then(
																		Commands.literal("reserved_entries")
																				.then(
																						Commands.argument("entry_identifier", IdentifierArgument.id())
																								.executes(
																										(ctx) -> resetReservedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.of(IdentifierArgument.getId(ctx, "entry_identifier")))
																								)
																				)
																				.executes(
																						(ctx) -> resetReservedEntries(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), Optional.empty())
																				)

																)
												)
								)
								.then(
										Commands.literal("banish_entry")
												.then(
														Commands.argument("player", EntityArgument.player())
																.then(
																		Commands.argument("entry_identifier", IdentifierArgument.id())
																				.executes(
																						(ctx) -> banishEntry(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IdentifierArgument.getId(ctx, "entry_identifier"))
																				)
																)
												)
								)
								.then(
										Commands.literal("reserve_entry")
												.then(
														Commands.argument("player", EntityArgument.player())
																.then(
																		Commands.argument("entry_identifier", IdentifierArgument.id())
																				.executes(
																						(ctx) -> reserveEntry(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IdentifierArgument.getId(ctx, "entry_identifier"))
																				)
																)
												)
								)
								.then(
										Commands.literal("use_entry")
												.then(
														Commands.argument("player", EntityArgument.player())
																.then(
																		Commands.argument("entry_identifier", IdentifierArgument.id())
																				.executes(
																						(ctx) -> useEntry(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IdentifierArgument.getId(ctx, "entry_identifier"))
																				)
																)
												)
								)
								.then(
										Commands.literal("roll_rogue_loot_table")
												.then(
														Commands.argument("player", EntityArgument.player())
																.then(
																		Commands.argument("rogue_loot_table_identifier", IdentifierArgument.id())
																				.then(
																						Commands.argument("saving_token", StringArgumentType.string())
																								.then(
																										Commands.argument("default_rolls", IntegerArgumentType.integer(1))
																												.then(
																														Commands.argument("default_min_choices", IntegerArgumentType.integer(1))
																																.then(
																																		Commands.argument("default_max_choices", IntegerArgumentType.integer(1))
																																				.then(
																																						Commands.literal("show_success_message")
																																								.executes(
																																										(ctx) -> rollRogueLootTable(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IdentifierArgument.getId(ctx, "rogue_loot_table_identifier"), StringArgumentType.getString(ctx, "saving_token"), IntegerArgumentType.getInteger(ctx, "default_rolls"), IntegerArgumentType.getInteger(ctx, "default_min_choices"), IntegerArgumentType.getInteger(ctx, "default_max_choices"), true)
																																								)
																																				)
																																				.executes(
																																						(ctx) -> rollRogueLootTable(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IdentifierArgument.getId(ctx, "rogue_loot_table_identifier"), StringArgumentType.getString(ctx, "saving_token"), IntegerArgumentType.getInteger(ctx, "default_rolls"), IntegerArgumentType.getInteger(ctx, "default_min_choices"), IntegerArgumentType.getInteger(ctx, "default_max_choices"), false)
																																				)
																																)
																												)
																								)
																				)
																)
												)
								)
				));
	}

	private static int rollRogueLootTable(final CommandSourceStack source, final ServerPlayer serverPlayer, final Identifier rogueLootTableIdentifier, String savingToken, int defaultRolls, int defaultMinChoices, int defaultMaxChoices, boolean showSuccessMessage) {
		if (RogueLootTable.getRogueLootTable(source.getLevel().registryAccess(), rogueLootTableIdentifier) == null) {
			source.sendFailure(Component.translatable("commands.rogueloot.roll_table.invalid_identifier", rogueLootTableIdentifier.toString()));
			return 0;
		}
		if (defaultMaxChoices < defaultMinChoices) {
			source.sendFailure(Component.translatable("commands.rogueloot.roll_table.max_choices_smaller_than_min_choices", defaultMaxChoices, defaultMinChoices));
			return 0;
		}
		RogueLootTableHelper.supplyRogueLootTable(
				source.getLevel(),
				serverPlayer,
				new GeneratedRogueLootData(
						rogueLootTableIdentifier,
						Identifier.fromNamespaceAndPath(
								rogueLootTableIdentifier.getNamespace(),
								rogueLootTableIdentifier.getPath() + "_" + savingToken
						),
						defaultRolls,
						RogueLootTableHelper.calculateModifiedRolls(source.getLevel(), serverPlayer, rogueLootTableIdentifier, defaultRolls),
						defaultMinChoices,
						RogueLootTableHelper.calculateModifiedMinChoices(source.getLevel(), serverPlayer, rogueLootTableIdentifier, defaultMinChoices),
						defaultMaxChoices,
						RogueLootTableHelper.calculateModifiedMaxChoices(source.getLevel(), serverPlayer, rogueLootTableIdentifier, defaultMaxChoices),
						Optional.empty(),
						Optional.empty(),
						Optional.empty()
				)
		);
		if (showSuccessMessage) {
			source.sendSuccess(() -> Component.translatable("commands.rogueloot.roll_table.success", rogueLootTableIdentifier.toString(), serverPlayer.getDisplayName()), true);
		}
		return 1;
	}

	private static int banishEntry(final CommandSourceStack source, final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		DataAttachmentHelper.banishRogueLootEntry(serverPlayer, entryIdentifier);
		source.sendSuccess(() -> Component.translatable("commands.rogueloot.banish_entry.success", entryIdentifier.toString(), serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int reserveEntry(final CommandSourceStack source, final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		DataAttachmentHelper.reserveRogueLootEntry(serverPlayer, entryIdentifier);
		source.sendSuccess(() -> Component.translatable("commands.rogueloot.reserve_entry.success", entryIdentifier.toString(), serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int useEntry(final CommandSourceStack source, final ServerPlayer serverPlayer, final Identifier entryIdentifier) {
		DataAttachmentHelper.useRogueLootEntry(serverPlayer, entryIdentifier);
		source.sendSuccess(() -> Component.translatable("commands.rogueloot.use_entry.success", entryIdentifier.toString(), serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int resetBanishedEntries(final CommandSourceStack source, final ServerPlayer serverPlayer, final Optional<Identifier> optionalEntryIdentifier) {
		DataAttachmentHelper.unbanishRogueLootEntry(serverPlayer, optionalEntryIdentifier);
		source.sendSuccess(() -> optionalEntryIdentifier.isPresent() ? Component.translatable("commands.rogueloot.reset_banished_entry.single", optionalEntryIdentifier.get().toString(), serverPlayer.getDisplayName()) : Component.translatable("commands.rogueloot.reset_banished_entry.all", serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int resetReservedEntries(final CommandSourceStack source, final ServerPlayer serverPlayer, final Optional<Identifier> optionalEntryIdentifier) {
		DataAttachmentHelper.unreserveRogueLootEntry(serverPlayer, optionalEntryIdentifier);
		source.sendSuccess(() -> optionalEntryIdentifier.isPresent() ? Component.translatable("commands.rogueloot.reset_reserved_entry.single", optionalEntryIdentifier.get().toString(), serverPlayer.getDisplayName()) : Component.translatable("commands.rogueloot.reset_reserved_entry.all", serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int resetEntryUses(final CommandSourceStack source, final ServerPlayer serverPlayer, final Optional<Identifier> optionalEntryIdentifier, boolean removeAllUses) {
		DataAttachmentHelper.unuseRogueLootEntry(serverPlayer, optionalEntryIdentifier, removeAllUses);
		source.sendSuccess(() -> optionalEntryIdentifier.isPresent() ? (removeAllUses ? Component.translatable("commands.rogueloot.reset_entry_uses.single.all_uses", optionalEntryIdentifier.get().toString(), serverPlayer.getDisplayName()) : Component.translatable("commands.rogueloot.reset_entry_uses.single.one_use", optionalEntryIdentifier.get().toString(), serverPlayer.getDisplayName())) : Component.translatable("commands.rogueloot.reset_entry_uses.all", serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int resetGeneratedEntries(final CommandSourceStack source, final ServerPlayer serverPlayer, final Optional<Identifier> optionalEntryIdentifier) {
		DataAttachmentHelper.removeGeneratedRogueLootEntry(serverPlayer, optionalEntryIdentifier);
		source.sendSuccess(() -> optionalEntryIdentifier.isPresent() ? Component.translatable("commands.rogueloot.reset_generated_entry.single", optionalEntryIdentifier.get().toString(), serverPlayer.getDisplayName()) : Component.translatable("commands.rogueloot.reset_generated_entry.all", serverPlayer.getDisplayName()), true);
		return 1;
	}

	private static int resetEverything(final CommandSourceStack source, final ServerPlayer serverPlayer) {
		DataAttachmentHelper.unbanishRogueLootEntry(serverPlayer, Optional.empty());
		DataAttachmentHelper.unuseRogueLootEntry(serverPlayer, Optional.empty(), true);
		DataAttachmentHelper.removeGeneratedRogueLootEntry(serverPlayer, Optional.empty());
		DataAttachmentHelper.unreserveRogueLootEntry(serverPlayer, Optional.empty());

		source.sendSuccess(() -> Component.translatable("commands.rogueloot.reset_everything", serverPlayer.getDisplayName()), true);
		return 1;
	}
}
