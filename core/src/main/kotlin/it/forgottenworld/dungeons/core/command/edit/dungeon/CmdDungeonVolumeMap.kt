package it.forgottenworld.dungeons.core.command.edit.dungeon

import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.serialization.box.BoxVolumeMap.Companion.getVolumeMap
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon.Companion.editableDungeon
import it.forgottenworld.dungeons.core.utils.dungeonWorld
import it.forgottenworld.dungeons.core.utils.plugin
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonVolumeMap : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val dungeon = sender.editableDungeon ?: run {
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
                .getVolumeMap(dungeonWorld)
                .saveToFile(plugin.dataFolder, "${dungeon.id}_volmap")) {
            sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_COULNDT_BE_SAVED)
            return true
        }

        sender.sendFWDMessage(Strings.DUNGEON_VOLUME_MAP_SAVED)
        return true
    }
}