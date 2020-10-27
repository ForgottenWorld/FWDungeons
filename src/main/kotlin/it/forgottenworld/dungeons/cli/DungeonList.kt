package it.forgottenworld.dungeons.cli

import it.forgottenworld.dungeons.model.dungeon.Difficulty
import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance
import it.forgottenworld.dungeons.service.DungeonService
import it.forgottenworld.dungeons.utils.extra
import it.forgottenworld.dungeons.utils.textComponent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import java.lang.Integer.max
import kotlin.math.ceil

private fun getJoinClickable(
        instance: DungeonFinalInstance,
        leader: Boolean,
        locked: Boolean,
        full: Boolean,
        inGame: Boolean) = textComponent {
    text = when {
        leader -> "${ChatColor.GREEN}CREATE"
        locked -> "${ChatColor.GOLD}PRIVATE"
        full -> "${ChatColor.RED}FULL"
        inGame -> "${ChatColor.RED}IN DUNGEON"
        else -> "${ChatColor.GREEN}JOIN"
    }
    if (!full && !locked && !inGame)
        clickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/fwdungeons join ${instance.dungeon.id} ${instance.id}")
}

private fun getPageClickable(text: String, page: Int) =
        textComponent(text) {
            color = ChatColor.AQUA
            clickEvent = ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/fwdungeons list $page")
        }

private fun getCarets(count: Int) = "${ChatColor.GRAY}${">".repeat(count)} "

private fun getColoredDifficulty(difficulty: Difficulty) =
        "${when (difficulty) {
            Difficulty.EASY -> ChatColor.DARK_GREEN
            Difficulty.MEDIUM -> ChatColor.GOLD
            Difficulty.HARD -> ChatColor.DARK_RED
        }}${difficulty.toString().toUpperCase()}\n"

fun getInteractiveDungeonList(page: Int) = textComponent {
    if (page < 0 || page > DungeonService.dungeons.count() - 1) return@textComponent
    val d = DungeonService.dungeons.values
            .filter { it.active}
            .toList()
            .getOrNull(page) ?: return@textComponent

    addExtra("${ChatColor.DARK_GRAY}====================[ ${
        getString(Strings.CHAT_PREFIX).dropLast(1)}${ChatColor.GRAY}ungeons ${
        ChatColor.DARK_GRAY}]====================\n\n")

    extra(getCarets(3)) {
        addExtra("${ChatColor.DARK_AQUA}DUNGEON:${ChatColor.WHITE} ${d.name}\n")
    }

    extra(getCarets(3)) {
        addExtra("${ChatColor.DARK_AQUA}DESCRIPTION:${ChatColor.WHITE} ${d.description}\n")
    }

    extra(getCarets(3)) {
        addExtra("${ChatColor.DARK_AQUA}DIFFICULTY:${ChatColor.WHITE} ")
        addExtra(getColoredDifficulty(d.difficulty))
    }

    extra(getCarets(3)) {
        addExtra("${ChatColor.DARK_AQUA}PLAYERS:${ChatColor.WHITE} ${
            d.numberOfPlayers.first.toString() +
                    if (d.numberOfPlayers.last != d.numberOfPlayers.first)
                        "-" + d.numberOfPlayers.last.toString()
                    else ""
        }\n\n")
    }

    d.instances.values.forEachIndexed { ii, inst ->

        extra(getCarets(1)) {
            addExtra("Room ${ii + 1} ${ChatColor.DARK_GRAY}| ${ChatColor.GRAY}Leader: ")
            extra(inst.leader?.name ?: "${ChatColor.DARK_GRAY}none") {
                if (text != "none") color = ChatColor.LIGHT_PURPLE
            }
        }

        extra {
            addExtra("  [ ")
            getJoinClickable(
                    inst,
                    inst.leader == null,
                    inst.isLocked,
                    inst.isFull,
                    inst.inGame)
            addExtra(" ]")
        }

        addExtra(if (inst.leader != null) "  [ ${inst.playerCount}/${inst.maxPlayers} ]\n" else "\n")
    }

    addExtra("\n".repeat(12 - d.instances.count() - ceil(max((d.description.length - 48), 0) / 60.0).toInt()))

    if (page > 0) {
        extra("${ChatColor.DARK_GRAY}=[ ") {
            extra { getPageClickable("PREVIOUS", page - 1) }
            addExtra("${ChatColor.DARK_GRAY} ]")
        }
    } else addExtra("${ChatColor.DARK_GRAY}=============")

    addExtra("${ChatColor.DARK_GRAY}===============================")
    if (page < DungeonService.dungeons.count() - 1) {
        extra("${ChatColor.DARK_GRAY}[ ") {
            extra { getPageClickable("NEXT", page + 1) }
            addExtra("${ChatColor.DARK_GRAY} ]=")
        }
    } else addExtra("${ChatColor.DARK_GRAY}=========")
}