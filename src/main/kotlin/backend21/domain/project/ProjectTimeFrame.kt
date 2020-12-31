package backend21.domain.project

import backend21.application.user.ProjectTimeFrameDTO
import backend21.domain.DomainException
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ProjectTimeFrame(@Id val id: String?=null, val fromDate: Date?=null, var toDate: Date?=null,
    val realtime: Int?=0)
{

    init {
        if (realtime == 1) this.toDate = Date()
        if (fromDate != null) if (fromDate > this.toDate) {
            throw DomainException(
                "Invalid dates. From cannot be greater than To: $fromDate, $toDate")
        }
    }
    companion object {
        fun fromDTO(projectTimeFrame: ProjectTimeFrameDTO): ProjectTimeFrame = ProjectTimeFrame(
            projectTimeFrame.id, projectTimeFrame.fromDate, projectTimeFrame.toDate,
            projectTimeFrame.realtime)

    }

    fun toDTO(): ProjectTimeFrameDTO = ProjectTimeFrameDTO(this.id!!, this.fromDate!!,
        this.toDate!!, this.realtime)
}
