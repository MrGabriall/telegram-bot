package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table
public class NotificationTask {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    private Long userID;
    private String message;
    private LocalDateTime notification;

    public NotificationTask() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(userID, that.userID) && Objects.equals(message, that.message) && Objects.equals(notification, that.notification);
    }

    public NotificationTask(Long id, Long userID, String message, LocalDateTime notification) {
        this.id = id;
        this.userID = userID;
        this.message = message;
        this.notification = notification;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userID, message, notification);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotification() {
        return notification;
    }

    public void setNotification(LocalDateTime notification) {
        this.notification = notification;
    }
}
