package com.example.reservation_system;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;
    private final ReservationNotificationProducer notificationProducer;

    public ReservationService(ReservationRepository repository,
                              ReservationNotificationProducer notificationProducer) {
        this.repository = repository;
        this.notificationProducer = notificationProducer;
    }



    @Transactional
    public void cancelReservation(Long id) {

        var reservation = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no reservation with id: " + id));

        if(reservation.getStatus().equals(ReservationStatus.APPROVED)){
            throw new IllegalStateException("Cannot cancel approved reservation");
        }

        if(reservation.getStatus().equals(ReservationStatus.CANCELLED)){
            throw new IllegalStateException("Reservation is already canceled");
        }

        repository.setStatus(id, ReservationStatus.CANCELLED);
        notificationProducer.sendStatusChangeNotification(id, ReservationStatus.CANCELLED);
        log.info("Successfully cancelled reservation: id={}", id);
    }


    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {

        if(!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())){
            throw new IllegalArgumentException("Start date must be earlier than end date");
        }

        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no reservation with id: " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify with status " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        var updatedReservation = repository.save(reservationToSave);

        return toDomainReservation(updatedReservation);
    }



    public Reservation getReservationById(Long id){
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no reservation with id: " + id));
        return toDomainReservation(reservationEntity);
    }



    public List<Reservation> findAllReservation() {

        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream().map(this::toDomainReservation).toList();
    }


    @Transactional
    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }

        if(!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())){
            throw new IllegalArgumentException("Start date must be earlier than end date");
        }

        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }


    @Transactional
    public Reservation approveReservation(Long id) {

        var reservationEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no reservation with id: " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot approve with status " + reservationEntity.getStatus());
        }

        var isConflict = isReservationConflict(reservationEntity);
        if (isConflict){
            throw new IllegalStateException("Cannot approve because of conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        notificationProducer.sendStatusChangeNotification(id, ReservationStatus.APPROVED);

        return toDomainReservation(reservationEntity);
    }



    private boolean isReservationConflict(ReservationEntity reservation){

        return repository.existsConflictingReservation(reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getId());
    }



    private Reservation toDomainReservation(ReservationEntity reservation){
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }
}
