package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.outcomes.Character

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<String>, characters: List<Character>): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            Guess(wordChooser.choseBasedOn(matches), matches)
        }
}

data class Guess(val guessedWord: String, val allPossibleWords: List<String>)
