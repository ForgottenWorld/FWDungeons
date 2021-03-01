package it.forgottenworld.dungeons.core.command.edit.helpers

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type.ACTIVE_AREA
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type.TRIGGER
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager.editableDungeon
import it.forgottenworld.dungeons.core.utils.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Singleton
class InteractiveRegionCommandHelper @Inject constructor(
    private val namespacedKeys: NamespacedKeys,
    private val configuration: Configuration
) {

    private val Type.singular get() = when(this) {
        ACTIVE_AREA -> "active area"
        TRIGGER -> "trigger"
    }

    private val Type.plural get() = when(this) {
        ACTIVE_AREA -> Strings.ACTIVE_AREAS
        TRIGGER -> Strings.TRIGGERS
    }

    fun setInteractiveRegionPos(sender: Player, posNo: Int, type: Type) {
        val block = sender.getTargetSolidBlock()

        if (block.blockData.material == Material.AIR) {
            sender.sendFWDMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (!dungeon.hasTestOrigin) {
            sender.sendFWDMessage(
                Strings.DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE.format(type.plural)
            )
            return
        }

        if (!dungeon.box!!.containsBlock(block, dungeon.testOrigin)) {
            sender.sendFWDMessage(Strings.TARGET_NOT_INSIDE_DUNGEON_BOX)
            return
        }

        val builder = if (type == TRIGGER) {
            dungeon.triggerBoxBuilder
        } else {
            dungeon.activeAreaBoxBuilder
        }

        if (posNo == 1) {
            builder.pos1(Vector3i.ofBlock(block))
        } else {
            builder.pos2(Vector3i.ofBlock(block))
        }

        val box = builder.build()
        if (box == null) {
            sender.sendFWDMessage(
                Strings.NTH_POS_SET_PICK_ANOTHER.format(
                    if (posNo == 1) Strings.FIRST else Strings.SECOND,
                    if (type == TRIGGER) "t" else "aa",
                    if (posNo == 1) 2 else 1
                )
            )
            return
        }

        launch {
            val id = dungeon.newInteractiveRegion(type, box)
            sender.sendFWDMessage(Strings.CREATED_IE_WITH_ID.format(type.singular, id))
        }

    }

    fun labelInteractiveRegion(sender: Player, label: String, type: Type, id: Int = -1) {
        if (label.isEmpty()) {
            sender.sendFWDMessage(Strings.NEA_PROVIDE_LABEL)
            return
        }

        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER &&
            dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        dungeon.labelInteractiveRegion(type, label, id)
        sender.sendFWDMessage(Strings.SET_LABEL.format(label))
    }

    fun unMakeInteractiveRegion(sender: Player, type: Type, ieId: Int?) {
        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER && dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        val id = dungeon.unmakeInteractiveRegion(type, ieId)
        sender.sendFWDMessage(Strings.DELETED_IE_WITH_ID.format(type.plural, id))
    }

    fun highlightInteractiveRegion(sender: Player, type: Type, ieId: Int?) {
        val dungeon = sender.uniqueId.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER && dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendFWDMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        val irs = if (type == TRIGGER) dungeon.triggers else dungeon.activeAreas
        if (!dungeon.hasTestOrigin) return
        val ir = irs[ieId] ?: return
        ParticleSpammer.highlightBox(
            ir.box.withContainerOrigin(Vector3i.ZERO, dungeon.testOrigin),
            configuration.dungeonWorld
        )

        sender.sendFWDMessage(Strings.HIGHLIGHTED_IE_WITH_ID.format(type.plural, ieId))
    }

    fun grantWandForInteractiveRegion(sender: Player, type: Type) {
        if (sender.inventory.itemInMainHand.type != Material.AIR) {
            sender.sendFWDMessage(Strings.MAIN_HAND_MUST_BE_EMPTY)
            return
        }
        val material = if (type == TRIGGER) Material.GOLDEN_HOE else Material.GOLDEN_SHOVEL
        val nsk = if (type == TRIGGER) namespacedKeys.triggerTool else namespacedKeys.activeAreaTool
        val itemStack = ItemStack(material, 1).apply {
            itemMeta = itemMeta.apply {
                persistentDataContainer.set(nsk, PersistentDataType.SHORT, 1)
            }
            addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
        }
        sender.inventory.setItemInMainHand(itemStack)
        sender.sendFWDMessage(Strings.NOW_HOLDING_WAND_FOR_MAKING_IE.format(type.plural))
    }
}