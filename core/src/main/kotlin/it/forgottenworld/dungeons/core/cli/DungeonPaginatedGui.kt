package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.Dungeon
import it.forgottenworld.dungeons.api.game.dungeon.DungeonManager
import it.forgottenworld.dungeons.api.game.dungeon.instance.DungeonInstance
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

class DungeonPaginatedGui(private val dungeonManager: DungeonManager) : PaginatedGui(
    Component.text(Strings.CHAT_HEADER + '\n'),
    "/fwd list",
    18,
    18
) {

    val dungeons get() = dungeonManager.getAllActiveFinalDungeons()

    override val itemCount: Int
        get() = dungeons.size


    private fun joinClickable(instance: DungeonInstance) = Component.text { builder ->
        val hasNoLeader = instance.leader == null
        val isLocked = instance.isLocked
        val isFull = instance.players.size == instance.dungeon.maxPlayers
        val isInGame = instance.isInGame

        builder.append(Component.text("  [ ", NamedTextColor.WHITE))
        val clickable = when {
            hasNoLeader -> Component.text(Strings.CREATE, NamedTextColor.GREEN)
            isLocked -> Component.text(Strings.PRIVATE, NamedTextColor.GOLD)
            isFull -> Component.text(Strings.FULL, NamedTextColor.RED)
            isInGame -> Component.text(Strings.IN_DUNGEON, NamedTextColor.RED)
            else -> Component.text(Strings.JOIN, NamedTextColor.GREEN)
        }

        if (!isFull && !isLocked && !isInGame) {
            builder.append(
                clickable.clickEvent(
                    ClickEvent.runCommand("/fwd join ${instance.dungeon.id} ${instance.id}")
                )
            )
        } else {
            builder.append(clickable)
        }

        builder.append(Component.text(" ]", NamedTextColor.WHITE))
    }

    private fun difficultyComponent(difficulty: Dungeon.Difficulty) = Component.text(
        difficulty.toString().toUpperCase(),
        when (difficulty) {
            Dungeon.Difficulty.EASY -> NamedTextColor.DARK_GREEN
            Dungeon.Difficulty.MEDIUM -> NamedTextColor.GOLD
            Dungeon.Difficulty.HARD -> NamedTextColor.DARK_RED
        }
    )

    private fun instanceListComponent(dungeon: Dungeon) = Component.text { builder ->
        val instances = dungeonManager.getDungeonInstances(dungeon)

        for ((i, inst) in instances.values.withIndex()) {
            val leader = inst.leader?.let(Bukkit::getPlayer)

            val leaderName = leader
                ?.let { Component.text(it.name, NamedTextColor.LIGHT_PURPLE) }
                ?: Component.text("none", NamedTextColor.DARK_GRAY)

            builder.append(
                TextComponent.ofChildren(
                    Component.text("> ", NamedTextColor.GRAY),
                    Component.text("${Strings.ROOM} ${i + 1} "),
                    Component.text("| ", NamedTextColor.DARK_GRAY),
                    Component.text("Leader: ", NamedTextColor.GRAY),
                    leaderName,
                    joinClickable(inst)
                )
            )

            if (inst.leader != null) {
                builder.append(Component.text("  [ ${inst.players.size}/${dungeon.maxPlayers} ]"))
            }
        }

        val descriptionHeight = (dungeon.description.length + 6 + Strings.DESCRIPTION.length) / 65
        val paddingLines = 13 - instances.size - descriptionHeight
        builder.append(Component.text("\n".repeat(paddingLines)))
    }

    override fun getItem(position: Int): Component {
        val dungeon = dungeons[position]

        return TextComponent.ofChildren(
            Component.text(">>> ", NamedTextColor.GRAY),
            Component.text("DUNGEON: ", NamedTextColor.DARK_AQUA),
            Component.text(dungeon.name, NamedTextColor.WHITE),
            Component.text("\n>>> ", NamedTextColor.GRAY),
            Component.text("${Strings.DESCRIPTION}: ", NamedTextColor.DARK_AQUA),
            Component.text(dungeon.description, NamedTextColor.WHITE),
            Component.text("\n>>> ", NamedTextColor.GRAY),
            Component.text("${Strings.DIFFICULTY}: ", NamedTextColor.DARK_AQUA),
            difficultyComponent(dungeon.difficulty),
            Component.text("\n>>> ", NamedTextColor.GRAY),
            Component.text("${Strings.PLAYERS}: ", NamedTextColor.DARK_AQUA),
            Component.text(dungeon.minPlayers, NamedTextColor.WHITE),
            if (dungeon.maxPlayers != dungeon.minPlayers) {
                Component.text("-${dungeon.maxPlayers}\n\n", NamedTextColor.WHITE)
            } else {
                Component.text("\n\n")
            },
            instanceListComponent(dungeon)
        )
    }
}