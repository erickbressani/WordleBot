package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<Word>, characters: List<Character>): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            Guess(wordChooser.choseBasedOn(matches), matches)
        }
}

data class Guess(val guessedWord: Word, val allPossibleWords: List<Word>)
