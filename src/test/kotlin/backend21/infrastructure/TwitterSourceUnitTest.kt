package backend21.infrastructure

import backend21.infrastructure.sources.NetworkSourceException
import backend21.infrastructure.sources.TwitterSource
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.domain.project.interfaces.ProjectRepository
import backend21.wrappers.CurlWrapper
import backend21.wrappers.EnvironmentVarWrapper
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations


class TwitterSourceUnitTest {
    internal lateinit var sut: TwitterSource

    @Mock
    internal lateinit var curlWrapper: CurlWrapper


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = TwitterSource(curlWrapper, EnvironmentVarWrapper())
    }


    @Test(expected=NetworkSourceException::class)
    fun test_start_emptyKeywordsGiven_throw() {
        sut.start(arrayListOf(), arrayListOf())
    }


    @Test
    fun test_start_keywordsGiven_correctCallToInnerCurlWrapper() {
        val keywords = arrayListOf("bin laden", "mula omar")
        sut.start(arrayListOf("facebook", Project.TWITTER), keywords)
        Mockito.`verify`(curlWrapper, times(1)).doPostWithJson("http://"+System.getenv("TWITTER2RABBIT_HOST")+":"+System.getenv("TWITTER2RABBIT_PORT")+"/keywords", "{\"keywords\": [\"bin laden\",\"mula omar\"], \"users\":[], \"locations\":[]}")
    }

    @Test(expected = NetworkSourceException::class)
    fun test_start_calledWithInvalidSource_throw() {
        sut.start(arrayListOf("facebook", "instagram"), arrayListOf("mula omar"))
    }

}