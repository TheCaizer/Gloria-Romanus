package part2Q1;

import java.time.LocalDate;
import java.util.ArrayList;


public class HotelBookingSystem{
    //a arraylist of all the hotels
    public ArrayList<Hotel> hotels;
    //constructor for the list
    public HotelBookingSystem(){
        this.hotels = new ArrayList<Hotel>();
    }
    //adds a new hotel to the array
    public void addHotel(Hotel h){
        hotels.add(h);
    }
    //adds a new room to the hotel if the room is added first then the hotel get
    // added to the arraylist of hotel
    public void addRoom(Room r, Hotel h){
        if(h.getRooms().size() == 0){
            addHotel(h);
        }
        h.addRoom(r);
    }
    //feeds a command and then uses a switch statement to call the command 
    //such as reuest change or cancel bookings
    public void Command(String json){}
    //the request command which takes in the id , the local date
    //for the starting and the checkout date and the size of the room needed
    //returns true if success and false if not
    public boolean request(int id, LocalDate start, LocalDate checkout, Size size){
        return false;
    }
    //the cancel command which takes in the id for what to cancel, returns true
    //if successful false if not
    public boolean cancel(int id){
        return false;
    }
    //the change command takes in the id , the new local date
    //for the starting and the checkout date and the size of the room needed
    //returns true if success and false if not
    //Finds the closest room to the checkout date and reassigns if possible
    public boolean change(int id, LocalDate start, LocalDate checkout, Size size){
        return false;
    }
    //goes through the array list and finds a room for request and the return it
    public Room findRoom(ArrayList<Hotel> hotels, Size size){
        Room r = new Room();
        return r;
    }
    public static void main(String []args){
    }
}