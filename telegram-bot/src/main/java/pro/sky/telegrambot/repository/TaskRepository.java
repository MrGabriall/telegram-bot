package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TaskRepository extends JpaRepository<NotificationTask, Long> {

    Collection<NotificationTask> getNotificationTasksByUserID(Long userID);

    Collection<NotificationTask> findNotificationTasksByNotificationBetween(LocalDateTime start, LocalDateTime range);
}
