package screens;

import components.ComponentTable;
import components.NavBar;
import components.NavHandler;
import components.ResultHighlightLabel;
import databaseServices.SearchService;
import databaseServices.UserService;
import dbObj.SearchResult;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchScreen extends Screen {

    private NavHandler navHandler;

    private SearchService searchService;
    private UserService userService;

    private static final int SCROLL_PANE_HEIGHT = 450; // Simply hardcoded
    private JScrollPane tableScrollPane;
    private ComponentTable table;
    private JTextField searchBox;

    public SearchScreen(SearchService searchService, UserService userService,
                        NavHandler navHandler) {
        this.searchService = searchService;
        this.userService = userService;

        this.navHandler = navHandler;
    }

    @Override
    public void populatePanel() {
        super.createPanel();
        JPanel parent = super.getPanel();
        super.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        NavBar navBar = new NavBar(navHandler, userService);
        parent.add(navBar.getPanel());

        JPanel searchRow = new JPanel();
        searchRow.add(new JLabel("Search: "));
        searchBox = new JTextField(40);
        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onUpdate();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                onUpdate();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                onUpdate();
            }

            public void onUpdate() {
                updateTable();
            }
        });
        searchRow.add(searchBox);
        parent.add(searchRow);

        tableScrollPane = new JScrollPane();
        table = new ComponentTable(new String[]{"Name", "Type"});
        tableScrollPane.setViewportView(table);
        parent.add(tableScrollPane);
    }

    @Override
    public void openScreen(ScreenOpenArgs args) {
        searchBox.setText("");
        updateTable();
    }

    private void updateTable() {
        ArrayList<JComponent[]> cells = new ArrayList<>();
        String query = searchBox.getText();
        if (query.length() > 0) {
            List<SearchResult> results = searchService.search(query);
            for (SearchResult result : results) {
                String typeName = switch (result.type()) {
                    case SearchResult.ATHLETE -> "Athlete";
                    case SearchResult.COURSE -> "Course";
                    case SearchResult.MEET -> "Meet";
                    default -> "Unknown";
                };
                ResultHighlightLabel nameLabel = new ResultHighlightLabel(result.name(), query);
                switch (result.type()) {
                    case SearchResult.ATHLETE:
                        nameLabel.addActionListener(() -> {
                            ScreenOpenArgs args = new ScreenOpenArgs();
                            args.add("id", result.id());
                            navHandler.navigate(ScreenTypes.AthleteView, args);
                        });
                        break;
                    case SearchResult.COURSE:
                        nameLabel.addActionListener(() -> {
                            ScreenOpenArgs args = new ScreenOpenArgs();
                            args.add("id", result.id());
                            navHandler.navigate(ScreenTypes.CourseView, args);
                        });
                        break;
                    case SearchResult.MEET:
                        nameLabel.addActionListener(() -> {
                            ScreenOpenArgs args = new ScreenOpenArgs();
                            args.add("id", result.id());
                            navHandler.navigate(ScreenTypes.MeetView, args);
                        });
                        break;
                }
                cells.add(new JComponent[]{
                    nameLabel, new JLabel(typeName)
                });
            }
        }
        table.setCells(cells);

        getPanel().repaint();
        getPanel().revalidate();

        tableScrollPane.setPreferredSize(new Dimension((int) table.getPreferredSize().getWidth() + 20,
                SCROLL_PANE_HEIGHT));
    }
}
