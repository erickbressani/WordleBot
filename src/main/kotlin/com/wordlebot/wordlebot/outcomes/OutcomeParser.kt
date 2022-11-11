package com.wordlebot.wordlebot.outcomes

class OutcomeParser {
    private val characters = mutableListOf<Character>()

    fun getAllParsedCharacters(): List<Character> = characters

    fun add(guessedWord: String, outcomes: List<Outcome>) {
        guessedWord.forEachIndexed { index, char ->
            when (outcomes[index]) {
                Outcome.InTheCorrectPosition -> addInTheCorrectPosition(char, index)
                Outcome.AtLeastInTheAnswer -> addAtLeastInTheAnswer(char, index)
                Outcome.NotInTheAnswer -> addNotInTheAnswer(char)
            }
        }
    }

    private fun addInTheCorrectPosition(char: Char, position: Int) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(
                Character.InTheAnswer(
                    char,
                    mutableSetOf(position),
                    characters.inTheAnswerPositions(besidesOf = position)
                )
            )
            is Character.InTheAnswer -> existent.positions.add(position)
            is Character.NotInTheAnswer -> {
                characters.remove(existent)
                characters.add(
                    Character.InTheAnswer(
                        char,
                        mutableSetOf(position),
                        allPositionsBut(setOf(position))
                    )
                )
            }
        }

        characters
            .filter { it.value != char }
            .forEach {
                when (it) {
                    is Character.InTheAnswer -> it.notInThePosition.add(position)
                    is Character.NotInTheAnswer -> {}
                }
            }
    }

    private fun addAtLeastInTheAnswer(char: Char, notInThePosition: Int) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(
                Character.InTheAnswer(
                    char,
                    mutableSetOf(),
                    (mutableSetOf(notInThePosition) + characters.inTheAnswerPositions()).toMutableSet()
                )
            )
            is Character.InTheAnswer -> existent.also { it.notInThePosition.add(notInThePosition) }
            is Character.NotInTheAnswer -> throw InvalidCharactersInParserException()
        }
    }

    private fun addNotInTheAnswer(char: Char) {
        when (val existent = characters.firstOrNull { it.value == char }) {
            null -> characters.add(Character.NotInTheAnswer(char))
            is Character.InTheAnswer -> {
                if (existent.positions.any()) {
                    existent.notInThePosition.addAll(allPositionsBut(existent.positions))
                } else {
                    characters.remove(existent)
                    characters.add(Character.NotInTheAnswer(char))
                }
            }
            is Character.NotInTheAnswer -> {}
        }
    }

    private fun allPositionsBut(positions: Set<Int>) =
        listOf(0, 1, 2, 3, 4).filter { !positions.contains(it) }.toMutableSet()
}

enum class Outcome {
    InTheCorrectPosition,
    AtLeastInTheAnswer,
    NotInTheAnswer
}

class InvalidCharactersInParserException : Exception("Invalid Characters In OutcomeParser")
