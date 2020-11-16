package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance.Companion.finalInstance
import it.forgottenworld.dungeons.utils.ktx.sendFWDMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun cmdDungeonLookup(sender: CommandSender, args: Array<out String>): Boolean {
    if (args.count() == 0) {
        sender.sendFWDMessage(Strings.PROVIDE_PLAYER_NAME)
        return true
    }

    val player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendFWDMessage(Strings.PLAYER_NOT_FOUND)
                return true
            }

    val instance = player.finalInstance
            ?: run {
                sender.sendFWDMessage(Strings.PLAYER_IS_NOT_IN_PARTY_OR_INSTANCE)
                return true
            }

    sender.sendFWDMessage(Strings.LOOKUP_RESULT.format(args[0], instance.dungeon.id, instance.id))

    return true
}