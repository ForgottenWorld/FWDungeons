package it.forgottenworld.dungeons.core.cli

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.math.ceil

abstract class PaginatedGui(
    private val header: Component? = null,
    private val listCommand: String,
    private val bodyLineCount: Int = USEABLE_LINES,
    private val itemLineCount: Int = 1,
) {

    abstract val itemCount: Int

    private val itemsPerPage = bodyLineCount / itemLineCount

    private val pageCount get() = ceil(itemCount.toDouble() / itemsPerPage)


    protected abstract fun getItem(position: Int): Component


    private fun changePageButton(text: String, page: Int) = TextComponent.ofChildren(
        Component.text("=[ ", NamedTextColor.DARK_GRAY),
        Component.text(text, NamedTextColor.AQUA)
            .clickEvent(ClickEvent.runCommand("$listCommand $page")),
        Component.text(" ]=", NamedTextColor.DARK_GRAY)
    )

    private fun navigationControls(page: Int) = Component.text { builder ->
        if (page > 0) {
            builder.append(changePageButton("<<<<", page - 1))
        } else {
            builder.append(Component.text("========", NamedTextColor.DARK_GRAY))
        }

        builder.append(Component.text("=====================================", NamedTextColor.DARK_GRAY))

        if (page < pageCount - 1) {
            builder.append(changePageButton(">>>>", page + 1))
        } else {
            builder.append(Component.text("========", NamedTextColor.DARK_GRAY))
        }
    }


    fun get(page: Int = 0) = Component.text { builder ->
        header?.let(builder::append)
        val from = page * itemsPerPage
        val to = (page * itemsPerPage + itemsPerPage - 1).coerceAtMost(itemCount - 1)
        var used = 0
        for (i in from..to) {
            builder.append(getItem(i))
            used += itemLineCount
        }
        val left = bodyLineCount - used
        if (left > 0) {
            builder.append(Component.text("\n".repeat(left)))
        }
        builder.append(navigationControls(page))
    }


    companion object {

        private const val USEABLE_LINES = 19
    }
}