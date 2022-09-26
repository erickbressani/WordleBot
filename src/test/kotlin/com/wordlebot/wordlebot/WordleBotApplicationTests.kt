package com.wordlebot.wordlebot

import java.io.File
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.system.measureTimeMillis

@SpringBootTest
class WordleBotApplicationTests {
	fun getPossibleWords(): Sequence<String> =
		File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
			.bufferedReader()
			.readText()
			.split("\n")
			.asSequence()
			.filter { it.isNotEmpty() }
	@Test
	fun test() {
		var correctCount = 0
		var wrongCount = 0
		var pureLuckCount = 0
		val wrongAnswers = mutableListOf<String>()
		val possibleWords = getPossibleWords()

		possibleWords.mapIndexed { index, correctAnswer ->
			runAsync(index, correctAnswer, possibleWords)
		}.let {
			runBlocking(Dispatchers.Unconfined) {
				it.forEach {
					it.await().let { result ->
						when (result) {
							Result.Correct -> correctCount++
							Result.LucklyCorrect -> {
								correctCount++
								pureLuckCount++
							}
							Result.Wrong -> wrongCount++
						}
					}
				}
			}
		}

		println("pure luck: $pureLuckCount")
		println("wrong: $wrongCount")
		println("correct: $correctCount")
		println("pure correct: ${correctCount - pureLuckCount}")
		println(wrongAnswers)
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun runAsync(index: Int, correctAnswer: String, possibleWords: Sequence<String>) = GlobalScope.async {
		val accumulator = Accumulator()
		val guesser = Guesser(mutableListOf<String>().apply { addAll(possibleWords) }.asSequence())

		for (tryNumber in 0..5) {
			val word = guesser.guess(accumulator.getAll())

			if (word == correctAnswer) {
				if (tryNumber == 5 && guesser.getPossibleAnswersCount() > 1) {
//					println("$index CORRECT: $correctAnswer | ${guesser.getPossibleAnswersCount()}")
					return@async Result.LucklyCorrect
				} else {
//					println("$index CORRECT: $correctAnswer | $tryNumber")
					return@async Result.Correct
				}
			} else if (tryNumber == 5) {
//				println("$index WRONG: $correctAnswer | ${guesser.getPossibleAnswersCount()}")
				return@async Result.Wrong
			}

			accumulator.add(word, word.getOutcomesBasedOn(correctAnswer))
		}

		return@async Result.Wrong
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
