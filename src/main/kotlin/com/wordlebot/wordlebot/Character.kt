package com.wordlebot.wordlebot

sealed interface Character {
    val value: Char

    data class AtLeastInTheAnswer(override val value: Char, val notInThePosition: MutableSet<Int>): Character
    data class InTheCorrectPosition(override val value: Char, val positions: MutableSet<Int>, val notInThePosition: MutableSet<Int>): Character
    data class NotInTheAnswer(override val value: Char): Character
}

fun List<Character>.notInTheAnswer(): List<Character.NotInTheAnswer> =
    filterIsInstance<Character.NotInTheAnswer>()

fun List<Character>.inTheCorrectSpot(): List<Character.InTheCorrectPosition> =
    filterIsInstance<Character.InTheCorrectPosition>()

fun List<Character>.atLeastInTheAnswer(): List<Character.AtLeastInTheAnswer> =
    filterIsInstance<Character.AtLeastInTheAnswer>()

fun List<Character>.inTheCorrectPositionIndexes(): MutableSet<Int> =
    inTheCorrectSpot().map { it.positions }.flatten().toMutableSet()

fun List<Character>.toCodeSnippet(): String =
    StringBuilder().apply {
        append("listOf(")
        this@toCodeSnippet.forEach { appendLine("${it.toCodeSnippet()},") }
        appendLine(")")
    }.toString()

fun Character.toCodeSnippet() = when(this) {
    is Character.AtLeastInTheAnswer -> "Character.AtLeastInTheAnswer('$value', ${notInThePosition.toCodeSnippet()})"
    is Character.InTheCorrectPosition -> "Character.InTheCorrectPosition('$value', ${positions.toCodeSnippet()}, ${notInThePosition.toCodeSnippet()})"
    is Character.NotInTheAnswer -> "Character.NotInTheAnswer('$value')"
}

private fun MutableSet<Int>.toCodeSnippet():String =
    StringBuilder().apply {
        append("mutableSetOf(")
        this@toCodeSnippet.forEach { append("$it${if (it == this@toCodeSnippet.last()) "" else ", "}") }
        append(")")
    }.toString()
