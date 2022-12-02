package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordChooserByScore
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.runners.AutoPlayRunner
import com.wordlebot.wordlebot.runners.Result
import io.kotest.matchers.should
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
	private val scoreboardForAllWords = WordChooserByScore().choseBasedOn(possibleWords, possibleWords)

	private val wordChooser = object : WordChooser {
		override fun choseBasedOn(wordsToGuess: List<Word>, currentMatches: List<Word>): Word {
			if (wordsToGuess.count() == this@BatchTests.possibleWords.size) {
				return scoreboardForAllWords
			}
			return WordChooserByScore().choseBasedOn(wordsToGuess, currentMatches)
		}
	}

	@ParameterizedTest
	@MethodSource("batches")
	fun testWith(batchNumber: Int, expectedTestOutcome: TestOutcome) {
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

//		println(correctCount)
		TestOutcome(pureLuckCount, correctCount) shouldBe expectedTestOutcome
	}

	private fun List<Word>.getBatch(batchNumber: Int): List<Word> =
		drop((batchNumber - 1) * batchSize).take(batchSize)

	companion object {
		@JvmStatic
		fun batches() = listOf(
			Arguments.of(1, TestOutcome(lucklyCorrectCount=2, correctCount=497)),
			Arguments.of(2, TestOutcome(lucklyCorrectCount=1, correctCount=495)),
			Arguments.of(3, TestOutcome(lucklyCorrectCount=7, correctCount=488)),
			Arguments.of(4, TestOutcome(lucklyCorrectCount=10, correctCount=487)),
			Arguments.of(5, TestOutcome(lucklyCorrectCount=10, correctCount=490)),
			Arguments.of(6, TestOutcome(lucklyCorrectCount=6, correctCount=485)),
			Arguments.of(7, TestOutcome(lucklyCorrectCount=13, correctCount=487)),
			Arguments.of(8, TestOutcome(lucklyCorrectCount=11, correctCount=488)),
			Arguments.of(9, TestOutcome(lucklyCorrectCount=29, correctCount=492)),
			Arguments.of(10, TestOutcome(lucklyCorrectCount=26, correctCount=485)),
			Arguments.of(11, TestOutcome(lucklyCorrectCount=17, correctCount=478)),
			Arguments.of(12, TestOutcome(lucklyCorrectCount=13, correctCount=484)),
			Arguments.of(13, TestOutcome(lucklyCorrectCount=35, correctCount=466)),
			Arguments.of(14, TestOutcome(lucklyCorrectCount=8, correctCount=483)),
			Arguments.of(15, TestOutcome(lucklyCorrectCount=13, correctCount=479)),
			Arguments.of(16, TestOutcome(lucklyCorrectCount=18, correctCount=477)),
			Arguments.of(17, TestOutcome(lucklyCorrectCount=13, correctCount=479)),
			Arguments.of(18, TestOutcome(lucklyCorrectCount=1, correctCount=488)),
			Arguments.of(19, TestOutcome(lucklyCorrectCount=6, correctCount=494)),
			Arguments.of(20, TestOutcome(lucklyCorrectCount=12, correctCount=484)),
			Arguments.of(21, TestOutcome(lucklyCorrectCount=7, correctCount=479)),
			Arguments.of(22, TestOutcome(lucklyCorrectCount=7, correctCount=482)),
			Arguments.of(23, TestOutcome(lucklyCorrectCount=6, correctCount=453)),
			Arguments.of(24, TestOutcome(lucklyCorrectCount=4, correctCount=472)),
			Arguments.of(25, TestOutcome(lucklyCorrectCount=3, correctCount=476)),
			Arguments.of(26, TestOutcome(lucklyCorrectCount=1, correctCount=488)),
			Arguments.of(27, TestOutcome(lucklyCorrectCount=4, correctCount=496)),
			Arguments.of(28, TestOutcome(lucklyCorrectCount=9, correctCount=477)),
			Arguments.of(29, TestOutcome(lucklyCorrectCount=29, correctCount=437)),
			Arguments.of(30, TestOutcome(lucklyCorrectCount=9, correctCount=316)),
		)
	}
}

data class TestOutcome(val lucklyCorrectCount: Int, val correctCount: Int) {
	fun toCodeSnippet(batchNumber: Int): String =
		"Arguments.of($batchNumber, $this),"
}
