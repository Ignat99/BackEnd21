package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInConceptAlertStrategy

import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInConceptAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "analysis.concepts.concept"
    override val varField = "feature"
    override val alias = "feature_concept"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInConceptAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}