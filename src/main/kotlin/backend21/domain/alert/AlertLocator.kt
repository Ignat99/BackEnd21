package backend21.domain.alert

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.alert.strategy.AlertConstants
import backend21.domain.alert.strategy.AlertStrategy
import backend21.domain.alert.strategy.FeatureInConceptAlertStrategy
import backend21.domain.alert.strategy.FeatureInEntityAlertStrategy
import backend21.domain.alert.strategy.FeatureInHashTagAlertStrategy
import backend21.domain.alert.strategy.FeatureInTopicAlertStrategy
import backend21.domain.alert.strategy.FeatureInMessageAlertStrategy
import backend21.domain.alert.strategy.SuspiciousMessageStrategy
import backend21.domain.alert.strategy.FeatureInKeyIdeaAlertStrategy
import backend21.domain.alert.strategy.UserWriteMessageAlertStrategy
import backend21.domain.alert.strategy.UserMentionAlertStrategy
import backend21.domain.alert.strategy.XMentionYAlertStrategy
import backend21.domain.alert.strategy.PostThroughputByMinuteAlertStrategy
import backend21.domain.alert.strategy.PostThroughputByHourAlertStrategy
import backend21.domain.alert.strategy.ThreatLevelThresholdAlertStrategy
import backend21.domain.alert.strategy.ThreatLevelChangeStrategy
import backend21.domain.alert.strategy.SystemAlertNumPostsStrategy
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.alert.strategy.PercentIncrementByDayStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.InvalidParameterException

@Component
class AlertLocator @Autowired constructor(private val elasticTweetsService: ElasticTweetsService,
    private val projectAlertExecutionRepository: ProjectAlertExecutionRepository)
{
    fun getAlertStrategy(alias: String, extravars: HashMap<String, String>,
        project: Project):AlertStrategy
    {
        when(alias) {
            AlertConstants.XY_ALIAS -> return XMentionYAlertStrategy(elasticTweetsService,
                projectAlertExecutionRepository, project,
                extravars["screenname1"]!!, extravars["screenname2"]!!)
            AlertConstants.WRITES_ALIAS -> return UserWriteMessageAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["screenname"]!!)
            AlertConstants.MENTION_ALIAS -> return UserMentionAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["username"]!!)
            AlertConstants.THRESHOLD_ALIAS -> return ThreatLevelThresholdAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository,
                extravars["threat_level"]!!.toLong(), project)
            AlertConstants.THREAT_ALIAS -> return ThreatLevelChangeStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project)
            AlertConstants.SYSTEM_ALIAS -> return SystemAlertNumPostsStrategy(
                elasticTweetsService, projectAlertExecutionRepository, extravars["num"]!!.toLong(),
                project)
            AlertConstants.SUSPICIOUS_ALIAS -> return SuspiciousMessageStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["threat_level_min"]!!.toInt())
            AlertConstants.MINUTE_ALIAS -> return PostThroughputByMinuteAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["num"]!!.toLong())
            AlertConstants.HOUR_ALIAS -> return PostThroughputByHourAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["num"]!!.toLong())
            AlertConstants.PERCENT_ALIAS -> return PercentIncrementByDayStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["ratio"]!!.toFloat())
            AlertConstants.TOPIC_ALIAS -> return FeatureInTopicAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
            AlertConstants.MESSAGE_ALIAS -> return FeatureInMessageAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
            AlertConstants.KEYIDEA_ALIAS -> return FeatureInKeyIdeaAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
            AlertConstants.HASHTAG_ALIAS -> return FeatureInHashTagAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
            AlertConstants.ENTITY_ALIAS -> return FeatureInEntityAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
            AlertConstants.CONCEPT_ALIAS -> return FeatureInConceptAlertStrategy(
                elasticTweetsService, projectAlertExecutionRepository, project,
                extravars["feature"] as String)
        }
        throw InvalidParameterException("Invalid alias: $alias")
    }
}
