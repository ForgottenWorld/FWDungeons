package it.forgottenworld.dungeons.core.command.edit.unlockables

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.math.Vector3i
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdUnlockablesLookupPlate @Inject constructor(
    private val unlockableManager: UnlockableManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val loc = sender.location
        val pos = Vector3i(loc.blockX, loc.blockY, loc.blockZ)
        val block = sender.world.getBlockAt(pos.x, pos.y, pos.z)

        if (!unlockableManager.isBlockRecognizedPlate(block)) {
            sender.sendPrefixedMessage(Strings.NO_PRESSURE_PLATE_BELOW_YOU)
            return true
        }

        val unlockableData = unlockableManager.lookUpPlate(loc.world.uid, pos) ?: run {
            sender.sendPrefixedMessage(Strings.PRESSURE_PLATE_IS_NOT_BOUND)
            return true
        }

        sender.sendPrefixedMessage(
            Strings.PRESSURE_PLATE_IS_BOUND_TO,
            unlockableData.first,
            unlockableData.second
        )
        return true
    }
}