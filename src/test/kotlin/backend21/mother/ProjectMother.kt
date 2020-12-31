package backend21.mother

import backend21.domain.project.Project
import java.util.*

class ProjectMother {

    fun testInstance(id:String="1", name: String, owner:String, description:String?="a description", createdAt:Date?= Date()): Project = Project(id, name, "keyword", arrayListOf("ala", "alo man"), arrayListOf("ele", "ili"), arrayListOf("twitter"), arrayListOf(), description, owner, createdAt)
}