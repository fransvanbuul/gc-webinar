package io.axoniq.demo.gcwebinar.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Profile;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@Profile("command")
public class GiftCard {

    @AggregateIdentifier
    private String id;
    private int balance;

    private GiftCard() {
    }

    @CommandHandler
    public GiftCard(IssueCmd cmd) {
        if(cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        apply(new IssuedEvt(cmd.getId(), cmd.getAmount()));
    }

    @CommandHandler
    public void handle(RedeemCmd cmd) {
        if(cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        if(cmd.getAmount() > balance) throw new IllegalArgumentException("amount > balance");
        apply(new RedeemedEvt(cmd.getId(), cmd.getAmount()));
    }

    @EventSourcingHandler
    public void on(IssuedEvt evt) {
        id = evt.getId();
        balance = evt.getAmount();
    }

    @EventSourcingHandler
    public void on(RedeemedEvt evt) {
        balance -= evt.getAmount();
    }

}
