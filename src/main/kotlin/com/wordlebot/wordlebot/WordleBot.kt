package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.outcomes.Outcome
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.outcomes.toCodeSnippet

class WordleBot(
    private val possibleWords: List<String>,
    private val outcomeParser: OutcomeParser,
    private val wordGuesser: WordGuesser
) {
    fun run() {
        println("Possible Answers: ${possibleWords.count()}")

        try {
            (1..6).forEach { tryNumber ->
                wordGuesser.guessBasedOn(outcomeParser.getAllParsedCharacters()).let { answer ->
                    println("$tryNumber - Possible Answers: ${answer.possibleAnswersCount}")
                    println(answer.guessedWord)

                    if (tryNumber < 6) {
                        readOutcomes().let {
                            if (it == null) {
                                finish()
                            } else {
                                outcomeParser.add(answer.guessedWord, it)
                            }
                        }
                    } else {
                        finish()
                    }
                }
            }
        } catch (ex: Exception) {
            finish()
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

    private fun finish() {
        println("Finish!")
        println("Characters Used:")
        println(outcomeParser.getAllParsedCharacters().toCodeSnippet())
    }
}
