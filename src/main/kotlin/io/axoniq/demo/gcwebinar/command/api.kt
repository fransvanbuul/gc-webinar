package io.axoniq.demo.gcwebinar.command

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class IssueCmd(@TargetAggregateIdentifier val id: String, val amount: Int)
data class IssuedEvt(val id: String, val amount: Int)
data class RedeemCmd(@TargetAggregateIdentifier val id: String, val amount: Int)
data class RedeemedEvt(val id: String, val amount: Int)
