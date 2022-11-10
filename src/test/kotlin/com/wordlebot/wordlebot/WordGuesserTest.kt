package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.guesses.WordGuesser
import java.io.File
import org.junit.jupiter.api.Test

internal class WordGuesserTest {
    fun getPossibleWords(): List<String> =
        File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
            .bufferedReader()
            .readText()
            .split("\n")
            .filter { it.isNotEmpty() }

    private val wordGuesser = WordGuesser(getPossibleWords())

    @Test
    fun `test`() {
//        val characters = listOf(
//            Character.InTheCorrectPosition('s', mutableListOf(0), mutableListOf(1)),
//            Character.InTheCorrectPosition('o', mutableListOf(1), mutableListOf(0)),
//            Character.NotInTheAnswer('a'),
//            Character.NotInTheAnswer('r'),
//            Character.NotInTheAnswer('e'),
//            Character.NotInTheAnswer('i'),
//            Character.NotInTheAnswer('l'),
//        )
//
//        guesser.guess(characters, TryNumber.Third)
    }
}
