package backend21.domain.socialnetworks

import backend21.application.project.PostHistogramDTO
import backend21.application.project.ThreatLevelCountDTO
import java.util.*
import kotlin.collections.ArrayList

data class ThreatLevelCount(val threatLevel: Long, val numPosts: Long) {
    fun toDTO(): ThreatLevelCountDTO = ThreatLevelCountDTO(threatLevel, numPosts)
}
data class PostHistogram(val values: SortedMap<String, ArrayList<ThreatLevelCount>>) {
    fun toDTO(): PostHistogramDTO {
        val copyValues = sortedMapOf<String, ArrayList<ThreatLevelCountDTO>>()
        values.keys.forEach {
            val valArray = values[it]
            val copyValArray = ArrayList<ThreatLevelCountDTO>()
            valArray!!.forEach {
                item-> copyValArray.add(item.toDTO())
            }
            copyValues[it] = copyValArray
        }
        return PostHistogramDTO(copyValues)
    }

}
