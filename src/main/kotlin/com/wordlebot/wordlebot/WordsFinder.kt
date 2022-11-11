package com.wordlebot.wordlebot

import com.wordlebot.wordlebot.models.Word
import java.io.File

class WordsFinder {
    companion object {
        fun get(): List<Word> =
            File("/Users/erickbressani/Documents/git/erick/WordleBot/src/main/kotlin/com/wordlebot/WordleBot/wordle-words")
                .bufferedReader()
                .readText()
                .split("\n")
                .filter { it.isNotEmpty() }
                .map { Word(it) }
    }
}
