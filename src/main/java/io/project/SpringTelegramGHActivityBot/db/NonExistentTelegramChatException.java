package io.project.SpringTelegramGHActivityBot.db;

public class NonExistentTelegramChatException extends NonExistentEntityException {

//    private static final long serialVersionUID = 8633588908169766368L;

    public NonExistentTelegramChatException() {
        super("TelegramChat does not exist");
    }
}
