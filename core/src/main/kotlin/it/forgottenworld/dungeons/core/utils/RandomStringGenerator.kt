package it.forgottenworld.dungeons.core.utils

import com.google.inject.Singleton

@Singleton
class RandomStringGenerator {
    private val stringChars = ('0'..'z').toList().toTypedArray()

    fun generate(length: Int) = CharArray(length) { stringChars.random() }.concatToString()
}