package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.command.api.CommandHandler
import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.game.dungeon.FinalDungeon
import it.forgottenworld.dungeons.game.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdDungeonJoinInstance : CommandHandler<Player> {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.count() < 2) {
            sender.sendFWDMessage(Strings.PROVIDE_BOTH_DUNGEON_AND_INSTANCE_ID)
            return true
        }

        val dungeonId = args[0].toIntOrNull()
        val instanceId = args[1].toIntOrNull()

        if (dungeonId == null || instanceId == null) {
            sender.sendFWDMessage(Strings.DUNGEON_AND_INSTANCE_ID_SHOULD_BE_INT)
            return true
        }

        val pass = if (args.count() > 2) args[2] else ""

        val dungeon = FinalDungeon.dungeons[dungeonId] ?: run {
            sender.sendFWDMessage(Strings.INVALID_DUNGEON_ID)
            return true
        }

        if (!dungeon.isActive) {
            sender.sendFWDMessage(Strings.DUNGEON_IS_NOT_DISABLED)
            return true
        }

        if (sender.finalInstance != null) {
            sender.sendFWDMessage(Strings.ALREADY_IN_PARTY)
            return true
        }

        val instance = dungeon.instances[instanceId] ?: run {
            sender.sendFWDMessage(Strings.INVALID_INSTANCE_ID)
            return true
        }

        if (instance.isLocked && pass != instance.partyKey) {
            sender.sendFWDMessage(Strings.DUNGEON_PARTY_IS_PRIVATE_YOURE_NOT_INVITED)
            return true
        }

        instance.onPlayerJoin(sender)

        return true
    }
}