package part2Q1;

import java.time.LocalDate;

public class Booking {
    //the hotel which the booking is targeted at
    public Hotel h;
    // The unique id of the booking
    public int id; 
    //The toom which the booking assinged to
    public Room rooms;
    //the start date and end date
    public LocalDate start;
    public LocalDate end;

    public Booking(LocalDate start, LocalDate end, Room r, Hotel h, int id){
        this.h = h;
        this.id = id;
        this.rooms = r;
        this.start = start;
        this.end = end;
    }
}
