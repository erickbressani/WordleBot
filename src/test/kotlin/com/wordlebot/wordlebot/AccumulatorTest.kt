package com.wordlebot.wordlebot

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AccumulatorTest {
    private val accumulator = Accumulator()

    @Test
    fun `should add NotInTheAnswer`() {
        accumulator.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        accumulator.getAll().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.NotInTheAnswer } shouldBe true
            wordWithUniqueLetters.forEachIndexed { index, char -> characters[index].value shouldBe char }
        }
    }

    @Test
    fun `should add AtLeastInTheAnswer`() {
        accumulator.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer,
                Outcome.AtLeastInTheAnswer
            )
        )

        accumulator.getAll().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.AtLeastInTheAnswer }  shouldBe true
            wordWithUniqueLetters.forEachIndexed { index, char ->
                characters[index].value shouldBe char
                (characters[index] as Character.AtLeastInTheAnswer).notInThePosition shouldBe mutableListOf(index)
            }
        }
    }

    @Test
    fun `should add InTheCorrectPosition`() {
        accumulator.add(
            wordWithUniqueLetters,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        accumulator.getAll().let { characters ->
            characters.shouldHaveSize(5)
            characters.all { it is Character.InTheCorrectPosition }  shouldBe true
            wordWithUniqueLetters.forEachIndexed { index, char ->
                characters[index].value shouldBe char
                (characters[index] as Character.InTheCorrectPosition).notInThePosition shouldBe
                        listOf(0, 1, 2, 3, 4).filter { it != index }.toMutableList()
            }
        }
    }

    @Test
    fun `should add only one InTheCorrectPosition per character`() {
        accumulator.add(
            wordWithRepeatedLetters,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        accumulator.getAll().let { characters ->
            characters.shouldHaveSize(2)
            characters.all { it is Character.InTheCorrectPosition }  shouldBe true

            (characters[0] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'e'
                it.notInThePosition shouldBe mutableListOf(2, 3, 4)
                it.positions shouldBe mutableListOf(0, 1)
            }

            (characters[1] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'a'
                it.notInThePosition shouldBe mutableListOf(0, 1)
                it.positions shouldBe mutableListOf(2, 3, 4)
            }
        }
    }

    @Test
    fun `should not add NotInTheAnswer when there is another with same value`() {
        accumulator.add(
            wordWithRepeatedAndUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        accumulator.getAll().let { characters ->
            characters.filter { it.value == 'u' } should {
                it.shouldHaveSize(1)
                (it[0] as Character.InTheCorrectPosition).positions shouldBe listOf(1)
                (it[0] as Character.InTheCorrectPosition).notInThePosition shouldBe listOf(0, 2, 3, 4)
            }
        }
    }

    @Test
    fun `should remove NotInTheAnswer when there is somewhere in the word the value`() {
        accumulator.add(
            wordWithRepeatedAndUniqueLetters,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer
            )
        )

        accumulator.getAll().let { characters ->
            characters.filter { it.value == 'u' } should {
                it.shouldHaveSize(1)
                (it[0] as Character.InTheCorrectPosition).positions shouldBe listOf(3)
                (it[0] as Character.InTheCorrectPosition).notInThePosition shouldBe listOf(0, 1, 2, 4)
            }
        }
    }

    @Test
    fun `should add only InTheCorrectPosition per character`() {
        accumulator.add(
            wordWithRepeatedLetters,
            listOf(
                Outcome.AtLeastInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.AtLeastInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.InTheCorrectPosition
            )
        )

        accumulator.getAll().let { characters ->
            characters.shouldHaveSize(2)
            characters.all { it is Character.InTheCorrectPosition }  shouldBe true

            (characters[0] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'e'
                it.notInThePosition shouldBe mutableListOf(0, 3, 4)
                it.positions shouldBe mutableListOf(1)
            }

            (characters[1] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'a'
                it.notInThePosition shouldBe mutableListOf(2, 1)
                it.positions shouldBe mutableListOf(3, 4)
            }
        }
    }

    @Test
    fun `should add 10 characters`() {
        // answer = axhbj

        accumulator.add(
            abcde,
            listOf(
                Outcome.InTheCorrectPosition,
                Outcome.AtLeastInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer
            )
        )

        accumulator.add(
            fghij,
            listOf(
                Outcome.NotInTheAnswer,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition,
                Outcome.NotInTheAnswer,
                Outcome.InTheCorrectPosition
            )
        )

        accumulator.getAll().sortedBy { it.value }.let { characters ->
            characters.shouldHaveSize(10)

            (characters[2] as Character.NotInTheAnswer).value shouldBe 'c'
            (characters[3] as Character.NotInTheAnswer).value shouldBe 'd'
            (characters[4] as Character.NotInTheAnswer).value shouldBe 'e'
            (characters[5] as Character.NotInTheAnswer).value shouldBe 'f'
            (characters[6] as Character.NotInTheAnswer).value shouldBe 'g'
            (characters[8] as Character.NotInTheAnswer).value shouldBe 'i'

            (characters[0] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'a'
                it.notInThePosition shouldBe mutableListOf(2, 4)
                it.positions shouldBe mutableListOf(0)
            }

            (characters[1] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'b'
                it.notInThePosition shouldBe mutableListOf(1, 0, 2, 4)
                it.positions shouldBe mutableListOf(3)
            }

            (characters[7] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'h'
                it.notInThePosition shouldBe mutableListOf(0, 4)
                it.positions shouldBe listOf(2)
            }

            (characters[9] as Character.InTheCorrectPosition) should {
                it.value shouldBe 'j'
                it.notInThePosition shouldBe mutableListOf(0, 2)
                it.positions shouldBe listOf(4)
            }
        }
    }

    companion object {
        const val wordWithUniqueLetters = "arise"
        const val wordWithRepeatedLetters = "eeaaa"
        const val wordWithRepeatedAndUniqueLetters = "queue"
        const val abcde = "abcde"
        const val fghij = "fghij"
    }
}
