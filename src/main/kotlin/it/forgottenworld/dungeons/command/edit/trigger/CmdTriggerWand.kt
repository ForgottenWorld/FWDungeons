package it.forgottenworld.dungeons.command.edit.trigger

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun cmdTriggerWand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    if (sender.inventory.itemInMainHand.type != Material.AIR) run {
        sender.sendFWDMessage("Your main hand must be empty")
        return true
    }

    sender.inventory.setItemInMainHand(ItemStack(Material.GOLDEN_HOE, 1).apply {
        itemMeta = itemMeta?.apply {
            persistentDataContainer.set(
                    NamespacedKey(FWDungeonsPlugin.instance, "FWD_TRIGGER_WAND"),
                    PersistentDataType.SHORT, 1)
        }
        addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
    })

    sender.sendFWDMessage("You're now holding a wand for making triggers")

    return true
}