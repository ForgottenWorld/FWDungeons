package it.forgottenworld.dungeons.utils

private val STRING_CHARACTERS = ('0'..'z').toList().toTypedArray()

fun getRandomString(length: Int) = CharArray(length) { STRING_CHARACTERS.random() }.concatToString()