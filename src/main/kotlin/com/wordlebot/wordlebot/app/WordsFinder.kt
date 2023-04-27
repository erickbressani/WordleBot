package com.wordlebot.wordlebot.app

import com.wordlebot.wordlebot.models.Word
import java.io.File

class WordsFinder {
    companion object {
        fun get(): List<Word> =
            this::class.java.classLoader.getResource("wordle-words")!!
                .readText()
                .split("\n")
                .filter { it.isNotEmpty() }
                .map(::Word)
    }
}
