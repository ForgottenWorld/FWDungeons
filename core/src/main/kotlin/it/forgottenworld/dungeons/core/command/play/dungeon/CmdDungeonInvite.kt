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
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CmdDungeonInvite @Inject constructor(
    private val dungeonManager: DungeonManager
) : PlayerCommand() {

    override fun command(sender: Player, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendPrefixedMessage(Strings.PROVIDE_NAME_OF_INVITEE)
            return true
        }

        val instance = dungeonManager.getPlayerInstance(sender.uniqueId) ?: run {
            sender.sendPrefixedMessage(Strings.CURRENTLY_NOT_IN_DUNGEON_PARTY)
            return true
        }

        if (instance.leader != sender.uniqueId) {
            sender.sendPrefixedMessage(Strings.ONLY_LEADER_MAY_INVITE)
            return true
        }

        val toPlayer = Bukkit.getServer().getPlayer(args[0]) ?: run {
            sender.sendPrefixedMessage(Strings.NO_ONLINE_PLAYER_HAS_THIS_NAME)
            return true
        }

        toPlayer.sendMessage(
            TextComponent.ofChildren(
                Component.text(Strings.CHAT_PREFIX),
                Component.text(
                    Strings.PLAYER_INVITED_YOU_TO_JOIN_PARTY_CLICK.format(sender.name),
                    NamedTextColor.WHITE
                ),
                Component.text(Strings.HERE, NamedTextColor.GREEN)
                    .clickEvent(
                        ClickEvent.runCommand(
                            "/fwdungeons join ${instance.dungeon.id} ${instance.id} ${
                                instance.partyKey
                            }"
                        )
                    ),
                Component.text(Strings.TO_ACCEPT, NamedTextColor.WHITE)
            )
        )

        sender.sendPrefixedMessage(Strings.INVITE_SENT)

        return true
    }
}