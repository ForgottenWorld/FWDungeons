package it.forgottenworld.dungeons.manager

import it.forgottenworld.dungeons.model.Dungeon
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.util.*

object DungeonEditManager {

    val dungeonEditors = mutableMapOf<UUID, Dungeon>()
    val wipDungeons = mutableListOf<Dungeon>()

    val wipDungeonPos1s = mutableMapOf<UUID, Block>()
    val wipDungeonPos2s = mutableMapOf<UUID, Block>()

    val wipTriggerPos1s = mutableMapOf<UUID, Block>()
    val wipTriggerPos2s = mutableMapOf<UUID, Block>()

    val wipActiveAreaPos1s = mutableMapOf<UUID, Block>()
    val wipActiveAreaPos2s = mutableMapOf<UUID, Block>()

    val wipDungeonOrigins = mutableMapOf<UUID, BlockVector>()
    val wipTestInstances = mutableMapOf<UUID, DungeonInstance>()

    val Player.isEditingDungeon
        get() = dungeonEditors.containsKey(uniqueId)

    fun purgeWorkingData(player: Player) {
        val dungeon = dungeonEditors[player.uniqueId] ?: return

        dungeonEditors.remove(player.uniqueId)
        wipDungeonPos1s.remove(player.uniqueId)
        wipDungeonPos2s.remove(player.uniqueId)
        wipTriggerPos1s.remove(player.uniqueId)
        wipTriggerPos2s.remove(player.uniqueId)
        wipActiveAreaPos1s.remove(player.uniqueId)
        wipActiveAreaPos2s.remove(player.uniqueId)
        wipDungeonOrigins.remove(player.uniqueId)
        wipTestInstances[player.uniqueId]?.doHighlightFrames?.value = false
        wipTestInstances.remove(player.uniqueId)
        wipDungeons.remove(dungeon)

        player.sendFWDMessage("${ChatColor.GRAY}You're no longer editing a dungeon")
    }
}