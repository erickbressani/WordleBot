package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Word
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class WordChooserByScoreTest {
    private val chooser = WordChooserByScore()

    @Test
    fun `should choose word with highest score`() {
        val words = words(
            "aapas",
            "aargh",
            "aarti",
            "abaca",
            "abaci",
            "abode",
            "glare",
            "yechy",
            "yedes"
        )

        val (result) = chooser.choseBasedOn(words, words)

        result shouldBe "abode"
    }

    @Test
    fun `should choose word with most repeatable pattern when a score tie is found`() {
        val words = words(
            "acode",
            "acole",
            "trode",
            "grola"
        )

        val (result) = chooser.choseBasedOn(words, words)

        result shouldBe "acode"
    }

    @Test
    fun `should consider only matches when scoring chars`() {
        val words = words(
            "aapas",
            "aargh",
            "aarti",
            "abaca",
            "abaci",
            "abode",
            "glare",
            "yechy",
            "yedes"
        )

        val matches = words(
            "aapas",
            "aargh",
            "aarti",
            "abaca",
            "abaci",
        )

        val (result) = chooser.choseBasedOn(words, matches)

        result shouldBe "abaci"
    }

    private fun words(vararg words: String): List<Word> =
        words.map(::Word)
}
