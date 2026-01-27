package demo.avoris.domain.model;

import java.time.LocalDate;


public record Search(
        String searchId,
        LocalDate checkIn,
        LocalDate checkOut,
        Hotel hotel,
        int count
) {
}