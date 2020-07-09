import java.io.IOException;

public class Employee {
    public int money = 0;                                                   //Деньги
    public int start = -1;                                                  //Желаемая стартовая вершина
    public int end = -1;                                                    //конечная
    public double multiplier = 1;                                           //Множитель
    public static Employee[] list;                                          //Список сотрудников
    public static int employeeNum;                                          //Их число
    public static void Init(String path) throws IOException {               //Инициализация
        var employeeContent = Main.getFileContent(path).replaceAll("\r\n", "").split(";");
        employeeNum = employeeContent.length;
        list = new Employee[employeeNum];
        for (var i = 0; i < employeeNum; i++){
            var employeeInfo = employeeContent[i].split(" ");
            var emp = new Employee();
            emp.start = Integer.parseInt(employeeInfo[0])-1;
            emp.end = Integer.parseInt(employeeInfo[1])-1;
            list[i] = emp;
        }
    }
}
