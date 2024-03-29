package com.wordlebot.wordlebot.runners

import com.wordlebot.wordlebot.guesses.Guess
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.Outcome
import com.wordlebot.wordlebot.outcomes.OutcomeParser

class InteractiveRunner(
    private var possibleWords: List<Word>,
    private val outcomeParser: OutcomeParser,
    private val wordGuesser: WordGuesser
): Runner(outcomeParser) {
    fun run() {
        println("Possible Answers: ${possibleWords.count()}")

        forEachAttempt { attempt ->
            wordGuesser.guessBasedOn(possibleWords, outcomeParser.getAllParsedCharacters(), attempt).let { guess ->
                possibleWords = guess.nextWordsToTry
                guess.print(attempt)

                readOutcomes(
                    onQuit = {
                        printCharactersUsed()
                        return@readOutcomes
                    }
                ) { outcomes -> outcomeParser.add(guess.guessedWord, outcomes) }
            }
        }

        printCharactersUsed()
    }

    private fun readOutcomes(onQuit: () -> Unit, block: (List<Outcome>) -> Unit) {
        var input: String

        do {
            input = readln().trim()

            if (input == "q" || input == ".....") {
                onQuit()
            }
        } while (input.length != 5 || input.any { !listOf('x', '.', '?').contains(it) })

        return input.toOutcomes().run(block)
    }

    private fun String.toOutcomes(): List<Outcome> =
        map {
            when {
                it.lowercase() == "x" -> Outcome.NotInTheAnswer
                it == '?' -> Outcome.AtLeastInTheAnswer
                else -> Outcome.InTheCorrectPosition
            }
        }

    private fun Guess.print(attempt: Attempt) {
        println("$attempt - Possible Answers: ${allPossibleWords.count()}")
        println(guessedWord)
    }
}
