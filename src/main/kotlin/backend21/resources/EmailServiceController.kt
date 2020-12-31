package backend21.resources

import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.email.EmailService
import backend21.domain.project.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api")
class EmailServiceController @Autowired constructor(private val emailService: EmailService) {
    @GetMapping("/email")
    fun todo() {
        val project = Project("2", "name2", "keyword", arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), arrayListOf("twitter"), arrayListOf(), "alala", "owner2", Date())

        emailService.alert(ProjectAlertExecution("2", "2", "alias3", Date(), Date(), "user=juan", 1, Date()), project, "http://localhost:8080/api")

    }
}