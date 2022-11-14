package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.Guess
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.Outcome
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.models.toCodeSnippet

class WordleBot(
    private var possibleWords: List<Word>,
    private val outcomeParser: OutcomeParser,
    private val wordGuesser: WordGuesser
) {
    fun run() {
        println("Possible Answers: ${possibleWords.count()}")

        forEachTry { tryNumber ->
            wordGuesser.guessBasedOn(possibleWords, outcomeParser.getAllParsedCharacters()).let { guess ->
                possibleWords = guess.allPossibleWords
                guess.print(tryNumber)

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

    private fun forEachTry(block: (Int) -> Unit) =
        try {
            (1..6).forEach(block)
        } catch (ex: Exception) {
            printCharactersUsed()
            throw ex
        }

    private fun readOutcomes(onQuit: () -> Unit, block: (List<Outcome>) -> Unit) {
        var input: String

        do {
            input = readln().trim()

            if (input == "q") {
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

    private fun Guess.print(tryNumber: Int) {
        println("$tryNumber - Possible Answers: ${allPossibleWords.count()}")
        println(guessedWord)
    }

    private fun printCharactersUsed() {
        println("Characters Used:")
        println(outcomeParser.getAllParsedCharacters().toCodeSnippet())
    }
}
