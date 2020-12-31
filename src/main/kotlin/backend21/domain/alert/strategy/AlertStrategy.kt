package backend21.domain.alert.strategy

import backend21.domain.alert.Alert
import backend21.domain.alert.ProjectAlertExecution
import backend21.domain.project.Project
import backend21.domain.socialnetworks.TweetResults
import java.util.*

interface AlertStrategy {
    fun process(): ProjectAlertExecution?
    fun getName(): String
    fun getTweets(from: String, to:String, tweetId:String?=null,
        threatLevel:Int?=null): TweetResults
}
