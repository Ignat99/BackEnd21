package backend21.infrastructure

import backend21.SpringEntryPoint
import backend21.domain.user.User
import backend21.domain.user.interfaces.UserRepository
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import backend21.mother.UserMother

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [(SpringEntryPoint::class)])
@TestPropertySource("classpath:test.properties")
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository
    private var user: User? = null

    @Before
    fun setUp() {
        userRepository?.deleteAll()
        user = UserMother().testAdminInstance()
        setupInitialUser()
    }

    @After
    fun tearDown() {
        userRepository?.deleteAll()
    }

    @Test
    fun test_save_saveUnexistentUser_correctInsertion() {
        val actual = userRepository?.findOne("1")
        val expected = "luis"
        Assert.assertEquals(expected, actual?.name)
    }

    @Test
    fun test_save_existsUserWithSameId_updateUser() {
        val updatedUser = User(user?.id, user?.username, user?.email, user?.password, "luisa", user?.surname)
        userRepository?.save(updatedUser)
        val persistedUsers = userRepository?.findAll()
        val expected = "luisa"
        Assert.assertEquals(1, persistedUsers?.size)
        Assert.assertEquals(expected, persistedUsers!![0]?.name)
    }


    @Test
    fun test_findByUsername_calledWithExistentUserWithEmail_returnCorrectUser()
    {
        val user = userRepository?.findByUsername("admi")
        Assert.assertEquals("admi", user?.get()?.username)
    }

    @Test
    fun test_findByUsername_calledWithNoExistentUserWithEmail_returnEmptyUser()
    {
        val user = userRepository?.findByUsername("juan")
        Assert.assertEquals(false, user?.isPresent)
    }


    private fun setupInitialUser() {
        val retrievedUser = userRepository?.findOne("1")
        Assert.assertNull("guard - user exists", retrievedUser)
        userRepository?.save(user)
    }


}