package backend21.domain.socialnetworks

class ScoreTweetPonderation {
    companion object {
        val ponderations = hashMapOf<Long, Long>(
                0L to 1,
                1L to 1,
                2L to 2,
                3L to 3,
                4L to 5,
                5L to 8
        )

        fun getPonderation(key: Long): Long {
            return ponderations[key]!!
        }
    }


}
