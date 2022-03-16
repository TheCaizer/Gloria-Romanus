package staff.test;

import java.time.LocalDate;

import staff.StaffMember;

public class StaffTest{
    public static void printStaffDetails(StaffMember staff){
        String details = staff.toString();
        System.out.println(details);
    }
    
    public static void main(String args[]){
            LocalDate date1 = LocalDate.of(2020, 9, 29);
            LocalDate date2 = LocalDate.of(2020, 10, 9);
            LocalDate date3 = LocalDate.of(1999, 10, 10);
            LocalDate date4 = LocalDate.of(2009, 9, 9);

            StaffMember tim = new StaffMember("tim", 100.10, date1, date2);
            StaffMember.Lecturer Jim = new StaffMember.Lecturer("Jim", 100, date3, date4, "CSE", 'A');

            printStaffDetails(tim);
            printStaffDetails(Jim);

            if(tim.equals(Jim)){
                System.out.println("tim is Jim");
            }
            else{
                System.out.println("tim is not Jim");
            }
        }
}