package backend21.domain.alert

import backend21.domain.alert.strategy.FeatureInConceptAlertStrategy
import backend21.domain.alert.strategy.UserWriteMessageAlertStrategy

import org.junit.Before
import org.mockito.MockitoAnnotations

class UserWriteMessageAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "user.screenName"
    override val varField = "screenname"
    override val alias = "user_writes"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = UserWriteMessageAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}