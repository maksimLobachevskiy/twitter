package com.twitterdan.service;

import com.twitterdan.dao.NotificationRepository;
import com.twitterdan.domain.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void saveNotification(Notification notification){
        notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(Long id) {

        return notificationRepository.getNotificationById(id);
    }

    public List<Notification> getAllNotifications() {

        return notificationRepository.findAll();
    }

    public void deactivateNotificationById(Long id) {
       Optional<Notification> optNotif = notificationRepository.getNotificationById(id);
    }

    public void deleteNotificationById(Long id) {
//        notificationRepository.deleteNotificationById(id);
        notificationRepository.deleteById(id);
    }

}
