package backend21.domain

import backend21.domain.stopwords.EnStopWords
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EsStopWordsUnitTest() {
    private lateinit var sut: EnStopWords
    @Before
    fun setUp() {
        sut = EnStopWords()
    }


    @Test
    fun test_isStopWord_calledWithStopWord_returnTrue() {
        Assert.assertEquals(true, sut.isStopWord("above"))
    }


    @Test
    fun test_isStopWord_calledWithNoStopWord_returnFalse() {
        Assert.assertEquals(false, sut.isStopWord("car"))
    }


}