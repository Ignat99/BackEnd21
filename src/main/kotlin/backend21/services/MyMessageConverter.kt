package backend21.services

import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import java.io.IOException
import java.lang.reflect.Type

@Service
class MyMessageConverter: MappingJackson2HttpMessageConverter() {




    @Throws(IOException::class, HttpMessageNotReadableException::class)
    override fun read(type: Type, contextClass: Class<*>, inputMessage: HttpInputMessage): Any {
        val result = super.read(type, contextClass, inputMessage)
        return result
    }


}
