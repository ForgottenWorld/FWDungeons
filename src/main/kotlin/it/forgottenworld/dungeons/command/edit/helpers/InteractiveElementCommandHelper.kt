package it.forgottenworld.dungeons.command.edit.helpers

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType.ACTIVE_AREA
import it.forgottenworld.dungeons.model.interactiveelement.InteractiveElementType.TRIGGER
import it.forgottenworld.dungeons.utils.ktx.*
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
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (!dungeon.hasTestInstance) {
            sender.sendFWDMessage(Strings.DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE.format(
                    if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS
            ))
            return
        }

        if (!dungeon.testInstance!!.box.containsBlock(block)) {
            sender.sendFWDMessage(Strings.TARGET_NOT_INSIDE_DUNGEON_BOX)
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
            sender.sendFWDMessage(Strings.NTH_POS_SET_PICK_ANOTHER.format(
                    if (posNo == 1) Strings.FIRST else Strings.SECOND,
                    "${if (type == TRIGGER) "trigger" else "activearea"} pos${if (posNo == 1) "2" else "1"}"))
            return
        }

        launch {
            val id = dungeon.newInteractiveElement(type, box)
            sender.sendFWDMessage(Strings.CREATED_IE_WITH_ID.format(if (type == TRIGGER) "trigger" else "active area", id))
        }

    }

    fun labelInteractiveElement(sender: Player, label: String, type: InteractiveElementType) {
        if (label.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_LABEL)
            return
        }

        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER  && dungeon.triggers.isEmpty() || type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS))
            return
        }

        dungeon.labelInteractiveElement(type, label)
        sender.sendFWDMessage(Strings.SET_LABEL.format(label))
    }

    fun unMakeInteractiveElement(sender: Player, type: InteractiveElementType, ieId: Int?) {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER  && dungeon.triggers.isEmpty() || type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS))
            return
        }

        val id = dungeon.unmakeInteractiveElement(type, ieId)
        sender.sendFWDMessage(Strings.DELETED_IE_WITH_ID.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS, id))
    }

    fun highlightInteractiveElement(sender: Player, type: InteractiveElementType, ieId: Int?) {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER  && dungeon.triggers.isEmpty() || type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS))
            return
        }

        (if (type == TRIGGER) dungeon.testInstance?.triggers else dungeon.testInstance?.activeAreas)
                ?.get(ieId)?.box?.highlightAll()
        sender.sendFWDMessage(Strings.HIGHLIGHTED_IE_WITH_ID.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS, ieId))
    }

    fun grantWandForInteractiveElement(sender: Player, type: InteractiveElementType) {
        if (sender.inventory.itemInMainHand.type != Material.AIR) {
            sender.sendFWDMessage(Strings.MAIN_HAND_MUST_BE_EMPTY)
            return
        }

        val material = if (type == TRIGGER) Material.GOLDEN_HOE else Material.GOLDEN_SHOVEL
        sender.inventory.setItemInMainHand(ItemStack(material, 1).apply {
            itemMeta = itemMeta?.apply {
                persistentDataContainer
                        .set(NamespacedKey(
                                getPlugin(),
                                if (type == TRIGGER) "FWD_TRIGGER_WAND" else "FWD_ACTIVE_AREA_WAND"
                        ), PersistentDataType.SHORT, 1)
            }
            addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
        })

        sender.sendFWDMessage(Strings.NOW_HOLDING_WAND_FOR_MAKING_IE.format(if (type == TRIGGER) Strings.TRIGGERS else Strings.ACTIVE_AREAS))

    }
}