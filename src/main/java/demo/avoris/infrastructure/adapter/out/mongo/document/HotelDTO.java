package demo.avoris.infrastructure.adapter.out.mongo.document;

import java.util.List;

public record HotelDTO(
        String hotelId,
        String checkIn,
        String checkOut,
        List<Integer> ages
) {}
