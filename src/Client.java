public class Client {

    private int time;
    private boolean visited;
    private int employeeNum;
    private int moneyCount;

    public void setEmployeeNum(int employeeNum){
        this.employeeNum = employeeNum;
    }
    public void setVisited(boolean visited){
        this.visited = visited;
    }
    public void setMoneyCount(int moneyCount){
        this.moneyCount = moneyCount;
    }
    public void setTime(int time){
        this.time = time;
    }
    public int getMoneyCount(){
        return moneyCount;
    }
    public int getEmployeeNum(){
        return employeeNum;
    }
    public boolean getVisited(){
        return visited;
    }
    public int getTime(){
        return time;
    }
}
