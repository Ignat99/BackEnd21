package backend21.domain.email




import backend21.domain.DomainException
import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.project.Project
import backend21.domain.user.UserService
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import java.io.StringWriter
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import org.apache.velocity.app.Velocity
import org.springframework.mail.javamail.JavaMailSender
import java.util.*


@Service
class EmailService (private val mailSender: JavaMailSender, private val userService:UserService) {

    private val taskExecutor: Executor = Executors.newSingleThreadScheduledExecutor()

    private fun realMailSending(): Boolean = System.getenv("SEND_MAIL") != "false"

    /*fun test(){
        val message = mailSender.createMimeMessage()
        val messageHelper = MimeMessageHelper(message)

        messageHelper.setTo("david.gomez@apiumhub.com")
        messageHelper.setFrom(System.getenv("EMAIL_USERNAME"))
        messageHelper.setSubject("this is a test from java")
        messageHelper.setText("this is the body of the test from java", true)

        if(realMailSending()){
            mailSender.send(message)
        }
    }*/



    fun alert(projectAlertExecution: ProjectAlertExecution, project: Project, frontendUrl: String) {
        val url = "$frontendUrl/alert"
        val emailSubject = "Notificación de alerta"
        val velocityEngine = VelocityEngine()

        val p = Properties()
        p.setProperty("resource.loader", "class")
        p.setProperty("class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader")
        velocityEngine.init(p)



        val template = velocityEngine.getTemplate("/templates/alert.html", "UTF-8")
        val velocityContext = projectAlertExecution.toVelocityContext()
        velocityContext.put("alertUrl", url)
        velocityContext.put("projectName", project.name)
        val stringWriter = StringWriter()
        template.merge(velocityContext, stringWriter)
        var email = "francisco.raya@apiumhub.com"
        try {
            val user = userService.findById(project.owner!!)
            email = user.email!!
        }
        catch(de: DomainException) 
        {
            println(de)
        }
        sendEmail(arrayOf(email), emailSubject, stringWriter.toString())
    }



    private fun sendEmail(toEmail: Array<String>, subject: String, body: String){
        taskExecutor.execute(MailSenderTask(toEmail, subject, body))
    }

    private inner class MailSenderTask(val toEmail: Array<String>, val subject: String,
        val body: String) : Runnable 
    {

        override fun run() {
            val message = mailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(message)


            messageHelper.setTo(toEmail)
            messageHelper.setFrom(System.getenv("EMAIL_USERNAME"))
            messageHelper.setSubject(subject)
            messageHelper.setText(body, true)


            if(realMailSending()) mailSender.send(message)
        }

    }
}
