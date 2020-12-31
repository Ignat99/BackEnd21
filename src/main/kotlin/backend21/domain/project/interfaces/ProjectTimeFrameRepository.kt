package backend21.domain.project.interfaces

import backend21.domain.project.ProjectTimeFrame
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectTimeFrameRepository: JpaRepository<ProjectTimeFrame, String> {
    fun findById(id: String): Optional<ProjectTimeFrame>
}
