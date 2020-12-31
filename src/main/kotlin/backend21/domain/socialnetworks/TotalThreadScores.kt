package backend21.domain.socialnetworks

import kotlin.math.round


data class TotalThreadScores(val total: Long, val scores:HashMap<Long, Long>) {
    fun getThreatLevel(): Int {
        if (total.equals(0L)) return 0
        var ponderation: Long = 0
        var totalMedia: Long = 0
        scores.keys.forEach {
            val numDocs = scores[it]!!.or(0)
            val weight: Long = ScoreTweetPonderation.getPonderation(it)
            totalMedia += numDocs * weight.or(0)
            ponderation += numDocs * weight.or(0) * it
        }

        val floatMean = ponderation.toFloat().div(totalMedia.toFloat())
        return round(floatMean).toInt()
    }
}
