package backend21.application.project

import backend21.domain.socialnetworks.ThreatLevelCount
import java.util.*

data class PostHistogramDTO(val values: SortedMap<String, ArrayList<ThreatLevelCountDTO>>)
