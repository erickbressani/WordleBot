package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.models.Word
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
	runApplication<WordleBotApplication>(*args)

	WordsFinder.get().let { possibleWords ->
		println("Would like to run which mode?")
		println()
		println("(1) Interactive")
		println("(2) Auto")

		while (true) {
			readln().let { option ->
				when(option) {
					"1" -> {
						WordleBot(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser())).run()
						return
					}
					"2" -> {
						println("Write expected answer:")
						val correctAnswer = readln().let(::Word)
						println("-------")
						AutoPlayRunner(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser())).run(correctAnswer)
						return
					}
					else -> {}
				}
			}
		}
	}
}
