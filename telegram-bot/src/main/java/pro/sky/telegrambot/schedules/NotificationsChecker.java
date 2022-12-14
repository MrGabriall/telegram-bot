package pro.sky.telegrambot.schedules;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;

@Component
public class NotificationsChecker {
    private final TelegramBotUpdatesListener telegramBotUpdatesListener;

    public NotificationsChecker(TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }

    @Scheduled(cron = "0 * * ? * *")
    public void everyMinuteCheckBD(){
        telegramBotUpdatesListener.checkCurrentTasks();
    }
}
