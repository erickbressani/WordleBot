package com.wordlebot.wordlebot.models

sealed interface Character {
    val value: Char

    data class InTheAnswer(override val value: Char): Character {
        private val positions = mutableSetOf<Int>()
        private val notInThePositions = mutableSetOf<Int>()

        fun getPositionsMarkedAsFound(): Set<Int> =
            positions

        fun getPositionsMarkedAsNotFound(): Set<Int> =
            notInThePositions

        fun markAsFoundIn(position: Int) {
            position
                .apply(notInThePositions::remove)
                .apply(positions::add)
        }

        fun markAsNotFoundIn(position: Int) {
            position
                .apply(positions::remove)
                .apply(notInThePositions::add)

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
    inTheAnswer().map { it.getPositionsMarkedAsFound() }.flatten().filter { it != besidesOf }.toMutableSet()

fun List<Character>.contains(char: Char) =
    map { it.value }.contains(char)

fun List<Character>.toCodeSnippet(): String =
    StringBuilder().apply {
        append("listOf(")
        this@toCodeSnippet.forEach { appendLine("${it.toCodeSnippet()},") }
        appendLine(")")
    }.toString()

fun Character.toCodeSnippet() = when(this) {
    is Character.InTheAnswer -> "Character.InTheAnswer('$value', ${getPositionsMarkedAsFound().toCodeSnippet()}, ${getPositionsMarkedAsFound().toCodeSnippet()})"
    is Character.NotInTheAnswer -> "Character.NotInTheAnswer('$value')"
}

private fun Set<Int>.toCodeSnippet():String =
    StringBuilder().apply {
        append("mutableSetOf(")
        this@toCodeSnippet.forEach { append("$it${if (it == this@toCodeSnippet.last()) "" else ", "}") }
        append(")")
    }.toString()
