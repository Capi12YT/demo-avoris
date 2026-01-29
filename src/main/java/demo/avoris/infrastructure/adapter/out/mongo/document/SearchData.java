package demo.avoris.infrastructure.adapter.out.mongo.document;

import java.util.List;

public record SearchData(
        String hotelId,
        String checkIn,
        String checkOut,
        List<Integer> ages
) {
    public SearchData {
        ages = List.copyOf(ages);
    }
}
