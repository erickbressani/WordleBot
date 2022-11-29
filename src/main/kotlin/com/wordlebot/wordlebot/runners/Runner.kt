package com.wordlebot.wordlebot.runners

import com.wordlebot.wordlebot.models.toCodeSnippet
import com.wordlebot.wordlebot.outcomes.OutcomeParser

abstract class Runner(
    private val outcomeParser: OutcomeParser,
    private val printInConsole: Boolean = true
) {
    fun forEachAttempt(block: (Attempt) -> Unit) =
        try {
            createAttempts().forEach(block)
        } catch (ex: Exception) {
            printCharactersUsed()
            throw ex
        }

    fun printCharactersUsed() {
        if (printInConsole) {
            println("Characters Used:")
            println(outcomeParser.getAllParsedCharacters().toCodeSnippet())
        }
    }

    private fun createAttempts(): List<Attempt> =
        (1..6).map(::Attempt)
}

data class Attempt(private val number: Int) {
    init {
        require(number in 1..6) { "There can only be 1 to 6 tries" }
    }

    fun isBeforeAttempt(attemptNumber: Int) =
        number < attemptNumber

    fun isFirst(): Boolean =
        number == 1

    fun isLast(): Boolean =
        number == 6

    override fun toString(): String =
        number.toString()
}
