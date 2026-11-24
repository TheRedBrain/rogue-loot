# Rogue Loot (formerly known as Lootable Blocks)

Introduces a rogue-like loot choice system, which allows players to choose rewards out of several options.

Reward options can consist out of a variety of different types, e.g. items, status effects, advancements, etc.

Players can reroll their options, banish specific rewards from appearing again or reserve rewards to appear again after a reroll.

The entire system is data-driven, allowing for maximal customization.

## How to roll a Rogue Loot Table

A Rogue Loot Table can be supplied in various ways.

### Block Interactions

#### Rogue Loot Vault Block

This block looks like the vanilla Vault Block and behaves almost exactly like it. The main difference is that instead of spawning the reward in the world, a Rogue Loot Table is rolled for the interacting player.

The block configuration is data-driven, similar to the vanilla Vault Block.

Example:

> Note that the data used in all examples is implemented in the test mod visible on GitHub.

```json5
{
  "loot_table_identifier": "rogueloot_test:test", // the identifier of the supplied rogue loot table
  "loot_table_mode": "rogue_loot", // the mode of the rogue loot vault ("rogue_loot" is currently the only valid option, this option exists for integration with other mods
  "rolls": 3, // amount of rolls
  "min_choices": 1, // minimum amount of choices
  "max_choices": 1, // maximum amount of choices
  "activation_range": 4.0, // must be <= 'deactivation_range'
  "deactivation_range": 4.5,
  "key_item": {
      "id": "minecraft:trial_key",
      "count": 1
  },
  "display_loot_table_identifier": "rogueloot_test:chests/test_display" // the identifier of the display loot_table, refers to a vanilla loot table
}
```

Example /give command:
```
/give @s rogueloot:rogue_loot_vault_block[block_state={ominous:"false"},block_entity_data={id:"rogueloot:rogue_loot_vault_block",config_identifier:"rogueloot_test:test"}]
```

#### Interactive Rogue Loot Block

This block is a block intended for map-makers and server admins, and it's available in the operator items creative tab.

Interacting with it opens a config screen (when player is in creative mode and has permission level of game master), where several config options can be set.

When interacted with in survival or adventure mode the block supplies a rogue/vanilla loot table, according to its settings.

### Item Interactions

When using items with the "rogueloot:" data component, a rogue loot table is supplied to the player. The exact specifications are set in the component.

Example /give command:
```
/give @s minecraft:stick[rogueloot:supply_rogue_loot_on_use={"rogue_loot_table_identifier":"rogueloot_test:test","saving_token":"test","rolls":5,"min_choices":1,"max_choices":2,"consume":true}]
```

### Commands

The "/rogue_loot" command allows (among other things) to supply a rogue loot table to players.

## How to create rogue loot tables?

Rogue Loot Tables are data-driven, which means they are defined using a data pack.

Some parts of the Rogue Loot Table are data-driven on their own.

### Rogue Loot Table

Path:
```
data/your_namespace/rogue_loot/table/your_rogue_loot_table.json
```

Example:

```json5
{
  "rogue_loot_table_entries": "#rogueloot_test:test",
  "rarity_overrides": {
    "rogueloot_test:epic": "rogueloot_test:uncommon"
  },
  "gui_settings_id": "rogueloot_test:test",
  "roll_amount_modifier": {
    "min_value": 1,
    "max_value": 20,
    "influencing_attribute_identifier": "minecraft:luck",
    "influence_multiplier": 2.0
  },
  "rerolling": {
    "enabled": true,
    "cost": {
      "id": "minecraft:lapis_lazuli"
    }
  },
  "banishing": {
    "enabled": true,
    "cost": {
      "id": "minecraft:emerald"
    }
  },
  "reserving": {
    "enabled": true,
    "cost": {
      "id": "minecraft:iron_ingot",
      "count": 10
    }
  },
  "random_sequence": "rogueloot_test:test"
}
```

### GUI Settings

Gui Settings are referenced by Rogue Loot Tables. They define several cosmetic features and are entirely optional.

Path:
```
data/your_namespace/rogue_loot/gui_settings/your_table_gui_settings.json
```

Example:

```json5
{
  "background_texture_path": "",
  "entries_asset_path_addition": "",
  "confirmation_requirements_not_met": {
    "formatting": [
      "red"
    ]
  },
  "confirmation_requirements_met": {
    "formatting": [
      "green"
    ]
  }
}
```

### Table Entry

Table Entries are referenced by Rogue Loot Tables. They define the individual reward choices.

Path:
```
data/your_namespace/rogue_loot/table_entry/your_table_entry.json
```

Example:

```json5
{
  "component": {
    "type": "rogueloot:item_stack",
    "item_stack": {
      "id": "minecraft:iron_sword"
    }
  },
  "display_stack": {
    "id": "minecraft:iron_sword"
  },
  "label_text": "Iron Sword",
  "tooltip_stack": {
    "id": "minecraft:iron_sword"
  },
  "rarity": "rogueloot_test:rare",
  "use_limit": 0,
  "allow_banishing": true,
  "allow_reserving": true,
  "is_permanent": false
}
```

### Entry Rarity

Each Table Entry can reference an Entry Rarity. These are mostly cosmetic, but they also define how often a Table Entry is presented to a player, based on a weight system.

Path:
```
data/your_namespace/rogue_loot/entry_rarity/your_entry_rarity.json
```

Example:

```json5
{
  "asset_path_addition": "common",
  "weight": 15,
  "banished_label": {
    "formatting": [
      "gray"
    ]
  },
  "default_label": {
    "formatting": [
      "gray"
    ]
  },
  "reserved_label": {
    "formatting": [
      "gray"
    ]
  },
  "selected_label": {
    "formatting": [
      "gray"
    ]
  }
}
```

### Entry Component

A Table Entry defines a list of rewards, which are granted to the player when the entry is chosen. The rewards are called Entry Components, and they have a variety of possible types. These types are defined in Java code, but can have data-driven fields for further customization.

Component Types:

- "rogueloot:add_experience_level"
- "rogueloot:add_experience_point"
- "rogueloot:advancement_criterion"
- "rogueloot:banish_rogue_loot_table_entries"
- "rogueloot:deal_damage"
- "rogueloot:heal_to_x_percent"
- "rogueloot:heal_x_amount"
- "rogueloot:item_stack"
- "rogueloot:list"
- "rogueloot:mob_effect_instance"
- "rogueloot:modify_equipment"
- "rogueloot:play_sound"
- "rogueloot:random_components"
- "rogueloot:reserve_rogue_loot_table_entries"
- "rogueloot:set_experience_level"
- "rogueloot:set_experience_point"
- "rogueloot:unbanish_rogue_loot_table_entries"
- "rogueloot:unreserve_rogue_loot_table_entries"
- "rogueloot:unuse_rogue_loot_table_entries"
- "rogueloot:use_rogue_loot_table_entries"
- "rogueloot:vanilla_loot_table"

Component Types can easily be added by third-party mods. Rogue Loot also adds several component types that depend on third-party mods.

Mana Attributes:
- "rogueloot:compat/manaattributes/add_mana"
- "rogueloot:compat/manaattributes/set_mana"

Pufferfish's Skills:
- "rogueloot:compat/puffish_skills/add_experience_level"
- "rogueloot:compat/puffish_skills/add_experience"
- "rogueloot:compat/puffish_skills/add_points"
- "rogueloot:compat/puffish_skills/lock_category"
- "rogueloot:compat/puffish_skills/lock_skill"
- "rogueloot:compat/puffish_skills/set_experience_level"
- "rogueloot:compat/puffish_skills/set_experience"
- "rogueloot:compat/puffish_skills/set_points"
- "rogueloot:compat/puffish_skills/unlock_category"
- "rogueloot:compat/puffish_skills/unlock_skill"

Stamina Attributes:
- "rogueloot:compat/staminaattributes/add_stamina"
- "rogueloot:compat/staminaattributes/set_stamina"


### Common Data Structures

These structures are used by several of the data files described above.

#### Label Settings

Text labels (e.g. on buttons) can be customized. A "Chat Formatting" is used to describe such a customization. Label Settings can define a list of these formattings, which will then be applied to the text in question.

These formattings exist:
- "black"
- "dark_blue"
- "dark_green"
- "dark_aqua"
- "dark_red"
- "dark_purple"
- "gold"
- "gray"
- "dark_gray"
- "blue"
- "green"
- "aqua"
- "red"
- "light_purple"
- "yellow"
- "white"
- "obfuscated"
- "bold"
- "strikethrough"
- "underline"
- "italic"

Additionally, the text shadow can be disabled.

Example:

```json5
{
  "formatting": [
    "gray",
    "bold"
  ],
  "has_shadow": true
}
```

#### Attribute Influenced Integer Modifier

Some values used by various mechanics in Rogue Loot can optionally be modified by the value of an entity attribute. 

The current value of the specified entity attribute is multiplied by a defined value and added to the given default value.

The modified value is clamped by the given min_value and max_value.

Example:

```json5
{
  "min_value": -50,
  "max_value": 50,
  "influencing_attribute_identifier": "minecraft:luck",
  "influence_multiplier": 2.0,
}
```
