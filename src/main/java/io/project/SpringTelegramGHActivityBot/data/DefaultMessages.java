package io.project.SpringTelegramGHActivityBot.data;

public enum DefaultMessages {

    ERROR(0, "error"),
    ENTER_OWNER_NAME (1, "Enter the owner name of a repository you want to track \uD83D\uDE0E:"),
    CHOOSE_REPOSITORY_USING_COMMAND(2, "Choose a repository using /set_repository command"),
    REPOSITORY_INFO_RETURN_TEXT(3, """
                                    <b>Selected repository id</b> - %s;
                                    <b>Repository name</b> - %s
                                    <b>Repository owner</b> - %s
                                    <b>Description</b> - %s
                                    <b>URL</b> - %s
                                    \uD83E\uDD13
                                    """),
    SERVICE_CURRENTLY_UNAVAILABLE(4, "\uD83D\uDE13 Service is currently unavailable"),
    REPOSITORY_ACTIVITY_RETURN_TEXT(5,"""
                                <b>Total number of commits</b> - %s
                                <b>Total number of open Pull Requests</b> - %s
                                <b>Last week commits count</b> - %s (syncing may take time 🥱)
                                """),
    REPOSITORY_LAST_PULL_REQUEST_RETURN_TEXT(6, """
                                        <b>Last Pull Request</b> 🧐
                                        <b>ID</b> - %s
                                        <b>Title</b> - %s
                                        <b>Created by</b> - %s
                                        <b>Created at</b> - %s
                                        <b>Updated at</b> - %s
                                        <b>URL</b> - %s
                                        """),
    NO_PULL_REQUESTS_FOUND(7, "There are no Pull Requests \uD83D\uDC4C"),
    ALL_DATA_CLEARED(8, "All data successfully cleared! \uD83E\uDD78"),
    HELP_RETURN_TEXT(9, """
                        /set_repository - Allows you to specify the repository whose activity you want to monitor
                        
                        /repository_info - Information about the monitored repository
                        
                        /repository_activity - Activity of the monitored repository
                        
                        /repository_last_pr - Watch last Pull Request of selected repository
                        
                        /subscribe - Subscribe for selected repository Git Hub Activity
                        
                        /clear_data - Clearing all data
                        
                        🥸"""),
    START_RETURN_TEXT(10, "Let's start! Type /set_repository command"),
    NO_SUCH_COMMAND(11, "Sorry, no such command \uD83D\uDE2E"),
    CHOOSE_REPOSITORY(12, "Choose repository: \uD83D\uDE0F"),
    NO_REPOSITORIES_FOUND(13, "No repositories found \uD83D\uDE2D"),
    DATA_IS_OLD(14, "Sorry, data is old, try again \uD83D\uDE13"),
    NO_OWNER_FOUND(15, "\uD83D\uDE13 No such owner found / service is currently unavailable");

    private final int id;
    private final String message;

    DefaultMessages(int id, String message){
        this.id = id;
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}