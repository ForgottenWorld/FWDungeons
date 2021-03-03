package it.forgottenworld.dungeons.core.command.edit.unlockables

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import it.forgottenworld.dungeons.core.utils.sendFWDMessage
import org.bukkit.entity.Player

class CmdUnlockablesUnbindPlate @Inject constructor(
    private val unlockableManager: UnlockableManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val loc = sender.location
        val pos = Vector3i(loc.blockX, loc.blockY, loc.blockZ)
        val block = sender.world.getBlockAt(pos.x, pos.y, pos.z)

        if (!UnlockableManager.RECOGNIZED_PRESSURE_PLATE_TYPES.contains(block.type)) {
            sender.sendFWDMessage(Strings.NO_PRESSURE_PLATE_BELOW_YOU)
            return true
        }

        if (unlockableManager.unbindPlate(loc.world.uid, pos)) {
            sender.sendFWDMessage(Strings.PRESSURE_PLATE_HAS_BEEN_UNBOUND)
            return true
        }

        sender.sendFWDMessage(Strings.PRESSURE_PLATE_IS_NOT_BOUND)
        return true
    }
}