package com.wordlebot.wordlebot

import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.system.measureTimeMillis

@SpringBootTest
class WordleBotApplicationTests {
	fun getPossibleWords(): List<String> =
		File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
			.bufferedReader()
			.readText()
			.split("\n")
			.filter { it.isNotEmpty() }

	@Test
	fun test() {
		var correctCount = 0
		var wrongCount = 0
		var pureLuckCount = 0
		val wrongAnswers = mutableListOf<String>()
		val possibleWords = getPossibleWords()

		possibleWords.forEach { correctAnswer ->
			run(correctAnswer, possibleWords)
				.let {
					when (it) {
						Result.Correct -> correctCount++
						Result.LucklyCorrect -> {
							correctCount++
							pureLuckCount++
						}
						Result.Wrong -> wrongCount++
					}
				}
		}

		println("pure luck: $pureLuckCount")
		println("wrong: $wrongCount")
		println("correct: $correctCount")
		println("pure correct: ${correctCount - pureLuckCount}")
		println(wrongAnswers)
	}

	private fun run(correctAnswer: String, possibleWords: List<String>): Result {
		val outcomeParser = OutcomeParser()
		val guesser = Guesser(mutableListOf<String>().apply { addAll(possibleWords) })

		for (tryNumber in 0..5) {
			val answer = guesser.guess(outcomeParser.getAllParsedCharacters())

			if (answer.guessedWord == correctAnswer) {
				return if (tryNumber == 5 && answer.possibleAnswersCount > 1) {
					Result.LucklyCorrect
				} else {
					Result.Correct
				}
			} else if (tryNumber == 5) {
				return Result.Wrong
			}

			outcomeParser.add(answer.guessedWord, answer.guessedWord.getOutcomesBasedOn(correctAnswer))
		}

		return Result.Wrong
	}

	@Test
	fun specific()  {
		val possibleAnswers = getPossibleWords()
		val wordleBot = Guesser(possibleAnswers)

		val characters = listOf<Character>(
		)

		repeat(2) { println() }

		println(wordleBot.guess(characters))

		repeat(2) { println() }
	}

	private fun String.getOutcomesBasedOn(correctAnswer: String): List<Outcome> {
		val outcomesPerChar = mutableMapOf<Char, Outcome>()
		val outcomesPerIndex = mutableMapOf<Int, Outcome>()
		val charsToCheckForAtLeastInAnswer = mutableMapOf<Int, Char>()

		forEachIndexed { index, char ->
			if (char == correctAnswer[index]) {
				outcomesPerChar[char] = Outcome.InTheCorrectPosition
				outcomesPerIndex[index] = Outcome.InTheCorrectPosition
			} else if (!correctAnswer.contains(char)) {
				outcomesPerChar[char] = Outcome.NotInTheAnswer
				outcomesPerIndex[index] = Outcome.NotInTheAnswer
			} else {
				charsToCheckForAtLeastInAnswer[index] = char
			}
		}

		charsToCheckForAtLeastInAnswer.forEach { (index, char) ->
			val correctInOutcomesCount = outcomesPerChar.count { it.key == char }
			val charInAnswerCount = correctAnswer.count { it == char }

			if (charInAnswerCount == correctInOutcomesCount) {
				outcomesPerIndex[index] = Outcome.NotInTheAnswer
			} else {
				outcomesPerIndex[index] = Outcome.AtLeastInTheAnswer
			}
		}

		return outcomesPerIndex.keys.sorted()
			.map { outcomesPerIndex[it]!! }
	}

	@Test
	fun testCoroutine() = runBlocking {
		val time = measureTimeMillis {
			val one = async { doSomethingUsefulOne() }
			val two = async { doSomethingUsefulTwo() }
			println("The answer is ${one.await() + two.await()}")
		}
		println("Completed in $time ms")
	}

	suspend fun doSomethingUsefulOne(): Int {
		delay(1000L) // pretend we are doing something useful here
		return 13
	}

	suspend fun doSomethingUsefulTwo(): Int {
		delay(1000L) // pretend we are doing something useful here, too
		return 29
	}
}

enum class Result {
	Correct,
	LucklyCorrect,
	Wrong
}
