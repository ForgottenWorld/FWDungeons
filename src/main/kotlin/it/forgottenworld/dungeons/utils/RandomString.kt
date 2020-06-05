package it.forgottenworld.dungeons.utils

private val STRING_CHARACTERS = ('0'..'z').toList().toTypedArray()

fun getRandomString(length: Int) =
    (1..length).map { STRING_CHARACTERS.random() }.joinToString("")