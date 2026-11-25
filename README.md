# Rogue Loot (formerly known as Lootable Blocks)

Introduces a rogue-like loot choice system, which allows players to choose rewards out of several options.

Reward options can consist out of a variety of different types, e.g. items, status effects, advancements, etc.

Players can reroll their options, banish specific rewards from appearing again or reserve rewards to appear again after a reroll.

The entire system is data-driven, allowing for maximal customization.

## How to roll a Rogue Loot Table

A Rogue Loot Table can be supplied to players in various ways.

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

When using items with the "rogueloot:supply_rogue_loot_on_use" data component, a rogue loot table is supplied to the player. The exact specifications are set in the component.

Example /give command:
```
/give @s minecraft:stick[rogueloot:supply_rogue_loot_on_use={"rogue_loot_table_identifier":"rogueloot_test:test","saving_token":"test","rolls":5,"min_choices":1,"max_choices":2,"consume":true}]
```

### Commands

The "/rogue_loot" command allows (among other things) to supply a rogue loot table to players.

Examples:
```
/rogue_loot roll_rogue_loot_table @s rogueloot_test:test saving_token 3 1 1 show_success_message
```

## Optional Functionality

### Banishing

If enabled by the Rogue Loot Table, Table Entries can be banished. They will no longer appear in any newly rolled Rogue Loot Table that contains this entry. Banished entries can no longer be selected in Rogue Loot Table choices that have already been rolled. Depending on a client config option, banished Table Entries are either hidden or visible, but not interactable. Table Entries can be defined to be unbanishable.

If banishing is enabled, a new button appears in the UI, which toggles "banishing mode". While in this mode, clicking on a Table Entry banishes that entry and exits banishing mode. Clicking on the button again also exits banishing mode.

Banishing optionally has an item cost. In that case, the cost is removed from the inventory when a Table Entry is banished. The currently available amount of banishments is displayed on the button.

The "/rogue_loot" command allows (un)banishing Table Entries. Using the command does not cost any items, but requires the Game Master permission level.

Examples:
```
/rogue_loot banish_entry @s rogueloot_test:test
```

```
/rogue_loot reset @s banished_entries rogueloot_test:test
```

---

### Rerolling

If enabled by the Rogue Loot Table, they can be rerolled. If enabled, a new button appears in the UI. Rerolling takes into account all currently banished and reserved Table Entries.

Rerolling optionally has an item cost. In that case, the cost is removed from the inventory when a Rogue Loot Table is rerolled. The currently available amount of rerolls is displayed on the button.

---

### Reserving

If enabled by the Rogue Loot Table, Table Entries can be reserved. They are now guaranteed to appear in any newly rolled Rogue Loot Table that contains this entry. Reserved entries can no longer be selected in Rogue Loot Table choices that have already been rolled. Depending on a client config option, reserved Table Entries are either hidden or visible, but not interactable. When a reserved Table Entries appears in a newly rolled Rogue Loot Table, it will no longer be reserved. Table Entries can be defined to be unreservable.

If reserving is enabled, a new button appears in the UI, which toggles "reserving mode". While in this mode, clicking on a Table Entry reserves that entry and exits reserving mode. Clicking on the button again also exits reserving mode.

Reserving optionally has an item cost. In that case, the cost is removed from the inventory when a Table Entry is reserved. The currently available amount of reservings is displayed on the button.

The "/rogue_loot" command allows (un)banishing Table Entries. Using the command does not cost any items, but requires the Game Master permission level.

Example:
```
/rogue_loot reserve_entry @s rogueloot_test:test
```

```
/rogue_loot reset @s reserved_entries rogueloot_test:test
```

---

### Use Limit

Table Entries can define a use limit. When they are chosen, their use counter increases by 1. If the use limit is reached, then the entry will no longer appear in newly rolled Rogue Loot Tables. Entries which reached their use limit can no longer be selected in Rogue Loot Table choices that have already been rolled. Depending on a client config option, reserved Table Entries are either hidden or visible, but not interactable. When a reserved Table Entries appears in a newly rolled Rogue Loot Table, it will no longer be reserved. Table Entries can be defined to be unreservable.

If reserving is enabled, a new button appears in the UI, which toggles "reserving mode". While in this mode, clicking on a Table Entry reserves that entry and exits reserving mode. Clicking on the button again also exits reserving mode.

Entries ith a use limit show their remaining uses in their tooltip.

Reserving optionally has an item cost. In that case, the cost is removed from the inventory when a Table Entry is reserved. The currently available amount of reservings is displayed on the button.

The "/rogue_loot" command allows increasing/decreasing the uses of Table Entries. This requires the Game Master permission level.

Example:
```
/rogue_loot use_entry @s rogueloot_test:test
```

```
/rogue_loot reset @s entry_uses rogueloot_test:test
```

---

### Permanent Entries

Table Entries can be permanent. These entries will always appear in any Rogue Loot Tables that contains it. However, this is only true if the entry is unlocked according to their unlock predicate, if it has not reached its use limit and if it's not banished.



TODO

```
/rogue_loot reset @s generated_entries rogueloot_test:test
```

```
/rogue_loot reset @s everything
```


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

- "rogueloot:add_experience_level", adds the specified amount of experience levels
- "rogueloot:add_experience_point", adds the specified amount of experience points
- "rogueloot:advancement_criterion", grants the specified criterion of the specified advancement
- "rogueloot:banish_rogue_loot_table_entries", banishes the specified Table Entry (tag)
- "rogueloot:deal_damage", deals the specified amount of damage with the specified damage type to the player
- "rogueloot:heal_to_x_percent", sets the player's health to the specified percentage
- "rogueloot:heal_x_amount", heals the player by the specified amount
- "rogueloot:item_stack", adds the specified item stack to the player's inventory (or drops it on the ground when no space is left)
- "rogueloot:list", grants all specified Entry Components
- "rogueloot:mob_effect_instance", applies the specified mob effect instance
- "rogueloot:modify_equipment", applies the specified list of loot item functions to all items in the specified equipment slots (optionally filtered by a condition)
- "rogueloot:play_sound", plays the specified sound at the player's location
- "rogueloot:random_components", grants a random reward out of a list of Entry Components, each having a weight.
- "rogueloot:reserve_rogue_loot_table_entries", reserves the specified Table Entry (tag)
- "rogueloot:set_experience_level", sets the player's experience level To the specified amount
- "rogueloot:set_experience_point", sets the player's experience points To the specified amount
- "rogueloot:unbanish_rogue_loot_table_entries", unbanishes the specified Table Entry (tag)
- "rogueloot:unreserve_rogue_loot_table_entries", unreserves the specified Table Entry (tag)
- "rogueloot:unuse_rogue_loot_table_entries", removes one use (optionally all uses) of the specified Table Entry (tag)
- "rogueloot:use_rogue_loot_table_entries", adds one use to the specified Table Entry (tag)
- "rogueloot:vanilla_loot_table", rolls the specified vanilla loot table and adds the items to the player's inventory (or drops them on the ground when no space is left)

Component Types can easily be added by third-party mods. Rogue Loot also adds several component types that depend on third-party mods.

Mana Attributes:
- "rogueloot:compat/manaattributes/add_mana", adds the specified amount of mana
- "rogueloot:compat/manaattributes/set_mana", sets the player's mana to the specified percentage

Pufferfish's Skills:
- "rogueloot:compat/puffish_skills/add_experience_level", adds the specified amount of experience levels to the specified category
- "rogueloot:compat/puffish_skills/add_experience", adds the specified amount of experience to the specified category
- "rogueloot:compat/puffish_skills/add_points", adds the specified amount of skill points from the specified point source to the specified category, either with or without announcement
- "rogueloot:compat/puffish_skills/lock_category", locks the specified category
- "rogueloot:compat/puffish_skills/lock_skill", locks the specified skill in the specified category
- "rogueloot:compat/puffish_skills/set_experience_level", sets the player's experience level in the specified category to the specified amount
- "rogueloot:compat/puffish_skills/set_experience", sets the player's experience in the specified category to the specified amount
- "rogueloot:compat/puffish_skills/set_points", sets the player's skill points from the specified point source in the specified category to the specified amount, either with or without announcement
- "rogueloot:compat/puffish_skills/unlock_category", unlocks the specified category
- "rogueloot:compat/puffish_skills/unlock_skill", unlocks the specified skill in the specified category

Stamina Attributes:
- "rogueloot:compat/staminaattributes/add_stamina", adds the specified amount of stamina
- "rogueloot:compat/staminaattributes/set_stamina", sets the player's stamina to the specified percentage


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
