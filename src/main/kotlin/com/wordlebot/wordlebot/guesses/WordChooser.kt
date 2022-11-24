package com.wordlebot.wordlebot.guesses

import com.wordlebot.wordlebot.models.Word

fun interface WordChooser {
    fun choseBasedOn(possibleWords: List<Word>): Word

    companion object {
        val default = WordChooser { possibleWords ->
            with(possibleWords) {
                getScorePerChar()
                    .run { getScorePerWord { char -> this[char]!! } }
                    .getWordWithHighestScore()
            }
        }

        private fun List<Word>.getScorePerChar(): Map<Char, CharScore> =
            toCharDetailsMap().toCharScoreMap()

        private fun List<Word>.getScorePerWord(getCharScoreBy: (Char) -> (CharScore)): Map<Word, Int> =
            associateWith { word -> getScoreOf(word, getCharScoreBy) }

        private fun Map<Word, Int>.getWordWithHighestScore(): Word =
            maxByOrNull { it.value }!!.key

        private fun getScoreOf(word: Word, getCharScoreBy: (Char) -> (CharScore)): Int =
            word.getScoreForEachRecurrentChar(getCharScoreBy) + word.getScoreForEachCharInSamePositions(getCharScoreBy) + word.vowels.count()

        private fun Word.getScoreForEachRecurrentChar(getCharScoreBy: (Char) -> (CharScore)): Int =
            distinctChars.sumOf { getCharScoreBy(it).foundCount } * 2

        private fun Word.getScoreForEachCharInSamePositions(getCharScoreBy: (Char) -> (CharScore)): Int =
            chars.mapIndexed { index, char -> getCharScoreBy(char).scoreIn(index) }.sum()

        private fun List<Word>.toCharDetailsMap(): Map<Char, List<CharDetail>> =
            map { (word) -> word.mapIndexed { index, char -> CharDetail(char, CharPosition.values()[index], word) } }
                .flatten()
                .groupBy { it.value }

        private fun Map<Char, List<CharDetail>>.toCharScoreMap(): Map<Char, CharScore> =
            map { charDetail ->
                charDetail.key to CharScore(
                    charDetail.value.distinctBy { it.fromWord }.count(),
                    charDetail.countOnPosition(CharPosition.First),
                    charDetail.countOnPosition(CharPosition.Second),
                    charDetail.countOnPosition(CharPosition.Third),
                    charDetail.countOnPosition(CharPosition.Fourth),
                    charDetail.countOnPosition(CharPosition.Fifth)
                )
            }.toMap()

        private fun Map.Entry<Char, List<CharDetail>>.countOnPosition(charPosition: CharPosition): Int =
            value.count { it.isOnPosition(charPosition) }
    }
}

data class CharScore(
    val foundCount: Int,
    val inPosition1: Int,
    val inPosition2: Int,
    val inPosition3: Int,
    val inPosition4: Int,
    val inPosition5: Int
) {
    fun scoreIn(index: Int): Int =
        when (index) {
            0 -> inPosition1
            1 -> inPosition2
            2 -> inPosition3
            3 -> inPosition4
            4 -> inPosition5
            else -> throw IllegalArgumentException()
        }
}

data class CharDetail(val value: Char, val position: CharPosition, val fromWord: String) {
    fun isOnPosition(charPosition: CharPosition): Boolean =
        position == charPosition
}

enum class CharPosition {
    First,
    Second,
    Third,
    Fourth,
    Fifth
}
