package backend21.domain

import backend21.domain.flag.Flag
import backend21.domain.flag.FlagService
import backend21.domain.flag.interfaces.FlagRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import java.util.*


class FlagServiceUnitTest {
    @Mock
    internal lateinit var flagRepository: FlagRepository

    internal lateinit var sut: FlagService

    internal lateinit var flag: Flag


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        flag = Flag("32", "a name")

        sut = FlagService(flagRepository)
    }

    @Test
    fun createFlag_called_correctCallToInnerRepository() {
        Mockito.`when`(flagRepository.save(flag)).thenReturn(flag)
        sut.createFlag(flag)
        val argument = ArgumentCaptor.forClass(Flag::class.java)
        Mockito.`verify`(flagRepository, Mockito.times(1)).save(argument.capture())
    }


    @Test(expected = DomainException::class)
    fun createFlag_calledWithException_throwDomainException() {
        Mockito.`when`(flagRepository.save(flag)).thenAnswer( {
            throw DataIntegrityViolationException("")
        })
        sut.createFlag(flag)
    }


    @Test
    fun getFlagsByProjectId_repositoryReturnsNull_returnEmptyList() {
        Mockito.`when`(flagRepository.findByProjectId("33")).thenReturn(Optional.empty())
        val actual = sut.getFlagsByProjectId("33")
        Assert.assertEquals(emptyList<Flag>(), actual)
    }

    @Test
    fun getFlagsByProjectId_called_correctCallToInnerRepository() {
        Mockito.`when`(flagRepository.findByProjectId("33")).thenReturn(Optional.of(listOf(Flag("33", "name"))))
        sut.getFlagsByProjectId("33")
        Mockito.`verify`(flagRepository, Mockito.times(1)).findByProjectId("33")

    }

}