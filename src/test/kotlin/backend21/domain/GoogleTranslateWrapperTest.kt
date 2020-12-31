package backend21.domain

import backend21.wrappers.GoogleTranslateWrapper
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class GoogleTranslateWrapperTest {
    @Ignore
    @Test
    fun test_todo() {
        val googleTranslateWrapper = GoogleTranslateWrapper()
        val translatedText = googleTranslateWrapper.translate("مهما يقولون الكلاب النابحة كفو قاهر المجوس واذنابهم اخوان الشياطين", "ar", "en")

        Assert.assertEquals("No matter what the arrogant dogs say, the power of the magi and their tails is the brothers of demons", translatedText)
    }
}