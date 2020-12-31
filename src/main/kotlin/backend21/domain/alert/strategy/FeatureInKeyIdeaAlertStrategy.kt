package backend21.domain.alert.strategy

import backend21.domain.alert.interfaces.ProjectAlertExecutionRepository
import backend21.domain.project.Project
import backend21.domain.socialnetworks.ElasticTweetsService
import org.springframework.beans.factory.annotation.Autowired

class FeatureInKeyIdeaAlertStrategy @Autowired constructor(
    override  val elasticTweetsService: ElasticTweetsService,
    override val projectAlertExecutionRepository: ProjectAlertExecutionRepository,
    override val project: Project, override val feature: String, 
    override val date:String?=null): FeatureInFieldStrategyBase(elasticTweetsService,
    projectAlertExecutionRepository, project, feature, "analysis.keyIdeas.keyIdea",
    AlertConstants.KEYIDEA_ALIAS, "feature", date)
{
    override fun getName(): String = AlertConstants.KEYIDEA_ALIAS
}
