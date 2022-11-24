package com.wordlebot.wordlebot.outcomes

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import com.wordlebot.wordlebot.models.Character
import com.wordlebot.wordlebot.models.Word

internal class OutcomeParserTest {
    private val outcomeParser = OutcomeParser()

    @Test
    fun `should add NotInTheAnswer`() {
        outcomeParser.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.NotInTheAnswer } shouldBe true
            wordWithUniqueLetters.forEachCharIndexed { index, char -> characters[index].value shouldBe char }
        }
    }

    @Test
    fun `should add AtLeastInTheAnswer`() {
        outcomeParser.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.InTheAnswer }  shouldBe true
            wordWithUniqueLetters.forEachCharIndexed { index, char ->
                characters[index].value shouldBe char
                (characters[index] as Character.InTheAnswer).getPositionsMarkedAsNotFound() shouldBe mutableListOf(index)
            }
        }
    }

    @Test
    fun `should add InTheCorrectPosition`() {
        outcomeParser.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.InTheAnswer }  shouldBe true
            wordWithUniqueLetters.forEachCharIndexed { index, char ->
                characters[index].value shouldBe char
                (characters[index] as Character.InTheAnswer).getPositionsMarkedAsNotFound() shouldBe
                        listOf(0, 1, 2, 3, 4).filter { it != index }.toMutableList()
            }
        }
    }

    @Test
    fun `should add only one InTheCorrectPosition per character`() {
        outcomeParser.add(
            wordWithRepeatedLetters,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.shouldHaveSize(2)
            characters.all { it is Character.InTheAnswer }  shouldBe true

            (characters[0] as Character.InTheAnswer) should {
                it.value shouldBe 'e'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(2, 3, 4)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(0, 1)
            }

            (characters[1] as Character.InTheAnswer) should {
                it.value shouldBe 'a'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(0, 1)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(2, 3, 4)
            }
        }
    }

    @Test
    fun `should not add NotInTheAnswer when there is another with same value`() {
        outcomeParser.add(
            wordWithRepeatedAndUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.filter { it.value == 'u' } should {
                it.shouldHaveSize(1)
                (it[0] as Character.InTheAnswer).getPositionsMarkedAsFound() shouldBe listOf(1)
                (it[0] as Character.InTheAnswer).getPositionsMarkedAsNotFound() shouldBe listOf(0, 2, 3, 4)
            }
        }
    }

    @Test
    fun `should remove NotInTheAnswer when there is somewhere in the word the value`() {
        outcomeParser.add(
            wordWithRepeatedAndUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.filter { it.value == 'u' } should {
                it.shouldHaveSize(1)
                (it[0] as Character.InTheAnswer).getPositionsMarkedAsFound() shouldBe listOf(3)
                (it[0] as Character.InTheAnswer).getPositionsMarkedAsNotFound() shouldBe listOf(0, 1, 2, 4)
            }
        }
    }

    @Test
    fun `should add only InTheCorrectPosition per character`() {
        outcomeParser.add(
            wordWithRepeatedLetters,
            listOf(
                Outcome.AtLeastInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.AtLeastInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        outcomeParser.getAllParsedCharacters().let { characters ->
            characters.shouldHaveSize(2)
            characters.all { it is Character.InTheAnswer }  shouldBe true

            (characters[0] as Character.InTheAnswer) should {
                it.value shouldBe 'e'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(0, 3, 4)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(1)
            }

            (characters[1] as Character.InTheAnswer) should {
                it.value shouldBe 'a'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(2, 1)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(3, 4)
            }
        }
    }

    @Test
    fun `should add 10 characters`() {
        // answer = axhbj

        outcomeParser.add(
            abcde,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.AtLeastInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        outcomeParser.add(
            fghij,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition
            )
        )

        outcomeParser.getAllParsedCharacters().sortedBy { it.value }.let { characters ->
            characters.shouldHaveSize(10)

            (characters[2] as Character.NotInTheAnswer).value shouldBe 'c'
            (characters[3] as Character.NotInTheAnswer).value shouldBe 'd'
            (characters[4] as Character.NotInTheAnswer).value shouldBe 'e'
            (characters[5] as Character.NotInTheAnswer).value shouldBe 'f'
            (characters[6] as Character.NotInTheAnswer).value shouldBe 'g'
            (characters[8] as Character.NotInTheAnswer).value shouldBe 'i'

            (characters[0] as Character.InTheAnswer) should {
                it.value shouldBe 'a'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(2, 4)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(0)
            }

            (characters[1] as Character.InTheAnswer) should {
                it.value shouldBe 'b'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(1, 0, 2, 4)
                it.getPositionsMarkedAsFound() shouldBe mutableListOf(3)
            }

            (characters[7] as Character.InTheAnswer) should {
                it.value shouldBe 'h'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(0, 4)
                it.getPositionsMarkedAsFound() shouldBe listOf(2)
            }

            (characters[9] as Character.InTheAnswer) should {
                it.value shouldBe 'j'
                it.getPositionsMarkedAsNotFound() shouldBe mutableListOf(0, 2)
                it.getPositionsMarkedAsFound() shouldBe listOf(4)
            }
        }
    }

    companion object {
        val wordWithUniqueLetters = Word("arise")
        val wordWithRepeatedLetters = Word("eeaaa")
        val wordWithRepeatedAndUniqueLetters = Word("queue")
        val abcde = Word("abcde")
        val fghij = Word("fghij")
    }
}
