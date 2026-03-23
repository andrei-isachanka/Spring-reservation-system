package com.example.reservation_system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class ReservationNotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReservationNotificationConsumer.class);

    @KafkaListener(topics = "reservation-notifications", groupId = "notification-group")
    public void consume(String message){
        log.info("Received message {}", message);
        log.info("email with new status sent to user");
    }

}
