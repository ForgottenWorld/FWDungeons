package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.state.DungeonEditState
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.toVector
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector

fun cmdDungeonSetStart(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val dungeon = DungeonEditState.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }
    if (!dungeon.hasBox) {
        sender.sendFWDMessage("Dungeon box should be set before adding a starting location")
        return true
    }

    var newBlock: Block? = null
    if (DungeonEditState.wipDungeonOrigins[sender.uniqueId]?.let {
                newBlock = sender.world.getBlockAt(
                        sender.location.subtract(
                                it.toVector())
                )
                dungeon.box.withOrigin(it).containsPlayer(sender)

            } != true) {
        sender.sendFWDMessage("You're outside of the dungeon box")
        return true
    }

    dungeon.startingLocation = BlockVector(newBlock!!.location.toVector())
    sender.sendFWDMessage("Dungeon starting location set succesfully")

    return true
}