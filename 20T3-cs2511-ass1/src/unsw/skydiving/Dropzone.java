package unsw.skydiving;

import java.util.ArrayList;
import java.util.Objects;

public class Dropzone {

    ArrayList<Flight> flight;
    String name;
    /*
    * constructor
    * @param the name of dropzone
    */
    public Dropzone(String name){
        flight = new ArrayList<Flight>();
        this.name = name;
    }
    /*
    * default constructor
    */
    public Dropzone() {
    }

    public ArrayList<Flight> getFlight() {
        return this.flight;
    }

    public void setFlight(ArrayList<Flight> flight) {
        this.flight = flight;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Dropzone)) {
            return false;
        }
        Dropzone dropzone = (Dropzone) o;
        return Objects.equals(flight, dropzone.flight) && Objects.equals(name, dropzone.name);
    }

}