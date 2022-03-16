package unsw.skydiving;

import java.util.Objects;
import java.time.LocalDateTime;

public class Skydivers{

    String name;
    String licence;
    Dropzone dropzone = null;
    boolean participating = false;
    int NumJump = 0;
    LocalDateTime last_jump_before = null;
    LocalDateTime last_jump = null;

    /*
    * constructor with dropzone given 
    */
    public Skydivers(String name, String licence, Dropzone dropzone){
        this.name = name;
        this.licence = licence;
        this.dropzone = dropzone;
    }
    /*
    * constructor without dropzone given
    */
    public Skydivers(String name, String licence){
        this.name = name;
        this.licence = licence;
    }

    public Skydivers() {
    }

    public LocalDateTime getLastJumpBefore(){
        return this.last_jump_before;
    }

    public void setLastJumpBefore(LocalDateTime t){
        this.last_jump_before = t;
    }
    
    public boolean getParticipating(){
        return this.participating;
    }

    public void setParticipating(boolean participating){
        this.participating = participating;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumJump(int num){
        this.NumJump = num;
    }

    public int getNumJump(){
        return this.NumJump;
    }

    /*
    * add a jump to the number of jump skydiver is doing
    */
    public void addNumJump(){
        this.NumJump = this.NumJump + 1;
    }

    /*
    * minus a jump to the number of jump skydiver is doing
    */
    public void minusNumJump(){
        this.NumJump = this.NumJump - 1;
    }

    public String getLicence() {
        return this.licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public Dropzone getDropzone() {
        return this.dropzone;
    }

    public void setDropzone(Dropzone dropzone) {
        this.dropzone = dropzone;
    }

    public void setLastJump(LocalDateTime lastTime){
        this.last_jump = lastTime;
    }

    public LocalDateTime getLast_Jump(){
        return this.last_jump;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Skydivers)) {
            return false;
        }
        Skydivers skydivers = (Skydivers) o;
        return Objects.equals(name, skydivers.name) && Objects.equals(licence, skydivers.licence) && Objects.equals(dropzone, skydivers.dropzone);
    }
}