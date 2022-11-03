package com.wordlebot.wordlebot

class OutcomeParser {
    private val characters = mutableListOf<Character>()

    fun getAllParsedCharacters(): List<Character> = characters

    fun add(guessedWord: String, outcomes: List<Outcome>) {
        (0 until 5).forEach { index ->
            val char = guessedWord[index]

            when (outcomes[index]) {
                Outcome.InTheCorrectPosition -> addInTheCorrectPosition(char, index)
                Outcome.AtLeastInTheAnswer -> addAtLeastInTheAnswer(char, index)
                Outcome.NotInTheAnswer -> addNotInTheAnswer(char)
            }
        }

        characters
            .filterIsInstance<Character.AtLeastInTheAnswer>()
            .forEach { it.tryConvertToInTheCorrectPosition() }
    }

    private fun addInTheCorrectPosition(char: Char, position: Int) {
        val existent = characters.firstOrNull { it.value == char }

        if (existent == null) {
            characters.add(Character.InTheCorrectPosition(char, mutableSetOf(position), characters.inTheCorrectPositionIndexes()))
        } else {
            when (existent) {
                is Character.AtLeastInTheAnswer -> {
                    characters.remove(existent)
                    characters.add(Character.InTheCorrectPosition(char, mutableSetOf(position), existent.notInThePosition))
                }
                is Character.InTheCorrectPosition -> existent.also { it.positions.add(position) }
                is Character.NotInTheAnswer -> {
                    characters.remove(existent)
                    characters.add(
                        Character.InTheCorrectPosition(
                            char,
                            mutableSetOf(position),
                            allPositionsBut(setOf(position))
                        )
                    )
                }
            }
        }

        characters
            .filter { it.value != char }
            .forEach {
                when (it) {
                    is Character.AtLeastInTheAnswer -> it.notInThePosition.add(position)
                    is Character.InTheCorrectPosition -> it.notInThePosition.add(position)
                    is Character.NotInTheAnswer -> {}
                }
            }
    }

    private fun addAtLeastInTheAnswer(char: Char, notInThePosition: Int) {
        val existent = characters.firstOrNull { it.value == char }

        if (existent == null) {
            characters.add(
                Character.AtLeastInTheAnswer(
                    char,
                    (mutableListOf(notInThePosition) + characters.inTheCorrectPositionIndexes()).toMutableSet()
                )
            )
        } else {
            when (existent) {
                is Character.AtLeastInTheAnswer -> existent.also { it.notInThePosition.add(notInThePosition) }
                is Character.InTheCorrectPosition -> existent.also { it.notInThePosition.add(notInThePosition) }
                is Character.NotInTheAnswer -> throw InvalidCharactersInParserException()
            }
        }
    }

    private fun Character.AtLeastInTheAnswer.tryConvertToInTheCorrectPosition() {
        if (notInThePosition.count() == 4) {
            characters.remove(this)
            characters.add(Character.InTheCorrectPosition(value, allPositionsBut(notInThePosition), notInThePosition))
        }
    }

    private fun addNotInTheAnswer(char: Char) {
        val existent = characters.firstOrNull { it.value == char }

        if (existent == null) {
            characters.add(Character.NotInTheAnswer(char))
        } else {
            when (existent) {
                is Character.AtLeastInTheAnswer -> {
                    characters.remove(existent)
                    characters.add(Character.NotInTheAnswer(char))
                }
                is Character.InTheCorrectPosition -> existent.also { it.notInThePosition.addAll(allPositionsBut(it.positions)) }
                is Character.NotInTheAnswer -> {}
            }
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
