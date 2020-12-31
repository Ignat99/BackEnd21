package backend21.domain

import backend21.application.project.ProjectDTO
import backend21.domain.project.Project
import org.junit.Assert
import org.junit.Test
import java.util.*

class ProjectUnitTest {

    @Test(expected= InvalidPropertiesFormatException::class)
    fun test_constructor_stopWordInAKeywordsList_throw() {
        Project(null, "testName", "keyword", arrayListOf("bin laden", "car", "above", "house"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
    }


    @Test(expected= InvalidPropertiesFormatException::class)
    fun test_constructor_invalidType_throw() {
        Project(null, "testName", "lalala", arrayListOf("bin laden", "car", "house"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
    }

    @Test
    fun test_constructor_keywordsWithNoStopWords_correctInstance() {
        val project = Project(null, "testName", "keyword", arrayListOf("bin laden", "car", "abubilleik", "house"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
        Assert.assertEquals("testName", project.name)
    }


}