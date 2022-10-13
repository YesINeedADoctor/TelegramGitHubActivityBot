package io.project.SpringTelegramGHActivityBot.db;

public class NonExistentRepositoryException extends NonExistentEntityException {

//    private static final long serialVersionUID = 8633588908169766368L;

    public NonExistentRepositoryException() {
        super("Repository does not exist");
    }
}