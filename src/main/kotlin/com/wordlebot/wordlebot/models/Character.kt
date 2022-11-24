package com.wordlebot.wordlebot.models

sealed interface Character {
    val value: Char

    data class InTheAnswer(override val value: Char): Character {
        val positions: Set<Int> = mutableSetOf()
        val notInThePositions: Set<Int> = mutableSetOf()

        fun markAsFoundIn(position: Int) {
            position
                .apply((notInThePositions as MutableSet)::remove)
                .apply((positions as MutableSet)::add)
        }

        fun markAsNotFoundIn(position: Int) {
            position
                .apply((positions as MutableSet)::remove)
                .apply((notInThePositions as MutableSet)::add)

            if (notInThePositions.count() == 4) {
                positions.addAll(allPositions - notInThePositions)
            }
        }

        companion object {
            private val allPositions = (0..4)
        }
    }
    data class NotInTheAnswer(override val value: Char): Character
}

fun List<Character>.notInTheAnswer(): List<Character.NotInTheAnswer> =
    filterIsInstance<Character.NotInTheAnswer>()

fun List<Character>.inTheAnswer(): List<Character.InTheAnswer> =
    filterIsInstance<Character.InTheAnswer>()

fun List<Character>.inTheAnswerPositions(besidesOf: Int? = null): MutableSet<Int> =
    inTheAnswer().map { it.positions }.flatten().filter { it != besidesOf }.toMutableSet()

fun List<Character>.contains(char: Char) =
    map { it.value }.contains(char)

fun List<Character>.toCodeSnippet(): String =
    StringBuilder().apply {
        append("listOf(")
        this@toCodeSnippet.forEach { appendLine("${it.toCodeSnippet()},") }
        appendLine(")")
    }.toString()

fun Character.toCodeSnippet() = when(this) {
    is Character.InTheAnswer -> "Character.InTheAnswer('$value', ${positions.toCodeSnippet()}, ${notInThePositions.toCodeSnippet()})"
    is Character.NotInTheAnswer -> "Character.NotInTheAnswer('$value')"
}

private fun Set<Int>.toCodeSnippet():String =
    StringBuilder().apply {
        append("mutableSetOf(")
        this@toCodeSnippet.forEach { append("$it${if (it == this@toCodeSnippet.last()) "" else ", "}") }
        append(")")
    }.toString()
