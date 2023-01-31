package components;

import screens.ScreenOpenArgs;
import screens.ScreenTypes;

public interface NavHandler {
    void navigate(ScreenTypes screen, ScreenOpenArgs args);
}
