package part2Q1;

public class Room {
    // The room number of the room
    int roomNumber;
    // The size of the room (small, medium or large)
    Size size;
    //The hotel the room is associated with
    Hotel hotel;
    //A boolean to check if the room is occupoed already until request
    Boolean occupied;
    //default consructor
    public Room(){

    }
    //Constructor for the room
    public Room(int n, Size size, Hotel h){
        this.roomNumber = n;
        this.size = size;
        this.hotel = h;
        this.occupied = false;
    }
    //simple getter for size
    public Size getSize(){
        return this.size;
    }
    //simple getter for hotel
    public Hotel getHotel(){
        return this.hotel;
    }
     //simple getter for roomnum
    public int getRoomNum(){
        return this.roomNumber;
    }
    //simple getter for occupied
    public boolean isOccupied(){
        return this.occupied;
    }
    //books or unbooks the room
    public void BookRoom(){
        this.occupied = !occupied;
    }
}
