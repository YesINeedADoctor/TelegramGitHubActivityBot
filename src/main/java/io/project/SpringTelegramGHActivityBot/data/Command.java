package io.project.SpringTelegramGHActivityBot.data;

import java.util.List;
import java.util.ArrayList;

public enum Command {
    START(0, "/start"),
    HELP(1, "/help"),
    SET_REPOSITORY(2, "/set_repository"),
    REPOSITORY_INFO(3, "/repository_info"),
    REPOSITORY_ACTIVITY(4, "/repository_activity"),
    REPOSITORY_LAST_PULL_REQUEST(5, "/repository_last_pr"),
    CLEAR_DATA(6, "/clear_data"),
    STUB(7, "");

    private final int id;

    private final String title;

    Command(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static Command getCommandByTitle(String title) {
        for (Command command : Command.values()) {
            if (command.getTitle().equals(title)) return command;
        }
        return null;
    }

    public static List<String> getTitles() {
        List<String> returnList = new ArrayList<>();
        for (Command command : Command.values()) returnList.add(command.getTitle());
        return returnList;
    }
}