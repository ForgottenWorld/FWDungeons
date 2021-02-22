package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.interactiveregion.InteractiveRegion
import it.forgottenworld.dungeons.core.config.Strings
import it.forgottenworld.dungeons.core.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.core.game.interactiveregion.TriggerImpl
import it.forgottenworld.dungeons.core.utils.append
import it.forgottenworld.dungeons.core.utils.chatComponent
import it.forgottenworld.dungeons.core.utils.clickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import kotlin.math.floor

object InteractiveRegionListGui {

    private const val ITEMS_PER_PAGE = 16

    private fun clickables(interactiveEl: InteractiveRegion, type: String) = chatComponent {
        append("  [", ChatColor.WHITE)
        append(" HL ", ChatColor.GREEN)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type hl ${interactiveEl.id}")
        append("] [", ChatColor.WHITE)
        append(" X ", ChatColor.RED)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde $type unmake ${interactiveEl.id}")
        append("]", ChatColor.WHITE)
        if (interactiveEl is TriggerImpl) {
            append(" [", ChatColor.WHITE)
            if (interactiveEl.effectCode.isEmpty()) {
                append(" NO CODE ", ChatColor.GRAY)
            } else {
                append(" SHOW CODE ", ChatColor.LIGHT_PURPLE)
                clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwde trigger code ${interactiveEl.id}")
            }
            append("]", ChatColor.WHITE)
        }
        append("\n")
    }

    private fun pageClickable(text: String, page: Int, type: String) = chatComponent {
        append(text, ChatColor.AQUA)
        clickEvent(ClickEvent.Action.RUN_COMMAND, "/fwdungeonsedit $type list $page")
    }

    private fun paginator(page: Int, maxPage: Int, type: String) = chatComponent {

        if (page > 0) {
            append("=[ ", ChatColor.DARK_GRAY)
            append(pageClickable("<<<<", page - 1, type))
            append(" ]=", ChatColor.DARK_GRAY)
        } else {
            append("=======", ChatColor.DARK_GRAY)
        }

        append("=====================================", ChatColor.DARK_GRAY)

        if (page < maxPage) {
            append("=[ ")
            color(ChatColor.DARK_GRAY)
            append(pageClickable(">>>>", page + 1, type))
            append(" ]=")
            color(ChatColor.DARK_GRAY)
        } else {
            append("=======")
            color(ChatColor.DARK_GRAY)
        }
    }

    fun showActiveAreas(dungeon: EditableDungeon, page: Int) = chatComponent {

        append("====================[ ", ChatColor.DARK_GRAY)
        append(Strings.CHAT_PREFIX_NO_SPACE)
        append("ungeons ", ChatColor.GRAY)
        append("]====================\n", ChatColor.DARK_GRAY)

        append("====================[ ", ChatColor.DARK_GRAY)
        append("Active Areas", ChatColor.GREEN)
        append(" ]===================\n\n", ChatColor.DARK_GRAY)

        for ((k, v) in dungeon
            .activeAreas
            .entries
            .toList()
            .slice(page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                .coerceAtMost(dungeon.activeAreas.size - 1)
            )) {
            append(">>> ", ChatColor.GRAY)
            append("#$k: ", ChatColor.DARK_AQUA)
            append(v.label ?: "NO LABEL", ChatColor.WHITE)
            clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde aa label id:${v.id} ")
            append(clickables(v, "aa"))
        }

        append("\n".repeat(ITEMS_PER_PAGE - dungeon.activeAreas.size))
        append(paginator(page, floor(dungeon.activeAreas.size / ITEMS_PER_PAGE.toDouble()).toInt(), "aa"))
    }

    fun showTriggers(dungeon: EditableDungeon, page: Int) = chatComponent {

        append("====================[ ", ChatColor.DARK_GRAY)
        append(Strings.CHAT_PREFIX_NO_SPACE)
        append("ungeons ", ChatColor.GRAY)
        append("]====================\n", ChatColor.DARK_GRAY)

        append("=====================[ ", ChatColor.DARK_GRAY)
        append("Triggers", ChatColor.BLUE)
        append(" ]=====================\n\n", ChatColor.DARK_GRAY)

        for ((k, v) in dungeon
            .triggers
            .entries
            .toList()
            .slice(
                page * ITEMS_PER_PAGE..((page + 1) * ITEMS_PER_PAGE - 1)
                    .coerceAtMost(dungeon.triggers.size - 1)
            )
        ) {
            append(">>> ", ChatColor.GRAY)
            append("#$k: ", ChatColor.DARK_AQUA)
            append(v.label ?: "NO LABEL", ChatColor.WHITE)
            clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fwde t label id:${v.id} ")
            append(clickables(v, "t"))
        }

        append("\n".repeat(ITEMS_PER_PAGE - dungeon.triggers.size))
        append(paginator(page, floor(dungeon.triggers.size / ITEMS_PER_PAGE.toDouble()).toInt(), "t"))
    }
}