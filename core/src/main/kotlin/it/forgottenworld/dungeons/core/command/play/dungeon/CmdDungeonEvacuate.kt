package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.SenderCommand
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.command.CommandSender

class CmdDungeonEvacuate @Inject constructor(
    private val dungeonManager: DungeonManager
) : SenderCommand() {

    override fun command(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.count() < 2) {
            sender.sendPrefixedMessage(Strings.PROVIDE_BOTH_DUNGEON_AND_INSTANCE_ID)
            return true
        }

        val dungeonId = args[0].toIntOrNull()
        val instanceId = args[1].toIntOrNull()

        if (dungeonId == null || instanceId == null) {
            sender.sendPrefixedMessage(Strings.DUNGEON_AND_INSTANCE_ID_SHOULD_BE_INT)
            return true
        }

        sender.sendPrefixedMessage(
            if (dungeonManager.getFinalDungeonById(dungeonId)
                    ?.let { dungeonManager.getDungeonInstances(it) }
                    ?.get(instanceId)
                    ?.evacuate() != null
            ) {
                Strings.ADVENTURERS_BROUGHT_BACK_TO_SAFETY_INST_RESET
            } else {
                Strings.DUNGEON_INSTANCE_NOT_FOUND
            }
        )

        return true
    }
}