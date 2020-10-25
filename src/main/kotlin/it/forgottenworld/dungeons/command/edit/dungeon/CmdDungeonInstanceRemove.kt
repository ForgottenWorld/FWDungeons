package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.bukkitThreadAsync
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

fun cmdDungeonInstanceRemove(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true

    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId]
            ?: run {
                sender.sendFWDMessage("You're not editing any dungeons")
                return true
            }

    if (DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("Dungeon instances may only be removed from fully protoyped dungeons")
        return true
    }

    if (dungeon.instances.isEmpty()) {
        sender.sendFWDMessage("Dungeon has no instances")
        return true
    }

    var id : Int = -4 // defaults to instance not found
    dungeon.instances.removeIf {
        if (it.box.containsPlayer(sender)) {
            id = it.id
            true
        } else false
    }

    try {
        val config = YamlConfiguration()
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")

        if (file.exists()) config.load(file) else {
            sender.sendFWDMessage("No instances at this location")
            return true
        }
        config.set("${dungeon.id}-$id", null)
        bukkitThreadAsync { config.save(file) }
    } catch (e: Exception) {
        Bukkit.getLogger().warning(e.message)
    }

    sender.sendFWDMessage("Removed instance with id $id")

    return true
}