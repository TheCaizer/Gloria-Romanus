package unsw.enrolment;

/*
* Composite pattern 
* an interface for marks
*/

public interface MarksInterface{
    public String getName();
    public int getMark();
    public int getFinalMarks();
    public int getPracMarks();
    public boolean Passed();
}