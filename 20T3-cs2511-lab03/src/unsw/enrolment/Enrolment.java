package unsw.enrolment;

public class Enrolment {

    private CourseOffering offering;
    private Grade grade;
    private Student student;

    public Enrolment(CourseOffering offering, Student student) {
        this.offering = offering;
        this.student = student;
        this.grade = new Grade();
        offering.addEnrolment(this);
    }

    public Course getCourse() {
        return offering.getCourse();
    }

    public String getTerm() {
        return offering.getTerm();
    }

    /*
    *@return the mark by calling the getter from mark
    */
    public int getMark(){
        return this.grade.getMark();
    }

    /*
    *@return the grade object
    */
    public Grade getGrade(){
        return this.grade;
    }

    /*
    *@param mark to set 
    */
    public void setMark(int mark){
        this.grade.setMark(mark);
    }
}
