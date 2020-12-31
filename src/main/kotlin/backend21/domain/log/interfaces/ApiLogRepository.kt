package backend21.domain.log.interfaces

import backend21.domain.log.ApiLog
import org.springframework.data.jpa.repository.JpaRepository

interface ApiLogRepository: JpaRepository<ApiLog, String>
