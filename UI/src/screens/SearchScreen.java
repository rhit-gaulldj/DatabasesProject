package screens;

import databaseServices.SearchService;

public class SearchScreen extends Screen {

    private SearchService searchService;

    public SearchScreen(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void populatePanel() {

    }

    @Override
    public void openScreen(ScreenOpenArgs args) {

    }
}
