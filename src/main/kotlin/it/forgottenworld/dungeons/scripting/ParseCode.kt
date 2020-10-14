package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.dungeon.DungeonInstance

fun parseCode(instance: DungeonInstance, lines: List<String>): () -> Unit {
    val parsedCodeLines = lines.map { doParseCode(instance, it.split(" ").iterator()) }
    return { parsedCodeLines.forEach { it() } }
}

