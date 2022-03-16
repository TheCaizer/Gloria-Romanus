package unsw.enrolment;
import java.util.ArrayList;
import java.util.List;

public class CourseOffering {

    private Course course;
    private String term;
    private List<Session> sessions;
    private List<Enrolment> enrolments;

    public CourseOffering(Course course, String term) {
        this.course = course;
        this.term = term;
        this.sessions = new ArrayList<>();
        this.enrolments = new ArrayList<>();
        this.course.addOffering(this);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    /*
    *@param add enrolments to the list
    */
    public void addEnrolments(Enrolment enrol){
        this.enrolments.add(enrol);
    }

    public Course getCourse() {
        return course;
    }

    public String getTerm() {
        return term;
    }

}
