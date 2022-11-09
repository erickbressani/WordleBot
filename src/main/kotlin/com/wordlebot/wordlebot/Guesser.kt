package com.wordlebot.wordlebot

class Guesser(private var possibleWords: Sequence<String>) {
    fun guess(characters: List<Character>): Answer {
        keepOnlyPossibleMatches(characters)

        return Answer(findWordWithHighestScore(), possibleWords.count())
    }

    private fun keepOnlyPossibleMatches(characters: List<Character>) = with(characters) {
        possibleWords = possibleWords
            .filterOut(notInTheAnswer())
            .filter(inTheAnswer()).toList().asSequence()
    }

    private fun findWordWithHighestScore(): String =
        ScoreBoard(possibleWords).getWordWithHighestScore()

    private fun Sequence<String>.filterOut(characters: List<Character.NotInTheAnswer>): Sequence<String> =
        filter { word -> !word.any { characters.map { char -> char.value }.contains(it) } }

    @JvmName("filterAtLeastInTheAnswer")
    private fun Sequence<String>.filter(characters: List<Character.InTheAnswer>): Sequence<String> =
        filter { word -> characters.all { word.has(it) } }

    private fun String.has2(character: Character.InTheAnswer): Boolean =
        (character.positions.all { position -> this[position] == character.value } && !hasAnyInPositionsOf(character.value, character.notInThePosition)) &&
                (contains(character.value) && !hasAnyInPositionsOf(character.value, character.notInThePosition))

    private fun String.has(character: Character.InTheAnswer): Boolean =
        (character.positions.isEmpty() || character.positions.all { position -> this[position] == character.value })
                && contains(character.value)
                && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    private fun String.hasAnyInPositionsOf(value: Char, positions: Set<Int>): Boolean {
        forEachIndexed { index, char ->
            if (positions.contains(index) && char == value) return true
        }

        return false
    }
}

data class Answer(val guessedWord: String, val possibleAnswersCount: Int)
