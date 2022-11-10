package com.wordlebot.wordlebot

class Guesser(private var possibleAnswers: List<String>) {
    fun guess(characters: List<Character>): Answer {
        keepOnlyMatches(characters)
        return Answer(findWordWithHighestScore(), possibleAnswers.count())
    }

    private fun keepOnlyMatches(characters: List<Character>) = with(characters) {
        possibleAnswers = possibleAnswers
            .filter(inTheAnswer())
            .filterOut(notInTheAnswer())
    }

    private fun findWordWithHighestScore(): String =
        WordChooser(possibleAnswers).chosenWord

    private fun List<String>.filterOut(characters: List<Character.NotInTheAnswer>): List<String> =
        filter { word -> !word.any { characters.map { char -> char.value }.contains(it) } }

    private fun List<String>.filter(characters: List<Character.InTheAnswer>): List<String> =
        filter { word -> characters.all { word.has(it) } }

    private fun String.has(character: Character.InTheAnswer): Boolean =
        contains(character.value)
                && (character.positions.isEmpty() || character.positions.all { position -> this[position] == character.value })
                && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    private fun String.hasAnyInPositionsOf(value: Char, positions: Set<Int>): Boolean {
        forEachIndexed { index, char ->
            if (positions.contains(index) && char == value) {
                return true
            }
        }

        return false
    }
}

data class Answer(val guessedWord: String, val possibleAnswersCount: Int)
