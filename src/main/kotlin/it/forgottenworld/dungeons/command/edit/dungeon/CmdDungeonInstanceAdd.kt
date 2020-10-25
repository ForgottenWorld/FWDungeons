package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.FWDungeonsPlugin
import it.forgottenworld.dungeons.model.ActiveArea
import it.forgottenworld.dungeons.model.DungeonInstance
import it.forgottenworld.dungeons.model.Trigger
import it.forgottenworld.dungeons.manager.DungeonEditManager
import it.forgottenworld.dungeons.utils.blockVector
import it.forgottenworld.dungeons.utils.bukkitThreadAsync
import it.forgottenworld.dungeons.utils.sendFWDMessage
import it.forgottenworld.dungeons.utils.targetBlock
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.BlockVector
import java.io.File

fun cmdDungeonInstanceAdd(sender: CommandSender, command: Command, _label: String, args: Array<String>): Boolean {
    if (sender !is Player) return true
    val block = sender.targetBlock

    if (block.blockData.material == Material.AIR) {
        sender.sendFWDMessage("You need to be targeting a block within 5 blocks of you before calling this")
        return true
    }


    val dungeon = DungeonEditManager.dungeonEditors[sender.uniqueId] ?: run {
        sender.sendFWDMessage("You're not editing any dungeons")
        return true
    }

    if (DungeonEditManager.wipDungeons.contains(dungeon)) {
        sender.sendFWDMessage("Dungeon instances may only be created for fully protoyped dungeons")
        return true
    }

    val id = dungeon.instances.maxByOrNull { it.id }?.id?.plus(1) ?: 0
    dungeon.instances.add(
            DungeonInstance(
                    id,
                    dungeon,
                    block.location.toVector().toBlockVector(),
                    dungeon.triggers.map {
                        Trigger(it.id,
                                it.dungeon,
                                it.box.withContainerOrigin(BlockVector(0,0,0), block.blockVector),
                                it.effect,
                                it.requiresWholeParty
                        ).apply { label = it.label }
                    }.map { it.id to it }.toMap().toMutableMap(),
                    dungeon.activeAreas.map {
                        ActiveArea(it.id,
                                it.box.withContainerOrigin(BlockVector(0,0,0), block.blockVector),
                                it.startingMaterial
                        ).apply { label = it.label}
                    }.toMutableList()
            ).apply {
                try {
                    val config = YamlConfiguration()
                    val file = File(FWDungeonsPlugin.pluginDataFolder, "instances.yml")

                    if (file.exists()) config.load(file)

                    toConfig(config.createSection("${dungeon.id}-$id"))

                    bukkitThreadAsync { config.save(file) }
                } catch (e: Exception) {
                    Bukkit.getLogger().warning(e.message)
                }
                resetInstance()
            })

    sender.sendFWDMessage("Created instance with id $id")

    return true
}