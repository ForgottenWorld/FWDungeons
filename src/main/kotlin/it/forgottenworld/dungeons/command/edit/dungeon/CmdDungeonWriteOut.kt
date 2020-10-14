package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonWriteOut(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }
    if (!DungeonEditState.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("This dungeon was already exported beforehand")
        return true
    }

    val whatIsMissing = dungeon.whatIsMissingForWriteout()
    if (whatIsMissing.isNotEmpty()) {
        sender.sendFWDMessage("Can't writeout yet, missing: $whatIsMissing")
        return true
    }

    ConfigManager.saveDungeonConfig(FWDungeonsPlugin.pluginDataFolder, dungeon, true)
    DungeonEditState.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon succesfully exported")

    return true
}