package backend21.domain.flag.interfaces



import backend21.domain.flag.Flag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface FlagRepository: JpaRepository<Flag, String> {
    fun findByProjectId(projectId: String): Optional<List<Flag>>
}
