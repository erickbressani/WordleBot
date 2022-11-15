package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.models.contains
import com.wordlebot.wordlebot.models.inTheAnswer
import com.wordlebot.wordlebot.models.notInTheAnswer

class WordMatcher {
    fun findMatchesBasedOn(possibleWords: List<Word>, characters: List<Character>): List<Word> = with(characters) {
        possibleWords
            .filter(inTheAnswer())
            .filterOut(notInTheAnswer())
    }

    private fun List<Word>.filterOut(characters: List<Character.NotInTheAnswer>): List<Word> =
        filter { (word) -> !word.any { characters.contains(it) } }

    private fun List<Word>.filter(characters: List<Character.InTheAnswer>): List<Word> =
        filter { (word) -> characters.all { word.has(it) } }

    private fun String.has(character: Character.InTheAnswer): Boolean =
        contains(character) && hasInEveryPositionOf(character) && !hasAnyInIncorrectPositionOf(character)

    private fun String.contains(character: Character): Boolean =
        contains(character.value)

    private fun String.hasInEveryPositionOf(character: Character.InTheAnswer): Boolean = with(character) {
        positions.isEmpty() || positions.all { position -> this@hasInEveryPositionOf[position] == value }
    }

    private fun String.hasAnyInIncorrectPositionOf(character: Character.InTheAnswer): Boolean = with(character) {
        forEachIndexed { index, char ->
            if (notInThePosition.contains(index) && char == value) {
                return true
            }
        }

        return false
    }
}
