package unsw.enrolment;

public class Grade {
    private int mark;
    private String grade;

    /*
    *@param mark the mark given 
    *also checks if the mark is valid
    */
    public Grade(int mark){
        if(mark < 0 || mark > 100){
            System.out.println("Invalid mark");
            return;
        }
        this.mark = mark;
        this.grade = null;
    }
    public Grade(){
        super();
    }

    /*
    *@param sets the mark 
    */
    public void setMark(int mark){
        this.mark = mark;
    }

    /*
    *@param set a specific grade
    */

    public void setGrade(String grade){
        this.grade = grade;
    }

    /*
    *@return return the string of the grade depending on the mark
    */

    public String getGrade(){
        if(this.grade != null){
            return this.grade;
        }
        if(this.mark >= 85){
            return "HD";
        }
        else if(this.mark >= 75){
            return "D";
        }
        else if(this.mark >= 65){
            return "CR";
        }
        else if(this.mark >= 50){
            return "P";
        }
        return "F";
    }

    public int getMark(){
        return this.mark;
    }


}
