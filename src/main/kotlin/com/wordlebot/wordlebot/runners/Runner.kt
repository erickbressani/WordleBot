package com.wordlebot.wordlebot.runners

import com.wordlebot.wordlebot.models.toCodeSnippet
import com.wordlebot.wordlebot.outcomes.OutcomeParser

abstract class Runner(
    private val outcomeParser: OutcomeParser,
    private val printInConsole: Boolean = true
) {
    fun forEachTry(block: (Int) -> Unit) =
        try {
            (1..6).forEach(block)
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
}
