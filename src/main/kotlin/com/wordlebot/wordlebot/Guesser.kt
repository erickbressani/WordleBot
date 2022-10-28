package com.wordlebot.wordlebot

class Guesser(
    private var possibleWords: Sequence<String>
) {
    fun getPossibleAnswersCount() = possibleWords.count()

    fun guess(characters: List<Character>): String {
        keepOnlyPossibleMatches(characters)
        return findWordWithHighestScore()
    }

    private fun keepOnlyPossibleMatches(characters: List<Character>) = with(characters) {
        possibleWords = possibleWords
            .filterOut(notInTheAnswer())
            .filter(inTheCorrectSpot())
            .filter(atLeastInTheAnswer())
    }

    private fun findWordWithHighestScore(): String {
        val wordScoreMap = mutableMapOf<String, Int>()
        val scoreBoard = ScoreBoard.create(possibleWords)

        possibleWords.forEach { word -> wordScoreMap[word] = scoreBoard.getScoreOf(word) }

        return wordScoreMap
            .toList()
            .maxByOrNull { it.second }!!.first
    }

    private fun Sequence<String>.filterOut(characters: List<Character.NotInTheAnswer>): Sequence<String> =
        filter { word -> !word.any { characters.map { char -> char.value }.contains(it) } }

    @JvmName("filterInTheCorrectPosition")
    private fun Sequence<String>.filter(characters: List<Character.InTheCorrectPosition>): Sequence<String> =
        this.filter { word -> characters.all { word.has(it) } }

    private fun String.has(character: Character.InTheCorrectPosition): Boolean =
        character.positions.all { position -> this[position] == character.value } && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    @JvmName("filterAtLeastInTheAnswer")
    private fun Sequence<String>.filter(characters: List<Character.AtLeastInTheAnswer>): Sequence<String> =
        filter { word -> characters.all { word.has(it) } }

    private fun String.has(character: Character.AtLeastInTheAnswer): Boolean =
        contains(character.value) && !hasAnyInPositionsOf(character.value, character.notInThePosition)

    private fun String.hasAnyInPositionsOf(value: Char, positions: Set<Int>): Boolean {
        forEachIndexed { index, char ->
            if (positions.contains(index) && char == value) return true
        }

        return false
    }
}


