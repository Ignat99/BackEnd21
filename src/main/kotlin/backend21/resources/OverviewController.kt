package backend21.resources


import backend21.application.project.*
import backend21.domain.DomainException
import backend21.domain.socialnetworks.PostHistogram
import backend21.domain.socialnetworks.ThreatLevelCount


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.ArrayList

@RestController
@RequestMapping("/api")
class OverviewController @Autowired constructor(private val projectAppService: ProjectAppService):BaseController() {

    @GetMapping("/overview/{projectId}/{page}/{interval}")
    fun getOverview(@PathVariable("projectId") projectId: String, @PathVariable("page") page: Int, @PathVariable("interval") interval: Int): ResponseEntity<ProjectOverviewDTO> {
        return ResponseEntity.ok(checkCredentials("projectId=$projectId,page=$page,interval=$interval") {

            try {
                val overviewDTO = projectAppService.getProjectOverview(projectId, it, page, interval)
                val project = projectAppService.getProjectById(projectId, it)
                val filteredHistogram = overviewDTO.histogram.values.filter { SimpleDateFormat("yyyy-MM-dd").parse(it.key) >= SimpleDateFormat("yyyy-MM-dd").parse(SimpleDateFormat("yyyy-MM-dd").format(project.createdAt)) }
                val sortedFilteredHistogram = sortedMapOf<String, ArrayList<ThreatLevelCountDTO>>()
                filteredHistogram.map { sortedFilteredHistogram[it.key] = it.value }
                val histogramDTO = PostHistogramDTO(sortedFilteredHistogram)
                ResponseEntity.ok(ProjectOverviewDTO(histogramDTO, overviewDTO.topData, overviewDTO.topRetweets))
            }
            catch(e: DomainException) {
                ResponseEntity.notFound().build()
            }

        }) as ResponseEntity<ProjectOverviewDTO>
    }


    //projectId: String, owner: String, page: Int, threatFilter: Int? = null, limit: Int = 10, date: Date = Date()


    @GetMapping("/topcontent")
    fun getTopContent(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopAnalysis(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataDTO>
    }


    @GetMapping("/topusers")
    fun getTopUsers(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopUsers(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }

    @GetMapping("/tophashtags")
    fun getTopHashTags(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopHashTags(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }


    @GetMapping("/toptopics")
    fun getTopTopics(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopTopics(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }

    @GetMapping("/topkeyideas")
    fun getTopKeyIdeas(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopKeyIdeas(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }

    @GetMapping("/topconcepts")
    fun getTopConcepts(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopConcepts(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }

    @GetMapping("/topentities")
    fun getTopEntities(@RequestBody topContentCommand: TopContentDTO): ResponseEntity<TopDataItemArrayDTO> {
        return ResponseEntity.ok(checkCredentials(topContentCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTopEntities(topContentCommand.projectId, it, topContentCommand.page, topContentCommand.threatFilter, topContentCommand.limit, topContentCommand.date))
        }) as ResponseEntity<TopDataItemArrayDTO>
    }

}