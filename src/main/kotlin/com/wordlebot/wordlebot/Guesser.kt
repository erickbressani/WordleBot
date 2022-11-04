package com.wordlebot.wordlebot

class Guesser(private var possibleWords: Sequence<String>) {
    fun getPossibleAnswersCount() = possibleWords.count()

    fun guess(characters: List<Character>): String {
        keepOnlyPossibleMatches(characters)
        return findWordWithHighestScore()
    }

    private fun keepOnlyPossibleMatches(characters: List<Character>) = with(characters) {
        possibleWords = possibleWords
            .filterOut(notInTheAnswer())
            .filter(inTheAnswer())
    }

    private fun findWordWithHighestScore(): String =
        ScoreBoard(possibleWords).getWordWithHighestScore()

    private fun Sequence<String>.filterOut(characters: List<Character.NotInTheAnswer>): Sequence<String> =
        filter { word -> !word.any { characters.map { char -> char.value }.contains(it) } }

    @JvmName("filterAtLeastInTheAnswer")
    private fun Sequence<String>.filter(characters: List<Character.InTheAnswer>): Sequence<String> =
        filter { word -> characters.all { word.has(it) } }

    private fun String.has(character: Character.InTheAnswer): Boolean =
        (character.positions.all { position -> this[position] == character.value } && !hasAnyInPositionsOf(character.value, character.notInThePosition)) ||
                (contains(character.value) && !hasAnyInPositionsOf(character.value, character.notInThePosition))

    private fun String.hasAnyInPositionsOf(value: Char, positions: Set<Int>): Boolean {
        forEachIndexed { index, char ->
            if (positions.contains(index) && char == value) return true
        }

        return false
    }
}


