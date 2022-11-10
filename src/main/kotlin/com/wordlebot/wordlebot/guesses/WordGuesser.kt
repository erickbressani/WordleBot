package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.outcomes.Character

class WordGuesser(private var possibleAnswers: List<String>) {
    fun guessBasedOn(characters: List<Character>): Answer =
        WordMatcher(possibleAnswers)
            .findMatchesBasedOn(characters)
            .let { possibleAnswers ->
                Answer(WordChooser(possibleAnswers).chosenWord, possibleAnswers.count())
            }
}

data class Answer(val guessedWord: String, val possibleAnswersCount: Int)
