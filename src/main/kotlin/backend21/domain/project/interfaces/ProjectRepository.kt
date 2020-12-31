package backend21.domain.project.interfaces

import backend21.domain.project.Project
import backend21.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ProjectRepository: JpaRepository<Project, String> {
    @Query("select keywords from Project where active=1")
    fun findAllKeywords(): List<String>
    @Query("select p from Project p where p.owner= ?1 and p.active= ?2 and (p.lowerName like %?3% or p.lowerDescription like %?3%)")
    fun findByOwnerAndActiveWithTerm(
        owner: String, active: Int, term: String): Optional<List<Project>>

    fun findByOwnerAndActive(owner: String, active:Int): Optional<List<Project>>
    fun findByIdAndActive(id: String, active: Int): Optional<Project>
    fun findByActive(active: Int): Optional<List<Project>>
    fun findByIdAndOwnerAndActive(id: String, owner: String, active: Int): Optional<Project>

}
