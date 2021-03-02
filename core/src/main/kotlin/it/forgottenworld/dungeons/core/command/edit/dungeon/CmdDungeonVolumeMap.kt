package it.forgottenworld.dungeons.core.command.edit.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.serialization.box.BoxVolumeMap.Companion.getVolumeMap
import it.forgottenworld.dungeons.core.FWDungeonsPlugin
import it.forgottenworld.dungeons.core.config.Configuration
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonVolumeMap @Inject constructor(
    private val plugin: FWDungeonsPlugin,
    private val configuration: Configuration,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = dungeonManager.getPlayerEditableDungeon(sender.uniqueId) ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        if (!dungeon.hasTestOrigin) {
            sender.sendFWDMessage(Strings.DUNGEON_BOX_SHOULD_BE_SET_BEFORE_SAVING_VOLUME_MAP)
            return true
        }

        if (!dungeon
                .box!!
                .withOrigin(dungeon.testOrigin)
                .getVolumeMap(configuration.dungeonWorld)
                .saveToFile(plugin.dataFolder, "${dungeon.id}_volmap")
        ) {
            sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_COULNDT_BE_SAVED)
            return true
        }

        sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_SAVED)
        return true
    }
}