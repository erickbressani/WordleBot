package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.models.inCorrectPositionCount
import com.wordlebot.wordlebot.runners.Attempt

class WordGuesser(private val wordMatcher: WordMatcher, private val wordChooser: WordChooser) {
    fun guessBasedOn(possibleWords: List<Word>, characters: List<Character>, attempt: Attempt): Guess =
        wordMatcher.findMatchesBasedOn(possibleWords, characters).let { matches ->
            if (shouldConsiderOnlyMatches(characters, attempt, matches)) {
                chooseBetweenMatches(matches)
            } else {
                chooseWordWithUnusedCharacters(possibleWords, characters, matches)
            }
        }

    private fun shouldConsiderOnlyMatches(characters: List<Character>, attempt: Attempt, matches: List<Word>): Boolean = with(attempt) {
        when {
            number <= 3 -> matches.count() + attempt.number <= 6
            number == 4 -> characters.inCorrectPositionCount() < 4 || matches.count() < 4
            else -> true
        }
    }

    private fun chooseWordWithUnusedCharacters(possibleWords: List<Word>, characters: List<Character>, matches: List<Word>): Guess =
        possibleWords
            .filter { word -> !word.containsAny(characters) }
            .ifEmpty { matches }
            .let { wordChooser.choseBasedOn(it, matches) }
            .toGuess(possibleWords, matches)

    private fun chooseBetweenMatches(matches: List<Word>): Guess =
        wordChooser
            .choseBasedOn(matches, matches)
            .toGuess(matches)

    private fun Word.toGuess(nextWordsToTry: List<Word>, allPossibleWords: List<Word>? = null): Guess =
        Guess(this, nextWordsToTry, allPossibleWords ?: nextWordsToTry)
}

data class Guess(val guessedWord: Word, val nextWordsToTry: List<Word>, val allPossibleWords: List<Word>)
