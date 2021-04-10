package it.forgottenworld.dungeons.core.cli

import it.forgottenworld.dungeons.api.game.dungeon.EditableDungeon
import it.forgottenworld.dungeons.api.game.dungeon.subelement.interactiveregion.SpawnArea
import it.forgottenworld.dungeons.core.storage.Strings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

class SpawnAreaPaginatedGui(private val editableDungeon: EditableDungeon) : DungeonSubElementPaginatedGui<SpawnArea>(
    TextComponent.ofChildren(
        Component.text(Strings.CHAT_HEADER),
        Component.text("====================[ ", NamedTextColor.DARK_GRAY),
        Component.text("Spawn Areas", NamedTextColor.LIGHT_PURPLE),
        Component.text(" ]===================\n\n", NamedTextColor.DARK_GRAY)
    ),
    "sa"
) {

    override val elements: List<SpawnArea>
        get() = editableDungeon.spawnAreas.values.toList()
}