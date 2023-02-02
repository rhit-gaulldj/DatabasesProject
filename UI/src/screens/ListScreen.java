package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavBar;
import components.NavHandler;
import databaseServices.AbstractDBService;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.UserService;
import dbObj.Athlete;
import dbObj.Gender;
import util.IntReturnAction;
import util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ListScreen extends Screen {

    private int page = 0;
    private final int pageSize;
    private int maxEntries; // Maximum # of entries tracked by this screen
    private final String objName;

    private JButton nextPageButton;
    private JButton prevPageButton;
    private ComponentTable table;

    private NavHandler handler;
    private UserService userService;

    private AbstractDBService service;

    private SimpleAction onAdd;
    private DBObjectToFieldsHandler toFieldsHandler;

    public ListScreen(int pageSize, NavHandler handler, UserService userService, String objName,
                      AbstractDBService service) {
        super();
        this.pageSize = pageSize;
        this.objName = objName;
        this.handler = handler;
        this.userService = userService;
        this.service = service;
    }

    public void populatePanel(String[] headers) {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        NavBar navBar = new NavBar(handler, userService);
        parent.add(navBar.getPanel());

        JPanel buttonRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add New " + objName);
        addButton.addActionListener(e -> {
            onAdd.call();
        });
        buttonRowPanel.add(addButton);
        parent.add(buttonRowPanel);

        table = new ComponentTable(headers);
        parent.add(table);

        JPanel pageButtonPanel = new JPanel();
        nextPageButton = new JButton(">>");
        prevPageButton = new JButton("<<");
        nextPageButton.addActionListener(e -> nextPage());
        prevPageButton.addActionListener(e -> prevPage());
        pageButtonPanel.add(prevPageButton);
        pageButtonPanel.add(nextPageButton);
        parent.add(pageButtonPanel);
    }

    private void nextPage() {
        page++;
        myUpdateTable();
    }
    private void prevPage() {
        page--;
        myUpdateTable();
    }

    private void myUpdateTable() {
        //updateTable(table, page);
        List<Object> objects = service.getObjects(page, pageSize);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Object o : objects) {
            String[] fields = toFieldsHandler.toFields(o);
            LinkButton editButton = new LinkButton(new Color(5, 138, 255), "Edit", 12);
            LinkButton deleteButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            editButton.addActionListener(() -> {
                // TODO: Add edit and delete functionality
                //edit(a.id());
            });
            deleteButton.addActionListener(() -> {
                //delete(a.id());
            });
            // Do +2 to account for edit/delete buttons
            JComponent[] row = new JComponent[fields.length + 2];
            for (int i = 0; i < fields.length; i++) {
                row[i] = new JLabel(fields[i]);
            }
            row[row.length - 2] = editButton;
            row[row.length - 1] = deleteButton;
            rows.add(row);
        }
        table.setCells(rows);

        prevPageButton.setEnabled(page > 0);
        int maxPage = (maxEntries - 1) / pageSize;
        nextPageButton.setEnabled(page < maxPage);

        getPanel().repaint();
        getPanel().revalidate();
    }

    public void openScreen(ScreenOpenArgs args) {
        if (args.has("page")) {
            page = (int) args.get("page");
        }
        updateAll();
    }

    public void updateAll() {
        maxEntries = service.getObjectCount();
        myUpdateTable();
    }


    public void addOnAddHandler(SimpleAction action) {
        onAdd = action;
    }
    public void addGetFieldsHandler(DBObjectToFieldsHandler toFieldsHandler) {
        this.toFieldsHandler = toFieldsHandler;
    }

}
