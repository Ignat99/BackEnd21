package backend21.domain.alert


import backend21.domain.alert.strategy.FeatureInKeyIdeaAlertStrategy

import org.junit.Before
import org.mockito.MockitoAnnotations

class FeatureInKeyIdeaAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "analysis.keyIdeas.keyIdea"
    override val varField = "feature"
    override val alias = "feature_keyidea"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = FeatureInKeyIdeaAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}