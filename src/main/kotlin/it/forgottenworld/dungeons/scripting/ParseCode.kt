package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.DungeonInstance

fun parseCode(lines: List<String>): (DungeonInstance) -> Unit {
    val parsedCodeLines = lines.map { doParseCode(it.split(" ").iterator()) }
    return { parsedCodeLines.forEach { f -> f(it) } }
}

