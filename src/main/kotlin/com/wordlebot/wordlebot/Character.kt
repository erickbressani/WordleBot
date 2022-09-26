package com.wordlebot.wordlebot

sealed interface Character {
    val value: Char

    data class AtLeastInTheAnswer(override val value: Char, val notInThePosition: MutableSet<Int>): Character
    data class InTheCorrectPosition(override val value: Char, val positions: MutableSet<Int>, val notInThePosition: MutableSet<Int>): Character
    data class NotInTheAnswer(override val value: Char): Character
}

fun List<Character>.getNotInTheAnswer(): List<Character.NotInTheAnswer> =
    filterIsInstance<Character.NotInTheAnswer>()

fun List<Character>.getInTheCorrectSpot(): List<Character.InTheCorrectPosition> =
    filterIsInstance<Character.InTheCorrectPosition>()

fun List<Character>.getAtLeastInTheAnswer(): List<Character.AtLeastInTheAnswer> =
    filterIsInstance<Character.AtLeastInTheAnswer>()

fun List<Character>.getInTheCorrectPositionIndexes(): MutableSet<Int> =
    getInTheCorrectSpot().map { it.positions }.flatten().toMutableSet()

fun List<Character>.toCode(): String =
    StringBuilder().apply {
        append("listOf(")
        this@toCode.forEach {appendLine("${it.toCode()},") }
        appendLine(")")
    }.toString()

fun Character.toCode() = when(this) {
    is Character.AtLeastInTheAnswer -> "Character.AtLeastInTheAnswer('$value', ${notInThePosition.toCode()})"
    is Character.InTheCorrectPosition -> "Character.InTheCorrectPosition('$value', ${positions.toCode()}, ${notInThePosition.toCode()})"
    is Character.NotInTheAnswer -> "Character.NotInTheAnswer('$value')"
}

private fun MutableSet<Int>.toCode():String =
    StringBuilder().apply {
        append("mutableSetOf(")
        this@toCode.forEach { append("$it${if (it == this@toCode.last()) "" else ", "}") }
        append(")")
    }.toString()
