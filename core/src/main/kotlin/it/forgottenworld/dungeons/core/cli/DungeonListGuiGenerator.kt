package it.forgottenworld.dungeons.core.cli

import com.google.inject.Inject
import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

@Singleton
class DungeonListGuiGenerator @Inject constructor(
    private val dungeonManager: DungeonManager
) {

    private fun joinClickable(instance: DungeonInstance) = Component.text { builder ->
        val leader = instance.leader == null
        val locked = instance.isLocked
        val full = instance.players.size == instance.dungeon.maxPlayers
        val inGame = instance.isInGame

        builder.append(Component.text("  [ ", NamedTextColor.WHITE))
        builder.append(
            Component.text(
                when {
                    leader -> Strings.CREATE
                    locked -> Strings.PRIVATE
                    full -> Strings.FULL
                    inGame -> Strings.IN_DUNGEON
                    else -> Strings.JOIN
                },
                when {
                    locked -> NamedTextColor.GOLD
                    full || inGame -> NamedTextColor.RED
                    else -> NamedTextColor.GREEN
                }
            ).run {
                if (!full && !locked && !inGame) {
                    clickEvent(
                        ClickEvent.runCommand("/fwdungeons join ${instance.dungeon.id} ${instance.id}")
                    )
                } else {
                    this
                }
            }
        )

        builder.append(Component.text(" ]", NamedTextColor.WHITE))
    }

    private fun pageClickable(text: String, page: Int) = TextComponent.ofChildren(
        Component.text("=[ ", NamedTextColor.DARK_GRAY),
        Component.text(text, NamedTextColor.AQUA)
            .clickEvent(ClickEvent.runCommand("/fwdungeons list $page")),
        Component.text(" ]=", NamedTextColor.DARK_GRAY)
    )

    private fun paginator(page: Int) = Component.text { builder ->
        if (page > 0) {
            builder.append(pageClickable("<<<<", page - 1))
        } else {
            builder.append(Component.text("========", NamedTextColor.DARK_GRAY))
        }
        builder.append(Component.text("=====================================", NamedTextColor.DARK_GRAY))
        if (page < dungeonManager.finalDungeonCount - 1) {
            builder.append(pageClickable(">>>>", page + 1))
        } else {
            builder.append(Component.text("========", NamedTextColor.DARK_GRAY))
        }
    }

    private fun coloredDifficulty(difficulty: Dungeon.Difficulty) = Component.text(
        difficulty.toString().toUpperCase(),
        when (difficulty) {
            Dungeon.Difficulty.EASY -> NamedTextColor.DARK_GREEN
            Dungeon.Difficulty.MEDIUM -> NamedTextColor.GOLD
            Dungeon.Difficulty.HARD -> NamedTextColor.DARK_RED
        }
    )

    private fun dungeonDetails(dungeon: Dungeon) = TextComponent.ofChildren(
        Component.text(">>> ", NamedTextColor.GRAY),
        Component.text("§3DUNGEON: §f${dungeon.name}\n"),
        Component.text(">>> ", NamedTextColor.GRAY),
        Component.text("§3${Strings.DESCRIPTION}: §f${dungeon.description}\n"),
        Component.text(">>> ", NamedTextColor.GRAY),
        Component.text("§3${Strings.DIFFICULTY}: "),
        coloredDifficulty(dungeon.difficulty),
        Component.text("\n"),
        Component.text(">>> ", NamedTextColor.GRAY),
        Component.text("§3${Strings.PLAYERS}: "),
        Component.text("§f${dungeon.minPlayers}${
            if (dungeon.maxPlayers != dungeon.minPlayers) "-${dungeon.maxPlayers}" else ""
        }"),
        Component.text("\n\n"),
        instanceDetails(dungeon)
    )

    private fun instanceDetails(dungeon: Dungeon) = Component.text { builder ->
        val instances = dungeonManager.getDungeonInstances(dungeon)

        for ((i, inst) in instances.values.withIndex()) {
            val leader = inst.leader?.let(Bukkit::getPlayer)
            val leaderName = leader?.let { "§d${it.name}" } ?: "§8none"
            builder.append(Component.text("> ", NamedTextColor.GRAY))
            builder.append(Component.text("${Strings.ROOM} ${i + 1} §8| §7Leader: $leaderName"))
            builder.append(joinClickable(inst))
            if (inst.leader != null) {
                builder.append(Component.text("  [ ${inst.players.size}/${dungeon.maxPlayers} ]"))
            }
        }

        val paddingLines = 13 - instances.size - (dungeon.description.length + 6 + Strings.DESCRIPTION.length) / 55
        builder.append(Component.text("\n".repeat(paddingLines)))
    }

    fun showPage(page: Int): Component {
        if (page < 0 || page > dungeonManager.finalDungeonCount - 1) {
            return Component.text("")
        }

        val dungeon = dungeonManager
            .getAllActiveFinalDungeons()
            .getOrNull(page) ?: return Component.text("")

        return TextComponent.ofChildren(
            Component.text(Strings.CHAT_HEADER),
            Component.text("\n"),
            dungeonDetails(dungeon),
            paginator(page)
        )
    }
}