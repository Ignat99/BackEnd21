package backend21.application.project

import backend21.application.AppServiceBase
import backend21.application.user.ProjectTimeFrameDTO
import backend21.infrastructure.sources.NetworkSource
import backend21.domain.BoundingBox
import backend21.domain.DomainException
import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import backend21.domain.project.ProjectTimeFrameService
import backend21.domain.project.ProjectTimeFrame
import backend21.domain.socialnetworks.ElasticTweetsService
import backend21.domain.socialnetworks.TopDataItemArray
import backend21.domain.socialnetworks.TotalThreadScores
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class ProjectAppService @Autowired constructor(
    private val projectService: ProjectService,
    private val networkSource: NetworkSource,
    private val elasticTweetsService: ElasticTweetsService,
    private val projectTimeFrameService: ProjectTimeFrameService) : AppServiceBase(projectService)
{
    fun createProject(project: ProjectDTO): ProjectDTO {
        val entityProject = Project.fromDTO(project)
        val createdProject = projectService.createProject(entityProject)
        this.createProjectTimeFrame(ProjectTimeFrameDTO(createdProject.id!!, createdProject.createdAt, Date(), 1))
        callToNodeWithKeywords(createdProject)
        return createdProject.toDTO()
    }

    fun getProjectsByOwner(owner: String, term: String? = null): List<ProjectDTO> {
        val projects = projectService.getProjectsByOwner(owner, term)
        return projects.map {
            val dto = it.toDTO()
            val totalThreadScores: TotalThreadScores = elasticTweetsService.getTotalThreadScoresByProject(it)
            dto.numPosts = totalThreadScores.total
            dto.threatLevel = totalThreadScores.getThreatLevel()
            dto
        }
    }


    fun getProjectOverview(
        projectId: String, owner: String,
        page: Int, interval: Int = 0,
        date: Date = Date()): ProjectOverviewDTO
{

        val project = checkOwner(projectId, owner)
        val (endDate, startDate) = getDateRangeFromTimeFrame(date, project, projectId)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val projectOverview = elasticTweetsService.getOverviewByProject(
            project, simpleDateFormat.format(startDate), simpleDateFormat.format(endDate),
            page, interval, endDate)
        val topRetweets = projectOverview.getTopRetweets()
        var tweetResultsDTO = TweetResultsDTO(0, arrayListOf(), 0)
        if (topRetweets != null) {
            val items = topRetweets.getItems()
            val tweetResults = elasticTweetsService.getTweetsByIds(items)
            items.forEach {
                tweetResults.setRetweets(it, topRetweets.getTotal(it) ?: 0L)
            }
            tweetResultsDTO = tweetResults.toDTO()
        }
        val topDataDTO = projectOverview.topData.toDTO()
        val histogramDTO = projectOverview.histogram.toDTO()
        return ProjectOverviewDTO(histogramDTO, topDataDTO, tweetResultsDTO)
    }

    fun getTopAnalysis(projectId: String, owner: String, page: Int, threatFilter: Int? = null, 
            limit: Int = 10, date: Date = Date()): TopDataDTO 
    {
        val project = checkOwner(projectId, owner)
        val (endDate, startDate) = getDateRangeFromTimeFrame(date, project, projectId)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val topData = elasticTweetsService.getTopAnalysis(project,
            simpleDateFormat.format(startDate), simpleDateFormat.format(endDate), page,
            threatFilter, limit)
        return topData.toDTO()
    }


    fun getTopUsers(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopUsers(project, from, to, threat, pag, lim)
        }
    }


    fun getTopHashTags(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopHashTags(project, from, to, threat, pag, lim)
        }
    }


    fun getTopTopics(projectId: String, owner: String, page: Int, threatFilter: Int? = null, 
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO 
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopTopics(project, from, to, threat, pag, lim)
        }
    }


    fun getTopKeyIdeas(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopKeyIdeas(project, from, to, threat, pag, lim)
        }
    }


    fun getTopConcepts(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopConcepts(project, from, to, threat, pag, lim)
        }
    }


    fun getTopEntities(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date()): TopDataItemArrayDTO
    {
        return manageTop(projectId, owner, page, threatFilter, limit, date)
        { project, from, to, threat, pag, lim ->
            elasticTweetsService.getTopEntities(project, from, to, threat, pag, lim)
        }
    }

    fun setFlags(projectId: String, owner: String, tweetId: String, flags: List<String>)
    {
        val project = checkOwner(projectId, owner)
        elasticTweetsService.setFlags(project, tweetId, flags)
    }


    fun removeFlags(projectId: String, owner: String, tweetId: String, flags: List<String>) {
        val project = checkOwner(projectId, owner)
        elasticTweetsService.removeFlags(project, tweetId, flags)
    }

    fun getTweetsFromProjectFlag(owner: String, projectId: String, flagName: String, tweetId: String? = null,
        threatFilter: Int? = null, limit: Int = 20, date: Date = Date()): TweetResultsDTO
    {
        val project = checkOwner(projectId, owner)
        val (endDate, startDate) = getDateRangeFromTimeFrame(date, project, projectId)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val tweetResults = elasticTweetsService.getTweetsFromProjectByFlag(project, flagName,
            simpleDateFormat.format(startDate),  simpleDateFormat.format(endDate), tweetId,
            threatFilter, limit)
        return tweetResults.toDTO()
    }

    fun getAvancedSearchCombos(): HashMap<String, List<String>>
        = elasticTweetsService.getAvancedSearchCombos()


    private fun manageTop(projectId: String, owner: String, page: Int, threatFilter: Int? = null,
        limit: Int = 10, date: Date = Date(), callback: (Project, String, String, Int?, Int, Int)
            -> TopDataItemArray): TopDataItemArrayDTO
    {
        val project = checkOwner(projectId, owner)
        val (endDate, startDate) = getDateRangeFromTimeFrame(date, project, projectId)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val topData = callback(project, simpleDateFormat.format(startDate),
            simpleDateFormat.format(endDate), threatFilter, page, limit)
        return topData.toDTO()
    }


    fun getProjectByOwnerAndId(id: String, owner: String): ProjectDTO {
        val project = projectService.getByIdAndOwner(id, owner)
        return project.toDTO()
    }

//    fun getProjectByOwnerAndIdArchived(id: String, owner: String): ProjectDTO {
//        val project = projectService.getByIdAndOwner(id, owner)
//        return project.toDTO()
//    }

    fun deleteProject(id: String, owner: String) {
        val project = checkOwner(id, owner)
        projectService.deleteProject(project)
        callToNodeWithKeywords(project)
    }



    fun getProjectById(id: String, owner:String): Project = checkOwner(id, owner)



    fun updateProject(id: String, owner: String, project: ProjectDTOUpdate) {
        val sourceProject = checkOwner(id, owner)
        projectService.updateProject(sourceProject, project)
        callToNodeWithKeywords(sourceProject)
    }


    fun getTweetsFromProject(owner: String, projectId: String, tweetId: String? = null,
        threatFilter: Int? = null, limit: Int = 20, date: Date = Date()): TweetResultsDTO
    {
        val project = checkOwner(projectId, owner)
        val (endDate, startDate) = getDateRangeFromTimeFrame(date, project, projectId)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val tweetResults = elasticTweetsService.getTweetsFromProject(project,
            simpleDateFormat.format(startDate),  simpleDateFormat.format(endDate), tweetId,
            threatFilter, limit)
        return tweetResults.toDTO()
    }


    fun getTweetsFromProjectByLocation(owner: String, projectId: String, fromDate: String,
        toDate: String, boundingBox: BoundingBox, limit: Int=1): TweetResultsDTO
    {
        val project = checkOwner(projectId, owner)
        val tweetResults = elasticTweetsService.getTweetsFromProjectByLocation(project, fromDate,
            toDate, boundingBox, limit)
        return TweetResultsDTO(tweetResults.total, arrayListOf(), 0)
    }



    fun getTweetsFromProjectByLocationByPlace(
        owner: String, projectId: String, fromDate: String,
        toDate: String, boundingBox: BoundingBox, page: Int=1,
        limit: Int=10): ListLocationCountryCityDTO
    {
        val project = checkOwner(projectId, owner)
        val result = elasticTweetsService.getTweetsFromProjectByLocationByPlace(project, fromDate,
            toDate, boundingBox, page, limit)
        return ListLocationCountryCityDTO(result.topInfo.map 
            { LocationCountryCityDTO(it.item.split("|")[0], it.item.split("|")[1], it.numPosts) })
    }



    fun translateTweet(id: String): TweetTranslationDTO
        = elasticTweetsService.translateTweet(id).toDTO()


    fun createProjectTimeFrame(projectTimeFrame: ProjectTimeFrameDTO): ProjectTimeFrameDTO {
        val projectTimeFrameDomain = ProjectTimeFrame.fromDTO(projectTimeFrame)
        val project = projectService.getById(projectTimeFrameDomain.id!!)
        val finalProjectTimeFrame = ProjectTimeFrame(projectTimeFrameDomain.id,
            projectTimeFrameDomain.fromDate
                ?: project.createdAt, projectTimeFrameDomain.toDate,
            projectTimeFrameDomain.realtime)
        return projectTimeFrameService.createProjectTimeFrame(finalProjectTimeFrame).toDTO()
    }

    fun getTimeFrameByOwnerAndId(owner: String, id: String): ProjectTimeFrameDTO {
        projectService.getByIdAndOwner(id, owner)
        return projectTimeFrameService.findById(id).toDTO()
    }

    fun getSearchTweetsFromProject(owner: String, projectId: String, fromDate: String,
        toDate: String, keywords: List<String>? = arrayListOf(),
        excludedKeywords: List<String>? = arrayListOf(), language: String?, country: String?,
        tweetId: String?, threatFilter: Int?, limit: Int): TweetResultsDTO
    {
        val project = checkOwner(projectId, owner)
        val tweetResults = elasticTweetsService.getSearchTweetsFromProject(project, fromDate,
         toDate, keywords!!, excludedKeywords!!, language, country, tweetId, threatFilter, limit)
        return tweetResults.toDTO()
    }

    fun getAllKeywords(): List<String> = projectService.getAllKeywords()


    private fun callToNodeWithKeywords(createdProject: Project) {
        val keywords: List<String> = projectService.getAllKeywords()
        networkSource.start(createdProject.getSources(), keywords)
    }

    private fun getDateRangeFromTimeFrame(date: Date, project: Project,
        projectId: String): Pair<Date, Date?>
    {
        var endDate = date
        var startDate = project.createdAt
        try {
            val projectTimeFrame = projectTimeFrameService.findById(projectId)
            endDate = projectTimeFrame.toDate ?: Date()
            if (projectTimeFrame.realtime == 1) endDate = Date()
            startDate = projectTimeFrame.fromDate ?: startDate
        } catch (ex: DomainException) {
            println(ex) // dimitrii
        }

        return Pair(endDate, startDate)
    }

    fun deleteAllProjects() = projectService.deleteAllProjects()

}
