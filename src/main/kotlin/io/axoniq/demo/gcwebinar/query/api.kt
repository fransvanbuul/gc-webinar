package io.axoniq.demo.gcwebinar.query

import javax.persistence.Entity
import javax.persistence.Id

data class DataQuery(val offset: Int, val limit: Int)
class SizeQuery

@Entity
data class CardSummary(
        @Id var id: String? = null,
        var initialBalance: Int? = 0,
        var remainingBalance: Int? = 0
)