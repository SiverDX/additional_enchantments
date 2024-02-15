![](https://i.imgur.com/6l64DCT.png "Logo")
# FAQ
### General
- Duplicate keybinds work but only make sense if multiple player-configurable enchantments cannot be active at the same time
  - e.g. multiple bow-related enchantments or armor enchantments + main hand enchantments

### Versions / Loaders
- Fabric is not planned
- Backports are not being considered at the moment

### Known problems
- The player transparency rendering of the `Hunter` enchantment has some issues with transparent blocks (e.g. water)

# Enchantments
### Enchantment Categories
- **Melee**: Sword, Axe and Trident
- **Ranged**: Bow and Crossbow
- **Ranged and Trident**: Entries from **Ranged** and Tridents
  - Note that these are enchantments for projectiles, meaning for Tridents they only apply when thrown (and are therefor incompatible with the `Riptide` enchantment)
- **Digger**: Vanilla category and contains things like pickaxes or shovels

### Configuration
- All enchantments are configurable
- There might be some enchantment-related extra configuration

## Faster Attacks (Melee)
- Default max. level: 4
- Increases attack speed
- Enchantment level increases the amount

## Plague (Melee)
- Default max. level: 6
- Applies a plague effect to entities which deals magic damage
  - Also has a chance to spread to nearby targets (does not affect players or Tamable Animals owned by players)
  - Targets can be blacklisted with the tag `additional_enchantments:plague_blacklist`
- Enchantment level increases the duration, damage, application chance and reduces the time between damage ticks
  - In addition, it also increases the chance and range for the spread
- Not compatible with the `Wither` enchantment

## Wither (Melee)
- Default max. level: 6
- Applies a wither effect to entities which deals wither damage
- Enchantment level increases the duration, damage, application chance and reduces the time between damage ticks
- Not compatible with the `Plague` enchantment

## Confusion (Melee)
- Default max. level: 5
- Has a chance to cause the attacked target to retaliate against a randomly chosen nearby entity, dropping the attacker as a target
  - Targets can be blacklisted with the tag `additional_enchantments:confusion_blacklist`
- Enchantment level increases the chance of this effect to occur and also the range to check against possible retaliation targets

## Voiding (Digger)
- Max. level: 1
- Causes certain terrain blocks (specified by the tag `additional_enchantments:voiding`) to no longer drop their block as loot
  - Other loot added to these blocks is not affected (e.g. Flint from Gravel)
  - This behaviour can be enabled or disabled through the keybind (default being `G`)

## Hydro Shock (Trident)
- Default max. level: 5
- Deal increased damage to targets on fire, in water, weak to water or if it is raining at the position
  - When the Trident is thrown it may cause a lightning bolt to occur at the target position
    - The lightning bolt itself will not damage the player, items or experience orbs
    - The fire will still damage the player - there is some safety to make items and experience orbs fire immune if the target dies within a second of getting hit by the lightning bolt
- Enchantment level scales the damage multiplier and chance for the lightning bolt to occur

## Explosive Tip (Ranged and Trident)
- Default max. level: 4
- Causes an explosion on impact (which does not damage the shooter, their allies, experience orbs or items)
- Enchantment level increases explosion radius
- It's possible to switch between two modes (NONE and BREAK (which breaks blocks)) with the keybind (default being `J`)

## Homing (Ranged and Trident)
- Default max. level: 4
- Projectiles fly towards a chosen target
  - Targets can be blacklisted with the tag `additional_enchantments:homing_blacklist` (`minecraft:villager` and `minecraft:iron_golem` are added by default)
  - Invisible entities are only targeted if they're glowing (server-side (`Perception` is client-only))
- Enchantment level increases the radius in which a target gets picked (+ it slightly increases velocity of the arrow)
- It's possible to switch between various modes with the keybind (default being `H`)
  - **Type**: MONSTER, ANIMAL, BOSSES (`forge:bosses`), ANY and NONE (homing effect is not being applied)
  - **Priority**: (when `Shift` is also pressed): CLOSEST, LOWEST_HEALTH, HIGHEST_HEALTH and RANDOM

## Straight Shot (Ranged and Trident)
- Default max. level: 4
- Disables gravity for shot projectiles and has a chance to hit `Enderman`
- Enchantment level increases the chance to hit `Enderman`

## Tipped (Ranged and Trident)
- Default max. level: 4
- Applies random effects to the arrow (and in turn to the target it hits)
- Enchantment level increases the amount, amplifier and duration of the effects
- It's possible to switch between effect categories (HARMFUL, BENEFICIAL and NEUTRAL (can apply all)) with the keybind (default being `G`)
  - Effects can be blacklisted with the tag `additional_enchantmetns:tipped_blacklist`

## Shatter (Ranged)
- Default max. level: 4
- Allows the usage of `Amethyst Shards` as projectiles
  - There is a high chance for them to break on impact which will deal damage in an area (the projectile and area damage are `Magic`)
    - Targets can be blacklisted with the tag `additional_enchantments:shatter_aoe_blacklist` (`minecraft:villager` and `minecraft:iron_golem` are added by default)
- Enchantment level increases the shatter area of effect and damage

## Perception (Helmet)
- Default max. level: 4
- Outlines nearby entities and distinguishes them by color
  - DARK_PURPLE: `forge:bosses`, RED: Monsters, DARK_GREEN: Tamable Animals, GREEN: Animals, BLUE: Other
  - Items are colored depending on the color of their display name (GOLD if none is available)
- Enchantment level increases the range
- It's possible to switch between different modes (ALL, NO_ITEMS and NONE) with the keybind (default being `U`)
  - If `Shift` is pressed it will cycle through options for items, filtering them by rarity (COMMON, UNCOMMON, RARE, EPIC)
  - Entities can be blacklisted with the tag `additional_enchantments:perception_blacklist`
  - It's configurable whether invisible entities are outlined or not (`true` by default)

## Ore Sight (Helmet)
- Default max. level: 5
- Outlines nearby ore blocks (even through walls) with different colors based on their rarity group
  - The groups are based on three block tags: `additional_enchantments:common_ore`, `additional_enchantments:uncommon_ore` and `additional_enchantments:rare_ore`
  - If the block is a `forge:ores` but not part of a rarity group it will be outlined with a white color
- Enchantment level increases the range
- It's possible to switch between the rarities (ALL, COMMON, UNCOMMON, RARE and NONE (no outlines will be displayed)) with the keybind (default being `U`)
  - Blocks can be blacklisted with the tag `additional_enchantments:ore_sight_blacklist`

## Bracewalk (Legs)
- Default max. level: 4
- Reduces the effectiveness of knock back while also breaking certain blocks which slow you down (specified by the tag `additional_enchantments:bracewalk`) (e.g. Cobwebs)
- Enchantment level scales the knock back reduction

## Hunter (Boots)
- Default max. level: 6
- Causes you to become invisible (gaining hunter stacks) when you're walking on plant related blocks (or while you're inside them)
  - Once you walk outside of those blocks you will slowly start to become visible again (losing hunter stacks)
  - Becoming fully invisible will cause mobs to drop their focus on you, and they will no longer target you
  - Attacking while being fully invisible will deal a critical strike - this will use up all of your hunter stacks
  - The blocks are based on plant related material and the block tag `additional_enchantments:hunter_relevant` (e.g. flowers, crops or leaves)
- Enchantment level increases the critical damage, the rate at which you gain hunter stacks and reduces the rate at which you lose them
  - It will also reduce the amount of stacks needed to be considered at full stacks
- The max. hunter stacks are determined as following: `20 * (3 + Math.max(0, <maximum_enchantment_level> - <current_enchantment_level>)`
  - Meaning if the max. enchantment level is `6` and the current level is `3` the max. stacks will be `120`
  - This means at higher enchantment levels you will have fewer stacks (i.e. you will reach max. stacks more quickly)
  - Higher enchantment levels will lose stacks more slowly

# Misc

![](https://i.imgur.com/J2Hx4GR.gif "Hunter")