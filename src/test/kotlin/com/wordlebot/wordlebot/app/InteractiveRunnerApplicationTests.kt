package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.runners.AutoPlayRunner
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.wordlebot.wordlebot.runners.Result

@SpringBootTest
class InteractiveRunnerApplicationTests {
	private val possibleWords = WordsFinder.get()

	@Test
	fun test() {
		var correctCount = 0
		var wrongCount = 0
		var pureLuckCount = 0
		val wrongAnswers = mutableListOf<String>()

		possibleWords.forEach { correctAnswer ->
			val runner = AutoPlayRunner(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser()), printInConsole = false)

			runner.run(correctAnswer).let {
				when (it) {
					Result.Correct -> correctCount++
					Result.LucklyCorrect -> {
						correctCount++
						pureLuckCount++
					}
					Result.NotFound -> wrongCount++
				}
			}
		}

		println("pure luck: $pureLuckCount")
		println("wrong: $wrongCount")
		println("correct: $correctCount")
		println("pure correct: ${correctCount - pureLuckCount}")
		println(wrongAnswers)
	}

	@Test
	fun specific()  {
		val possibleAnswers = WordsFinder.get()
		val wordleBot = WordGuesser(WordMatcher(), WordChooser())

		val characters = listOf<Character>(
		)

		repeat(2) { println() }

		println(wordleBot.guessBasedOn(possibleAnswers, characters))

		repeat(2) { println() }
	}
}
