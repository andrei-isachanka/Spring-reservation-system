package com.example.reservation_system;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
            ){
        log.info("Called getReservationById with ID = " + id);
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationById(id));
        //return reservationService.getReservationById(id);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(){
        log.info("Called getAllReservations");
        return ResponseEntity.ok(reservationService.findAllReservation());
        //return reservationService.findAllReservation();
    }


    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate){
        log.info("Called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
        //return reservationService.createReservation(reservationToCreate);
    }

}
