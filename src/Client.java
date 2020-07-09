public class Client {

    private int time;                                                       //Время встречи
    private boolean visited;                                                //Была ли вершина посещена
    private int employeeNum;                                                //Номер сотрудника, поситившего вершину
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
