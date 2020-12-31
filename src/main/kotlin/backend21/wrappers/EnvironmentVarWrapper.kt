package backend21.wrappers

//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class EnvironmentVarWrapper : VariableVarWrapper {
    val defaultValues = hashMapOf(
            "development" to hashMapOf("TESTVAR" to "test"),
            "test" to hashMapOf("TESTVAR" to "test")
    )

    override fun getEnvironmentVar(key: String): String {
        return System.getenv(key) ?: defaultValues[System.getenv("ENV")]!![key] ?: System.getenv(key)
    }

    companion object {
//        private val LOG = LoggerFactory.getLogger(this::class.java)
        private val LOG = LogManager.getLogger(this::class.java)
    }

}
