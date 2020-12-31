package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInConceptAlertStrategy
import backend21.domain.alert.strategy.FeatureInTopicAlertStrategy

import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInTopicAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "analysis.topics.topic"
    override val varField = "feature"
    override val alias = "feature_topic"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInTopicAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}