package unsw.enrolment;
import java.util.ArrayList;

/*
* Composite pattern for Mark
*/

public class Marks implements MarksInterface{
    public String Assignment;
    public int Mark;
    public ArrayList<MarksInterface> AssignmentList;

    /*
    * Constructor
    * @param gets the assignment name 
    */
    public Marks(String Assignment){
        this.Assignment = Assignment;
        this.AssignmentList = new ArrayList<MarksInterface>();
        this.Mark = 0;
    }

    public void setMark(int Mark){
        this.Mark = Mark;
    }

    public int getMark(){
        return this.Mark;
    }

    public ArrayList<MarksInterface> getAssignmentList(){
        return this.AssignmentList;
    }

    public void addAssignment(MarksInterface Mark){
        this.AssignmentList.add(Mark);
    }
    /*
    * If there are milestones then average the marks
    * if not then return the mark that is given
    */
    public int AverageMark(){
        if(AssignmentList.isEmpty()){
            return this.Mark;
        }
        setMark(0);
        for(int i = 0; i < AssignmentList.size(); i++){
            this.Mark += AssignmentList.get(i).getMark();
        }
        int mark = this.Mark;
        int numAssignment = this.AssignmentList.size();
        int result = mark/numAssignment;
        return result;
    }

    @Override
    public String getName(){
        return this.Assignment;
    }

    /*
    * gets only the prac marks
    */
    public int getPracMarks() {
        if(AssignmentList.isEmpty()){
            return AverageMark();
        }
        else{
            int numFinals = 0;
            setMark(0);
            for(MarksInterface s: AssignmentList){
                if(s.getName().equals("FinalExam")){
                    numFinals++;
                }
                else{
                    this.Mark += s.getPracMarks();
                }
            }
            if(numFinals == 0) {
                this.Mark = this.Mark/this.AssignmentList.size();
                return this.Mark;
            }
            else {
                this.Mark = this.Mark/(this.AssignmentList.size() - numFinals);
                return this.Mark;
            }
        }
    }

    /*
    * gets the final mark including
    * finals and prac
    */
    @Override
    public int getFinalMarks(){
        if(AssignmentList.isEmpty()){
            return this.Mark;
        }
        else {
            setMark(0);
            for(MarksInterface s: AssignmentList) {
                if(s.getName().equals("FinalExam")){
                    this.Mark += s.getFinalMarks();
                }
            }
            this.Mark += getPracMarks();
            return this.Mark;
        }
    }
    /*
    * Checks if the person passed
    */
    @Override
    public boolean Passed(){
        this.Mark = getFinalMarks();
        if(this.Mark >= 50) {
            return true;
        }
        else {
            return false;
        }
    }
}