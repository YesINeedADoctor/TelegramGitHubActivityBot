package io.project.SpringTelegramGHActivityBot.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
public class MessagesReceiver implements Runnable {
    LinkedBlockingQueue<Update> BQ;

    public MessagesReceiver(LinkedBlockingQueue<Update> BQ) {
        this.BQ = BQ;
        log.info("MessagesReceiver queue is created");
    }

    @Override
    public void run() {
        try {
            BQ.put(new Update());
            System.out.println("Produced smth");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
