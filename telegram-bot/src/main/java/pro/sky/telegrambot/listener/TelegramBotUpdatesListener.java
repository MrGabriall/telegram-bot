package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final String PATTERN = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
    private final String SORRY = "Скорее всего что-то пошло и я не смог создать напоминание, попробуй снова";
    private final String START = "/start";
    private final String INFO = "/info";
    private final String EDIT = "/edit";
    private final String DELETE = "/delete";
    private final String GET_ALL = "/get_all";
    private final String COMMANDS = "/commands";
    private final String COMMANDS_MESSAGE = "!!!commands!!!"; //for update
    private final String START_MESSAGE = "Привет! Я бот для создания напоминаний Alex, но ты можешь звать меня Саня";
    private final String INFO_MESSAGE = "Для создания напоминания достаточно прислать мне сообщение " +
            "формата: \"дата время текст напоминания\" \nНапример: \n31.11.2022 23:59 Включить новогоднее обращение президента";
    private final String ANSWER_FOR_EDIT_1 = "Пришли напоминание, которое нужно изменить";
    private final String ANSWER_FOR_EDIT_2 = "Отлично, теперь пришли задачу в измененном виде";
    private final String ANSWER_FOR_DELETE_1 = "Пришли напоминание, которое нужно удалить";
    private final String ANSWER_FOR_DELETE_2 = "Сделаем вид, будто её никогда и не существовало...";
    private final String ANSWER_1 = "Отлично, так и запишем...";
    private final String ANSWER_2 = "Надеюсь и я не забуду об этом ";
    private final String ANSWER_3 = "Да у тебя грандиозные планы, надеюсь я их не испорчу ";


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
                    case START:
                        sendMessage(userId, START_MESSAGE);
                        break;
                    case INFO:
                        sendMessage(userId, INFO_MESSAGE);
                        break;
                    case COMMANDS:
                        sendMessage(userId, COMMANDS_MESSAGE);
                        break;
                    case GET_ALL:
                        sendMessage(userId, allTasks(userId));
                        break;
                    case EDIT:


                        break;
                    default:
                        //sendMessage(userId, checkMsgAndAddTask(userId, text));
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 * * ? * *")
    public void everyMinuteCheckBD(){
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime range = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1L);

        checkCurrentTask(notificationService.findCurrentTasks(start, range));

    }

    private void checkCurrentTask(Collection<NotificationTask> currentTasks){

    }

    private String checkMsgAndAddTask(Long userID, String text) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            NotificationTask task = parseTask(userID, matcher);
            notificationService.addTask(task);
            return getANSWER(text);
        }
        return SORRY;
    }

    private NotificationTask parseTask(Long userID, Matcher matcher) {
        String notification_text = matcher.group(3);
        String dateAndTime = matcher.group(1);
        LocalDateTime notification = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        return new NotificationTask(null, userID, notification_text, notification);
    }

    private String getANSWER(String text) {
        if (text.length() <= 12) {
            return ANSWER_1;
        } else if (text.length() >= 30) {
            return ANSWER_3;
        } else {
            return ANSWER_2;
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
