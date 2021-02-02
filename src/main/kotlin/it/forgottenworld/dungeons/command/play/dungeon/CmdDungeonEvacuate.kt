package it.forgottenworld.dungeons.command.play.dungeon

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.utils.sendFWDMessage
import org.bukkit.command.CommandSender

fun cmdDungeonEvacuate(sender: CommandSender, args: Array<out String>): Boolean {
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

    sender.sendFWDMessage(if (FinalDungeon.dungeons[dungeonId]?.instances?.get(instanceId)?.evacuate() != null)
        Strings.ADVENTURERS_BROUGHT_BACK_TO_SAFETY_INST_RESET
    else
        Strings.DUNGEON_INSTANCE_NOT_FOUND
    )

    return true
}