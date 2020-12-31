package backend21.resources



import backend21.application.project.*
import backend21.domain.BoundingBox
import org.apache.commons.lang3.time.DateFormatUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController

class TwitterController @Autowired constructor(private val projectAppService: ProjectAppService):BaseController() {
    
    @RequestMapping(path=["/keywords"], method=[RequestMethod.POST])
    fun keywords(@RequestBody params:String): ResponseEntity<String> {
        return ResponseEntity.ok("keywordsdone")
    }

    @PostMapping("/api/tweets")
    fun tweets(@RequestBody projectCommand: ProjectInputDTO): ResponseEntity<TweetResultsDTO> {
        return ResponseEntity.ok(checkCredentials(projectCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTweetsFromProject(it, projectCommand.projectId, projectCommand.tweetId, projectCommand.threatFilter, projectCommand.limit))
        } ) as ResponseEntity<TweetResultsDTO>
    }

    @PostMapping("/api/tweetflags")
    fun addFlags(@RequestBody flagCommand: FlagInputDTO): ResponseEntity<String> {
        checkCredentials(flagCommand.toString()) {
            ResponseEntity.ok(projectAppService.setFlags(flagCommand.projectId, it, flagCommand.tweetId, flagCommand.flags))
        }
        return return201()
    }

    @PostMapping("/api/removetweetflags")
    fun removeFlags(@RequestBody flagCommand: FlagInputDTO): ResponseEntity<String> {
        checkCredentials("") {
            ResponseEntity.ok(projectAppService.removeFlags(flagCommand.projectId, it, flagCommand.tweetId, flagCommand.flags))
        }
        return return201()
    }

    @PostMapping("/api/tweetsFlag")
    fun tweetsFlag(@RequestBody projectCommand: ProjectFlagDTO): ResponseEntity<TweetResultsDTO> {
        return ResponseEntity.ok(checkCredentials(projectCommand.toString()) {
            ResponseEntity.ok(projectAppService.getTweetsFromProjectFlag(it, projectCommand.projectId, projectCommand.flagName, projectCommand.tweetId, projectCommand.threatFilter, projectCommand.limit))
        } ) as ResponseEntity<TweetResultsDTO>
    }

    @PostMapping("/api/locationtweets")
    fun locationTweets(@RequestBody projectLocation: ProjectLocationCommandDTO): ResponseEntity<TweetResultsDTO> {
        return ResponseEntity.ok(checkCredentials(projectLocation.toString()) {
            ResponseEntity.ok(projectAppService.getTweetsFromProjectByLocation(it, projectLocation.projectId, DateFormatUtils.format(projectLocation.from, "yyyy-MM-dd'T'HH:mm:ss"), DateFormatUtils.format(projectLocation.to, "yyyy-MM-dd'T'HH:mm:ss"), BoundingBox(projectLocation.boundingBox.leftLong, projectLocation.boundingBox.leftLat, projectLocation.boundingBox.rightLong, projectLocation.boundingBox.rightLat), projectLocation.limit))
        }) as ResponseEntity<TweetResultsDTO>
    }


    @PostMapping("/api/locationtweetsbyplace")
    fun locationTweetsByPlace(@RequestBody projectLocation: ProjectLocationCommandDTO): ResponseEntity<ListLocationCountryCityDTO> {
        return ResponseEntity.ok(checkCredentials(projectLocation.toString()) {
            ResponseEntity.ok(projectAppService.getTweetsFromProjectByLocationByPlace(it, projectLocation.projectId, DateFormatUtils.format(projectLocation.from, "yyyy-MM-dd'T'HH:mm:ss"), DateFormatUtils.format(projectLocation.to, "yyyy-MM-dd'T'HH:mm:ss"), BoundingBox(projectLocation.boundingBox.leftLong, projectLocation.boundingBox.leftLat, projectLocation.boundingBox.rightLong, projectLocation.boundingBox.rightLat), projectLocation.page?:1, projectLocation.limit))
        }) as ResponseEntity<ListLocationCountryCityDTO>
    }



    @PostMapping("/api/searchtweets")
    fun searchTweets(@RequestBody projectSearchCommand: ProjectSearchCommandDTO): ResponseEntity<TweetResultsDTO> {
        return ResponseEntity.ok(checkCredentials(projectSearchCommand.toString()) {
            ResponseEntity.ok(projectAppService.getSearchTweetsFromProject(it, projectSearchCommand.projectCommand.projectId, DateFormatUtils.format(projectSearchCommand.projectCommand.from, "yyyy-MM-dd'T'HH:mm:ss"), DateFormatUtils.format(projectSearchCommand.projectCommand.to, "yyyy-MM-dd'T'HH:mm:ss"), projectSearchCommand.keywords?: arrayListOf(), projectSearchCommand.excludedKeywords?: arrayListOf(), projectSearchCommand.language, projectSearchCommand.country, projectSearchCommand.projectCommand.tweetId, projectSearchCommand.projectCommand.threatFilter, projectSearchCommand.projectCommand.limit))
        }) as ResponseEntity<TweetResultsDTO>
    }


    @GetMapping("/api/avancedsearchcombos")
    fun getAvancedSearchCombos(): ResponseEntity<HashMap<String, List<String>>> {
        return ResponseEntity.ok(checkCredentials("") {
            ResponseEntity.ok(projectAppService.getAvancedSearchCombos())
        }) as ResponseEntity<HashMap<String, List<String>>>
    }



    @GetMapping("/api/translate/{id}")
    fun translate(@PathVariable("id") id: String): ResponseEntity<TweetTranslationDTO> {
        return ResponseEntity.ok(checkCredentials("tweetId=$id") {
            val translate = projectAppService.translateTweet(id)
            ResponseEntity.ok(translate)
        }) as ResponseEntity<TweetTranslationDTO>
    }


}