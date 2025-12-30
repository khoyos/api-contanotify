package co.java.app.contanotify.repository;

import co.java.app.contanotify.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    Optional<Subscription> findByPublicId(String publicId);
}
