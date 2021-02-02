package it.forgottenworld.dungeons.scripting

fun cleanupCode(dirtyCode: String) = dirtyCode
    .replace("\n", "")
    .split(";")
    .map { it.trim() }
    .flatMap { it.split(" ") }
    .map { it.trim() }
    .filter { it.isNotEmpty() }