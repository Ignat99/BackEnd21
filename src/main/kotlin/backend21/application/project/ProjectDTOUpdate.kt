package backend21.application.project

import backend21.security.ProjectValidator
import java.util.*

data class ProjectDTOUpdate(val name: String?=null, val keywords: List<String>?=null,
    val excludedKeywords: List<String>?=null, val teamMembers: List<String>?=null,
    val description: String?=null)
{

    init {
        ProjectValidator.validateWords(keywords)
    }
}
