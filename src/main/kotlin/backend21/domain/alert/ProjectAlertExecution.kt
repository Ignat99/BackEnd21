package backend21.domain.alert

import backend21.application.alert.ProjectAlertExecutionDTO
import org.apache.velocity.VelocityContext
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class ProjectAlertExecution(@Id @GeneratedValue(strategy= GenerationType.IDENTITY)
        val id: String?=null, val projectId: String?=null, val alias: String?=null,
        val fromDate:Date?=Date(), val toDate:Date?=Date(), val extravars: String?=null,
        val showAlert: Int?=1, val executedAt: Date?=Date(), val visited:Int?=0,
        val archived:Int?=0)
{
    fun toDTO(): ProjectAlertExecutionDTO = ProjectAlertExecutionDTO(this.id, this.projectId,
        this.alias, this.fromDate, this.toDate, this.extravars, this.showAlert, this.executedAt,
        this.visited)
    fun isAlert():Boolean = this.showAlert == 1
    fun toVelocityContext(): VelocityContext {
        val velocityContext = VelocityContext()
        velocityContext.put("date", this.executedAt)
        velocityContext.put("project", this.projectId)
        velocityContext.put("alias", this.alias)
        velocityContext.put("extravars", this.extravars)
        return velocityContext
    }
}
