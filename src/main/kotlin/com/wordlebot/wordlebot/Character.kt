package com.wordlebot.wordlebot

sealed interface Character {
    val value: Char

    data class InTheAnswer(override val value: Char, val positions: MutableSet<Int>, val notInThePosition: MutableSet<Int>): Character
    data class NotInTheAnswer(override val value: Char): Character
}

fun List<Character>.notInTheAnswer(): List<Character.NotInTheAnswer> =
    filterIsInstance<Character.NotInTheAnswer>()

fun List<Character>.inTheAnswer(): List<Character.InTheAnswer> =
    filterIsInstance<Character.InTheAnswer>()

fun List<Character>.inTheAnswerPositions(): MutableSet<Int> =
    inTheAnswer().map { it.positions }.flatten().toMutableSet()

fun List<Character>.toCodeSnippet(): String =
    StringBuilder().apply {
        append("listOf(")
        this@toCodeSnippet.forEach { appendLine("${it.toCodeSnippet()},") }
        appendLine(")")
    }.toString()

fun Character.toCodeSnippet() = when(this) {
    is Character.InTheAnswer -> "Character.InTheAnswer('$value', ${positions.toCodeSnippet()}, ${notInThePosition.toCodeSnippet()})"
    is Character.NotInTheAnswer -> "Character.NotInTheAnswer('$value')"
}

private fun MutableSet<Int>.toCodeSnippet():String =
    StringBuilder().apply {
        append("mutableSetOf(")
        this@toCodeSnippet.forEach { append("$it${if (it == this@toCodeSnippet.last()) "" else ", "}") }
        append(")")
    }.toString()
