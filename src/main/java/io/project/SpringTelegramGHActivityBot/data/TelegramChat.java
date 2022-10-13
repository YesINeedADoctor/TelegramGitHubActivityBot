package io.project.SpringTelegramGHActivityBot.data;

public class TelegramChat {

    private Long chatId;

    private Long repositoryId;

    private Command lastCommand;

    public static class Builder {
        private Long chatId;
        private Long repositoryId;
        private Command lastCommand;

        public Builder(Long chatId) {
            this.chatId = chatId;
        }

        public Builder setRepositoryId(Long repositoryId) {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder setLastCommand(Command lastCommand) {
            this.lastCommand = lastCommand;
            return this;
        }

        public TelegramChat build() {
            return new TelegramChat(this);
        }
    }

    private TelegramChat(Builder builder) {
        this.chatId = builder.chatId;
        this.repositoryId = builder.repositoryId;
        this.lastCommand = builder.lastCommand;
    }

    public String toString() {
        return "TelegramChat[%s, %s, %s]".formatted(getId(), getRepositoryId(), getLastCommand());
    }

    public Long getId() {
        return chatId;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public Command getLastCommand() {
        return lastCommand == null? Command.STUB : lastCommand;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }
}