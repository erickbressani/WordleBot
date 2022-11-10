package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.outcomes.Character
import com.wordlebot.wordlebot.outcomes.inTheAnswer
import com.wordlebot.wordlebot.outcomes.notInTheAnswer

class WordMatcher(private var words: List<String>) {
    fun findMatchesBasedOn(characters: List<Character>): List<String> = with(characters) {
        words
            .filter(inTheAnswer())
            .filterOut(notInTheAnswer())
    }

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
