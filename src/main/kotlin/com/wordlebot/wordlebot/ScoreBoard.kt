package com.wordlebot.wordlebot

class ScoreBoard private constructor(private val scorePerChar: Map<Char, CharScore>) {
    fun getScoreOf(word: String): Int {
        var score = word.map { it }.distinct().sumOf { getCharScoreBy(it).foundCount } * 2
        score += word.mapIndexed { index, char -> getCharScoreBy(char).scoreIn(index) }.sum()
        return score
    }

    private fun getCharScoreBy(char: Char) =
        scorePerChar[char]!!

    companion object {
        fun create(words: Sequence<String>): ScoreBoard =
            words.toScoreBoard()

        private fun Sequence<String>.toScoreBoard(): ScoreBoard =
            toCharDetailsMap()
                .toCharScoreMap()
                .let(::ScoreBoard)

        private fun Sequence<String>.toCharDetailsMap(): Map<Char, List<CharDetail>> =
            map { word -> word.mapIndexed { index, char -> CharDetail(char, CharPosition.values()[index], word) } }
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
            value.count { it.isPosition(charPosition) }
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
    fun isPosition(charPosition: CharPosition): Boolean =
        position == charPosition
}

enum class CharPosition {
    First,
    Second,
    Third,
    Fourth,
    Fifth
}
