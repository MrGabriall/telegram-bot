package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final String REGEX = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
    private final Pattern PATTERN = Pattern.compile(REGEX);


    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationService notificationService;

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationService notificationService, TelegramBot telegramBot) {
        this.notificationService = notificationService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (update.message() != null && !update.message().text().isBlank()) {
                Long userId = update.message().chat().id();
                String text = update.message().text();
                switch (text) {
                    case "/start":
                        sendMessage(userId, Messages.START_MESSAGE.getMessage());
                        break;
                    case "/info":
                        sendMessage(userId, Messages.INFO_MESSAGE.getMessage());
                        break;
                    case "/get_all":
                        sendMessage(userId, allTasks(userId));
                        break;
                    default:
                        sendMessage(userId, checkMsgAndAddTask(userId, text));
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void checkCurrentTasks() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> currentTasks = notificationService.findCurrentTasks(time);
        if (!currentTasks.isEmpty()) {
            for (NotificationTask task : currentTasks) {
                telegramBot.execute(new SendMessage(task.getUserID(), Messages.NOTIFICATION_MSG.getMessage()));
                telegramBot.execute(new SendMessage(task.getUserID(), task.getMessage()));
            }
        }
    }

    private String checkMsgAndAddTask(Long userID, String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            NotificationTask task = parseTask(userID, matcher);
            notificationService.addTask(task);
            return getAnswer(text);
        }
        return Messages.SORRY.getMessage();
    }

    private NotificationTask parseTask(Long userID, Matcher matcher) {
        String notification_text = matcher.group(3);
        String dateAndTime = matcher.group(1);
        LocalDateTime notification = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        return new NotificationTask(userID, notification_text, notification);
    }

    private String getAnswer(String text) {
        if (text.length() <= 32) {
            return Messages.ANSWER_1.getMessage();
        } else if (text.length() >= 64) {
            return Messages.ANSWER_3.getMessage();
        } else {
            return Messages.ANSWER_2.getMessage();
        }
    }

    private String allTasks(Long userID) {
        Collection<NotificationTask> tasks = notificationService.getAllTasks(userID);
        StringBuilder stringBuilder = new StringBuilder();
        for (NotificationTask task : tasks) {
            stringBuilder.append(task.getNotification())
                    .append(" ")
                    .append(task.getMessage())
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    private void sendMessage(Long userID, String out) {
        telegramBot.execute(new SendMessage(userID, out));
    }
}
