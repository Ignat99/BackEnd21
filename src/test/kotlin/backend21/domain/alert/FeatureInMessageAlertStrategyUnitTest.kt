package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInMessageAlertStrategy
import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInMessageAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "unifiedText"
    override val varField = "feature"
    override val alias = "feature_message"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInMessageAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}