package backend21.domain.flag

import backend21.domain.DomainException
import backend21.domain.flag.interfaces.FlagRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.*

@Service
class FlagService @Autowired constructor(private val flagRepository: FlagRepository) {
    fun createFlag(flag: Flag): Flag {
        flag.id = UUID.randomUUID().toString()
        try {
            return flagRepository.save(flag)
        }
        catch(ex: DataIntegrityViolationException) {
            throw DomainException(ex.message?:"")
        }
    }


    fun getFlagsByProjectId(projectId: String): List<Flag> {
        val result = flagRepository.findByProjectId(projectId)
        if (result.isPresent) return result.get()
        return emptyList()
    }
}
