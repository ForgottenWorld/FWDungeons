package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.launchAsync
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

fun cmdDungeonInstanceRemove(sender: Player, args: Array<out String>): Boolean {
    val dungeon = DungeonEditManager.wipDungeons[sender.uniqueId]
            ?: run {
                sender.sendFWDMessage("You're not editing any dungeons")
                return true
            }

    val finalInstanceId = dungeon.finalInstanceLocations.keys.lastOrNull() ?: run {
        sender.sendFWDMessage("Dungeon has no instances")
        return true
    }

    dungeon.finalInstanceLocations.remove(finalInstanceId)

    try {
        val config = YamlConfiguration()
        val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")
        if (file.exists()) config.load(file) else return true

        config.set("${dungeon.id}-$finalInstanceId", null)
        launchAsync { config.save(file) }
    } catch (e: Exception) {
        Bukkit.getLogger().warning(e.message)
    }

    sender.sendFWDMessage("Last instance was removed")

    return true
}