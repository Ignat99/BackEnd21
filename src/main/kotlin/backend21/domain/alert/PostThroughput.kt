package backend21.domain.alert

import java.util.*

data class PostThroughput(val values: SortedMap<String, Long>) {
    fun achievesThreshold(threshold: Long): Set<String> =
        values.filter { it.value > threshold }.keys
}
