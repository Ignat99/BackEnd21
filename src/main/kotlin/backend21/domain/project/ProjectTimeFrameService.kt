package backend21.domain.project

import backend21.domain.DomainException
import backend21.domain.project.interfaces.ProjectTimeFrameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ProjectTimeFrameService @Autowired constructor(
    private val projectTimeFrameRepository: ProjectTimeFrameRepository)
{
    fun createProjectTimeFrame(projectTimeFrame: ProjectTimeFrame): ProjectTimeFrame =
        projectTimeFrameRepository.save(projectTimeFrame)


    fun findById(id: String): ProjectTimeFrame {
        val projectTimeFrame = projectTimeFrameRepository.findById(id)
        if (projectTimeFrame.isPresent) return projectTimeFrame.get()
        throw DomainException("ProjectTimeFrame with id $id doesn't exist")
    }

}
