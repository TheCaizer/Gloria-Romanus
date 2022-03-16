package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Jumps{

    String type;
    String id;
    LocalDateTime starttime;
    LocalDateTime endtime;
    Flight flight = null;
    ArrayList<Skydivers> skydivers = new ArrayList<Skydivers>();
    int num_divers;

    /*
    * constructor that takes one skydiver for training or tandem jumps
    * num diver is two for passenger/trainee and instructor/tandem-master
    */
    public Jumps(String type, String id, LocalDateTime starttime, Skydivers skydivers){
        this.type = type;
        this.id = id;
        this.starttime = starttime;
        this.skydivers.add(skydivers);
        this.num_divers = 2;
    }
    /*
    * constructor for fun jump an array of people
    */
    public Jumps(String type, String id, LocalDateTime starttime, ArrayList<Skydivers> skydivers){
        this.type = type;
        this.id = id;
        this.starttime = starttime;
        this.skydivers = skydivers;
        this.num_divers = this.skydivers.size();
    }
    /*
    * default constructor
    */
    public Jumps() {
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStarttime() {
        return this.starttime;
    }

    public void setEndtime(LocalDateTime endtime) {
        this.endtime = endtime;
    }

    public LocalDateTime getEndtime() {
        return this.endtime;
    }

    public void setStarttime(LocalDateTime starttime) {
        this.starttime = starttime;
    }

    public void setFlight(Flight flight){
        this.flight = flight;
    }

    public Flight getFlight(){
        return this.flight;
    }
    
    public ArrayList<Skydivers> getSkydivers() {
        return this.skydivers;
    }

    /*
    * @return returns the  number of skydivers in the jump
    */
    public int length(){
        return this.skydivers.size();
    }

    public void setSkydivers(ArrayList<Skydivers> skydivers) {
        this.skydivers = skydivers;
    }
    
    public void setNum_divers(int divers){
        this.num_divers = divers;
    }

    public int getNum_divers(){
        return this.num_divers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Jumps)) {
            return false;
        }
        Jumps jumps = (Jumps) o;
        return Objects.equals(type, jumps.type) && Objects.equals(id, jumps.id) && Objects.equals(starttime, jumps.starttime) && Objects.equals(skydivers, jumps.skydivers);
    }

}