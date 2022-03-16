package staff;

/**
 * A staff member
 * @author Jackie Cai
 *
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class StaffMember {
    public String name;
    public double salary;
    public LocalDate startDate;
    public LocalDate endDate;
    /*
    * Constructor with parameters of name, salary and the date where staff started
    * @param name name of staff
    * @param salary the salary of staff
    * @param startDate the date the staff started 
    */
    public StaffMember(String name, double salary, LocalDate start, LocalDate end){
        this.name = name;
        this.salary = salary;
        this.startDate = start;
        this.endDate = end;
    }
    /*
    * default constructor
    */
    public StaffMember(){
        super();
    }
    /*
    * @return the name of staff object
    */
    public String getName(){
        return this.name;
    }

    /*
    * set the name of the staff object
    * @param name the new name
    */
    public void setName(String name){
        this.name = name;
    }
    /*
    * get the salary of the staff object
    * @return salary return the salary of staff object
    */
    public double getSalary(){
        return this.salary;
    }
    /*
    * set the salary of the staff object
    * @param salary the new salary of staff
    */
    public void setSalary(double salary){
        this.salary = salary;
    }
    /*
    * get the start date of the staff object
    * @return startDate start date
    */
    public String getStartDate(){
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(this.startDate);
    }
    /*
    * set the start date of the staff object
    * @param star the new startdate of staff
    */
    public void setStartDate(LocalDate start){
        this.startDate = start;
    }
    /*
    * get the end date of the staff object
    * @return the end date of the staff
    */
    public String getEndDate(){
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(this.endDate);
    }
    /*
    * set the end date of the staff object
    * @param end the new end date of staff
    */
    public void setEndDate(LocalDate end){
        this.endDate = end;
    }

    @Override
    public String toString(){
        String temp = "This staff's name is " + getName() + " gets paid " + getSalary() + " started on " + getStartDate() + " terminated on " + getEndDate();
        return temp;
    }

    @Override 
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        StaffMember temp = (StaffMember) obj;
		return this.name.equals(temp.name) && this.salary == temp.salary;
    }

public static class Lecturer extends StaffMember{
    String school;
    char academicStatus;
    /*
    * Constructor with parameters of name, salary and the date where staff started
    * academic status and school. Call super class constructor.
    * @param name name of staff
    * @param salary the salary of staff
    * @param startDate the date the staff started 
    * @param school the faculty the staff belongs to
    * @academicStatus the academic status of the staff lecturer A for Associate, B for
    * Lecturer and C for Senior Lecturer 
    */
    public Lecturer(String name, double salary, LocalDate start, LocalDate end, String school, char academicStatus){
        super(name, salary, start,end);
        setSchool(school);
        setAcademicStatus(academicStatus);
    }
    /*
    * @param school the school faculty the lecturer is in
    */
    public void setSchool(String school){
        this.school = school;
    }
    /*
    * @return returns the school the lecturer is in
    */
    public String getSchool(){
        return this.school;
    }
    /*
    * @param status the academic status of the lectuer A for Associate, B for
    * Lecturer and C for Senior Lecturer 
    */
    public void setAcademicStatus(char status){
        this.academicStatus = status;
    }
    /*
    * @return returns the academic status of the lecturer A for Associate, B for
    * Lecturer and C for Senior Lecturer 
    */
    public char getAcademicStatus(){
        return this.academicStatus;
    }

    @Override
    public String toString(){
        String temp = super.toString() + " in the school of " + getSchool() + " academic status of " + getAcademicStatus();
        return temp;
    }

    @Override
    public boolean equals(Object obj){
        if(super.equals(obj) != true){
            return false;
        }
        Lecturer temp = (Lecturer) obj;
        return this.school.equals(temp.school) && this.academicStatus == temp.academicStatus;
    }
}
}
