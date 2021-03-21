package it.forgottenworld.dungeons.core.command.edit.trigger

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.command.edit.helpers.InteractiveRegionCommandHelper
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdTriggerLabel @Inject constructor(
    private val interactiveRegionCommandHelper: InteractiveRegionCommandHelper
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.LABEL_CANNOT_BE_EMPTY)
            return true
        }
        val firstArg = args[0]
        if (firstArg.startsWith("id:")) {
            val id = firstArg.drop(3).toIntOrNull() ?: run {
                sender.sendPrefixedMessage(Strings.NO_TRIGGER_WITH_SUCH_ID)
                return true
            }
            if (args.size == 1) {
                sender.sendPrefixedMessage(Strings.LABEL_CANNOT_BE_EMPTY)
                return true
            }
            val label = args.drop(1).joinToString(" ").trim()
            interactiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER, id)
            return true
        }
        val label = args.joinToString(" ").trim()
        interactiveRegionCommandHelper.labelInteractiveRegion(sender, label, InteractiveRegion.Type.TRIGGER)
        return true
    }
}