package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.runners.Attempt

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<Word>, characters: List<Character>, attempt: Attempt): Guess =
        if (attempt.isBeforeAttempt(4)) {
            guessWithUnusedChars(possibleWords, characters)
        } else {
            guessConsideringOnlyMatches(possibleWords, characters)
        }

    private fun guessWithUnusedChars(possibleWords: List<Word>, characters: List<Character>): Guess =
        possibleWords
            .filter { word -> !word.containsAny(characters) }
            .ifEmpty { possibleWords }
            .let(wordChooser::choseBasedOn)
            .toGuess(possibleWords)

    private fun guessConsideringOnlyMatches(possibleWords: List<Word>, characters: List<Character>): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            wordChooser.choseBasedOn(matches).toGuess(matches)
        }
}

data class Guess(val guessedWord: Word, val allPossibleWords: List<Word>)
