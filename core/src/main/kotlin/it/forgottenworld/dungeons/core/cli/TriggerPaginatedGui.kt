package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.Trigger
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

class TriggerPaginatedGui(private val editableDungeon: EditableDungeon) : DungeonSubElementPaginatedGui<Trigger>(
    TextComponent.ofChildren(
        Component.text(Strings.CHAT_HEADER),
        Component.text("=====================[ ", NamedTextColor.DARK_GRAY),
        Component.text("Triggers", NamedTextColor.GOLD),
        Component.text(" ]=====================\n\n", NamedTextColor.DARK_GRAY)
    ),
    "t"
) {

    override val elements: List<Trigger>
        get() = editableDungeon.triggers.values.toList()
}