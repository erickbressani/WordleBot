package com.wordlebot.wordlebot

import java.io.File
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WordleBotApplication

fun main(args: Array<String>) {
	runApplication<WordleBotApplication>(*args)

	getPossibleWords().let { possibleWords ->
		WordleBot(possibleWords, Accumulator(), Guesser(possibleWords)).run()
	}
}

fun getPossibleWords(): Sequence<String> =
	File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
		.bufferedReader()
		.readText()
		.split("\n")
		.asSequence()
		.filter { it.isNotEmpty() }
