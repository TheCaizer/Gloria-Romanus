package unsw.enrolment;
import java.util.ArrayList;

public class Student {

    private String zid;
    private ArrayList<Enrolment> enrolments;

	public Student(String zid) {
        this.zid = zid;
        enrolments = new ArrayList<>();
    }

	public String getZID() {
		return zid;
    }
    
    /*
    *@return returns the enrolement array list
    */
    public ArrayList<Enrolment> getEnrolments(){
        return enrolments;
    }

    /*
    *check if the person can enrol into the course given they pass the course
    */
    public boolean checkEnrolment(Course course){
        for(Course check: course.getPrereq){
            ArrayList<Enrolment> enrolled = new ArrayList<Enrolment>();
            for(Enrolment temp : this.enrolments){
                if(temp.getCourse() == course){
                    enrolled.add(temp);
                }
            }
            boolean passed = false;
            for(Enrolment temp : enrolled){
                if(e.getMark() >= 50){
                    passed = true;
                    break;
                }
            }
            if(passed != true){
                return false;
            }
        }
        return true;
    }

    /*
    *if can enrol get the course 
    */
    public boolean canEnrol(CouseOffering c){
        return this.canEnrol(c.getCourse);
    }

    /*
    *all checks out and then add to enrolment arraylist
    */
    public Enrolment Enrol(CourseOffering offer){
        if(!this.canEnrol(offer.getCourse())){
            System.out.println("Cannot Enrol");
            System.exit(0);
        }
        Enrolment enrol = new Enrolment(offer, this);
        this.enrolments.add(enrol);
        return enrolment;
    }
}
