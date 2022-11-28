package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordChooserByScore
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
	private val scoreboardForAllWords = WordChooserByScore().choseBasedOn(possibleWords)

	private val wordChooser = object : WordChooser {
		override fun choseBasedOn(possibleWords: List<Word>): Word {
			if (possibleWords.count() == this@BatchTests.possibleWords.size) {
				return scoreboardForAllWords
			}
			return WordChooserByScore().choseBasedOn(possibleWords)
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
			Arguments.of(1, ExpectedOutcome(12, 486)),
			Arguments.of(2, ExpectedOutcome(16, 483)),
			Arguments.of(3, ExpectedOutcome(21, 476)),
			Arguments.of(4, ExpectedOutcome(23, 468)),
			Arguments.of(5, ExpectedOutcome(14, 465)),
			Arguments.of(6, ExpectedOutcome(26, 458)),
			Arguments.of(7, ExpectedOutcome(23, 459)),
			Arguments.of(8, ExpectedOutcome(24, 460)),
			Arguments.of(9, ExpectedOutcome(57, 472)),
			Arguments.of(10, ExpectedOutcome(53, 456)),
			Arguments.of(11, ExpectedOutcome(48, 427)),
			Arguments.of(12, ExpectedOutcome(45, 429)),
			Arguments.of(13, ExpectedOutcome(33, 423)),
			Arguments.of(14, ExpectedOutcome(21, 457)),
			Arguments.of(15, ExpectedOutcome(30, 430)),
			Arguments.of(16, ExpectedOutcome(34, 437)),
			Arguments.of(17, ExpectedOutcome(25, 428)),
			Arguments.of(18, ExpectedOutcome(10, 465)),
			Arguments.of(19, ExpectedOutcome(25, 427)),
			Arguments.of(20, ExpectedOutcome(30, 418)),
			Arguments.of(21, ExpectedOutcome(20, 434)),
			Arguments.of(22, ExpectedOutcome(15, 452)),
			Arguments.of(23, ExpectedOutcome(8, 412)),
			Arguments.of(24, ExpectedOutcome(16, 449)),
			Arguments.of(25, ExpectedOutcome(18, 435)),
			Arguments.of(26, ExpectedOutcome(14, 446)),
			Arguments.of(27, ExpectedOutcome(15, 462)),
			Arguments.of(28, ExpectedOutcome(10, 448)),
			Arguments.of(29, ExpectedOutcome(24, 354)),
			Arguments.of(30, ExpectedOutcome(14, 253))
		)
	}
}

data class ExpectedOutcome(val lucklyCorrectCount: Int, val correctCount: Int)
