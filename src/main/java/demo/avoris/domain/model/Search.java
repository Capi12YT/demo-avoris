package demo.avoris.domain.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;


public record Search(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages,
        int count
)  {
    public Search(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages, int count) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
        this.count = count;
    }

}