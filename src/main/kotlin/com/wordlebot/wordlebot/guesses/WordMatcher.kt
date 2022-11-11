package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.models.inTheAnswer
import com.wordlebot.wordlebot.models.notInTheAnswer

class WordMatcher {
    fun findMatchesBasedOn(possibleWords: List<Word>, characters: List<Character>): List<Word> = with(characters) {
        possibleWords
            .filter(inTheAnswer())
            .filterOut(notInTheAnswer())
    }

    private fun List<Word>.filterOut(characters: List<Character.NotInTheAnswer>): List<Word> =
        filter { (word) -> !word.any { characters.map { char -> char.value }.contains(it) } }

    private fun List<Word>.filter(characters: List<Character.InTheAnswer>): List<Word> =
        filter { (word) -> characters.all { word.has(it) } }

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
