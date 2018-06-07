package io.axoniq.demo.gcwebinar.gui;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import io.axoniq.demo.gcwebinar.command.IssueCmd;
import io.axoniq.demo.gcwebinar.command.RedeemCmd;
import io.axoniq.demo.gcwebinar.query.CardSummary;
import io.axoniq.demo.gcwebinar.query.DataQuery;
import io.axoniq.demo.gcwebinar.query.SizeQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;

import java.util.stream.Stream;

@SpringUI
public class GiftCardGUI extends UI {

    private final CommandGateway commandGateway;
    private final DataProvider<CardSummary, Void> dataProvider;

    public GiftCardGUI(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.dataProvider = dataProvider(queryGateway);
    }

    private DataProvider<CardSummary, Void> dataProvider(QueryGateway queryGateway) {
        return new AbstractBackEndDataProvider<CardSummary, Void>() {
            @Override
            protected Stream<CardSummary> fetchFromBackEnd(Query<CardSummary, Void> query) {
                return queryGateway.query(new DataQuery(query.getOffset(), query.getLimit()),
                        ResponseTypes.multipleInstancesOf(CardSummary.class)
                        ).join()
                        .stream();
            }

            @Override
            protected int sizeInBackEnd(Query<CardSummary, Void> query) {
                return queryGateway.query(new SizeQuery(), ResponseTypes.instanceOf(Integer.class))
                        .join();
            }
        };
    }



    @Override
    protected void init(VaadinRequest vaadinRequest) {
        HorizontalLayout commands = new HorizontalLayout();
        commands.setSizeFull();
        commands.addComponents(issuePanel(), redeemPanel());

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponents(commands, summaryGrid());

        setContent(layout);

        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable cause = event.getThrowable();
                while(cause.getCause() != null) cause = cause.getCause();
                Notification.show(cause.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });


    }

    private Panel issuePanel() {
        TextField id = new TextField("Id");
        TextField amount = new TextField("Amount");
        Button submit = new Button("Submit");

        submit.addClickListener(event -> {
            IssueCmd cmd = new IssueCmd(
                    id.getValue(),
                    Integer.parseInt(amount.getValue())
            );
            commandGateway.sendAndWait(cmd);
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(x -> dataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.addComponents(id, amount, submit);

        Panel panel = new Panel("Issue");
        panel.setContent(form);
        return panel;
    }

    private Panel redeemPanel() {
        TextField id = new TextField("Id");
        TextField amount = new TextField("Amount");
        Button submit = new Button("Submit");

        submit.addClickListener(event -> {
            RedeemCmd cmd = new RedeemCmd(
                    id.getValue(),
                    Integer.parseInt(amount.getValue())
            );
            commandGateway.sendAndWait(cmd);
            Notification.show("Success", Notification.Type.HUMANIZED_MESSAGE)
                    .addCloseListener(x -> dataProvider.refreshAll());
        });

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.addComponents(id, amount, submit);

        Panel panel = new Panel("Redeem");
        panel.setContent(form);
        return panel;
    }

    private Grid<CardSummary> summaryGrid() {
        Grid<CardSummary> grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(CardSummary::getId).setCaption("Id");
        grid.addColumn(CardSummary::getInitialBalance).setCaption("Initial balance");
        grid.addColumn(CardSummary::getRemainingBalance).setCaption("Remaining balance");

        grid.setDataProvider(dataProvider);

        return grid;
    }

}
