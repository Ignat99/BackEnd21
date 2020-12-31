package backend21.application.flag

import backend21.application.AppServiceBase
import backend21.domain.flag.Flag
import backend21.domain.flag.FlagService
import backend21.domain.project.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FlagAppService @Autowired constructor(
        private val flagService: FlagService,
        projectService: ProjectService): AppServiceBase(projectService)
{

    fun createFlag(owner: String, flagDTO: FlagDTO): FlagDTO {
        checkOwner(flagDTO.projectId!!, owner)
        val flag = Flag.fromDTO(flagDTO)
        return flagService.createFlag(flag).toDTO()
    }


    fun getFlags(owner: String, projectId: String): List<FlagDTO> {
        checkOwner(projectId, owner)
        return flagService.getFlagsByProjectId(projectId).map { it.toDTO() }
    }
}
