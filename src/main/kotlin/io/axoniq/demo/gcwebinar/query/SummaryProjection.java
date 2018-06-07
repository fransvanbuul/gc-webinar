package io.axoniq.demo.gcwebinar.query;

import io.axoniq.demo.gcwebinar.command.IssuedEvt;
import io.axoniq.demo.gcwebinar.command.RedeemedEvt;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Component
@Profile("query")
public class SummaryProjection {

    private final EntityManager entityManager;

    public SummaryProjection(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @EventHandler
    public void on(IssuedEvt evt) {
        entityManager.persist(new CardSummary(
                evt.getId(),
                evt.getAmount(),
                evt.getAmount()
        ));
    }

    @EventHandler
    public void on(RedeemedEvt evt) {
        CardSummary summary = entityManager.find(CardSummary.class,
                evt.getId());
        summary.setRemainingBalance(summary.getRemainingBalance()
            - evt.getAmount());
    }

    @Autowired
    public void config(EventHandlingConfiguration config) {
        config.registerTrackingProcessor(getClass().getPackage().getName());
    }

    @QueryHandler
    public List<CardSummary> handle(DataQuery query) {
        return entityManager
                .createQuery("SELECT c FROM CardSummary c ORDER BY c.id",
                        CardSummary.class)
                .getResultList();
    }

    @QueryHandler
    public Integer handle(SizeQuery query) {
        return entityManager
                .createQuery("SELECT COUNT(c) FROM CardSummary c", Long.class)
                .getSingleResult()
                .intValue();
    }




}
