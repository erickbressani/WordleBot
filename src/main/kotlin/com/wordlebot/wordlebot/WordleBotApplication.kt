package com.wordlebot.wordlebot

import java.io.File
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
	runApplication<WordleBotApplication>(*args)

	getPossibleAnswers().let { possibleAnswers ->
		WordleBot(possibleAnswers, OutcomeParser(), Guesser(possibleAnswers)).run()
	}
}

fun getPossibleAnswers(): List<String> =
	File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
		.bufferedReader()
		.readText()
		.split("\n")
		.filter { it.isNotEmpty() }
