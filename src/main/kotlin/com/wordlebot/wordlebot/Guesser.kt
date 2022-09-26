package com.wordlebot.wordlebot

class Guesser(
    private var possibleWords: Sequence<String>
) {
    fun getPossibleAnswersCount() = possibleWords.count()

    fun guess(characters: List<Character>): String {
        keepOnlyPossibleMatches(characters)
        return findWordWithHighestScore()
    }

    private fun keepOnlyPossibleMatches(characters: List<Character>) {
        possibleWords = possibleWords
            .filter { word -> !word.containsAny(characters.getNotInTheAnswer()) }
            .filter { word -> word.filterInTheCorrectPosition(characters.getInTheCorrectSpot())  }
            .filter { word -> word.filterAtLeastInTheAnswer(characters.getAtLeastInTheAnswer())  }
    }

    private fun findWordWithHighestScore(): String {
        val wordScoreMap = mutableMapOf<String, Int>()
        val scoreBoard = ScoreBoard.create(possibleWords)

        possibleWords.forEach { word -> wordScoreMap[word] = scoreBoard.getScoreOf(word) }

        return wordScoreMap
            .toList()
            .maxByOrNull { it.second }!!.first
    }

    private fun String.containsAny(chars: List<Character>) =
        any { chars.map { char -> char.value }.contains(it) }

    private fun String.filterInTheCorrectPosition(characters: List<Character.InTheCorrectPosition>) =
        characters.all { has(it) }

    private fun String.has(character: Character.InTheCorrectPosition): Boolean =
        character.positions.all { position -> this[position] == character.value } && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    private fun String.filterAtLeastInTheAnswer(characters: List<Character.AtLeastInTheAnswer>) =
        characters.all { has(it) }

    private fun String.has(character: Character.AtLeastInTheAnswer): Boolean =
        contains(character.value) && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    private fun String.hasAnyInPositionsOf(value: Char, positions: Set<Int>): Boolean {
        forEachIndexed { index, char ->
            if (positions.contains(index) && char == value) return true
        }

        return false
    }
}


