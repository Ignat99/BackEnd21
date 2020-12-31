package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.alert.interfaces.SystemAlertRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class SystemAlertRepositoryIntegrationTest {
    @Autowired
    private var systemAlertRepository: SystemAlertRepository?=null

    @Test
    fun test_findAll_called_returnListUserAlerts() {
        val systemAlerts = systemAlertRepository?.findAll()
        val expected = 10
        val actual = systemAlerts?.size
        Assert.assertEquals(expected, actual)
    }


}