package io.project.SpringTelegramGHActivityBot.service;

import io.project.SpringTelegramGHActivityBot.config.IntegrationConfig;
import io.project.SpringTelegramGHActivityBot.config.JdbcConfig;
import io.project.SpringTelegramGHActivityBot.data.*;
import io.project.SpringTelegramGHActivityBot.db.PostgreSqlDBCreator;
import io.project.SpringTelegramGHActivityBot.db.PostgreSqlDaoRequestRepository;
import io.project.SpringTelegramGHActivityBot.db.PostgreSqlDaoTelegramChat;
import io.project.SpringTelegramGHActivityBot.queue.MessagesSender;
import io.project.SpringTelegramGHActivityBot.telegram_bot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class BotProcessService {
    private Command lastCommand = Command.STUB;
    private Long currentChatId = null;
    private Long currentRepositoryId = null;
    private final TelegramBot bot;
    private final MessagesSender messagesSender;
    LinkedBlockingQueue<Update> BQ = new LinkedBlockingQueue<>();

    @Autowired
    public BotProcessService(TelegramBot bot) {
        this.bot = bot;
        messagesSender = new MessagesSender(BQ, this);
    }

    @Autowired
    private JdbcConfig jdbcConfig;
    @Autowired
    private PostgreSqlDaoRequestRepository REPOSITORY_DAO;
    @Autowired
    private PostgreSqlDaoTelegramChat TELEGRAMCHAT_DAO;
    //    @Autowired
//    private IntegrationConfig IntegrationConfig;
    @Autowired
    private GHGetPullRequestsService getPullRequestsService;
    @Autowired
    private GHGetRepositoriesService getRepositoriesService;
    @Autowired
    private GHGetEachDayCommitsService getEachDayCommitsService;
    @Autowired
    private GHGetWeeklyParticipationService getWeeklyParticipationService;

    public void startDBServices() {
        PostgreSqlDBCreator dbCreator = new PostgreSqlDBCreator(jdbcConfig);
        dbCreator.createDataBase();
        dbCreator.createTables();
    }

    public void startMessagesSender() {
        Thread senderThread = new Thread(messagesSender::run);
        senderThread.setDaemon(true);
        senderThread.setName("MessagesSender");
        senderThread.start();
    }

    public void onUpdateReceived(Update update) {
        BQ.add(update);
    }

    public void queueProcessMessage(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                setMainProperties(update.getCallbackQuery().getMessage());
                handleCallback(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else if (update.hasMessage()) {
            try {
                setMainProperties(update.getMessage());
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        String[] param = callbackQuery.getData().split(":");
        if (param.length > 0) {
            String callBackDataType = callbackQuery.getData().split(":")[0];
            if (callBackDataType.equals("requested_repo")) {
                SetRepositoryByCallBack(callbackQuery, param);
            }
        } else {
            //Удаляем результаты и постим сообщение о недостатке data
            SetRepositoryByCallBackWithNoData(callbackQuery);
        }
    }

    private void handleCommandMessage(Message message) throws TelegramApiException {
        if (!message.getText().isEmpty()) {
            switch (lastCommand) {
                case SET_REPOSITORY:
                    SetRepository(message);
                    break;
            }
        }
    }

    public void handleMessage(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities()
                    .stream()
                    .filter(e -> "bot_command".equals(e.getType()))
                    .findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText()
                        //Такая форма из-за того, что оффсет задает абсолютное положение команды в стриме
                        //Бывают ситуации, что офсет - 38, а длина - 2)
                        //Типично для таких строк: /\/\\/\\\\////////\/\\/\\/////\\\\/\/\/f\/dws\f/s\g/s\f/
                        .substring(commandEntity.get().getOffset(), commandEntity.get()
                                .getOffset() + commandEntity.get().getLength());
                handleCommand(command, message);
            }
        } else if (message.hasText()) handleCommandMessage(message);
    }

    public void handleCommand(String command, Message message) throws TelegramApiException {
        String returnText = "";
        if ((lastCommand = Command.getCommandByTitle(command)) != null) {
            switch (lastCommand) {
                case SET_REPOSITORY -> returnText = DefaultMessages.ENTER_OWNER_NAME.getMessage();
                case REPOSITORY_INFO -> {
                    //Если нет инфы в базе, можно попробовать дернуть сервис
                    returnText = DefaultMessages.CHOOSE_REPOSITORY_USING_COMMAND.getMessage();
                    if (currentRepositoryId != null) {
                        RequestRepository dbRequestRepository = getRepositoryFromDB(currentRepositoryId);
                        if (dbRequestRepository.getId() != null) {

                            returnText = DefaultMessages.REPOSITORY_INFO_RETURN_TEXT.getMessage().formatted(
                                    currentRepositoryId,
                                    dbRequestRepository.getName(),
                                    dbRequestRepository.getOwnerName(),
                                    dbRequestRepository.getDescription(),
                                    dbRequestRepository.getHTML_URL());
                        }
                    }
                }
                case REPOSITORY_ACTIVITY -> {
                    if (currentRepositoryId != null) {
                        RequestRepository dbRequestRepository = getRepositoryFromDB(currentRepositoryId);

                        String firstParam = getEachDayCommitsService.getEachDayCommits(
                                dbRequestRepository.getOwnerName(),
                                dbRequestRepository.getName()
                        );
                        String secondParam = getWeeklyParticipationService.getWeeklyParticipation(
                                dbRequestRepository.getOwnerName(),
                                dbRequestRepository.getName()
                        );
                        String thirdParam = getPullRequestsService.getPullRequests(
                                dbRequestRepository.getOwnerName(),
                                dbRequestRepository.getName()
                        );
                        firstParam = firstParam.equals("ERR") ? DefaultMessages.SERVICE_CURRENTLY_UNAVAILABLE.getMessage() :
                                String.valueOf(getEachDayCommitsService.getTotalCommitsCount());

                        secondParam = secondParam.equals("ERR") ? DefaultMessages.SERVICE_CURRENTLY_UNAVAILABLE.getMessage() :
                                String.valueOf(getWeeklyParticipationService.getLastWeekCommitsCount());

                        thirdParam = thirdParam.equals("ERR") ? DefaultMessages.SERVICE_CURRENTLY_UNAVAILABLE.getMessage() :
                                String.valueOf(getPullRequestsService.getPullRequestsCount());

                        returnText = DefaultMessages.REPOSITORY_ACTIVITY_RETURN_TEXT.getMessage().formatted(
                                firstParam,
                                secondParam,
                                thirdParam
                        );
                    } else returnText = DefaultMessages.CHOOSE_REPOSITORY_USING_COMMAND.getMessage();
                }
                case REPOSITORY_LAST_PULL_REQUEST -> {
                    if (currentRepositoryId != null) {
                        RequestRepository dbRequestRepository = getRepositoryFromDB(currentRepositoryId);

                        String invocationResult = getPullRequestsService.getLastPullRequest(
                                dbRequestRepository.getOwnerName(),
                                dbRequestRepository.getName());

                        if (invocationResult.equals("ERR")) {
                            returnText = DefaultMessages.SERVICE_CURRENTLY_UNAVAILABLE.getMessage();
                        } else {
                            PullRequest tempPR = getPullRequestsService.getLastPullRequestDetails();
                            if (tempPR != null) {
                                returnText = DefaultMessages.REPOSITORY_LAST_PULL_REQUEST_RETURN_TEXT.getMessage()
                                        .formatted(
                                                tempPR.getId(),
                                                tempPR.getTitle(),
                                                tempPR.user.getUserLogin(),
                                                tempPR.getCREATED_AT(),
                                                tempPR.getUPDATED_AT(),
                                                tempPR.getHTML_URL()
                                        );
                            } else {
                                returnText = DefaultMessages.NO_PULL_REQUESTS_FOUND.getMessage();
                            }
                        }
                    } else returnText = DefaultMessages.CHOOSE_REPOSITORY_USING_COMMAND.getMessage();
                }
                case CLEAR_DATA -> {
                    clearData();
                    returnText = DefaultMessages.ALL_DATA_CLEARED.getMessage();
                }
                case HELP -> returnText = DefaultMessages.HELP_RETURN_TEXT.getMessage();
                case START -> returnText = DefaultMessages.START_RETURN_TEXT.getMessage();
                default -> returnText = DefaultMessages.NO_SUCH_COMMAND.getMessage();
            }
        } else {
            returnText = DefaultMessages.NO_SUCH_COMMAND.getMessage();
            lastCommand = Command.STUB;
        }
        //update db if necessary
        if (!lastCommand.equals(Command.CLEAR_DATA)) {
            updateTelegramChatDB(new TelegramChat.Builder(currentChatId)
                    .setRepositoryId(currentRepositoryId)
                    .setLastCommand(lastCommand)
                    .build());
        }
        SendMessage(message.getChatId(), returnText);
    }

    public void clearData() {
        updateTelegramChatDB(new TelegramChat.Builder(currentChatId)
                .setRepositoryId(null)
                .setLastCommand(null)
                .build());
    }

    public void SetRepository(Message message) throws TelegramApiException {
        String returnText;
        String messageText = message.getText();
        String firstParam = getRepositoriesService.getRepositoriesByOwner(messageText);

        if (firstParam.equals("ERR")) {
            returnText = DefaultMessages.NO_OWNER_FOUND.getMessage();
            SendMessage(message.getChatId(), returnText);

        } else if (getRepositoriesService.getFoundRepositories().size() > 0) {
            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            for (int i = 0; i < getRepositoriesService.getFoundRepositories().size(); i++) {
                addRepositoryToDB(getRepositoriesService.getFoundRepositories()
                        .get(i));
                List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(getRepositoriesService.getFoundRepositories()
                        .get(i).getName());
                button.setCallbackData("requested_repo:%s"
                        .formatted(
                                getRepositoriesService.getFoundRepositories().get(i).getId()
                        ));
                buttonsRow.add(button);
                buttons.add(buttonsRow);
            }
            returnText = DefaultMessages.CHOOSE_REPOSITORY.getMessage();
            SendMessageWithReplyMarkup(message.getChatId(), returnText, InlineKeyboardMarkup.builder()
                    .keyboard(buttons)
                    .build());
        } else {
            returnText = DefaultMessages.NO_REPOSITORIES_FOUND.getMessage();
            SendMessage(message.getChatId(), returnText);
        }
    }

    public void SetRepositoryByCallBack(CallbackQuery callbackQuery, String[] param) throws TelegramApiException {
        String returnText = "";
        Long id = Long.parseLong(param[1]);
        String fullName = param[1] = getRepositoryFromDB(id).getFullName();
        String firstParam = getRepositoriesService.getRepositoryByFullName(fullName);
        if (firstParam.equals("ERR")) {
            returnText = DefaultMessages.SERVICE_CURRENTLY_UNAVAILABLE.getMessage();
        } else {
            if (getRepositoriesService.getFoundRepository() != null) {
                RequestRepository dataRepository = getRepositoriesService.getFoundRepository();
                currentRepositoryId = dataRepository.getId();
                log.info("currentRepositoryId pre save/update = " + currentRepositoryId);

                //Если репозиторий уже в базе - обновляем его
                if (getRepositoryFromDB(currentRepositoryId).getId() != null) {
                    updateRepositoryDB(dataRepository);
                } else {
                    //Кладем репозиторий в github_repositories
                    addRepositoryToDB(dataRepository);
                }
                //Обновляем запись в талице telegram_chats
                updateTelegramChatDB(new TelegramChat.Builder(callbackQuery.getMessage()
                        .getChatId())
                        .setRepositoryId(currentRepositoryId)
                        .setLastCommand(Command.STUB)
                        .build());

                returnText = currentRepositoryId == null ? DefaultMessages.CHOOSE_REPOSITORY_USING_COMMAND.getMessage() :
                        DefaultMessages.REPOSITORY_INFO_RETURN_TEXT.getMessage()
                                .formatted(
                                        currentRepositoryId,
                                        dataRepository.getName(),
                                        dataRepository.getOwnerName(),
                                        dataRepository.getDescription(),
                                        dataRepository.getHTML_URL());
            } else {
                returnText = DefaultMessages.DATA_IS_OLD.getMessage();
            }
        }
        //Удаляем сообщение с выбором вариантов
        DeleteMessage(callbackQuery.getMessage()
                .getChatId(), callbackQuery.getMessage().getMessageId());

        SendMessage(callbackQuery.getMessage()
                .getChatId(), returnText);
        //update db
        updateTelegramChatDB(new TelegramChat.Builder(currentChatId)
                .setRepositoryId(currentRepositoryId)
                .setLastCommand(Command.STUB)
                .build());
    }

    public void SetRepositoryByCallBackWithNoData(CallbackQuery callbackQuery) {
//        STUB
    }

    private void setMainProperties(Message message) {
        //Устанавливаем основные параметры сессии
        currentChatId = message.getChatId();
        TelegramChat dbTelegramChat = getTelegramChatFromDB(currentChatId);
        if (dbTelegramChat.getId() != null) {
            currentRepositoryId = dbTelegramChat.getRepositoryId() == null ? null :
                    getRepositoryFromDB(dbTelegramChat.getRepositoryId()).getId();
            lastCommand = dbTelegramChat.getLastCommand() == null ? Command.STUB : dbTelegramChat.getLastCommand();
        } else {
            addTelegramChatToDB(new TelegramChat.Builder(currentChatId)
                    .setRepositoryId(null)
                    .setLastCommand(Command.STUB)
                    .build());
        }
    }

    public void DeleteMessage(long chatId, int messageId) throws TelegramApiException {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void SendMessage(long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode(ParseMode.HTML);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//            log.info(message.toString());
//            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void SendMessageWithReplyMarkup(long chatId, String text, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            message.setReplyMarkup(keyboard);
            bot.execute(message);
        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public TelegramChat getTelegramChatFromDB(Long id) {
        Optional<TelegramChat> telegramChat = TELEGRAMCHAT_DAO.get(id);
        return telegramChat.orElseGet(() -> new TelegramChat.Builder(null).setRepositoryId(null)
                .setLastCommand(null)
                .build());


    }

    public Optional addTelegramChatToDB(TelegramChat telegramChat) {
        return TELEGRAMCHAT_DAO.save(telegramChat);
    }

    public void updateTelegramChatDB(TelegramChat telegramChat) {
        TELEGRAMCHAT_DAO.update(telegramChat);
    }

    public RequestRepository getRepositoryFromDB(Long id) {
        Optional<RequestRepository> repository = REPOSITORY_DAO.get(id);
        return repository.orElseGet(() -> new RequestRepository.Builder(null).build());
    }

    public void updateRepositoryDB(RequestRepository repository) {
        REPOSITORY_DAO.update(repository);
    }

    public Optional addRepositoryToDB(RequestRepository repository) {
        return REPOSITORY_DAO.save(repository);
    }
}