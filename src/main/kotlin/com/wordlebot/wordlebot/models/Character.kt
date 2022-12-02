package com.wordlebot.wordlebot.models

import kotlin.math.max

sealed interface Character {
    val value: Char

    data class InTheAnswer(override val value: Char): Character {
        val positions: Set<Int> = mutableSetOf()
        val notInThePositions: Set<Int> = mutableSetOf()
        private var recurrencesInTheAnswer: Int? = null

        fun markRecurrenceInTheAnswer(count: Int) {
            recurrencesInTheAnswer = count
            tryMarkingPositionsBasedOnRecurrence()
        }

        fun markAsFoundIn(position: Int) {
            position
                .apply((notInThePositions as MutableSet)::remove)
                .apply((positions as MutableSet)::add)

            trySettingRecurrenceBasedOnPositions()
            tryMarkingPositionsBasedOnRecurrence()
        }

        fun markAsNotFoundIn(position: Int) {
            position
                .apply((positions as MutableSet)::remove)
                .apply((notInThePositions as MutableSet)::add)

            if (notInThePositions.count() == 4) {
                positions.addAll(allPositions - notInThePositions)
            }

            trySettingRecurrenceBasedOnPositions()
            tryMarkingPositionsBasedOnRecurrence()
        }

        override fun toString(): String =
            "InTheAnswer: $value | Positions: ${positions.joinToString(", ")} | NotInThePositions: ${notInThePositions.joinToString(",")} | Recurrences: $recurrencesInTheAnswer"

        private fun trySettingRecurrenceBasedOnPositions() {
            if (recurrencesInTheAnswer == null && positions.count() + notInThePositions.count() == 5) {
                markRecurrenceInTheAnswer(positions.count())
            }
        }

        private fun tryMarkingPositionsBasedOnRecurrence() {
            val recurrencePositionsDiff = max(0, (recurrencesInTheAnswer ?: 0) - positions.count())

            if (notInThePositions.count() + recurrencePositionsDiff == 5) {
                (positions as MutableSet).addAll(allPositions - (notInThePositions + positions))
            }
        }

        companion object {
            private val allPositions = (0..4)
        }
    }
    data class NotInTheAnswer(override val value: Char): Character {
        override fun toString(): String =
            "NotInTheAnswer: $value"
    }
}

fun List<Character>.notInTheAnswer(): List<Character.NotInTheAnswer> =
    filterIsInstance<Character.NotInTheAnswer>()

fun List<Character>.inTheAnswer(): List<Character.InTheAnswer> =
    filterIsInstance<Character.InTheAnswer>()

fun List<Character>.inTheAnswerPositions(besidesOf: Int? = null): MutableSet<Int> =
    inTheAnswer().map { it.positions }.flatten().filter { it != besidesOf }.toMutableSet()

fun List<Character>.inCorrectPositionCount(): Int =
    filterIsInstance<Character.InTheAnswer>().sumOf { it.positions.count() }

fun List<Character>.findIfInTheAnswer(char: Char): Character.InTheAnswer? =
    filterIsInstance<Character.InTheAnswer>().find { it.value == char }

fun List<Character>.contains(char: Char) =
    map { it.value }.contains(char)

fun List<Character>.allToString(): String =
    joinToString("\n")
