package backend21.application.project

import backend21.security.ProjectValidator
import java.util.*

data class ProjectDTO(var id: String?=null, val name: String, val type: String,
    val keywords: List<String>, val excludedKeywords: List<String>, val sources: List<String>,
    val teamMembers: List<String>, val description: String?=null, var numPosts: Long?=0,
    var threatLevel: Int?=null, val numAlerts:Int?=0, val createdAt:Date?=Date())
{
    var owner: String?=null

    init {
        val allowedTypes = arrayListOf("keyword", "dataset")
        if (!allowedTypes.contains(type)) 
            throw InvalidPropertiesFormatException("Invalid project type")
        ProjectValidator.validateWords(keywords)
        ProjectValidator.validateType(type)
        if (sources.isEmpty())
            throw InvalidPropertiesFormatException("sources list cannot be empty")
    }
}
