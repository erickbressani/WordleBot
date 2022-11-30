package com.wordlebot.wordlebot.models

import com.wordlebot.wordlebot.guesses.Guess

data class Word(val value: String) {
    val chars: CharArray = value.toCharArray()
    val distinctChars: List<Char> = chars.distinct()

    init {
        require(value.length == 5) { "Word can only have 5 letters." }
    }

    fun forEachCharIndexed(action: (Int, Char) -> Unit) =
        value.forEachIndexed(action)

    fun containsAny(characters: List<Character>): Boolean =
        characters.map { it.value }.any(value::contains)

    override fun toString(): String =
        value
}
