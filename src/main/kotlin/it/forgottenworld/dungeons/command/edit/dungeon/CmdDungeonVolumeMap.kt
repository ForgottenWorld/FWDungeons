package it.forgottenworld.dungeons.command.edit.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.box.BoxVolumeMap.Companion.volumeMap
import it.forgottenworld.dungeons.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.utils.plugin
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonVolumeMap : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
            sender.sendFWDMessage(Strings.NOT_EDITING_ANY_DUNGEONS)
            return true
        }

        val box = dungeon.testInstance?.box ?: run {
            sender.sendFWDMessage(Strings.DUNGEON_BOX_SHOULD_BE_SET_BEFORE_SAVING_VOLUME_MAP)
            return true
        }

        if (!box.volumeMap.saveToFile(plugin.dataFolder, "${dungeon.id}_volmap")) {
            sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_COULNDT_BE_SAVED)
            return true
        }

        sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_SAVED)
        return true
    }
}