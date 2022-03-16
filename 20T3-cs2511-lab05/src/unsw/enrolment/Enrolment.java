package unsw.enrolment;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.logging.*;

public class Enrolment {

    private CourseOffering offering;
    private MarksInterface grade;
    private Student student;
    private List<Session> sessions;
    private Logger logger;
    private String logString;

    public Enrolment(CourseOffering offering, Student student, Session... sessions) {
        this.offering = offering;
        this.student = student;
        this.grade = null; // Student has not completed course yet.
        student.addEnrolment(this);
        offering.addEnrolment(this);
        this.sessions = new ArrayList<>();
        for (Session session : sessions) {
            this.sessions.add(session);
        }

        this.logString = String.format("%s-%s-%s", this.getCourse().getCourseCode(),
        this.getTerm(), this.getStudent().getZID());
        logger = Logger.getLogger(logString);
        logger.setUseParentHandlers(false);

        FileHandler handler;

        try{
            handler = new FileHandler(logString + ".log", true);
            logger.addHandler(handler);

            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
        } catch (SecurityException | IOException i){
            i.printStackTrace();
        }
    }

    public Course getCourse() {
        return offering.getCourse();
    }

    public String getTerm() {
        return offering.getTerm();
    }

    public boolean hasPassed() {
        return grade != null && grade.Passed();
    }

    public void setGrade(Marks grade){
        this.grade = grade;
        writeChange();
    }

    public Student getStudent(){
        return this.student;
    }

    private void writeChange(){
        logger.info(String.format("Marks Updated: %s = %d", this.grade.getName(), this.grade.getMark()));
    }

//    Whole course marks can no longer be assigned this way.
//    public void assignMark(int mark) {
//        grade = new Grade(mark);
//    }

}
