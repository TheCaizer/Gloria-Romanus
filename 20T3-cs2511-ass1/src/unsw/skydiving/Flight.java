package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Flight {

    String id;
    Dropzone dropzone;
    LocalDateTime starttime;
    LocalDateTime endtime;
    ArrayList<Jumps> flight_jumps = new ArrayList<Jumps>();
    int maxload;
    int currload = 0;

    /*
    * default constructor
    */
    public Flight() {
    }

    /*
    * constructor 
    */
    public Flight(String id, Dropzone dropzone, LocalDateTime starttime, LocalDateTime endtime, int maxload) {
        this.id = id;
        this.dropzone = dropzone;
        this.starttime = starttime;
        this.endtime = endtime;
        this.maxload = maxload;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Dropzone getDropzone() {
        return this.dropzone;
    }

    public void setDropzone(Dropzone dropzone) {
        this.dropzone = dropzone;
    }

    public LocalDateTime getStarttime() {
        return this.starttime;
    }

    public void setStarttime(LocalDateTime starttime) {
        this.starttime = starttime;
    }

    public LocalDateTime getEndtime() {
        return this.endtime;
    }

    public void setEndtime(LocalDateTime endtime) {
        this.endtime = endtime;
    }

    public int getMaxload() {
        return this.maxload;
    }

    public void setMaxload(int maxload) {
        this.maxload = maxload;
    }

    public int getCurrload(){
        return this.currload;
    }

    public void setCurrload(int currload){
        this.currload = currload;
    }

    /*
    * add one current load to the flight
    */
    public void addCurrload1(){
        this.currload = this.currload + 1;
    }

    /*
    * add two current load to the flight
    */
    public void addCurrload2(){
        this.currload = this.currload + 2;
    }

    public void setFlight_Jumps(ArrayList<Jumps> jumps){
        this.flight_jumps = jumps;
    }

    public ArrayList<Jumps> getFlight_Jumps(){
        return this.flight_jumps;
    }

    /*
    * method to add a jump to the list
    * @param jump the jump to add to the list
    */
    public void addJumps(Jumps jump){
        this.flight_jumps.add(jump);
    }
    /*
    * @return returns the number of avaliable seats
    */
    public int free_load(){
        return this.maxload - this.currload;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Flight)) {
            return false;
        }
        Flight flight = (Flight) o;
        return Objects.equals(id, flight.id) && Objects.equals(dropzone, flight.dropzone) && Objects.equals(starttime, flight.starttime) && Objects.equals(endtime, flight.endtime) && maxload == flight.maxload;
    }

}