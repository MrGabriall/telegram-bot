package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class NotificationService {

    private final TaskRepository taskRepository;

    public NotificationService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public NotificationTask addTask(NotificationTask task) {
        return taskRepository.save(task);

    }

    public Collection<NotificationTask> getAllTasks(Long userID) {
        return taskRepository.getNotificationTasksByUserID(userID);
    }

    public Collection<NotificationTask> findCurrentTasks(LocalDateTime current){
        return taskRepository.findNotificationTasksByNotification(current);
    }
}
