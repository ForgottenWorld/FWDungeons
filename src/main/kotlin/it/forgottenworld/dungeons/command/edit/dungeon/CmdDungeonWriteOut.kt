package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonWriteOut(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }
    if (!DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    val whatIsMissing = dungeon.whatIsMissingForWriteout()
    if (whatIsMissing.isNotEmpty()) {
        sender.sendFWDMessage("Can't writeout yet, missing: $whatIsMissing")
        return true
    }

    ConfigManager.saveDungeonConfig(FWDungeonsPlugin.pluginDataFolder, dungeon, true)
    DungeonEditManager.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon succesfully exported")

    return true
}