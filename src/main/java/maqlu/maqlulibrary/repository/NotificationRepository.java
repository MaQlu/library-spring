package maqlu.maqlulibrary.repository;

import maqlu.maqlulibrary.entities.Notification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification,Long> {

}
