package unsw.enrolment.test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import unsw.enrolment.Course;
import unsw.enrolment.CourseOffering;
import unsw.enrolment.Enrolment;
import unsw.enrolment.Lecture;
import unsw.enrolment.Student;

public class EnrolmentTest {

    public static void main(String[] args) {

        // Create courses
        Course comp1511 = new Course("COMP1511", "Programming Fundamentals");
        Course comp1531 = new Course("COMP1531", "Software Engineering Fundamentals");
        comp1531.addPrereq(comp1511);
        Course comp2521 = new Course("COMP2521", "Data Structures and Algorithms");
        comp2521.addPrereq(comp1511);

        CourseOffering comp1511Offering = new CourseOffering(comp1511, "19T1");
        CourseOffering comp1531Offering = new CourseOffering(comp1531, "19T1");
        CourseOffering comp2521Offering = new CourseOffering(comp2521, "19T2");

        //  Create some sessions for the courses
        Lecture lecT13A = new Lecture("Online", DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(15, 00), "Aresh");
        Lecture lecM14B = new Lecture("In Person", DayofWeek.MONDAY, LocalTime.of(14, 00), LocalTIme.of(16,00),"Jim");
        Tutorial tutF15A = new Tutorial("Online", DayOfWeek.FRIDAY, LocalTime.of(15, 00), LocalTime.of(17,00), "Tim");
        Lab labH9A = new Lab("Delivery", DayOfWeek.THURSDAY, LocalTime.of(9,00), LocalTime.of(11,00), "Lim");
        comp1511Offering.addSession(lecT13A);
        comp1511Offering.addSession(tutF15A);
        comp2521Offering.addSession(lecM14B);
        comp1531Offering.addSession(labH9A);
        //  Create a student
        Student Jack = new Student("z1234567");
        //  Enrol the student in COMP1511 for T1 (this should succeed)
        Enrolment comp1511Enolment = Jack.enrol(comp1511Offering);
        //  Enrol the student in COMP1531 for T1 (this should fail as they
        // have not met the prereq)
        boolean temp = false;
        try{
            Enrolment comp1531Enrolment = enrol(comp1531Offering);
        }catch(Error e){
            temp = true;
        }
        assert(temp);
        //  Give the student a passing grade for COMP1511
        comp1511Enolment.setMark(51);
        //  Enrol the student in 2521 (this should succeed as they have met
        // the prereqs)
        Enrolment comp2521Enrolment = Jack.enrol(comp2521Offering);
        
    }
}
