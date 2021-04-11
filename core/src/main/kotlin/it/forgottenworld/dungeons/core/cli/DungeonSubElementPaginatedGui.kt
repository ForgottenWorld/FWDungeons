package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.subelement.DungeonSubElement
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.InteractiveRegion
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

abstract class DungeonSubElementPaginatedGui<T : DungeonSubElement>(
    header: Component,
    private val command: String,
) : PaginatedGui(header, "/fwde $command list", 16, 1) {

    protected abstract val elements: List<T>

    override val itemCount: Int
        get() = elements.size


    private fun buttons(
        subElement: DungeonSubElement
    ) = Component.text { builder ->
        if (subElement is InteractiveRegion) {
            builder.append(
                TextComponent.ofChildren(
                    Component.text(" [", NamedTextColor.WHITE),
                    Component.text(" HL ", NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/fwde $command hl ${subElement.id}")),
                    Component.text("]", NamedTextColor.WHITE)
                )
            )
        }
        builder.append(
            TextComponent.ofChildren(
                Component.text(" [", NamedTextColor.WHITE),
                Component.text(" X ", NamedTextColor.RED)
                    .clickEvent(ClickEvent.runCommand("/fwde $command unmake ${subElement.id}")),
                Component.text("]\n", NamedTextColor.WHITE)
            )
        )
    }


    override fun getItem(position: Int): TextComponent {
        val item = elements[position]
        return TextComponent.ofChildren(
            Component.text(">>> ", NamedTextColor.GRAY),
            Component.text("#$position: ", NamedTextColor.DARK_AQUA),
            Component.text(item.label ?: "NO LABEL", NamedTextColor.WHITE)
                .clickEvent(ClickEvent.suggestCommand("/fwde $command label id:${item.id} ")),
            buttons(item)
        )
    }
}