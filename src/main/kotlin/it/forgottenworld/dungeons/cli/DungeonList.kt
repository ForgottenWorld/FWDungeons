package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.config.Strings
import it.forgottenworld.dungeons.model.dungeon.Difficulty
import it.forgottenworld.dungeons.model.dungeon.FinalDungeon
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.utils.ktx.append
import it.forgottenworld.dungeons.utils.ktx.clickEvent
import it.forgottenworld.dungeons.utils.ktx.component
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

private fun getJoinClickable(instance: DungeonFinalInstance) = component {
    val leader = instance.leader == null
    val locked = instance.isLocked
    val full = instance.isFull
    val inGame = instance.inGame

    append("  [ ", ChatColor.WHITE)
    append(when {
        leader -> Strings.CREATE
        locked -> Strings.PRIVATE
        full -> Strings.FULL
        inGame -> Strings.IN_DUNGEON
        else -> Strings.JOIN
    })
    color(when {
        locked -> ChatColor.GOLD
        full || inGame -> ChatColor.RED
        else -> ChatColor.GREEN
    })
    if (!full && !locked && !inGame)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons join ${instance.dungeon.id} ${instance.id}")
    append(" ]", ChatColor.WHITE)
}

private fun getPageClickable(text: String, page: Int) = component {
    append(text, ChatColor.AQUA)
    clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeons list $page")
}

private fun getPaginator(page: Int) = component {
    if (page > 0) {
        append("=[ ", ChatColor.DARK_GRAY)
        append(getPageClickable("<<<<", page - 1))
        append(" ]=", ChatColor.DARK_GRAY)
    } else {
        append("=======", ChatColor.DARK_GRAY)
    }

    append("=====================================", ChatColor.DARK_GRAY)

    if (page < FinalDungeon.dungeons.count() - 1) {
        append("=[ ")
        color(ChatColor.DARK_GRAY)
        append(getPageClickable(">>>>", page + 1))
        append(" ]=")
        color(ChatColor.DARK_GRAY)
    } else {
        append("=======")
        color(ChatColor.DARK_GRAY)
    }
}

private fun getChevrons(count: Int) = "${ChatColor.GRAY}${">".repeat(count)} "

private fun getColoredDifficulty(difficulty: Difficulty) = component {
    append(difficulty.toString().toUpperCase())
    color(when (difficulty) {
        Difficulty.EASY -> ChatColor.DARK_GREEN
        Difficulty.MEDIUM -> ChatColor.GOLD
        Difficulty.HARD -> ChatColor.DARK_RED
    })
}

fun getInteractiveDungeonList(page: Int) = component {

    if (page < 0 || page > FinalDungeon.dungeons.count() - 1) return@component
    val dng = FinalDungeon.dungeons.values
            .filter { it.isActive }
            .getOrNull(page) ?: return@component

    append("====================[ ", ChatColor.DARK_GRAY)
    append(Strings.CHAT_PREFIX.dropLast(1))
    append("ungeons ", ChatColor.GRAY)
    append("]====================\n\n", ChatColor.DARK_GRAY)

    append(getChevrons(3))
    append("DUNGEON: ", ChatColor.DARK_AQUA)
    append("${dng.name}\n", ChatColor.WHITE)

    append(getChevrons(3))
    append("${Strings.DESCRIPTION}: ", ChatColor.DARK_AQUA)
    append("${dng.description}\n", ChatColor.WHITE)

    append(getChevrons(3))
    append("${Strings.DIFFICULTY}: ", ChatColor.DARK_AQUA)
    append(getColoredDifficulty(dng.difficulty))
    append("\n")

    append(getChevrons(3))
    append("${Strings.PLAYERS}: ", ChatColor.DARK_AQUA)

    val minPl = dng.numberOfPlayers.first
    val maxPl = dng.numberOfPlayers.last
    append("$minPl${if (maxPl != minPl) "-$maxPl" else ""}\n\n", ChatColor.WHITE)

    dng.instances.values.forEachIndexed { ii, inst ->
        append(getChevrons(1))
        append("${Strings.ROOM} ${ii + 1} ")
        append("| ", ChatColor.DARK_GRAY)
        append("Leader: ", ChatColor.GRAY)
        inst.leader?.name
                ?.let { append(it, ChatColor.LIGHT_PURPLE) }
                ?: append("none", ChatColor.DARK_GRAY)
        append(getJoinClickable(inst))
        append(inst.leader?.let{ "  [ ${inst.playerCount}/${inst.maxPlayers} ]" } ?: "")
    }

    append("\n".repeat(13 - dng.instances.size - floor((dng.description.length + 6 + Strings.DESCRIPTION.length) / 55.0).toInt()))
    append(getPaginator(page))
}