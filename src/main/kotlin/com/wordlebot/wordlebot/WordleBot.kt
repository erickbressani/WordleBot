package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.outcomes.Outcome
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.outcomes.toCodeSnippet

class WordleBot(
    private var possibleWords: List<String>,
    private val outcomeParser: OutcomeParser,
    private val wordGuesser: WordGuesser
) {
    fun run() {
        println("Possible Answers: ${possibleWords.count()}")

        try {
            (1..6).forEach { tryNumber ->
                wordGuesser.guessBasedOn(possibleWords, outcomeParser.getAllParsedCharacters()).let { guess ->
                    possibleWords = guess.allPossibleWords
                    println("$tryNumber - Possible Answers: ${guess.allPossibleWords.count()}")
                    println(guess.guessedWord)

                    if (tryNumber < 6) {
                        readOutcomes().let { outcomes ->
                            if (outcomes == null) {
                                printCharactersUsed()
                                return
                            } else {
                                outcomeParser.add(guess.guessedWord, outcomes)
                            }
                        }
                    } else {
                        printCharactersUsed()
                    }
                }
            }
        } catch (ex: Exception) {
            printCharactersUsed()
            throw ex
        }
    }

    private fun readOutcomes(): List<Outcome>? {
        var input: String

        do {
            input = readln().trim()
            if (input == "q") return null
        } while (input.length != 5 || input.any { !listOf('x', '.', '?').contains(it) })

        return input.toOutcomes()
    }

    private fun String.toOutcomes() =
        map {
            when {
                it.lowercase() == "x" -> Outcome.NotInTheAnswer
                it == '?' -> Outcome.AtLeastInTheAnswer
                else -> Outcome.InTheCorrectPosition
            }
        }

    private fun printCharactersUsed() {
        println("Characters Used:")
        println(outcomeParser.getAllParsedCharacters().toCodeSnippet())
    }
}
