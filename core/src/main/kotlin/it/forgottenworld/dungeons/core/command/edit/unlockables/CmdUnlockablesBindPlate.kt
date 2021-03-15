package it.forgottenworld.dungeons.core.command.edit.unlockables

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdUnlockablesBindPlate @Inject constructor(
    private val unlockableManager: UnlockableManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val seriesId = args.getOrNull(0)?.toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.PROVIDE_VALID_UNLOCKABLE_SERIES_ID)
            return true
        }

        val unlockableId = args.getOrNull(1)?.toIntOrNull() ?: run {
            sender.sendPrefixedMessage(Strings.PROVIDE_VALID_UNLOCKABLE_ID)
            return true
        }

        val loc = sender.location
        val pos = Vector3i(loc.blockX, loc.blockY, loc.blockZ)
        val block = sender.world.getBlockAt(pos.x, pos.y, pos.z)

        if (!UnlockableManager.RECOGNIZED_PRESSURE_PLATE_TYPES.contains(block.type)) {
            sender.sendPrefixedMessage(Strings.NO_PRESSURE_PLATE_BELOW_YOU)
            return true
        }

        unlockableManager.bindPlateToUnlockable(seriesId, unlockableId, loc.world.uid, pos)

        sender.sendPrefixedMessage(Strings.PRESSURE_PLATE_IS_NOW_BOUND, seriesId, unlockableId)
        return true
    }
}