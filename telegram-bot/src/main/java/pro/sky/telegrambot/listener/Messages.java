package pro.sky.telegrambot.listener;

public enum Messages {
    SORRY("Скорее всего что-то пошло и я не смог создать напоминание, попробуй снова"),
    START_MESSAGE("Привет! Я бот для создания напоминаний Alex, но ты можешь звать меня Саня"),
    INFO_MESSAGE("Для создания напоминания достаточно прислать мне сообщение " +
            "формата: \"дата время текст напоминания\" \nНапример: \n31.11.2022 23:59 Включить новогоднее обращение президента"),
    ANSWER_1("Отлично, так и запишем..."),
    ANSWER_2("Надеюсь и я не забуду об этом "),
    ANSWER_3("Да у тебя грандиозные планы, надеюсь я их не испорчу "),

    NOTIFICATION_MSG("Кажется в твоих планах было:\n");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
