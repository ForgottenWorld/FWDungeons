package it.forgottenworld.dungeons.scripting

import it.forgottenworld.dungeons.model.instance.DungeonFinalInstance

fun parseCode(lines: List<String>): (DungeonFinalInstance) -> Unit {
    val parsedCodeLines = lines.map { doParseCode(it.split(" ").iterator()) }
    return { parsedCodeLines.forEach { f -> f(it) } }
}

