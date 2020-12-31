package backend21.domain.project

import backend21.application.login.LoginAppService
import backend21.application.project.ProjectDTO
import backend21.application.project.ProjectDTOUpdate
import backend21.domain.Domain
import backend21.domain.DomainException
import backend21.domain.project.interfaces.ProjectRepository
//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService @Autowired constructor(val projectRepository: ProjectRepository) {
    fun createProject(project: Project): Project {
        project.id = project.id?:UUID.randomUUID().toString()
        try {
            return projectRepository.save(project)
        }
        catch(ex: Exception) {
            throw DomainException(ex.message?:"")
        }
    }

    fun getAllKeywords(): List<String> {
        val keywords = projectRepository.findAllKeywords()
        val isolatedKeywords = keywords.flatMap { it.split(",") }
        return isolatedKeywords.distinct()
    }

    fun getProjectsByOwner(owner: String, term:String?=null): List<Project> {
        if (term == null) return projectRepository.findByOwnerAndActive(owner, 1)
            .orElse(arrayListOf())
        return projectRepository.findByOwnerAndActiveWithTerm(owner, 1, term.toLowerCase())
            .orElse(arrayListOf())
    }

    fun getById(id: String): Project {
        val project = projectRepository.findByIdAndActive(id, 1)
        if (!project.isPresent) throw DomainException("Project with id $id doesn't exist")
        return project.get()
    }

    fun getByIdAndOwner(id: String, owner: String): Project {
        val project = projectRepository.findByIdAndOwnerAndActive(id, owner, 1)
        if (!project.isPresent) throw DomainException("Project with id $id doesn't exist")
        return project.get()
    }

//    fun getByIdAndOwnerArchived(id: String, owner: String): Project {
//        val project = projectRepository.findByIdAndOwnerAndActive(id, owner, 0)
//        if (!project.isPresent) throw DomainException("Project with id $id doesn't exist")
//        return project.get()
//    }

    fun getProjects(): List<Project> {
        val projects = projectRepository.findByActive(1)
        if (!projects.isPresent) return emptyList()
        return projects.get()
    }


    fun deleteProject(project: Project) {
        project.active = 0
        projectRepository.save(project)
    }

    fun updateProject(sourceProject: Project, project: ProjectDTOUpdate) {
        val newProject = Project(sourceProject.id, project.name?:sourceProject.name,
            sourceProject.type, project.keywords?:sourceProject.getKeywords(),
            project.excludedKeywords?:sourceProject.getExcludedKeywords(),
            sourceProject.getSources(), project.teamMembers?:sourceProject.getTeamMembers(),
            project.description?:sourceProject.description, sourceProject.owner,
            sourceProject.createdAt, sourceProject.active)
        projectRepository.save(newProject)
    }

    fun deleteAllProjects() = projectRepository.deleteAll()

    companion object {
//        private val LOG = LoggerFactory.getLogger(ProjectService::class.java)
        private val LOG = LogManager.getLogger(ProjectService::class.java)
    }
}
