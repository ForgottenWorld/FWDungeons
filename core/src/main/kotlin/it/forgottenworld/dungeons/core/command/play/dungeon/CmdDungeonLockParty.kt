package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.cli.JsonMessageGenerator
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

class CmdDungeonLockParty @Inject constructor(
    private val jsonMessageGenerator: JsonMessageGenerator,
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            instance.isLocked -> sender.sendPrefixedMessage(Strings.DUNGEON_PARTY_ALREADY_PRIVATE)
            sender.uniqueId == instance.leader -> {
                instance.lock()
                sender.sendMessage(
                    TextComponent.ofChildren(
                        Component.text(Strings.CHAT_PREFIX),
                        Component.text(Strings.PARTY_NOW_PRIVATE_INVITE_WITH_OPEN_WITH),
                        jsonMessageGenerator.unlockLink
                    )
                )
            }
            else -> sender.sendPrefixedMessage(Strings.ONLY_LEADER_MAY_CLOSE_PARTY)
        }

        return true
    }
}