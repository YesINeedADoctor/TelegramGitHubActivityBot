package io.project.SpringTelegramGHActivityBot.db;

public class NonExistentEntityException extends Throwable {

//    private static final long serialVersionUID = -3760558819369784286L;

    public NonExistentEntityException(String message) {
        super(message);
    }
}
