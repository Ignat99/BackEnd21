package backend21.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl


@Configuration
class EmailServiceConfig @Autowired constructor(@Value("\${EMAIL_HOST}") val smtpHost: String,
                                                @Value("\${EMAIL_PORT}") val smtpPort: String,
                                                @Value("\${EMAIL_USERNAME}") val smtpUserName: String,
                                                @Value("\${EMAIL_PASSWORD}") val smtpUserPassword: String) {

    @Bean
    fun javaMailSender(): JavaMailSender = JavaMailSenderImpl().apply {
        host = smtpHost
        port = smtpPort.toInt()
        username = smtpUserName
        password = smtpUserPassword
        javaMailProperties.setProperty("mail.transport.protocol", "smtp")
        javaMailProperties.setProperty("mail.smtp.auth", "true")
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true")
        javaMailProperties.setProperty("mail.debug", "true")

    }
}
