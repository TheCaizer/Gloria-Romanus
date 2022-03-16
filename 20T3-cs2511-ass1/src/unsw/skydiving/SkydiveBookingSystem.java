package unsw.skydiving;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Skydive Booking System for COMP2511.
 *
 * A basic prototype to serve as the "back-end" of a skydive booking system. Input
 * and output is in JSON format.
 *
 * @author Jackie Cai
 *
 */

public class SkydiveBookingSystem {

    /*
     * Constructs a skydive booking system. Initially, the system contains no flights, skydivers, jumps or dropzones
    */
    public ArrayList<Flight> flight;
    public ArrayList<Dropzone> dropzone;
    public ArrayList<Skydivers> skydivers;
    public ArrayList<Jumps> jumps;

    public SkydiveBookingSystem(){
        flight = new ArrayList<Flight>(); 
        dropzone = new ArrayList<Dropzone>();
        skydivers = new ArrayList<Skydivers>();
        jumps = new ArrayList<Jumps>();
    }

    private void processCommand(JSONObject json) {

        switch (json.getString("command")) {
        /*
        * gets the id, maxload, starttime, endtime, name of dropzone
        * then checks if there is an dropzone in the dropzine array list
        * if there is not it makes a new dropzone and add to the dropzone list
        * and creates a new flight for the list and adds to the list
        * if there is an existing dropzone it makes a new flight and adds the 
        * flight to the flight list
        */
        case "flight":
            String id = json.getString("id");
            int maxload = json.getInt("maxload");
            LocalDateTime starttime = LocalDateTime.parse(json.getString("starttime"));
            LocalDateTime endtime = LocalDateTime.parse(json.getString("endtime"));
            String dropzone_name = json.getString("dropzone");
            Dropzone dropzone = findDropzone(dropzone_name, this.dropzone);
            // If you cant find a dropzone then make a new one
            if(dropzone == null){
                Dropzone new_dropzone = new Dropzone(dropzone_name);
                this.dropzone.add(new_dropzone);
                Flight newFlight = new Flight(id, new_dropzone, starttime, endtime, maxload);
                flight.add(newFlight);
                new_dropzone.getFlight().add(newFlight);
                break;
            }

            else{
                Flight newFlight = new Flight(id, dropzone, starttime, endtime, maxload);
                flight.add(newFlight);
                dropzone.getFlight().add(newFlight);
                break;
            }
        /*
        * gets the skydiver, licence and teh dropzone string and then checks if
        * if there is an existing drop zoe if there is then adds skydivers to
        * the list if there isnt make a new dropzone and adds the dropzone and skydiver
        * to the list
        */
        case "skydiver":
            String skydiver = json.getString("skydiver");
            String licence = json.getString("licence");
            // When there is a dropzone
            if(json.has("dropzone")){
                String skydiver_dropzone = json.getString("dropzone");
                Dropzone construct_dropzone = findDropzone(skydiver_dropzone, this.dropzone);
                if(construct_dropzone == null){
                    Dropzone new_dropzone = new Dropzone(skydiver_dropzone);
                    this.dropzone.add(new_dropzone);
                    Skydivers newSkydivers = new Skydivers(skydiver, licence, new_dropzone);
                    skydivers.add(newSkydivers);
                    break;
                }

                else{
                    Skydivers newSkydivers = new Skydivers(skydiver, licence, construct_dropzone);
                    skydivers.add(newSkydivers);
                    break;
                }
            }

            Skydivers newSkydivers = new Skydivers(skydiver, licence);
            skydivers.add(newSkydivers);
            break;
        /*
        * gets the type,id and skydivers
        * checks for skydivers passenger or trainee
        * adds depending on the skydivers (add the whole array or just add single)
        */
        case "request":
            String type = json.getString("type");
            String request_id = json.getString("id");
            LocalDateTime request_starttime = LocalDateTime.parse(json.getString("starttime"));

            //sorts the flights in ascending order of date
            Collections.sort(this.flight, new Comparator<Flight>(){
                public int compare(Flight f1, Flight  f2) {
                    return f1.getStarttime().compareTo(f2.getStarttime());
                }
            });

            if(json.has("skydivers")){
                int success = 0;
                JSONArray sky_array = json.getJSONArray("skydivers");
                ArrayList<Skydivers> new_sky_array = new ArrayList<Skydivers>();
                //Find all the skydiver in JSONArray and add to a normal ArrayList
                for(int i = 0; i < sky_array.length(); i++){
                    String skydiver_name = sky_array.getString(i);
                    Skydivers tempSkydivers = findSkydivers(skydiver_name, this.skydivers);
                    new_sky_array.add(tempSkydivers);
                }
                for(int i = 0; i < flight.size();i++){
                    Flight flight_loop = flight.get(i);
                    LocalDateTime flight_time = flight_loop.getStarttime();
                    // Since its sorted the earliest time would be the first one to check
                    if((flight_time.equals(request_starttime) || flight_time.isAfter(request_starttime)) && flight_time.getDayOfYear() == request_starttime.getDayOfYear()){
                        if(checkSkydivers(new_sky_array, flight_time)){
                            //Check Load
                            if(new_sky_array.size() + flight_loop.getCurrload() > flight_loop.getMaxload()){
                                continue;
                            }
                            else{
                                Jumps new_jump = new Jumps(type, request_id, request_starttime, new_sky_array);
                                jumps.add(new_jump);
                                flight_loop.addJumps(new_jump);
                                for(int j = 0; j < new_sky_array.size(); j++){
                                    Skydivers Set_Skydivers = new_sky_array.get(j);
                                    Set_Skydivers.setParticipating(true);
                                    Set_Skydivers.addNumJump();
                                    //If student no need to pack else need 
                                    if(Set_Skydivers.getLicence().equals("student")){
                                        Set_Skydivers.setLastJumpBefore(Set_Skydivers.getLast_Jump());
                                        Set_Skydivers.setLastJump(flight_loop.getEndtime());
                                    }
                                    else{
                                        Set_Skydivers.setLastJumpBefore(Set_Skydivers.getLast_Jump());
                                        Set_Skydivers.setLastJump(flight_loop.getEndtime().plusMinutes(10));
                                    }
                                }
                                //add to currload 
                                new_jump.setFlight(flight_loop);
                                flight_loop.setCurrload(flight_loop.getCurrload() + new_sky_array.size());
                                JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                                success = 1;
                                //sort by lexiconical order
                                Collections.sort(new_jump.getSkydivers(), new Comparator<Skydivers>(){
                                    public int compare(Skydivers f1, Skydivers  f2) {
                                        return f1.getName().compareTo(f2.getName());
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
                if(success == 1){
                    break;
                }
                JSONRejected();
                break;
            }

            else if (json.has("passenger")){
                String passenger = json.getString("passenger");
                Skydivers passenger2 = findSkydivers(passenger, skydivers);

                int success = 0;

                for(int i = 0; i < flight.size();i++){
                    Flight flight_loop = flight.get(i);
                    //minus 5 minute for debriefing
                    LocalDateTime flight_time = flight_loop.getStarttime().minusMinutes(5);
                    if(2 + flight_loop.getCurrload() > flight_loop.getMaxload()){
                        continue;
                    }
                    // Since its sorted the earliest time would be the first one to check
                    else if((flight_time.equals(request_starttime) || flight_time.isAfter(request_starttime)) && flight_time.getDayOfYear() == request_starttime.getDayOfYear()){
                        // Find the instrctor with the least amount of jumps
                        int min_jump = 999;
                        Skydivers instructor = null;
                        for(i = 0;i < skydivers.size();i++){
                            //find only tandem master
                            if(skydivers.get(i).getLicence().equals("tandem-master") && (skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(passenger2)){
                                if(skydivers.get(i).getNumJump() < min_jump){
                                    instructor = skydivers.get(i);
                                    if(checkSkydiversIndividual(instructor, flight_time)){
                                        min_jump = skydivers.get(i).getNumJump();
                                        success = 1;
                                    }
                                }
                            }
                        }
                        if(success == 1){
                            passenger2.setParticipating(true);
                            instructor.setParticipating(true);
                            passenger2.setLastJumpBefore(passenger2.getLast_Jump());
                            instructor.setLastJumpBefore(instructor.getLast_Jump());
                            passenger2.setLastJump(flight_loop.getEndtime());
                            instructor.setLastJump(flight_loop.getEndtime().plusMinutes(10));
                            passenger2.addNumJump();
                            instructor.addNumJump();
                            Jumps new_jump = new Jumps(type, request_id, request_starttime, passenger2);
                            new_jump.getSkydivers().add(instructor);
                            new_jump.setFlight(flight_loop);
                            flight_loop.addJumps(new_jump);
                            flight_loop.addCurrload2();
                            jumps.add(new_jump);
                            JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                            break;
                        }
                    }
                }
                if(success == 0){
                    JSONRejected();
                    break;
                }
                break;
            }

            else if (json.has("trainee")){
                Skydivers trainee = findSkydivers(json.getString("trainee"), skydivers);

                int success = 0;

                for(int i = 0; i < flight.size();i++){
                    Flight flight_loop = flight.get(i);
                    LocalDateTime flight_time = flight_loop.getStarttime();
                    if(2 + flight_loop.getCurrload() > flight_loop.getMaxload()){
                        continue;
                    }
                    // Since its sorted the earliest time would be the first one to check
                    else if((flight_time.equals(request_starttime) || flight_time.isAfter(request_starttime)) && flight_time.getDayOfYear() == request_starttime.getDayOfYear()){
                         // Find the instrctor with the least amount of jumps
                        int min_jump = 999;
                        Skydivers instructor = null;
                        for(i = 0;i < skydivers.size();i++){
                            if((skydivers.get(i).getLicence().equals("instructor") || skydivers.get(i).getLicence().equals("tandem-master"))){
                                if((skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(trainee)){
                                    instructor = skydivers.get(i);
                                    if(skydivers.get(i).getNumJump() < min_jump){
                                        if(checkSkydiversIndividual(instructor, flight_time)){
                                            instructor = skydivers.get(i);
                                            min_jump = skydivers.get(i).getNumJump();
                                            success = 1;
                                        }
                                    }
                                }
                            }
                        }
                        if(success == 1){
                            trainee.setParticipating(true);
                            instructor.setParticipating(true);
                            trainee.setLastJumpBefore(trainee.getLast_Jump());
                            instructor.setLastJumpBefore(instructor.getLast_Jump());
                            //Non student pack their parachute and debriefing
                            if(!trainee.getLicence().equals("student")){
                                trainee.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            }
                            else{
                                trainee.setLastJump(flight_loop.getEndtime().plusMinutes(15));
                            }
                            //Instructor always pack and debrief
                            instructor.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            trainee.addNumJump();
                            instructor.addNumJump();
                            Jumps new_jump = new Jumps(type, request_id, request_starttime, trainee);
                            new_jump.getSkydivers().add(instructor);
                            new_jump.setFlight(flight_loop);
                            flight_loop.addJumps(new_jump);
                            flight_loop.addCurrload2();
                            jumps.add(new_jump);
                            JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                            break;
                        }
                    }
                }
                if(success == 0){
                    JSONRejected();
                    break;
                }
                break;
            }
        
        case "change":
            String change_type = json.getString("type");
            String change_id = json.getString("id");
            Jumps change_jump = findJumps(change_id, jumps);

            LocalDateTime change_starttime = LocalDateTime.parse(json.getString("starttime"));

            if(json.has("skydivers")){
                int success = 0;
                Flight flight_loop = null;
                JSONArray sky_array = json.getJSONArray("skydivers");
                ArrayList<Skydivers> new_sky_array = new ArrayList<Skydivers>();
                for(int i = 0; i < sky_array.length(); i++){
                    String skydiver_name = sky_array.getString(i);
                    Skydivers tempSkydivers = findSkydivers(skydiver_name, this.skydivers);
                    new_sky_array.add(tempSkydivers);
                }
                for(int i = 0; i < flight.size();i++){
                    flight_loop = flight.get(i);
                    LocalDateTime flight_time = flight_loop.getStarttime();
                    // Since its sorted the earliest time would be the first one to check
                    if((flight_time.equals(change_starttime) || flight_time.isAfter(change_starttime)) && flight_time.getDayOfYear() == change_starttime.getDayOfYear()){
                        if(checkSkydiversChange(new_sky_array, flight_time)){
                            if(flight_loop.equals(change_jump.getFlight())){
                                //Check if its the same flight then minus previous load and add current load
                                if(new_sky_array.size() + flight_loop.getCurrload() - change_jump.length() > flight_loop.getMaxload()){
                                    continue;
                                }
                                else{
                                    success = 1;
                                    break;
                                }
                            }
                            else{
                                if(new_sky_array.size() + flight_loop.getCurrload() > flight_loop.getMaxload()){
                                    continue;
                                }
                                else{
                                    success = 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(success == 1){
                    //found flight delete from the array and add new
                    Jumps delete = findJumps(change_id, jumps);
                    if(delete == null){
                        break;
                    }
                    //removes the jump from the list
                    jumps.remove(delete);
                    //finds the flight from the delete flight's id from the flight list
                    Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
                    delete_from_flight.getFlight_Jumps().remove(delete);
                    Jumps new_jump = new Jumps(change_type, change_id, change_starttime, new_sky_array);
                    jumps.add(new_jump);
                    flight_loop.addJumps(new_jump);
                    for(int j = 0; j < new_sky_array.size(); j++){
                        Skydivers Set_Skydivers = new_sky_array.get(j);
                        Set_Skydivers.setParticipating(true);
                        if(Set_Skydivers.getLicence().equals("student")){
                            Set_Skydivers.setLastJump(flight_loop.getEndtime());
                        }
                        else{
                            Set_Skydivers.setLastJump(flight_loop.getEndtime().plusMinutes(10));
                        }
                    }
                    new_jump.setFlight(flight_loop);
                    if(flight_loop.equals(change_jump.getFlight())){
                        flight_loop.setCurrload(flight_loop.getCurrload() + new_sky_array.size() - change_jump.length());
                    }
                    else{
                        flight_loop.setCurrload(flight_loop.getCurrload() + new_sky_array.size());
                        delete.getFlight().setCurrload(delete.getFlight().getCurrload() - delete.getNum_divers());
                    }
                    //sort by lexiconical order
                    Collections.sort(new_jump.getSkydivers(), new Comparator<Skydivers>(){
                        public int compare(Skydivers f1, Skydivers  f2) {
                            return f1.getName().compareTo(f2.getName());
                        }
                    });
                    JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                    break;
                }
                JSONRejected();
                break;
            }
            else if(json.has("passenger")){
                String passenger = json.getString("passenger");
                Skydivers passenger2 = findSkydivers(passenger, skydivers);

                int success = 0;
                Flight flight_loop = null;

                for(int i = 0; i < flight.size();i++){
                    flight_loop = flight.get(i);
                    //minus 5 minute for debriefing
                    LocalDateTime flight_time = flight_loop.getStarttime().minusMinutes(5);
                    if(flight_loop.equals(change_jump.getFlight())){
                        if(2 + flight_loop.getCurrload() - change_jump.length() > flight_loop.getMaxload()){
                            continue;
                        }
                        else if((flight_time.equals(change_starttime) || flight_time.isAfter(change_starttime)) && flight_time.getDayOfYear() == change_starttime.getDayOfYear()){
                            // Find the instrctor with the least amount of jumps
                           int min_jump = 999;
                           Skydivers instructor = null;
                           for(i = 0;i < skydivers.size();i++){
                               if(skydivers.get(i).getLicence().equals("tandem-master") && (skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(passenger2)){
                                   if(skydivers.get(i).getNumJump() < min_jump){
                                       instructor = skydivers.get(i);
                                       if(checkSkydiversIndividual(instructor, flight_time)){
                                           min_jump = skydivers.get(i).getNumJump();
                                           success = 1;
                                       }
                                   }
                               }
                           }
                           Jumps delete = findJumps(change_id, jumps);
                           if(delete == null){
                               break;
                           }
                           //removes the jump from the list
                           jumps.remove(delete);
                           //finds the flight from the delete flight's id from the flight list
                           Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
                           delete_from_flight.getFlight_Jumps().remove(delete);
                           delete_from_flight.setCurrload(delete_from_flight.getCurrload() - delete.length());
                           
                           passenger2.setLastJump(flight_loop.getEndtime());
                           instructor.setLastJump(flight_loop.getEndtime().plusMinutes(10));
                           Jumps new_jump = new Jumps(change_type, change_id, change_starttime, passenger2);
                           new_jump.getSkydivers().add(instructor);
                           new_jump.setFlight(flight_loop);
                           flight_loop.addJumps(new_jump);
                           if(flight_loop.equals(change_jump.getFlight())){
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2 - change_jump.length());
                            }
                            else{
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2);
                                delete.getFlight().setCurrload(delete.getFlight().getCurrload() - delete.getNum_divers());
                            }
                           jumps.add(new_jump);
                           JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                           break;
                        }
                    }
                    else{
                        if(2 + flight_loop.getCurrload() > flight_loop.getMaxload()){
                            continue;
                        }
                        else if((flight_time.equals(change_starttime) || flight_time.isAfter(change_starttime)) && flight_time.getDayOfYear() == change_starttime.getDayOfYear()){
                            // Find the instrctor with the least amount of jumps
                           int min_jump = 999;
                           Skydivers instructor = null;
                           for(i = 0;i < skydivers.size();i++){
                               if(skydivers.get(i).getLicence().equals("tandem-master") && (skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(passenger2)){
                                   if(skydivers.get(i).getNumJump() < min_jump){
                                       instructor = skydivers.get(i);
                                       if(checkSkydiversIndividual(instructor, flight_time)){
                                           min_jump = skydivers.get(i).getNumJump();
                                           success = 1;
                                       }
                                   }
                               }
                           }
                           Jumps delete = findJumps(change_id, jumps);
                           if(delete == null){
                               break;
                           }
                           //removes the jump from the list
                           jumps.remove(delete);
                           //finds the flight from the delete flight's id from the flight list
                           Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
                           delete_from_flight.getFlight_Jumps().remove(delete);
                           delete_from_flight.setCurrload(delete_from_flight.getCurrload() - delete.length());

                           passenger2.setLastJump(flight_loop.getEndtime());
                           instructor.setLastJump(flight_loop.getEndtime().plusMinutes(10));
                           Jumps new_jump = new Jumps(change_type, change_id, change_starttime, passenger2);
                           new_jump.getSkydivers().add(instructor);
                           new_jump.setFlight(flight_loop);
                           flight_loop.addJumps(new_jump);
                           if(flight_loop.equals(change_jump.getFlight())){
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2 - change_jump.length());
                            }
                            else{
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2);
                                delete.getFlight().setCurrload(delete.getFlight().getCurrload() - delete.getNum_divers());
                            }
                           jumps.add(new_jump);
                           JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                           break;
                        }
                    }
                    if(success == 0){
                        JSONRejected();
                        break;
                    }  
                }
            }
            else if(json.has("trainee")){
                String trainee = json.getString("trainee");
                Skydivers trainee2 = findSkydivers(trainee, skydivers);

                int success = 0;
                Flight flight_loop = null;

                for(int i = 0; i < flight.size();i++){
                    flight_loop = flight.get(i);
                    LocalDateTime flight_time = flight_loop.getStarttime();
                    if(flight_loop.equals(change_jump.getFlight())){
                        if(2 + flight_loop.getCurrload() - change_jump.length() > flight_loop.getMaxload()){
                            continue;
                        }
                        else if((flight_time.equals(change_starttime) || flight_time.isAfter(change_starttime)) && flight_time.getDayOfYear() == change_starttime.getDayOfYear()){
                            // Find the instrctor with the least amount of jumps
                           int min_jump = 999;
                           Skydivers instructor = null;
                           for(i = 0;i < skydivers.size();i++){
                                if((skydivers.get(i).getLicence().equals("instructor") || skydivers.get(i).getLicence().equals("tandem-master"))){
                                    if((skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(trainee2)){
                                        instructor = skydivers.get(i);
                                        if(skydivers.get(i).getNumJump() < min_jump){
                                            if(checkSkydiversIndividual(instructor, flight_time)){
                                                instructor = skydivers.get(i);
                                                min_jump = skydivers.get(i).getNumJump();
                                                success = 1;
                                            }
                                        }
                                    }
                                }
                            }
                            Jumps delete = findJumps(change_id, jumps);
                            if(delete == null){
                                break;
                            }
                            //removes the jump from the list
                            jumps.remove(delete);
                            //finds the flight from the delete flight's id from the flight list
                            Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
                            delete_from_flight.getFlight_Jumps().remove(delete);
                            delete_from_flight.setCurrload(delete_from_flight.getCurrload() - delete.length());
                            
                            if(!trainee2.getLicence().equals("student")){
                                trainee2.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            }
                            else{
                                trainee2.setLastJump(flight_loop.getEndtime().plusMinutes(15));
                            }
                            instructor.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            Jumps new_jump = new Jumps(change_type, change_id, change_starttime, trainee2);
                            new_jump.getSkydivers().add(instructor);
                            new_jump.setFlight(flight_loop);
                            flight_loop.addJumps(new_jump);
                            if(flight_loop.equals(change_jump.getFlight())){
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2 - change_jump.length());
                            }
                            else{
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2);
                                delete.getFlight().setCurrload(delete.getFlight().getCurrload() - delete.getNum_divers());
                            }
                            jumps.add(new_jump);
                            JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                            break;
                        }
                    }
                    else{
                        if(2 + flight_loop.getCurrload() > flight_loop.getMaxload()){
                            continue;
                        }
                        else if((flight_time.equals(change_starttime) || flight_time.isAfter(change_starttime)) && flight_time.getDayOfYear() == change_starttime.getDayOfYear()){
                            // Find the instrctor with the least amount of jumps
                           int min_jump = 999;
                           Skydivers instructor = null;
                           for(i = 0;i < skydivers.size();i++){
                            if((skydivers.get(i).getLicence().equals("instructor") || skydivers.get(i).getLicence().equals("tandem-master"))){
                                if((skydivers.get(i).getDropzone() == null || skydivers.get(i).getDropzone().equals(flight_loop.getDropzone())) && !skydivers.get(i).equals(trainee2)){
                                    instructor = skydivers.get(i);
                                    if(skydivers.get(i).getNumJump() < min_jump){
                                        if(checkSkydiversIndividual(instructor, flight_time)){
                                            instructor = skydivers.get(i);
                                            min_jump = skydivers.get(i).getNumJump();
                                            success = 1;
                                        }
                                    }
                                }
                            }
                        }
                            Jumps delete = findJumps(change_id, jumps);
                            if(delete == null){
                                break;
                            }
                            //removes the jump from the list
                            jumps.remove(delete);
                            //finds the flight from the delete flight's id from the flight list
                            Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
                            delete_from_flight.getFlight_Jumps().remove(delete);
                            delete_from_flight.setCurrload(delete_from_flight.getCurrload() - delete.length());

                            if(!trainee2.getLicence().equals("student")){
                                trainee2.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            }
                            else{
                                trainee2.setLastJump(flight_loop.getEndtime().plusMinutes(15));
                            }
                            instructor.setLastJump(flight_loop.getEndtime().plusMinutes(25));
                            Jumps new_jump = new Jumps(change_type, change_id, change_starttime, trainee2);
                            new_jump.getSkydivers().add(instructor);
                            new_jump.setFlight(flight_loop);
                            flight_loop.addJumps(new_jump);
                            if(flight_loop.equals(change_jump.getFlight())){
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2 - change_jump.length());
                            }
                            else{
                                flight_loop.setCurrload(flight_loop.getCurrload() + 2);
                                delete.getFlight().setCurrload(delete.getFlight().getCurrload() - delete.getNum_divers());
                            }
                            jumps.add(new_jump);
                            JSONSuccess(flight_loop.getId(), flight_loop.getDropzone().getName());
                            break;
                        }
                    }
                    if(success == 0){
                        JSONRejected();
                        break;
                    }  
                }
            }
        case "cancel":
            String cancel_id = json.getString("id");
            Jumps delete = findJumps(cancel_id, jumps);
            if(delete == null){
                break;
            }
            for(int i = 0; i < delete.length(); i++){
                ArrayList<Skydivers> set_permission = delete.getSkydivers();
                Skydivers individual_sky = set_permission.get(i);
                individual_sky.setParticipating(false);
                individual_sky.setLastJump(individual_sky.last_jump_before);
                individual_sky.setLastJumpBefore(null); 
                individual_sky.minusNumJump();
            }
            //removes the jump from the list
            jumps.remove(delete);
            //finds the flight from the delete flight's id from the flight list
            Flight delete_from_flight = findFlight(delete.getFlight().getId(), flight);
            delete_from_flight.getFlight_Jumps().remove(delete);
            delete_from_flight.setCurrload(delete_from_flight.getCurrload() - delete.length());
            break;

        case "jump-run":
            String jump_run_id = json.getString("id");
            Flight RunFlight = findFlight(jump_run_id, flight);
            ArrayList<Jumps> fun = new ArrayList<Jumps>();
            ArrayList<Jumps> tandem = new ArrayList<Jumps>();
            ArrayList<Jumps> training = new ArrayList<Jumps>();
            JSONArray print = new JSONArray();
            JSONObject put = new JSONObject();

            for(Jumps s : RunFlight.getFlight_Jumps()){
                if(s.getType().equals("fun")){
                    fun.add(s);
                }
                else if(s.getType().equals("tandem")){
                    tandem.add(s);
                }
                else if(s.getType().equals("training")){
                    training.add(s);
                }
            }
            if(fun.size() + tandem.size() + training.size() == 0){
                System.out.println(print);
                break;
            }

            for (int i = 0 ; i < fun.size() - 1; i++) {
                Jumps p = fun.get(i);
                Jumps next =  fun.get(i+1);
                if(p.getNum_divers() < next.getNum_divers()) {
                    Collections.swap(fun, i, i + 1);
                }
            }
            for (int i = 0 ; i < tandem.size() - 1; i++) {
                Jumps p = tandem.get(i);
                Jumps next =  tandem.get(i+1);
                if(p.getNum_divers() < next.getNum_divers()) {
                    Collections.swap(tandem, i, i + 1);
                }
            }
            for (int i = 0 ; i < training.size() - 1; i++) {
                Jumps p = training.get(i);
                Jumps next =  training.get(i+1);
                if(p.getNum_divers() < next.getNum_divers()) {
                    Collections.swap(training, i, i + 1);
                }
            }
            for(int j = 0; j < fun.size();j++){
                ArrayList<String> FunName = new ArrayList<String>();
                for(int k = 0; k < fun.get(j).getSkydivers().size(); k++){
                    FunName.add(fun.get(j).getSkydivers().get(k).getName());
                }
                put.put("skydivers", FunName);
                print.put(put);
            }
            JSONObject put2 = new JSONObject();
            for(int j = 0; j < tandem.size();j++){
                put2.put("passenger", tandem.get(j).getSkydivers().get(0).getName());
                put2.put("jump-master", tandem.get(j).getSkydivers().get(1).getName());
                print.put(put2);
            }
            JSONObject put3 = new JSONObject();
            for(int j = 0; j < training.size();j++){
                put3.put("trainee", training.get(j).getSkydivers().get(0).getName());
                put3.put("instructor", training.get(j).getSkydivers().get(1).getName());
                print.put(put3);
            }
            System.out.println(print);
        }
    }
 

    public static void main(String[] args) {
        SkydiveBookingSystem system = new SkydiveBookingSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }

    /*
     *Used to find a skydivers in skydivers ArrayList
     *@param name of the skydiver
     *@param list the list of skydivers
     *@return returns the skydiver
    */
    public Skydivers findSkydivers(String name, ArrayList<Skydivers> list){
        for(Skydivers s : list){
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }
    /*
     *Used to find a dropzone in dropzone ArrayList
     *@param name of the dropzone
     *@param list the list of Dropzones
     *@return returns the dropzone
    */
    public Dropzone findDropzone(String name, ArrayList<Dropzone> list){
        for(Dropzone d : list){
            if(d.getName().equals(name)){
                return d;
            }
        }
        return null;
    }
    /*
     *Used to find a flight in flight ArrayList
     *@param name of the flight
     *@param list the list of FLights
     *@return returns the flight
    */
    public Flight findFlight(String id, ArrayList<Flight> list){
        for(Flight f : list){
            if(f.getId().equals(id)){
                return f;
            }
        }
        return null;
    }
    /*
    * this method checks if all skydivers are able to jump
    * @param List list of skydivers to check
    * @param starttime the starttime of the flight
    */
    public boolean checkSkydivers(ArrayList<Skydivers> List, LocalDateTime starttime){
        for(Skydivers s : List){
            if(s.getLast_Jump() == null){
                continue;
            }
            else if(starttime.getDayOfYear() == s.getLast_Jump().getDayOfYear()){
                if(s.getLast_Jump().isAfter(starttime)){
                    return false;
                }
            }
        }
        return true;
    }
    /*
    * this method checks if all skydivers are able to change their schedule
    * @param List  list of skydivers to check
    * @param starttime the starttime of the flight
    */
    public boolean checkSkydiversChange(ArrayList<Skydivers> List, LocalDateTime starttime){
        for(Skydivers s : List){
            if(s.getLastJumpBefore() == null){
                continue;
            }
            else if(starttime.getDayOfYear() == s.getLastJumpBefore().getDayOfYear()){
                if(s.getLastJumpBefore().isAfter(starttime)){
                    return false;
                }
            }
        }
        return true;
    }
    /*
    * this method checks if skydiver are able to change their schedule
    * @param List  list of skydivers to check
    * @param starttime the starttime of the flight
    */
    public boolean checkSkydiversIndividualChange(Skydivers s, LocalDateTime starttime){
        if(s.getLastJumpBefore() == null){
            return true;
        }
        else if(starttime.getDayOfYear() == s.getLastJumpBefore().getDayOfYear()){
            if(s.getLastJumpBefore().isAfter(starttime)){
                return false;
            }
        }
        return true;

    }
    /*
    * check if the skydiver can jump
    * @param s the skydivers need to check
    * @param starttime the starttime of the flight
    * @return true of false if can jump
    */
    public boolean checkSkydiversIndividual(Skydivers s, LocalDateTime starttime){
        if(s.getLast_Jump() == null){
            return true;
        }
        else if(starttime.getDayOfYear() == s.getLast_Jump().getDayOfYear()){
            if(s.getLast_Jump().isAfter(starttime)){
                return false;
            }
        }
        return true;

    }
    /*
     *Used to find a jumps in jumps ArrayList
     *@param name of the jumps
     *@param list the list of jumps
     *@return returns the jumps
    */
    public Jumps findJumps(String id, ArrayList<Jumps> list){
        for(Jumps j : list){
            if(j.getId().equals(id)){
                return j;
            }
        }
        return null;
    }
    /*
    * method to print out the rejected JSON line
    */
    public void JSONRejected(){
        JSONObject rejected = new JSONObject();
        rejected.put("status", "rejected"); 
        System.out.println(rejected.toString());
    }
    /*
    * method to print out the success JSON line with flight and dropzone
    * @param Flight_id the id of the flight
    * @param Dropzone_name the name of the dropzone
    */
    public void JSONSuccess(String Flight_id, String Dropzone_name){
        JSONObject success = new JSONObject();
        success.put("flight", Flight_id);
        success.put("dropzone", Dropzone_name);
        success.put("status", "success");
        System.out.println(success.toString());
    }
}