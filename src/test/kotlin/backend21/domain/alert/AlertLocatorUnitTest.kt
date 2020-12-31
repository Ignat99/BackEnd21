package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertConstants
import backend21.domain.alert.strategy.AlertStrategy
import backend21.domain.alert.strategy.UserMentionAlertStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.mother.ProjectMother
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AlertLocatorUnitTest {
    @Mock
    internal lateinit var elasticTweetsService: ElasticTweetsService

    @Mock
    internal lateinit var projectAlertExecutionRepository: ProjectAlertExecutionRepository

    internal lateinit var sut: AlertLocator

    private lateinit var project: Project

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sut = AlertLocator(elasticTweetsService, projectAlertExecutionRepository)
        project = ProjectMother().testInstance("1", "project1", "2")
    }


    @Test
    fun test_construct_called_correctInstanceCreated() {
        val caseTests = arrayListOf(Pair(AlertConstants.XY_ALIAS, hashMapOf("screenname1" to "a", "screenname2" to "b")),
                Pair(AlertConstants.WRITES_ALIAS, hashMapOf("screenname" to "a")),
                Pair(AlertConstants.MENTION_ALIAS, hashMapOf("username" to "a")),
                Pair(AlertConstants.THRESHOLD_ALIAS, hashMapOf("threat_level" to "3")),
                Pair(AlertConstants.THREAT_ALIAS, hashMapOf()),
                Pair(AlertConstants.SYSTEM_ALIAS, hashMapOf("num" to "400")),
                Pair(AlertConstants.SUSPICIOUS_ALIAS, hashMapOf("threat_level_min" to "3")),
                Pair(AlertConstants.MINUTE_ALIAS, hashMapOf("num" to "433")),
                Pair(AlertConstants.HOUR_ALIAS, hashMapOf("num" to "234")),
                Pair(AlertConstants.PERCENT_ALIAS, hashMapOf("ratio" to "0.1")),
                Pair(AlertConstants.TOPIC_ALIAS, hashMapOf("feature" to "man")),
                Pair(AlertConstants.MESSAGE_ALIAS, hashMapOf("feature" to "book")),
                Pair(AlertConstants.KEYIDEA_ALIAS, hashMapOf("feature" to "feature")),
                Pair(AlertConstants.HASHTAG_ALIAS, hashMapOf("feature" to "king")),
                Pair(AlertConstants.ENTITY_ALIAS, hashMapOf("feature" to "book")),
                Pair(AlertConstants.CONCEPT_ALIAS, hashMapOf("feature" to "flower")))
        caseTests.forEach {
            val (alias, extravars) = it
            val alert = sut.getAlertStrategy(alias, extravars, project)
            Assert.assertEquals(alias, alert.getName())
        }
    }


}