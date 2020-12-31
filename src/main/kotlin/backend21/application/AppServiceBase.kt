package backend21.application

import backend21.domain.oauthcredentials.CredentialsException
import backend21.domain.project.Project
import backend21.domain.project.ProjectService
import org.springframework.beans.factory.annotation.Autowired

open class AppServiceBase @Autowired constructor(private val projectService: ProjectService) {
    protected fun checkOwner(id: String, owner: String): Project {
        val project = projectService.getById(id)
        if (!project.hasSameOwner(owner)) throw CredentialsException("$owner isn't the owner of project $id")
        return project
    }
}
