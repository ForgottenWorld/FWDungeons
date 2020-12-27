package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.config.ConfigManager
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonSave(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon ?: run {
        sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
        return true
    }

    ConfigManager.saveDungeonConfig(dungeon.finalize(), false)

    sender.sendFWDMessage(Strings.DUNGEON_SAVED)

    return true
}