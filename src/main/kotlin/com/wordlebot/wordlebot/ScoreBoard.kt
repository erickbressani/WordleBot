package com.wordlebot.wordlebot

class ScoreBoard(words: List<String>) {
    private val scorePerWord: Map<String, Int>
    private val scorePerChar: Map<Char, CharScore>

    init {
        scorePerChar = words
            .toCharDetailsMap()
            .toCharScoreMap()

        scorePerWord = mutableMapOf<String, Int>().apply {
            words.forEach { word -> this[word] = getScoreOf(word) }
        }
    }

    fun getWordWithHighestScore() =
        scorePerWord
            .toList()
            .maxByOrNull { it.second }!!.first

    private fun getScoreOf(word: String): Int {
        var score = word.map { it }.distinct().sumOf { getCharScoreBy(it).foundCount } * 2
        score += word.mapIndexed { index, char -> getCharScoreBy(char).scoreIn(index) }.sum()
        return score
    }

    private fun getCharScoreBy(char: Char) =
        scorePerChar[char]!!

    companion object {
        private fun List<String>.toCharDetailsMap(): Map<Char, List<CharDetail>> =
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
