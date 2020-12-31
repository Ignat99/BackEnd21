package backend21.application.project

data class ProjectSearchCommandDTO(val projectCommand: ProjectCommandDTO,
    val keywords: List<String>?=null, val excludedKeywords: List<String>?=null,
    val language: String?=null, val country: String?=null)
