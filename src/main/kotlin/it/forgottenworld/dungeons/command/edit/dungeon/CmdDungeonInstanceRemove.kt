package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.model.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.entity.Player

fun cmdDungeonInstanceRemove(sender: Player, args: Array<out String>): Boolean {
    val dungeon = sender.editableDungeon
            ?: run {
                sender.sendFWDMessage("You're not editing any dungeons")
                return true
            }

    if (dungeon.finalInstanceLocations.size < 2) {
        sender.sendFWDMessage("Dungeons can't have less than one instance")
        return true
    }

    val instToRemove = if (args.isEmpty()) dungeon.finalInstanceLocations.lastIndex else args[0].toIntOrNull()

    if (instToRemove == null) {
        sender.sendFWDMessage("You must either provide an index, or no index (to remove the latest instance)")
        return true
    }

    dungeon.finalInstanceLocations.removeAt(instToRemove)

    sender.sendFWDMessage("Removed instance at index $instToRemove")

    return true
}