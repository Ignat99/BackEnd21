package backend21.domain

import backend21.domain.socialnetworks.Tweet
import backend21.domain.socialnetworks.TweetResults
import org.junit.Assert
import org.junit.Test

class TweetResultsUnitTest {
    @Test
    fun test_toDTO_calledWithTweets_correctInstantiation() {
        val tweets = arrayListOf(Tweet("1", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 0L, listOf(), "es", 0, 3),
                Tweet("2", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 0L, listOf(), "es", 0, 3),
                Tweet("3", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 0L, listOf(), "es", 0, 3),
                Tweet("4", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 0L, listOf(), "es", 0, 3),

                Tweet("5", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("6", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("7", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("8", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("9", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("10", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("11", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("12", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("13", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("14", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 3L, listOf(), "es", 0, 3),
                Tweet("15", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 5L, listOf(), "es", 0, 3),
                Tweet("16", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 5L, listOf(), "es", 0, 3),
                Tweet("17", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 5L, listOf(), "es", 0, 3),
                Tweet("18", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 5L, listOf(), "es", 0, 3),
                Tweet("19", "2018-09-09T12:12:12", "pepe", "juan", "juan", "", "3", 5L, listOf(), "es", 0, 3))

        val tweetResults = TweetResults(19, tweets)
        val actual = tweetResults.toDTO()
        val expected = 4
        Assert.assertEquals(expected, actual.threatLevel)
    }


    @Test
    fun test_toDTO_calledWithNoTweets_correctInstantiation() {
        val tweets = TweetResults(0, arrayListOf())
        val actual = tweets.toDTO()
        val expected = 0
        Assert.assertEquals(expected, actual.threatLevel)
    }
}