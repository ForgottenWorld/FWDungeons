package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.service.DungeonEditService
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonSave(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditService.wipDungeons[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    ConfigManager.saveDungeonConfig(
            FWDungeonsPlugin.pluginDataFolder,
            dungeon.finalize(),
            false
    )

    DungeonEditService.playerExitEditMode(sender)
    sender.sendFWDMessage("Dungeon saved succesfully")

    return true
}