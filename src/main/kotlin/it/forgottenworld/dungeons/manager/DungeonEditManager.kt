package it.forgottenworld.dungeons.manager

import it.forgottenworld.dungeons.model.box.BoxBuilder
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.*

object DungeonEditManager {

    val wipDungeons = mutableMapOf<UUID, EditableDungeon>()

    val dungeonBoxBuilders = mutableMapOf<UUID, BoxBuilder>()
    val triggerBoxBuilders = mutableMapOf<UUID, BoxBuilder>()
    val activeAreaBoxBuilders = mutableMapOf<UUID, BoxBuilder>()

    val Player.isEditingDungeon
        get() = wipDungeons.containsKey(uniqueId)

    val UUID.isEditingDungeon
        get() = wipDungeons.containsKey(this)

    val Player.dungeonBoxBuilder
        get() = dungeonBoxBuilders[uniqueId]
                    ?: BoxBuilder().also {
                        dungeonBoxBuilders[uniqueId] = it
                    }

    val UUID.dungeonBoxBuilder
        get() = dungeonBoxBuilders[this]
                ?: BoxBuilder().also {
                    dungeonBoxBuilders[this] = it
                }

    val Player.triggerBoxBuilder
        get() = triggerBoxBuilders[uniqueId]
                ?: BoxBuilder().also {
                    triggerBoxBuilders[uniqueId] = it
                }

    val UUID.triggerBoxBuilder
        get() = triggerBoxBuilders[this]
                ?: BoxBuilder().also {
                    triggerBoxBuilders[this] = it
                }

    val Player.activeAreaBoxBuilder
        get() = activeAreaBoxBuilders[uniqueId]
                ?: BoxBuilder().also {
                    activeAreaBoxBuilders[uniqueId] = it
                }

    val UUID.activeAreaBoxBuilder
        get() = activeAreaBoxBuilders[this]
                ?: BoxBuilder().also {
                    activeAreaBoxBuilders[this] = it
                }

    fun playerExitEditMode(player: Player) {
        (wipDungeons.remove(player.uniqueId) ?: return).testInstance?.onDestroy()

        dungeonBoxBuilders.remove(player.uniqueId)
        triggerBoxBuilders.remove(player.uniqueId)
        activeAreaBoxBuilders.remove(player.uniqueId)

        this.wipDungeons.remove(player.uniqueId)

        player.sendFWDMessage("${ChatColor.GRAY}You're no longer editing a dungeon")
    }
}