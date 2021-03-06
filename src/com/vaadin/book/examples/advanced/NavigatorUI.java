package com.vaadin.book.examples.advanced;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@Theme("book-examples")
// EXAMPLE-FILE: advanced.navigator.basic /com/vaadin/book/examples/advanced/MainView.html
// EXAMPLE-FILE: advanced.navigator.basic /com/vaadin/book/examples/advanced/AnimalViewer.html
// BEGIN-EXAMPLE: advanced.navigator.basic
public class NavigatorUI extends UI {
    private static final long serialVersionUID = 511085335415683713L;
    
    Navigator  navigator;
    
    /** A start view for navigating to the main view */
    public class LoginView extends VerticalLayout implements View {
        private static final long serialVersionUID = -3398565663865641952L;

        public static final String NAME = "";
        
        public LoginView() {
            setSizeFull();

            Button button = new Button("Go to Main View",
                    new Button.ClickListener() {
                private static final long serialVersionUID = -1809072471885383781L;

                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(MainView.NAME);
                }
            });
            addComponent(button);
            setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        }        
        
        @Override
        public void enter(ViewChangeEvent event) {
            Notification.show("Welcome to the Animal Farm");
        }
    }

    /** Main view with a menu (with declarative layout design) */
    @DesignRoot
    public class MainView extends VerticalLayout implements View {
        private static final long serialVersionUID = -3398565663865641952L;

        public static final String NAME = "main";

        // Menu navigation button listener
        class ButtonListener implements Button.ClickListener {
            private static final long serialVersionUID = -4941184695301907995L;

            String menuitem;
            public ButtonListener(String menuitem) {
                this.menuitem = menuitem;
            }

            @Override
            public void buttonClick(ClickEvent event) {
                // Navigate to a specific state
                navigator.navigateTo(MainView.NAME + "/" + menuitem);
            }
        }
        
        VerticalLayout menuContent;
        Panel equalPanel;
        Button logout;

        public MainView() {
            Design.read(this);

            menuContent.addComponent(new Button("Pig",
                      new ButtonListener("pig")));
            menuContent.addComponent(new Button("Cat",
                      new ButtonListener("cat")));
            menuContent.addComponent(new Button("Dog",      
                      new ButtonListener("dog")));
            menuContent.addComponent(new Button("Reindeer",
                      new ButtonListener("reindeer")));
            menuContent.addComponent(new Button("Penguin",
                      new ButtonListener("penguin")));
            menuContent.addComponent(new Button("Sheep",
                      new ButtonListener("sheep")));

            // Allow going back to the start
            logout.addClickListener(event -> // Java 8
                navigator.navigateTo(LoginView.NAME));
            
            // Create a grid
            Grid grid = new Grid();

            // Define some columns
            grid.addColumn("name", String.class);
            grid.addColumn("born", Integer.class);

            // Add some data rows
            grid.addRow("Nicolaus Copernicus", 1543);
            grid.addRow("Galileo Galilei", 1564);
            grid.addRow("Johannes Kepler", 1571);

            addComponent(grid);
            
            grid.addSelectionListener(new SelectionListener() {
                
                @Override
                public void select(SelectionEvent event) {
                    getUI().getNavigator().navigateTo(LoginView.NAME);
                }
            });            
        }        
        
        @DesignRoot
        class AnimalViewer extends VerticalLayout {
            private static final long serialVersionUID = 572784347380517093L;

            Label watching;
            Embedded pic;
            Label back;
            
            public AnimalViewer(String animal) {
                Design.read(this);
                
                watching.setValue("You are currently watching a " +
                                  animal);
                pic.setSource(new ThemeResource(
                    "img/" + animal + "-128px.png"));
                back.setValue("and " + animal +
                    " is watching you back");
            }
        }

        @Override
        public void enter(ViewChangeEvent event) {
            if (event.getParameters() == null
                || event.getParameters().isEmpty()) {
                equalPanel.setContent(
                    new Label("Nothing to see here, " +
                              "just pass along."));
                return;
            } else
                equalPanel.setContent(new AnimalViewer(
                    event.getParameters()));
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("Navigation Example");
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        
        // Create and register the views
        navigator.addView("", new LoginView());
        navigator.addView(MainView.NAME, new MainView());
    }
}
// END-EXAMPLE: advanced.navigator.basic
