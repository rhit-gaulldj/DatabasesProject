package screens;

import components.ComponentTable;
import components.NavBar;
import components.NavHandler;
import databaseServices.UserService;
import util.IntReturnAction;
import util.SimpleAction;

import javax.swing.*;
import java.awt.*;

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

    private SimpleAction onAdd;
    private IntReturnAction getCount;

    public ListScreen(int pageSize, NavHandler handler, UserService userService, String objName) {
        super();
        this.pageSize = pageSize;
        this.objName = objName;
        this.handler = handler;
        this.userService = userService;
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
        updateTable(table, page);

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
        maxEntries = getCount.call();
        myUpdateTable();
    }

    // Child must determine how to update the table
    protected abstract void updateTable(ComponentTable table, int page);

    public void addOnAddHandler(SimpleAction action) {
        onAdd = action;
    }
    public void addGetCountHandler(IntReturnAction action) {
        getCount = action;
    }

}
