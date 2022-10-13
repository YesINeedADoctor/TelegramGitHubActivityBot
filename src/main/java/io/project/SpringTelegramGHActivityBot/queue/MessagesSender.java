package io.project.SpringTelegramGHActivityBot.queue;

import io.project.SpringTelegramGHActivityBot.service.BotProcessService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
public class MessagesSender implements Runnable {
    private LinkedBlockingQueue<Update> BQ;
    private final BotProcessService BPS;

    public MessagesSender(LinkedBlockingQueue<Update> BQ, BotProcessService BPS) {
        this.BQ = BQ;
        this.BPS = BPS;
        log.info("[MessagesSender] queue is created");
    }

    @Override
    public void run() {
        log.info("[MessagesSender] queue is started");
        while (true) {
            try {
                Update update = BQ.take();
                log.info("[MessagesSender] got update - " + update.getUpdateId());
                BPS.queueProcessMessage(update);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
