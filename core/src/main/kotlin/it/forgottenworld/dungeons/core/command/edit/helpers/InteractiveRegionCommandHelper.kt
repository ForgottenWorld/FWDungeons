package it.forgottenworld.dungeons.core.command.edit.helpers

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion.Type.*
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.storage.Configuration
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
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

    private val Type.singular
        get() = when (this) {
            ACTIVE_AREA -> "active area"
            SPAWN_AREA -> "spawn area"
            TRIGGER -> "trigger"
        }

    private val Type.plural
        get() = when (this) {
            ACTIVE_AREA -> Strings.ACTIVE_AREAS
            SPAWN_AREA -> Strings.SPAWN_AREAS
            TRIGGER -> Strings.TRIGGERS
        }

    fun setInteractiveRegionPos(
        sender: Player,
        posNo: Int,
        type: Type
    ) {
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
                Strings.DUNGEON_BOX_AND_STARTPOS_SHOULD_BE_SET_BEFORE_ADDING_IE,
                type.plural
            )
            return
        }

        if (!dungeon.box!!.containsBlock(block, dungeon.testOrigin)) {
            sender.sendPrefixedMessage(Strings.TARGET_NOT_INSIDE_DUNGEON_BOX)
            return
        }

        val builder = when (type) {
            TRIGGER -> dungeon.triggerBoxBuilder
            ACTIVE_AREA -> dungeon.activeAreaBoxBuilder
            SPAWN_AREA -> dungeon.spawnAreaBoxBuilder
        }

        if (posNo == 1) {
            builder.pos1(Vector3i.ofBlock(block))
        } else {
            builder.pos2(Vector3i.ofBlock(block))
        }

        val cmd = when (type) {
            TRIGGER -> "t"
            ACTIVE_AREA -> "aa"
            SPAWN_AREA -> "sa"
        }

        val box = builder.build()
        if (box == null) {
            sender.sendPrefixedMessage(
                Strings.NTH_POS_SET_PICK_ANOTHER,
                if (posNo == 1) Strings.FIRST else Strings.SECOND,
                cmd,
                if (posNo == 1) 2 else 1
            )
            return
        }

        val id = dungeon.newInteractiveRegion(type, box)
        sender.sendPrefixedMessage(Strings.CREATED_IE_WITH_ID, type.singular, id)
        sender.sendMessage(
            TextComponent.ofChildren(
                Component.text(Strings.CLICK, NamedTextColor.WHITE),
                Component.text(Strings.HERE, NamedTextColor.GOLD)
                    .clickEvent(ClickEvent.suggestCommand("/fwde $cmd label id:$id ")),
                Component.text(Strings.TO_LABEL_IT)
            )
        )
    }

    fun labelInteractiveRegion(
        sender: Player,
        label: String,
        type: Type,
        id: Int = -1
    ) {
        if (label.isEmpty()) {
            sender.sendPrefixedMessage(Strings.NEA_PROVIDE_LABEL)
            return
        }

        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        val collection = when (type) {
            TRIGGER -> dungeon.triggers
            ACTIVE_AREA -> dungeon.activeAreas
            SPAWN_AREA -> dungeon.spawnAreas
        }

        if (collection.isEmpty()) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET, type.plural)
            return
        }

        if (collection.values.any { it.label.equals(label, ignoreCase = true) }) {
            sender.sendPrefixedMessage(Strings.LABEL_IS_TAKEN)
            return
        }

        dungeon.labelInteractiveRegion(type, label, id)
        sender.sendPrefixedMessage(Strings.SET_LABEL, label)
    }

    fun unMakeInteractiveRegion(
        sender: Player,
        type: Type,
        ieId: Int?
    ) {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER &&
            dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA &&
            dungeon.activeAreas.isEmpty() ||
            type == SPAWN_AREA &&
            dungeon.spawnAreas.isEmpty()
        ) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET, type.plural)
            return
        }

        val id = dungeon.unmakeInteractiveRegion(type, ieId)
        sender.sendPrefixedMessage(Strings.DELETED_IE_WITH_ID, type.plural, id)
    }

    fun highlightInteractiveRegion(
        sender: Player,
        type: Type,
        ieId: Int?
    ) {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return
        }

        if (type == TRIGGER &&
            dungeon.triggers.isEmpty() ||
            type == ACTIVE_AREA &&
            dungeon.activeAreas.isEmpty() ||
            type == SPAWN_AREA &&
            dungeon.spawnAreas.isEmpty()
        ) {
            sender.sendPrefixedMessage(Strings.THIS_DUNGEON_HAS_NO_IE_YET, type.plural)
            return
        }

        val regions = when (type) {
            TRIGGER -> dungeon.triggers
            ACTIVE_AREA -> dungeon.activeAreas
            SPAWN_AREA -> dungeon.spawnAreas
        }
        if (!dungeon.hasTestOrigin) return
        val region = regions[ieId] ?: return

        ParticleSpammer.highlightBox(
            region.box.withContainerOrigin(Vector3i.ZERO, dungeon.testOrigin),
            configuration.dungeonWorld
        )

        sender.sendPrefixedMessage(Strings.HIGHLIGHTED_IE_WITH_ID, type.plural, ieId)
    }

    fun grantWandForInteractiveRegion(
        sender: Player,
        type: Type
    ) {
        if (sender.inventory.itemInMainHand.type != Material.AIR) {
            sender.sendPrefixedMessage(Strings.MAIN_HAND_MUST_BE_EMPTY)
            return
        }
        val material: Material
        val nsk: NamespacedKey
        when (type) {
            TRIGGER -> {
                material = Material.GOLDEN_HOE
                nsk = namespacedKeys.triggerTool
            }
            ACTIVE_AREA -> {
                material = Material.GOLDEN_SHOVEL
                nsk = namespacedKeys.activeAreaTool
            }
            SPAWN_AREA -> {
                material = Material.CARROT_ON_A_STICK
                nsk = namespacedKeys.spawnAreaTool
            }
        }
        val itemStack = ItemStack(material, 1).apply {
            itemMeta = itemMeta.apply {
                persistentDataContainer.set(nsk, PersistentDataType.SHORT, 1)
            }
            addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10)
        }
        sender.inventory.setItemInMainHand(itemStack)
        sender.sendPrefixedMessage(Strings.NOW_HOLDING_WAND_FOR_MAKING_IE, type.plural)
    }
}