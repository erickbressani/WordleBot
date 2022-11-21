package com.wordlebot.wordlebot.outcomes

import com.wordlebot.wordlebot.models.toCodeSnippet

abstract class Runner(
    private val outcomeParser: OutcomeParser,
    private val printInConsole: Boolean
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
