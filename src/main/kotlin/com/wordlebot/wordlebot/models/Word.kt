package com.wordlebot.wordlebot.models

data class Word(val value: String) {
    val chars: CharArray = value.toCharArray()
    val distinctChars: List<Char> = chars.distinct()
    val vowels: List<Char> = chars.filter { VOWELS.contains(it) }

    init {
        require(value.length == 5) { "Word can only have 5 letters." }
    }

    fun forEachCharIndexed(action: (Int, Char) -> Unit) =
        value.forEachIndexed(action)

    override fun toString(): String =
        value

    companion object {
        val VOWELS = listOf('a', 'e', 'i', 'o', 'u')
    }
}
