package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.WordChooser
import com.wordlebot.wordlebot.guesses.WordGuesser
import com.wordlebot.wordlebot.guesses.WordMatcher
import com.wordlebot.wordlebot.outcomes.OutcomeParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
	runApplication<WordleBotApplication>(*args)

	WordsFinder.get().let { possibleWords ->
		WordleBot(possibleWords, OutcomeParser(), WordGuesser(WordMatcher(), WordChooser())).run()
	}
}
