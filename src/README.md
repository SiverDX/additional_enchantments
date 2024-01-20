# Enchantments
**Enchantment Categories**
- Melee: Sword, Axe and Trident
- Ranged: Bow and Crossbow

**Configuration**
- All enchantments are configurable

## Faster Attacks (Melee)
- Default max. level: 4
- Increases attack speed
- Enchantment level increases the amount

## Explosive Tip (Ranged)
- Default max. level: 4
- Causes an explosion on impact (which does not damage the shooter or their allies, experience orbs or items)
- Enchantment level increases explosion radius
- It's possible to switch between two modes (NONE and BREAK (which breaks blocks)) with the keybind (default being `J`)

## Homing (Ranged)
- Default max. level: 4
- Projectiles fly towards a chosen target
  - Targets can be blacklisted with `additional_enchantments:homing_blacklist` (`minecraft:villager` is added by default)
- Enchantment level increases the radius in which a target gets picked (+ it slightly increases velocity of the arrow)
- It's possible to switch between various modes with the keybind (default being `H`)
    - **Type**: MONSTER, ANIMAL, BOSSES (`forge:bosses`), ANY and NONE (homing effect is not being applied)
    - **Priority**: (when `Shift` is also pressed): CLOSEST, LOWEST_HEALTH, HIGHEST_HEALTH and RANDOM

## Shatter (Ranged)
- Default max. level: 4
- Allows the usage of `Amethyst Shards` as projectiles
  - There is a high chance for them to break on impact which will deal damage in an aoe radius (the projectile and aoe deal magic damage)
    - Targets can be blacklisted with `additional_enchantments:shatter_aoe_blacklist` (`minecraft:villager` is added by default)
- Enchantment level increases the shatter aoe radius and damage

## Straight Shot (Ranged)
- Default max. level: 1
- Disables gravity for shot projectiles and has a chance to damage `Enderman`
- Enchantment level increases the chance to damage `Enderman`

## Tipped (Ranged)
- Default max. level: 4
- Applies random effects to the arrow (and in turn to the target it hits)
- Enchantment level increases the amount, amplifier and duration of the effects
- It's possible to switch between effect categories (HARMFUL, BENEFICIAL and NEUTRAL (can apply all)) with the keybind (default being `G`)
  - Effects can be blacklisted with `additional_enchantmetns:tipped_blacklist`