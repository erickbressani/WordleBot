package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import com.wordlebot.wordlebot.runners.AutoPlayRunner
import com.wordlebot.wordlebot.runners.InteractiveRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
	runApplication<WordleBotApplication>(*args)
	printIntro()
	run(WordsFinder.get())
}

private fun printIntro() {
	println("Would like to run which mode?")
	println()
	println("(1) Interactive")
	println("(2) Auto")
}

private fun run(possibleWords: List<Word>) {
	readln().let { option ->
		when(option) {
			"1" -> runWithInteractiveRunner(possibleWords)
			"2" -> runWithAutoPlayRunner(possibleWords)
			else -> run(possibleWords)
		}
	}
}

private fun runWithInteractiveRunner(possibleWords: List<Word>) =
	InteractiveRunner(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser()))
		.run()

private fun runWithAutoPlayRunner(possibleWords: List<Word>) =
	AutoPlayRunner(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser()))
		.run(askForCorrectAnswer())

private fun askForCorrectAnswer(): Word {
	println("Write expected answer:")
	return readln().let(::Word).also { println("-------") }
}
