package backend21.domain.alert.interfaces

import backend21.domain.alert.ProjectAlertExecution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectAlertExecutionRepository : JpaRepository<ProjectAlertExecution, String> {
    fun findByArchivedAndAliasAndProjectIdAndExtravarsContainingOrderByExecutedAtDesc(
        archived: Int, alias: String, projectId: String,
        extravars: String): Optional<List<ProjectAlertExecution>>
    fun findTop20ByArchivedAndShowAlertAndVisitedOrderByIdDesc(
        archived: Int, showAlert: Int, read: Int): Optional<List<ProjectAlertExecution>>
    fun findTop20ByArchivedAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
        archived: Int, showAlert: Int, read: Int, id: Int): Optional<List<ProjectAlertExecution>>
    fun findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedOrderByIdDesc(
        archived: Int, projectId: String, showAlert: Int, read: Int): Optional<List<ProjectAlertExecution>>
    fun findTop20ByArchivedAndProjectIdAndShowAlertAndVisitedAndIdLessThanOrderByIdDesc(
        archived: Int, projectId: String, showAlert: Int,
        read: Int, id: Int): Optional<List<ProjectAlertExecution>>
    fun findByArchivedAndId(archived: Int, id: String): Optional<ProjectAlertExecution>
}
