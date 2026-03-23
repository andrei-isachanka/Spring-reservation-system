package com.example.reservation_system;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationNotificationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "reservation-notifications";

    public ReservationNotificationProducer(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatusChangeNotification(Long id, ReservationStatus newStatus){
        String message = String.format("Reservation %d changed status to %s", id, newStatus.name());
        kafkaTemplate.send(TOPIC, message);
    }
}
