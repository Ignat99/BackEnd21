package backend21.application.project

import backend21.application.user.ProjectTimeFrameDTO
import org.junit.Assert
import org.junit.Test
import java.util.*

class ProjectTimeFrameDTOUnitTest {
    @Test
    fun test_construct_called_correctInstance() {
        val instance = ProjectTimeFrameDTO("21", toDate= Date(), realtime = 1)
        Assert.assertEquals("21", instance.id)
    }

    @Test
    fun test_construct_calledWithDates_correctInstances() {
        val instance = ProjectTimeFrameDTO("21", Date(), Date(), 0)
        Assert.assertEquals("21", instance.id)
    }
}