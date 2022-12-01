package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Word
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class WordChooserByScoreTest {
    @Test
    fun `should choose word with highest score`() {
        val chooser = WordChooserByScore()

        val word = chooser.choseBasedOn(WORDS, WORDS)

        word.value shouldBe "abaci"
    }

    companion object {
        val WORDS = listOf(
            "aapas",
            "aargh",
            "aarti",
            "abaca",
            "abaci",
            "abaht",
            "abode",
        ).map(::Word)
    }
}
