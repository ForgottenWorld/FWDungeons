package it.forgottenworld.dungeons.core.command.play.dungeon

import com.google.inject.Inject
import it.forgottenworld.dungeons.api.command.PlayerCommand
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.core.storage.Strings
import it.forgottenworld.dungeons.core.utils.sendPrefixedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class CmdDungeonUnlockParty @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {

        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        when {
            !instance.isLocked -> {
                sender.sendPrefixedMessage(Strings.DUNGEON_PARTY_ALREADY_PUBLIC)
            }
            sender.uniqueId == instance.leader -> {
                instance.unlock()
                sender.sendMessage(
                    TextComponent.ofChildren(
                        Component.text(Strings.CHAT_PREFIX),
                        Component.text(Strings.PARTY_NOW_PUBLIC),
                        Component.text(Strings.HERE, NamedTextColor.GOLD)
                            .clickEvent(ClickEvent.runCommand("/fwdungeons lock"))
                    )
                )
            }
            else -> {
                sender.sendPrefixedMessage(Strings.ONLY_LEADER_MAY_OPEN_PARTY)
            }
        }

        return true
    }
}