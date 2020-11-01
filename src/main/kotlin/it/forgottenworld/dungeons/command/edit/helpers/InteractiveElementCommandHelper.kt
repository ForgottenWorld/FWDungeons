package it.forgottenworld.dungeons.command.edit.helpers

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType.ACTIVE_AREA
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType.TRIGGER
import it.forgottenworld.dungeons.utils.ktx.blockVector
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import it.forgottenworld.dungeons.utils.ktx.targetBlock
import it.forgottenworld.dungeons.utils.launch
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object InteractiveElementCommandHelper {

    fun setInteractiveElementPos(sender: Player, posNo: Int, type: InteractiveElementType) {
        val block = sender.targetBlock

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
            return
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return
        }

        if (!dungeon.hasTestInstance) {
            sender.sendFWDMessage("Dungeon box and starting position should be set before adding ${
                if (type == TRIGGER) "triggers"
                else "active areas"
            }")
            return
        }

        if (!dungeon.testInstance!!.box.containsBlock(block)) {
            sender.sendFWDMessage("Target is not inside the dungeon box")
            return
        }

        val builder = if (type == TRIGGER)
            dungeon.triggerBoxBuilder
        else
            dungeon.activeAreaBoxBuilder

        if (posNo == 1)
            builder.pos1(block.blockVector)
        else
            builder.pos2(block.blockVector)

        val box = builder.build()
        if (box == null) {
            sender.sendFWDMessage("${if (posNo == 1) "First" else "Second"} position set, now pick another with /fwde ${
                if (type == TRIGGER) "trigger"
                else "activearea"
            } pos${if (posNo == 1) "2" else "1"}")
            return
        }

        launch {
            val id = dungeon.newInteractiveElement(type, box)
            sender.sendFWDMessage("Created ${
                if (type == TRIGGER) "trigger"
                else "active area"
            } with id $id")
        }

    }

    fun labelInteractiveElement(sender: Player, label: String, type: InteractiveElementType) {
        if (label.isEmpty()) {
            sender.sendFWDMessage("Not enough arguments: please provide a non-whitespace only label")
            return
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return
        }

        if (type == TRIGGER  && dungeon.triggers.isEmpty() || type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()) {
            sender.sendFWDMessage("This dungeon has no ${if (type == TRIGGER) "triggers" else "active areas"} yet")
            return
        }

        dungeon.labelInteractiveElement(type, label)
        sender.sendFWDMessage("Set label $label")
    }

    fun unMakeInteractiveElement(sender: Player, type: InteractiveElementType) {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage("You're not editing any dungeons")
            return
        }

        if (type == TRIGGER  && dungeon.triggers.isEmpty() || type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()) {
            sender.sendFWDMessage("This dungeon has no ${if (type == TRIGGER) "triggers" else "active areas"} yet")
            return
        }

        val id = dungeon.unmakeInteractiveElement(type)
        sender.sendFWDMessage("Deleted ${if (type == TRIGGER) "trigger" else "active area"} with id $id")
    }

    fun grantWandForInteractiveElement(sender: Player, type: InteractiveElementType) {
        if (sender.inventory.itemInMainHand.type != Material.AIR) {
            sender.sendFWDMessage("Your main hand must be empty")
            return
        }

        val material = if (type == TRIGGER) Material.GOLDEN_HOE else Material.GOLDEN_SHOVEL
        sender.inventory.setItemInMainHand(ItemStack(material, 1).apply {
            itemMeta = itemMeta?.apply {
                persistentDataContainer
                        .set(NamespacedKey(
                                FWDungeonsPlugin.instance,
                                if (type == TRIGGER) "FWD_TRIGGER_WAND" else "FWD_ACTIVE_AREA_WAND"
                        ), PersistentDataType.SHORT, 1)
            }
            addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
        })

        sender.sendFWDMessage("You're now holding a wand for making ${if (type == TRIGGER) "triggers" else "active areas"}")

    }
}