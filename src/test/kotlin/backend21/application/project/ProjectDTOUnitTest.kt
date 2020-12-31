package backend21.application.project

import org.junit.Assert
import org.junit.Test
import java.util.*

class ProjectDTOUnitTest {
    @Test
    fun test_constructor_correctArgumentGiven_correctInstantiation() {
        val projectDTO = ProjectDTO(null, "testName", "keyword", arrayListOf("bin laden"), arrayListOf(), arrayListOf("twitter"), arrayListOf(), "a description")
        Assert.assertEquals("a description", projectDTO.description)
    }

    @Test
    fun test_constructor_correctArgumentGiven2_correctInstantiation() {
        val projectDTO = ProjectDTO(null, "testName", "dataset", arrayListOf("bin laden"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
        Assert.assertEquals(null, projectDTO.description)
    }

    @Test(expected=InvalidPropertiesFormatException::class)
    fun test_constructor_incorrectTypeGiven_throw() {
        ProjectDTO(null, "test", "aaa", arrayListOf(), arrayListOf(), arrayListOf("twitter"), arrayListOf())
    }

    @Test(expected=InvalidPropertiesFormatException::class)
    fun test_constructor_keywordsListEmpty_throw() {
        ProjectDTO(null, "testName", "keyword", arrayListOf(), arrayListOf(), arrayListOf("twitter"), arrayListOf())
    }

    @Test(expected=InvalidPropertiesFormatException::class)
    fun test_constructor_sourceListEmpty_throw() {
        ProjectDTO("2", "testName", "dataset", arrayListOf("testName"), arrayListOf(), arrayListOf(), arrayListOf())
    }

    @Test(expected=InvalidPropertiesFormatException::class)
    fun test_constructor_stopWordInAKeywordsList_throw() {
        ProjectDTO("3","testName", "keyword", arrayListOf("bin laden", "car", "above", "house"), arrayListOf(), arrayListOf("twitter"), arrayListOf())
    }
}