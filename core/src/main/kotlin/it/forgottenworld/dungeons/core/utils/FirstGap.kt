package it.forgottenworld.dungeons.core.utils

fun Iterable<Int>.firstGap() = sorted().find { !contains(it + 1) }?.plus(1) ?: 0