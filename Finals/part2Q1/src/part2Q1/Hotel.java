package part2Q1;

import java.util.ArrayList;

public class Hotel {
    //Number of rooms in the hotel
    int numRooms;
    //Name of the hotel
    String Name;
    //Arraylist of all the rooms in the hotel
    ArrayList<Room> rooms = new ArrayList<Room>();
    //Array list of the booking for the hotel
    ArrayList<Booking> booking = new ArrayList<Booking>();


    //default constructor
    public Hotel(){

    }
    //basic contructor for the hotel
    public Hotel(String n, int room){
        this.numRooms = room;
        this.Name = n;
    }
    //adds a room to the room arraylist of the hotel for manager
    //also checks the number of room is less than the total room specified
    //when constructing hotel
    public void addRoom(Room r){
        rooms.add(r);
    }
    //a function that finds a room in the hotel with the right size
    //iterates through the room list and then finds the room with right size
    public Room findRoom(Size size){
        Room r = new Room();
        return r;
    }
    //sorts the room in order of room creation, then booking start date.
    public ArrayList<Room> sortRooms(ArrayList<Room> room){
        return this.rooms;
    }
    //sorts the room using the sortRooms method and then returns the sorted list
    //Then it prints out if each room is occupied using the isOccupied method
    //To each room
    public ArrayList<Room> displayRooms(){
        return this.rooms;
    }
    //getter for room
    public ArrayList<Room> getRooms(){
        return this.rooms;
    }
}
