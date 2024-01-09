package maqlu.maqlulibrary.services;

import maqlu.maqlulibrary.entities.Notification;
import maqlu.maqlulibrary.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notifRepo;

    public void save (Notification notification) {
        notifRepo.save(notification);
    }

    public void saveById (Long id) {
        Notification notification = notifRepo.findById(id).get();
        notifRepo.save(notification);
    }

    public List<Notification> findAll(){
        List<Notification> notifications = (ArrayList<Notification>) notifRepo.findAll();
        return notifications;
    }

    public void deleteById(Long id) {
        notifRepo.deleteById(id);
    }
}
