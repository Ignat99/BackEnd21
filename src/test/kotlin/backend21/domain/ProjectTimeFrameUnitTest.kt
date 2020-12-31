package backend21.domain

import backend21.domain.project.ProjectTimeFrame
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat


class ProjectTimeFrameUnitTest {
    @Test
    fun test_construct_withNoRealtime_correctInstanceWithToDate() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val projectTimeFrame = ProjectTimeFrame("32", simpleDateFormat.parse("2014-05-05 12:12:12"), simpleDateFormat.parse("2016-02-02 12:12:12"), 0)
        val expected = "2016-02-02 12:12:12"
        val actual = simpleDateFormat.format(projectTimeFrame.toDate)
        Assert.assertEquals(expected, actual)
    }

    @Test(expected = DomainException::class)
    fun test_construct_withToDateLessThanFromDate_throw() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        ProjectTimeFrame("32", simpleDateFormat.parse("2017-05-05 12:12:12"), simpleDateFormat.parse("2016-02-02 12:12:12"), 0)
    }


    @Test
    fun test_construct_withRealTime_correctInstanceWithNowToDate() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val projectTimeFrame = ProjectTimeFrame("32", simpleDateFormat.parse("2017-05-05 12:12:12"), simpleDateFormat.parse("2016-02-02 12:12:12"), 1)
        val actual = projectTimeFrame.toDate!! > projectTimeFrame.fromDate
        val expected = true
        Assert.assertEquals(expected, actual)
    }
}