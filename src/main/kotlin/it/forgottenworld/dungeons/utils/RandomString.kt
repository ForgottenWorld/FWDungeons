package it.forgottenworld.dungeons.utils

object RandomString {
    private val STRING_CHARACTERS = ('0'..'z').toList().toTypedArray()

    fun generate(length: Int) = CharArray(length) { STRING_CHARACTERS.random() }.concatToString()
}