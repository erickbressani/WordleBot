package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.runners.Attempt

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<Word>, characters: List<Character>, attempt: Attempt): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            if (attempt.isBeforeAttempt(4) && matches.count() > 4) {
                possibleWords
                    .filter { word -> !word.containsAny(characters) }
                    .ifEmpty { matches }
                    .let(wordChooser::choseBasedOn)
                    .toGuess(possibleWords, matches)
            } else {
                wordChooser.choseBasedOn(matches).toGuess(matches)
            }
        }

    private fun Word.toGuess(nextWordsToTry: List<Word>, allPossibleWords: List<Word>? = null): Guess =
        Guess(this, nextWordsToTry, allPossibleWords ?: nextWordsToTry)
}

data class Guess(val guessedWord: Word, val nextWordsToTry: List<Word>, val allPossibleWords: List<Word>)
