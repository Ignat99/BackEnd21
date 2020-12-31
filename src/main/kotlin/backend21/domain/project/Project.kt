package backend21.domain.project

import backend21.application.project.ProjectDTO
import backend21.security.ProjectValidator
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
@Entity
class Project(@Id var id: String? = null, val name: String?=null, type: String?=null,
    keywords: List<String>?=null, excludedKeywords: List<String>?=null,
    sources: List<String>?=null, teamMembers: List<String>?=null, description: String?=null,
    var owner:String?=null, var createdAt: Date?=Date(), var active:Int?=1)
{
    private val keywords: String?
    private val excludedKeywords: String?
    private val sources: String?
    private val teamMembers: String?
    val type: String?
    val description: String?
    private val lowerName: String?
    private val lowerDescription: String?
    init {
        ProjectValidator.validateWords(keywords)
        ProjectValidator.validateType(type)
        this.type = type
        this.keywords = keywords?.joinToString(",")
        this.excludedKeywords = excludedKeywords?.joinToString(",")
        this.sources = sources?.joinToString(",")
        this.teamMembers = teamMembers?.joinToString(",")
        this.description = description?:""
        this.lowerName = this.name?.toLowerCase()?:""
        this.lowerDescription = this.description.toLowerCase()
    }


    fun getKeywords(): List<String> = stringToList(this.keywords)


    fun getExcludedKeywords(): List<String> = stringToList(this.excludedKeywords)


    fun getSources(): List<String> = stringToList(this.sources)
    fun getTeamMembers(): List<String> = stringToList(this.teamMembers)



    companion object {
        val TWITTER = "twitter"
        fun fromDTO(projectDTO: ProjectDTO): Project = Project(projectDTO.id,
            projectDTO.name, projectDTO.type, projectDTO.keywords, projectDTO.excludedKeywords,
            projectDTO.sources, projectDTO.teamMembers, projectDTO.description, projectDTO.owner,
            projectDTO.createdAt)
    }

    fun toDTO(): ProjectDTO {
        val projectDTO = ProjectDTO(this.id!!, this.name!!, this.type!!, this.getKeywords(),
            this.getExcludedKeywords(), this.getSources(), this.getTeamMembers(),
            this.description,createdAt=this.createdAt)
        projectDTO.owner = this.owner
        return projectDTO
    }

    fun hasSameOwner(owner: String): Boolean = this.owner.equals(owner)

    private fun stringToList(source: String?): List<String> {
        if (source == null) return listOf()
        if (source.isEmpty()) return listOf()
        return source.split(",")
    }


}
