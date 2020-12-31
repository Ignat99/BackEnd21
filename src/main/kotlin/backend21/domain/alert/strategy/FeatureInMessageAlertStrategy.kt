package backend21.domain.alert.strategy

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import org.springframework.beans.factory.annotation.Autowired

class FeatureInMessageAlertStrategy @Autowired constructor(
    override  val elasticTweetsService: ElasticTweetsService,
    override val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    override val project: Project, override val feature: String,
    override val date:String?=null): FeatureInFieldStrategyBase(elasticTweetsService,
    projectAlertExecutionRepository, project, feature, "unifiedText",
    AlertConstants.MESSAGE_ALIAS, "feature", date)
{
    override fun getName(): String = AlertConstants.MESSAGE_ALIAS
}
