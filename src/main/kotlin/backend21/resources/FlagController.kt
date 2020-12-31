package backend21.resources

import backend21.application.flag.FlagAppService
import backend21.application.flag.FlagDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class FlagController @Autowired constructor(private val flagAppService: FlagAppService) :BaseController() {
    @PostMapping(path = ["/flag"])
    fun createFlag(@RequestBody flagDTO: FlagDTO): ResponseEntity<FlagDTO> {
        return checkCredentials(flagDTO.toString()) {
            ResponseEntity.ok(flagAppService.createFlag(it, flagDTO))
        } as ResponseEntity<FlagDTO>

    }

    @GetMapping(path = ["/flags/{id}"])
    fun flags(@PathVariable("id") id: String): ResponseEntity<List<FlagDTO>> {
        return checkCredentials(id) {
            ResponseEntity.ok(flagAppService.getFlags(it, id))
        } as ResponseEntity<List<FlagDTO>>
    }
}

