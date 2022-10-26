package io.project.SpringTelegramGHActivityBot.telegram_bot;

import io.project.SpringTelegramGHActivityBot.config.BotConfig;
import io.project.SpringTelegramGHActivityBot.service.BotProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    @Autowired
    @Lazy
    private BotProcessService BPS;

    @Autowired
    public TelegramBot(BotConfig config) {
        this.config = config;
        log.info("[TelegramBot] is created");
    }

    public void startBot(){
        this.initBPS();
    }

    private void initBPS(){
        BPS.startDBServices();      // - creates db and needed tables in db
        BPS.startMessagesSender();  // - starts messages sender from queue
    }

    @Override
    public void onUpdateReceived(Update update) {
        BPS.onUpdateReceived(update);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}