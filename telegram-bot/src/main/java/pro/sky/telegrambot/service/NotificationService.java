package pro.sky.telegrambot.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.TaskNotFoundException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Service
public class NotificationService {

    private TaskRepository taskRepository;

    public NotificationService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public NotificationTask addTask(NotificationTask task) {
        return taskRepository.save(task);

    }

    public NotificationTask findTask(NotificationTask task) {
        return taskRepository.findById(task.getId()).orElseThrow(TaskNotFoundException::new);
    }

    public NotificationTask editTask(Long id, NotificationTask task) {
        NotificationTask notificationTask = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        notificationTask.setNotification(task.getNotification());
        notificationTask.setMessage(task.getMessage());
        return notificationTask;
    }

    public void deleteTask(Long id) {
        NotificationTask task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        taskRepository.delete(task);
    }

    public Collection<NotificationTask> getAllTasks(Long userID) {
        return taskRepository.getNotificationTasksByUserID(userID);
    }

    public Collection<NotificationTask> findCurrentTasks(LocalDateTime start, LocalDateTime range){
        return taskRepository.findNotificationTasksByNotificationBetween(start, range);
    }
}
