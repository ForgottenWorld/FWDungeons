package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonWriteOut(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    val whatIsMissing = dungeon.whatIsMissingForWriteout()
    if (whatIsMissing.isNotEmpty()) {
        sender.sendFWDMessage("Can't writeout yet, missing: $whatIsMissing")
        return true
    }

    val finalDungeon = dungeon.finalize()
    ConfigManager.saveDungeonConfig(FWDungeonsPlugin.pluginDataFolder, finalDungeon, true)
    sender.sendFWDMessage("Dungeon succesfully exported")

    return true
}