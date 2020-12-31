package backend21.wrappers

//import org.slf4j.LoggerFactory
import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component

const val STACK_TRACE = 3

@Component
class LoggerWrapper: TraceWrapper {
    override fun info(message: String)  = LOG.info(getCallingClassName() + " - " + message)

    override fun error(message: String) = LOG.error(getCallingClassName() + " - " + message)

    private fun getCallingClassName(): String = Thread.currentThread().stackTrace[STACK_TRACE].className

    companion object {
//        private val LOG = LoggerFactory.getLogger(this::class.java)
        private val LOG = LogManager.getLogger(this::class.java)
//        private static final Logger LOG = LogManager.getLogger(this::class.java)
    }

}
