# Enchantments
### Enchantment Categories
- **Melee**: Sword, Axe and Trident
- **Ranged**: Bow and Crossbow

### Configuration
- All enchantments are configurable

## Faster Attacks (Melee)
- Default max. level: 4
- Increases attack speed
- Enchantment level increases the amount

## Poison (Melee)
- Default max. level: 6
- Applies a poison effect to entities which deals magic damage
- Enchantment level increases the duration and damage while also reducing the duration between damage ticks
- Not compatible with the `Wither` enchantment

## Wither (Melee)
- Default max. level: 6
- Applies a wither effect to entities which deals wither damage
- Enchantment level increases the duration and damage while also reducing the duration between damage ticks
- Not compatible with the `Poison` enchantment

## Explosive Tip (Ranged)
- Default max. level: 4
- Causes an explosion on impact (which does not damage the shooter, their allies, experience orbs or items)
- Enchantment level increases explosion radius
- It's possible to switch between two modes (NONE and BREAK (which breaks blocks)) with the keybind (default being `J`)

## Homing (Ranged)
- Default max. level: 4
- Projectiles fly towards a chosen target
  - Targets can be blacklisted with `additional_enchantments:homing_blacklist` (`minecraft:villager` and `minecraft:iron_golem` are added by default)
  - Invisible entities are only targeted if they're glowing (server-side (`Perception` is client-only))
- Enchantment level increases the radius in which a target gets picked (+ it slightly increases velocity of the arrow)
- It's possible to switch between various modes with the keybind (default being `H`)
    - **Type**: MONSTER, ANIMAL, BOSSES (`forge:bosses`), ANY and NONE (homing effect is not being applied)
    - **Priority**: (when `Shift` is also pressed): CLOSEST, LOWEST_HEALTH, HIGHEST_HEALTH and RANDOM

## Shatter (Ranged)
- Default max. level: 4
- Allows the usage of `Amethyst Shards` as projectiles
  - There is a high chance for them to break on impact which will deal damage in an area (the projectile and area damage are `Magic`)
    - Targets can be blacklisted with `additional_enchantments:shatter_aoe_blacklist` (`minecraft:villager` and `minecraft:iron_golem` are added by default)
- Enchantment level increases the shatter area of effect and damage

## Straight Shot (Ranged)
- Default max. level: 4
- Disables gravity for shot projectiles and has a chance to hit `Enderman`
- Enchantment level increases the chance to hit `Enderman`

## Tipped (Ranged)
- Default max. level: 4
- Applies random effects to the arrow (and in turn to the target it hits)
- Enchantment level increases the amount, amplifier and duration of the effects
- It's possible to switch between effect categories (HARMFUL, BENEFICIAL and NEUTRAL (can apply all)) with the keybind (default being `G`)
  - Effects can be blacklisted with `additional_enchantmetns:tipped_blacklist`

## Perception (Helmet)
- Default max. level: 4
- Outlines nearby entities and distinguishes them by color
  - DARK_PURPLE: `forge:bosses`, RED: Monsters, DARK_GREEN: Tamable Animals, GREEN: Animals, BLUE: Other
  - Items are colored depending on the color of their display name (GOLD if none is available)
- Enchantment level increases the range
- It's possible to switch between different modes (ALL, NO_ITEMS and NONE) with the keybind (default being `U`)
  - If `Shift` is pressed it will cycle through options for items, filtering them by rarity (COMMON, UNCOMMON, RARE, EPIC)
  - Entities can be blacklisted with `additional_enchantments:perception_blacklist`
  - It's configurable whether invisible entities are outlined or not (`true` by default)