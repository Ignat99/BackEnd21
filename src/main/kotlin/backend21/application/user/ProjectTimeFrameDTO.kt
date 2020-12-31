package backend21.application.user

import backend21.domain.DomainException
import java.util.*

data class ProjectTimeFrameDTO(val id: String, val fromDate: Date?=null, var toDate: Date, val realtime: Int? = 1) {

    init {
        if (realtime == 1) this.toDate = Date()
        if (fromDate != null) if (fromDate > this.toDate) {
            throw DomainException("Invalid dates. From cannot be greater than To: $fromDate, $toDate")
        }
    }

}
