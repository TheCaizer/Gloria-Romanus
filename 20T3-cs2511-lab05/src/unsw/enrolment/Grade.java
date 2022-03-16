package unsw.enrolment;

import java.io.IOException;
import java.util.logging.*;
/*
* base component
*/
public class Grade implements MarksInterface{
    private int mark;
    private String grade;
    private String Assignment;
    private CourseOffering offering;
    private Student student;
    private Logger logger;

    public Grade(String Assignment, int mark, Student student, CourseOffering offering){
        this.student = student;
        this.offering = offering;
        this.grade = setGrade();
        this.Assignment = Assignment;

        String logString = String.format("%s-%s-%s", this.getCourse().getCourseCode(),
        this.getTerm(), this.getStudent().getZID());
        this.logger = Logger.getLogger(logString);
        logger.setUseParentHandlers(false);

        setMark(mark);
    }

    public int getMark(){
        return mark;
    }

    public void setMark(int mark){
        this.mark = mark;
        this.logger.info(String.format("Marks Updated: %s = %d\n", this.getName(), this.getMark()));
    }

    public Student getStudent(){
        return this.student;
    }

    public Course getCourse() {
        return offering.getCourse();
    }

    public String getTerm() {
        return offering.getTerm();
    }

    public String getGrade(){
        return this.grade;
    }
    /*
    * checks the mark and set the grade
    * @return Fail, Pass, Distinction or High Distinction
    */
    public String setGrade(){
        if(this.mark < 50){
            return "FL";
        }
        else if(this.mark < 65){
            return "PS";
        }
        else if(this.mark < 75){
            return "DN";
        }
        else{
            return "HD";
        }
    }

    public boolean Passed(){
        if(getFinalMarks()>= 50){
            return true;
        }
        else{
            return false;
        }
    }

    public String getName(){
        return Assignment;
    }

    public int getPracMarks(){
        return this.mark;
    }

    public int getFinalMarks(){
        return this.mark;
    }
}
