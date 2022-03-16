package unsw.enrolment;
import java.util.ArrayList;
import java.util.List;

/**
 * A course in the enrolment system.
 * @author Robert Clifton-Everest
 *
 */
public class Course {

    private String courseCode;
    private String title;
    private int uoc;
    private List<Course> prereqs;
    private List<CourseOffering> courseOfferings;


    public Course(String courseCode, String title) {
        this.courseCode = courseCode;
        this.title = title;
        this.prereqs = new ArrayList<Course>();
        this.courseOfferings = new ArrayList<CourseOffering>();
        this.uoc = 6;
    }

    public Course(){
        super();
    }
    /*
    * @return return the title of the object
    */
    public String getTitle(){
        return title;
    }

    public void addPrereq(Course course) {
        prereqs.add(course);
    }

    /*
    * @return a list of prereqs needed for the course 
    */
    public ArrayList<Course> getPrereq(){
        ArrayList<Course> prereq = new ArrayList<Course>();
        for(Course c : this.prereqs){
            prereq.add(c);
        }
        return prereq;
    }

    public void addOffering(CourseOffering offering) {
        courseOfferings.add(offering);
    }

    /*
    *getter for courseOfferings
    *@return a list of courseofferings for the course
    */
    public ArrayList<CourseOffering> getOffering(){
        ArrayList<CourseOffering> offer = new ArrayList<CourseOffering>();
        for(CourseOffering c : this.courseOfferings){
            offer.add(c);
        }
        return offer;
    }
    public String getCourseCode() {
        return courseCode;
    }

    public int getUOC() {
        return uoc;
    }

    public void setUOC(int uoc){
        this.uoc = uoc;
    }

}
