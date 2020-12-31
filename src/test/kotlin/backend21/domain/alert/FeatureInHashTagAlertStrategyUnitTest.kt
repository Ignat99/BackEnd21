package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInConceptAlertStrategy
import backend21.domain.alert.strategy.FeatureInHashTagAlertStrategy

import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInHashTagAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "analysis.hashtags.text"
    override val varField = "feature"
    override val alias = "feature_hashtag"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInHashTagAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}