package backend21.domain.alert

import backend21.domain.alert.strategy.UserMentionAlertStrategy
import org.junit.Before
import org.mockito.MockitoAnnotations

class UserMentionAlertStrategyUnitTest: FeatureInFieldUnitTestBase() {

    override val feature = "megaman"
    override val field = "userMentionEntities.screenName"
    override val varField = "username"
    override val alias = "user_mention"

    @Before
    fun setUp() {
        super.start()
        MockitoAnnotations.initMocks(this)
        sut = UserMentionAlertStrategy(elasticTweetsService, projectAlertExecutionRepository, project, feature, date)
    }

}