package screens;

import components.ComponentTable;
import components.LinkButton;
import components.NavBar;
import components.NavHandler;
import databaseServices.AbstractDBService;
import databaseServices.DBObjectConsumer;
import databaseServices.DBObjectToFieldsHandler;
import databaseServices.UserService;
import util.DeleteAction;
import util.EditAction;
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

    private DBObjectConsumer onFirstClick;

    private SimpleAction onAdd;
    private DBObjectToFieldsHandler toFieldsHandler;
    private EditAction edit;
    private DeleteAction delete;

    public ListScreen(int pageSize, NavHandler handler, UserService userService, String objName,
                      AbstractDBService service) {
        super();
        this.pageSize = pageSize;
        this.objName = objName;
        this.handler = handler;
        this.userService = userService;
        this.service = service;
    }

    public void setOnFirstClickEvent(DBObjectConsumer onFirstClick) {
        this.onFirstClick = onFirstClick;
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
        updateTable();
    }
    private void prevPage() {
        page--;
        updateTable();
    }

    private void updateTable() {
        List<Object> objects = service.getObjects(page, pageSize);
        ArrayList<JComponent[]> rows = new ArrayList<>();
        for (Object o : objects) {
            String[] fields = toFieldsHandler.toFields(o);
            final Color blueColor = new Color(5, 138, 255);
            LinkButton editButton = new LinkButton(blueColor, "Edit", 12);
            LinkButton deleteButton = new LinkButton(new Color(193, 71, 71), "Delete", 12);
            editButton.addActionListener(() -> {
                edit.edit(o);
            });
            deleteButton.addActionListener(() -> {
                delete.delete(o);
            });
            // Do +2 to account for edit/delete buttons
            JComponent[] row = new JComponent[fields.length + 2];

            boolean doingFirstAsButton = onFirstClick != null;
            if (doingFirstAsButton) {
                LinkButton firstButton = new LinkButton(blueColor, fields[0], 12);
                firstButton.addActionListener(() -> onFirstClick.use(o));
                row[0] = firstButton;
            }

            for (int i = (doingFirstAsButton ? 1 : 0); i < fields.length; i++) {
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
        updateTable();
    }

    public void addOnAddHandler(SimpleAction action) {
        onAdd = action;
    }
    public void addGetFieldsHandler(DBObjectToFieldsHandler toFieldsHandler) {
        this.toFieldsHandler = toFieldsHandler;
    }
    public void addEditHandler(EditAction edit) {
        this.edit = edit;
    }
    public void addDeleteHandler(DeleteAction delete) {
        this.delete = delete;
    }
}
