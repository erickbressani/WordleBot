package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.runners.AutoPlayRunner
import com.wordlebot.wordlebot.runners.Result
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Execution(ExecutionMode.CONCURRENT)
class BatchTests {
	private val possibleWords = WordsFinder.get()
	private val batchSize = 500
	private val scoreboardForAllWords = WordChooser.default.choseBasedOn(possibleWords)

	private val wordChooser = object : WordChooser {
		override fun choseBasedOn(possibleWords: List<Word>): Word {
			if (possibleWords.count() == this@BatchTests.possibleWords.size) {
				return scoreboardForAllWords
			}
			return WordChooser.default.choseBasedOn(possibleWords)
		}
	}

	@ParameterizedTest
	@MethodSource("batches")
	fun testWith(batchNumber: Int, expectedOutcome: ExpectedOutcome) {
		var correctCount = 0
		var pureLuckCount = 0

		possibleWords.getBatch(batchNumber).forEach { correctAnswer ->
			val runner = AutoPlayRunner(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), wordChooser), printInConsole = false)

			runner.run(correctAnswer).let {
				when (it) {
					Result.Correct -> correctCount++
					Result.LucklyCorrect -> {
						correctCount++
						pureLuckCount++
					}
					Result.NotFound -> {}
				}
			}
		}

		ExpectedOutcome(pureLuckCount, correctCount) shouldBe expectedOutcome
	}

	private fun List<Word>.getBatch(batchNumber: Int): List<Word> =
		drop((batchNumber - 1) * batchSize).take(batchSize)

	companion object {
		@JvmStatic
		fun batches() = listOf(
			Arguments.of(1, ExpectedOutcome(14, 482)),
			Arguments.of(2, ExpectedOutcome(12, 481)),
			Arguments.of(3, ExpectedOutcome(16, 473)),
			Arguments.of(4, ExpectedOutcome(24, 476)),
			Arguments.of(5, ExpectedOutcome(33, 472)),
			Arguments.of(6, ExpectedOutcome(30, 469)),
			Arguments.of(7, ExpectedOutcome(23, 461)),
			Arguments.of(8, ExpectedOutcome(25, 457)),
			Arguments.of(9, ExpectedOutcome(26, 464)),
			Arguments.of(10, ExpectedOutcome(28, 461)),
			Arguments.of(11, ExpectedOutcome(23, 450)),
			Arguments.of(12, ExpectedOutcome(31, 442)),
			Arguments.of(13, ExpectedOutcome(32, 457)),
			Arguments.of(14, ExpectedOutcome(38, 443)),
			Arguments.of(15, ExpectedOutcome(26, 448)),
			Arguments.of(16, ExpectedOutcome(32, 445)),
			Arguments.of(17, ExpectedOutcome(33, 440)),
			Arguments.of(18, ExpectedOutcome(28, 427)),
			Arguments.of(19, ExpectedOutcome(20, 438)),
			Arguments.of(20, ExpectedOutcome(33, 441)),
			Arguments.of(21, ExpectedOutcome(29, 433)),
			Arguments.of(22, ExpectedOutcome(33, 431)),
			Arguments.of(23, ExpectedOutcome(21, 395)),
			Arguments.of(24, ExpectedOutcome(19, 440)),
			Arguments.of(25, ExpectedOutcome(24, 407)),
			Arguments.of(26, ExpectedOutcome(18, 429)),
			Arguments.of(27, ExpectedOutcome(18, 419)),
			Arguments.of(28, ExpectedOutcome(15, 399)),
			Arguments.of(29, ExpectedOutcome(11, 407)),
			Arguments.of(30, ExpectedOutcome(11, 279))
		)
	}
}

data class ExpectedOutcome(val lucklyCorrectCount: Int, val correctCount: Int)
