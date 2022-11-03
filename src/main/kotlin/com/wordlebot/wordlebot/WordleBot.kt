package com.wordlebot.wordlebot

class WordleBot(
    private val possibleWords: Sequence<String>,
    private val accumulator: Accumulator,
    private val guesser: Guesser
) {
    fun run() {
        println("Possible Answers: ${possibleWords.count()}")

        try {
            (1..6).forEach { tryNumber ->
                guesser.guess(accumulator.getAll()).let { word ->
                    println("$tryNumber - Possible Answers: ${guesser.getPossibleAnswersCount()}")
                    println(word)

                    if (tryNumber < 6) {
                        readOutcomes().let {
                            if (it == null) {
                                finish()
                            } else {
                                accumulator.add(word, it)
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
        println(accumulator.getAll().toCodeSnippet())
    }
}
