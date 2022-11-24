package com.wordlebot.wordlebot.outcomes

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.models.inTheAnswerPositions

class OutcomeParser {
    private val characters = mutableListOf<Character>()

    fun getAllParsedCharacters(): List<Character> = characters

    fun add(guessedWord: Word, outcomes: List<Outcome>) {
        guessedWord.forEachCharIndexed { index, char ->
            when (outcomes[index]) {
                Outcome.InTheCorrectPosition -> addInTheCorrectPosition(char, index)
                Outcome.AtLeastInTheAnswer -> addAtLeastInTheAnswer(char, index)
                Outcome.NotInTheAnswer -> addNotInTheAnswer(char, index)
            }
        }
    }

    private fun addInTheCorrectPosition(char: Char, position: Int) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(
                Character.InTheAnswer(char).apply {
                    markAsFoundIn(position)
                    characters.inTheAnswerPositions(besidesOf = position).forEach(::markAsNotFoundIn)
                }
            )
            is Character.InTheAnswer -> existent.markAsFoundIn(position)
            is Character.NotInTheAnswer -> {
                characters.remove(existent)
                characters.add(
                    Character.InTheAnswer(char).apply {
                        markAsFoundIn(position)
                        allPositionsBut(setOf(position)).forEach(::markAsNotFoundIn)
                    }
                )
            }
        }

        characters
            .filter { it.value != char }
            .forEach {
                when (it) {
                    is Character.InTheAnswer -> it.markAsNotFoundIn(position)
                    is Character.NotInTheAnswer -> {}
                }
            }
    }

    private fun addAtLeastInTheAnswer(char: Char, notInThePosition: Int) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(
                Character.InTheAnswer(char).apply {
                    (mutableSetOf(notInThePosition) + characters.inTheAnswerPositions()).forEach(::markAsNotFoundIn)
                }
            )
            is Character.InTheAnswer -> existent.also { it.markAsNotFoundIn(notInThePosition) }
            is Character.NotInTheAnswer -> throw InvalidCharactersInParserException()
        }
    }

    private fun addNotInTheAnswer(char: Char, index: Int) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(Character.NotInTheAnswer(char))
            is Character.InTheAnswer -> {
                with (existent) {
                    if (positions.any()) {
                        allPositionsBut(positions).forEach(::markAsNotFoundIn)
                    } else {
                        markAsNotFoundIn(index)
                    }
                }
            }
            is Character.NotInTheAnswer -> {}
        }
    }

    private fun allPositionsBut(positions: Set<Int>) =
        (0..4).filter { !positions.contains(it) }.toMutableSet()
}

enum class Outcome {
    InTheCorrectPosition,
    AtLeastInTheAnswer,
    NotInTheAnswer
}

class InvalidCharactersInParserException : Exception("Invalid Characters In OutcomeParser")
