package backend21.domain

import backend21.domain.socialnetworks.TotalThreadScores
import org.junit.Assert
import org.junit.Test

class TotalThreadScoresUnitTest {
    @Test
    fun test_getThreatLevel_calledWithNoDocuments_return0() {
        val sut = TotalThreadScores(0, hashMapOf())
        val expected = 0
        exerciseGetThreadLevelAndVerify(sut, expected)
    }


    @Test
    fun test_getThreatLevel_calledWithDocuments_returnCorrectMean() {
        val sut = TotalThreadScores(10, hashMapOf(0L to 4L, 5L to 5L))
        val expected = 5
        exerciseGetThreadLevelAndVerify(sut, expected)
    }


    @Test
    fun test_getThreatLevel2_calledWithDocuments_returnCorrectMean() {
        val sut = TotalThreadScores(10, hashMapOf(0L to 4L, 3L to 10L, 5L to 5L))
        val expected = 4
        exerciseGetThreadLevelAndVerify(sut, expected)
    }

    @Test
    fun test_getThreatLevel3_calledWithDocuments_returnCorrectMean() {
        val sut = TotalThreadScores(10, hashMapOf(0L to 4L, 3L to 35L, 4L to 2L, 5L to 4L))
        val expected = 3
        exerciseGetThreadLevelAndVerify(sut, expected)
    }

    private fun exerciseGetThreadLevelAndVerify(sut: TotalThreadScores, expected: Int) {
        val actual = sut.getThreatLevel()
        Assert.assertEquals(expected, actual)
    }

}