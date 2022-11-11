package com.wordlebot.wordlebot.models

data class Word(val value: String) {
    init {
        require(value.length == 5) { "Word can only have 5 letters." }
    }

    fun forEachCharIndexed(action: (Int, Char) -> Unit) =
        value.forEachIndexed(action)

    override fun toString(): String =
        value
}
