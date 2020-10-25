package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonSave(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (DungeonEditManager.wipDungeons.contains(dungeon)) run {
        sender.sendFWDMessage("This dungeon was never exported, use /fwde dungeon writeout")
        return true
    }

    ConfigManager.saveDungeonConfig(
            FWDungeonsPlugin.pluginDataFolder,
            dungeon,
            false
    )

    DungeonEditManager.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon saved succesfully")

    return true
}