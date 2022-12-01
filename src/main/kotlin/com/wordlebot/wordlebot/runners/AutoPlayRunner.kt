package com.wordlebot.wordlebot.runners

import com.wordlebot.wordlebot.guesses.Guess
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.Outcome
import com.wordlebot.wordlebot.outcomes.OutcomeParser

class AutoPlayRunner(
    private val words: List<Word>,
    private val outcomeParser: OutcomeParser,
    private val wordGuesser: WordGuesser,
    private val printInConsole: Boolean = true
): Runner(outcomeParser, printInConsole) {
    fun run(correctAnswer: Word): Result {
        var result = Result.NotFound
        var possibleWords = words

        forEachAttempt { attempt ->
            with(wordGuesser.guessBasedOn(possibleWords, outcomeParser.getAllParsedCharacters(), attempt)) {
                if (result == Result.NotFound) {
                    possibleWords = nextWordsToTry

                    guessedWord
                        .getOutcomesBasedOn(correctAnswer)
                        .let { outcomes ->
                            outcomeParser.add(guessedWord, outcomes)
                            print(attempt, outcomes)
                        }

                    if (guessedWord == correctAnswer) {
                        result = if (nextWordsToTry.count() > 1 && attempt.isLast()) Result.LucklyCorrect else Result.Correct
                    }
                }
            }
        }

        return result.apply { print() }
    }

    private fun Result.print() {
        if (printInConsole) {
            repeat(2) { println() }

            if (isCorrect()) {
                println("It is correct! \uD83D\uDE00")
            } else {
                println("It is incorrect \uD83D\uDE2D")
            }

            println("print characters? (y) yes | (enter) leave")
            println()
            readln()
                .lowercase()
                .takeIf { it == "y" }
                ?.let { printCharactersUsed() }
        }
    }

    private fun Word.getOutcomesBasedOn(correctAnswer: Word): List<Outcome> {
        val outcomesPerChar = mutableMapOf<Char, Outcome>()
        val outcomesPerIndex = mutableMapOf<Int, Outcome>()
        val charsToCheckForAtLeastInAnswer = mutableMapOf<Int, Char>()

        forEachCharIndexed { index, char ->
            if (char == correctAnswer.value[index]) {
                outcomesPerChar[char] = Outcome.InTheCorrectPosition
                outcomesPerIndex[index] = Outcome.InTheCorrectPosition
            } else if (!correctAnswer.value.contains(char)) {
                outcomesPerChar[char] = Outcome.NotInTheAnswer
                outcomesPerIndex[index] = Outcome.NotInTheAnswer
            } else {
                charsToCheckForAtLeastInAnswer[index] = char
            }
        }

        charsToCheckForAtLeastInAnswer.forEach { (index, char) ->
            val correctInOutcomesCount = outcomesPerChar.count { it.key == char }
            val charInAnswerCount = correctAnswer.value.count { it == char }

            if (charInAnswerCount == correctInOutcomesCount) {
                outcomesPerIndex[index] = Outcome.NotInTheAnswer
            } else {
                val charOccurrencesBeforeOnGuess = value.take(index).count { it == char }

                outcomesPerIndex[index] = (charInAnswerCount - charOccurrencesBeforeOnGuess).let {
                    if (it <= 0) {
                        Outcome.NotInTheAnswer
                    } else {
                        Outcome.AtLeastInTheAnswer
                    }
                }
            }
        }

        return outcomesPerIndex.keys.sorted()
            .map { outcomesPerIndex[it]!! }
    }

    private fun Guess.print(attempt: Attempt, outcomes: List<Outcome>) = with(StringBuilder()) {
        if (printInConsole) {
            if (!attempt.isFirst()) readln()

            this@print.guessedWord.forEachCharIndexed { index, char ->
                when(outcomes[index]) {
                    Outcome.InTheCorrectPosition -> append(ANSI_GREEN + char)
                    Outcome.AtLeastInTheAnswer -> append(ANSI_YELLOW + char)
                    Outcome.NotInTheAnswer -> append(ANSI_RESET + char)
                }
            }

            print("$attempt - ${toString()} $ANSI_RESET - Possible Answers: ${this@print.allPossibleWords.count()}")
        }
    }

    companion object {
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_RESET = "\u001B[0m"
    }
}

enum class Result {
    Correct,
    LucklyCorrect,
    NotFound
}

private fun Result.isCorrect(): Boolean =
    this == Result.LucklyCorrect || this == Result.Correct
