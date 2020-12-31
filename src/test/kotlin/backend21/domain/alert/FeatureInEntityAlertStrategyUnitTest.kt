package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInEntityAlertStrategy
import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInEntityAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "analysis.entities.entity"
    override val varField = "feature"
    override val alias = "feature_entity"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInEntityAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}