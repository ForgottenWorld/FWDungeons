package it.forgottenworld.dungeons.command.edit.activearea

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun cmdActiveAreaWand(sender: Player, args: Array<out String>): Boolean {
    if (sender.inventory.itemInMainHand.type != Material.AIR) {
        sender.sendFWDMessage("Your main hand must be empty")
        return true
    }

    sender.inventory.setItemInMainHand(ItemStack(Material.GOLDEN_SHOVEL, 1).apply {
        itemMeta = itemMeta?.apply {
            persistentDataContainer
                    .set(NamespacedKey(
                            FWDungeonsPlugin.instance,
                            "FWD_ACTIVE_AREA_WAND"
                    ), PersistentDataType.SHORT, 1)
        }
        addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
    })

    sender.sendFWDMessage("You're now holding a wand for making active areas")

    return true
}