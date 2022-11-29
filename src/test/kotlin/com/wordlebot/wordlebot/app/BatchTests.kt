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
			Arguments.of(1, ExpectedOutcome(lucklyCorrectCount = 4, correctCount = 493)),
			Arguments.of(2, ExpectedOutcome(lucklyCorrectCount = 10, correctCount = 486)),
			Arguments.of(3, ExpectedOutcome(lucklyCorrectCount = 8, correctCount =483)),
			Arguments.of(4, ExpectedOutcome(lucklyCorrectCount = 15, correctCount =480)),
			Arguments.of(5, ExpectedOutcome(lucklyCorrectCount = 10, correctCount =490)),
			Arguments.of(6, ExpectedOutcome(lucklyCorrectCount = 11, correctCount =477)),
			Arguments.of(7, ExpectedOutcome(lucklyCorrectCount = 24, correctCount =481)),
			Arguments.of(8, ExpectedOutcome(lucklyCorrectCount = 16, correctCount =482)),
			Arguments.of(9, ExpectedOutcome(lucklyCorrectCount = 42, correctCount =484)),
			Arguments.of(10, ExpectedOutcome(lucklyCorrectCount = 44, correctCount=470)),
			Arguments.of(11, ExpectedOutcome(lucklyCorrectCount = 18, correctCount=478)),
			Arguments.of(12, ExpectedOutcome(lucklyCorrectCount = 12, correctCount = 482)),
			Arguments.of(13, ExpectedOutcome(lucklyCorrectCount = 46, correctCount = 457)),
			Arguments.of(14, ExpectedOutcome(lucklyCorrectCount = 14, correctCount = 465)),
			Arguments.of(15, ExpectedOutcome(lucklyCorrectCount = 17, correctCount = 468)),
			Arguments.of(16, ExpectedOutcome(lucklyCorrectCount = 8, correctCount= 489)),
			Arguments.of(17, ExpectedOutcome(lucklyCorrectCount = 13, correctCount= 486)),
			Arguments.of(18, ExpectedOutcome(lucklyCorrectCount = 3, correctCount= 490)),
			Arguments.of(19, ExpectedOutcome(lucklyCorrectCount = 9, correctCount= 482)),
			Arguments.of(20, ExpectedOutcome(lucklyCorrectCount = 7, correctCount= 487)),
			Arguments.of(21, ExpectedOutcome(lucklyCorrectCount = 11, correctCount= 466)),
			Arguments.of(22, ExpectedOutcome(lucklyCorrectCount = 7, correctCount = 476)),
			Arguments.of(23, ExpectedOutcome(lucklyCorrectCount = 7, correctCount = 442)),
			Arguments.of(24, ExpectedOutcome(lucklyCorrectCount = 4, correctCount = 468)),
			Arguments.of(25, ExpectedOutcome(lucklyCorrectCount = 7, correctCount = 469)),
			Arguments.of(26, ExpectedOutcome(lucklyCorrectCount = 6, correctCount = 481)),
			Arguments.of(27, ExpectedOutcome(lucklyCorrectCount = 14, correctCount = 484)),
			Arguments.of(28, ExpectedOutcome(lucklyCorrectCount = 13, correctCount = 466)),
			Arguments.of(29, ExpectedOutcome(lucklyCorrectCount = 38, correctCount = 407)),
			Arguments.of(30, ExpectedOutcome(lucklyCorrectCount = 12, correctCount = 314))
		)
	}
}

data class ExpectedOutcome(val lucklyCorrectCount: Int, val correctCount: Int)
