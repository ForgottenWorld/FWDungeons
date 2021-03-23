package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.game.unlockables.UnlockableManager
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import org.bukkit.entity.Player

class CmdDungeonJoinInstance @Inject constructor(
    private val dungeonManager: DungeonManager,
    private val unlockableManager: UnlockableManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
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

        val pass = if (args.count() > 2) args[2] else ""

        val dungeon = dungeonManager.getFinalDungeonById(dungeonId) ?: run {
            sender.sendPrefixedMessage(Strings.INVALID_DUNGEON_ID)
            return true
        }

        if (!dungeon.isActive) {
            sender.sendPrefixedMessage(Strings.DUNGEON_IS_NOT_DISABLED)
            return true
        }

        if (dungeonManager.getPlayerInstance(sender.uniqueId) != null) {
            sender.sendPrefixedMessage(Strings.ALREADY_IN_PARTY)
            return true
        }

        val seriesId = dungeon.unlockableSeriesId
        val unlockableId = dungeon.unlockableId
        val isUnlockable = seriesId != null && unlockableId != null
        if (isUnlockable && !unlockableManager.hasPlayerUnlocked(sender, seriesId!!, unlockableId!!)) {
            sender.sendPrefixedMessage(Strings.YOU_HAVENT_UNLOCKED_THIS_YET)
            return true
        }

        val instance = dungeonManager.getDungeonInstances(dungeon)[instanceId] ?: run {
            sender.sendPrefixedMessage(Strings.INVALID_INSTANCE_ID)
            return true
        }

        if (instance.isLocked && pass != instance.partyKey) {
            sender.sendPrefixedMessage(Strings.DUNGEON_PARTY_IS_PRIVATE_YOURE_NOT_INVITED)
            return true
        }

        instance.onPlayerJoin(sender)

        return true
    }
}