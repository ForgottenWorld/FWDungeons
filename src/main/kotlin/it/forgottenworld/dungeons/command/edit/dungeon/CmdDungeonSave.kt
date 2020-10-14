package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun cmdDungeonSave(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (DungeonEditState.wipDungeons.contains(dungeon)) run {
        sender.sendFWDMessage("This dungeon was never exported, use /fwde dungeon writeout")
        return true
    }

    ConfigManager.saveDungeonConfig(
            FWDungeonsPlugin.pluginDataFolder,
            dungeon,
            false
    )

    DungeonEditState.purgeWorkingData(sender)
    sender.sendFWDMessage("Dungeon saved succesfully")

    return true
}