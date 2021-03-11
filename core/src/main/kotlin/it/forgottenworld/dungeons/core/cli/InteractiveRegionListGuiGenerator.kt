package it.forgottenworld.dungeons.core.cli

import com.google.inject.Singleton
import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.api.game.interactiveregion.Trigger
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.utils.clickEvent
import it.forgottenworld.dungeons.core.utils.color
import it.forgottenworld.dungeons.core.utils.jsonMessage
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

@Singleton
class InteractiveRegionListGuiGenerator {

    private fun clickables(interactiveEl: InteractiveRegion, type: String) = jsonMessage {
        append("  §f[")
        append(" §aHL ")
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type hl ${interactiveEl.id}")
        append("§f] [")
        append(" §cX ")
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type unmake ${interactiveEl.id}")
        append("§f]")
        if (interactiveEl is Trigger) {
            append(" §f[")
            if (interactiveEl.effectCode.isEmpty()) {
                append(" §7NO CODE ")
            } else {
                append(" §dSHOW CODE ")
                clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde trigger code ${interactiveEl.id}")
            }
            append("§f]")
        }
        append("\n")
    }

    private fun pageClickable(text: String, page: Int, type: String) = jsonMessage {
        append(text) color ChatColor.AQUA
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeonsedit $type list $page")
    }

    private fun paginator(page: Int, maxPage: Int, type: String) = jsonMessage {
        if (page > 0) {
            append("=[ ") color ChatColor.DARK_GRAY
            append(pageClickable("<<<<", page - 1, type))
            append(" ]=") color ChatColor.DARK_GRAY
        } else {
            append("=======") color ChatColor.DARK_GRAY
        }

        append("=====================================") color ChatColor.DARK_GRAY

        if (page < maxPage) {
            append("§8=[ ")
            append(pageClickable(">>>>", page + 1, type))
            append("§8 ]=")
        } else {
            append("=======")
            color(ChatColor.DARK_GRAY)
        }
    }

    fun showActiveAreas(dungeon: EditableDungeon, page: Int) = jsonMessage {
        append("§8====================[ ${Strings.CHAT_PREFIX_NO_SPACE}§7ungeons §8]====================\n")
        append("§8=====================[ §aActive Areas §8]=====================\n\n")

        for ((k, v) in dungeon
            .activeAreas
            .entries
            .toList()
            .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                .coerceAtMost(dungeon.activeAreas.size - 1)
            )
        ) {
            append("§7>>> §3#$k: §f${v.label ?: "NO LABEL"}" )
            clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde aa label id:${v.id} ")
            append(clickables(v, "aa"))
        }

        append("\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size))
        append(paginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa"))
    }

    fun showTriggers(dungeon: EditableDungeon, page: Int) = jsonMessage {
        append("§8====================[ ${Strings.CHAT_PREFIX_NO_SPACE}§7ungeons §8]====================\n")
        append("§8=====================[ §9Triggers §8]=====================\n\n")

        for ((k,v) in dungeon
            .triggers
            .entries
            .toList()
            .slice(
                page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                    .coerceAtMost(dungeon.triggers.size - 1)
            )
        ) {
            append("§7>>> §3#$k: §f${v.label ?: "NO LABEL"}" )
            clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde t label id:${v.id} ")
            append(clickables(v, "t"))
        }

        append("\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size))
        append(paginator(page, dungeon.triggers.size / ITEMS_PER_PAGE, "t"))
    }

    companion object {
        private const val ITEMS_PER_PAGE = 16
    }
}