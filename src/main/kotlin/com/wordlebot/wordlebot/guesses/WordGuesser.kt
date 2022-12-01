package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.runners.Attempt

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<Word>, characters: List<Character>, attempt: Attempt): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            if (attempt.isBeforeAttempt(4) && matches.count() > 4) {
                chooseWithUnusedCharacters(possibleWords, characters, matches)
            } else {
                chooseWithMatches(matches)
            }
        }

    private fun chooseWithUnusedCharacters(possibleWords: List<Word>, characters: List<Character>, matches: List<Word>): Guess =
        possibleWords
            .filter { word -> !word.containsAny(characters) }
            .ifEmpty { matches }
            .let { wordChooser.choseBasedOn(it, matches) }
            .toGuess(possibleWords, matches)

    private fun chooseWithMatches(matches: List<Word>): Guess =
        wordChooser
            .choseBasedOn(matches, matches)
            .toGuess(matches)

    private fun Word.toGuess(nextWordsToTry: List<Word>, allPossibleWords: List<Word>? = null): Guess =
        Guess(this, nextWordsToTry, allPossibleWords ?: nextWordsToTry)
}

data class Guess(val guessedWord: Word, val nextWordsToTry: List<Word>, val allPossibleWords: List<Word>)
