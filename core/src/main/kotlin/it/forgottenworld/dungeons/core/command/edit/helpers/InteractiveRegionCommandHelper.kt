package it.forgottenworld.dungeons.core.command.edit.helpers

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type.ACTIVE_AREA
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type.TRIGGER
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.NamespacedKeys
import it.forgottenworld.dungeons.core.utils.ParticleSpammer
import it.forgottenworld.dungeons.core.utils.getTargetSolidBlock
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Singleton
class InteractiveRegionCommandHelper @Inject constructor(
    private val namespacedKeys: NamespacedKeys,
    private val configuration: Configuration,
    private val dungeonManager: DungeonManager
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
        val block = sender.getTargetSolidBlock() ?: run {
            sender.sendPrefixedMessage(Strings.YOU_NEED_TO_BE_TARGETING)
            return
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (!dungeon.hasTestOrigin) {
            sender.sendPrefixedMessage(
                Strings.DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE.format(type.plural)
            )
            return
        }

        if (!dungeon.box!!.containsBlock(block, dungeon.testOrigin)) {
            sender.sendPrefixedMessage(Strings.TARGET_NOT_INSIDE_DUNGEON_BOX)
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
            sender.sendPrefixedMessage(
                Strings.NTH_POS_SET_PICK_ANOTHER.format(
                    if (posNo == 1) Strings.FIRST else Strings.SECOND,
                    if (type == TRIGGER) "t" else "aa",
                    if (posNo == 1) 2 else 1
                )
            )
            return
        }

        val id = dungeon.newInteractiveRegion(type, box)
        sender.sendPrefixedMessage(Strings.CREATED_IE_WITH_ID.format(type.singular, id))
    }

    fun labelInteractiveRegion(sender: Player, label: String, type: Type, id: Int = -1) {
        if (label.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_LABEL)
            return
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER &&
            dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        dungeon.labelInteractiveRegion(type, label, id)
        sender.sendPrefixedMessage(Strings.SET_LABEL.format(label))
    }

    fun unMakeInteractiveRegion(sender: Player, type: Type, ieId: Int?) {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER && dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        val id = dungeon.unmakeInteractiveRegion(type, ieId)
        sender.sendPrefixedMessage(Strings.DELETED_IE_WITH_ID.format(type.plural, id))
    }

    fun highlightInteractiveRegion(sender: Player, type: Type, ieId: Int?) {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER && dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA && dungeon.activeAreas.isEmpty()
        ) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET.format(type.plural))
            return
        }

        val regions = if (type == TRIGGER) dungeon.triggers else dungeon.activeAreas
        if (!dungeon.hasTestOrigin) return
        val region = regions[ieId] ?: return

        ParticleSpammer.highlightBox(
            region.box.withContainerOrigin(Vector3i.ZERO, dungeon.testOrigin),
            configuration.dungeonWorld
        )

        sender.sendPrefixedMessage(Strings.HIGHLIGHTED_IE_WITH_ID.format(type.plural, ieId))
    }

    fun grantWandForInteractiveRegion(sender: Player, type: Type) {
        if (sender.inventory.itemInMainHand.type != Material.AIR) {
            sender.sendPrefixedMessage(Strings.MAIN_HAND_MUST_BE_EMPTY)
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
        sender.sendPrefixedMessage(Strings.NOW_HOLDING_WAND_FOR_MAKING_IE.format(type.plural))
    }
}