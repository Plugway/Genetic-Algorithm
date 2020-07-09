import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Employee {
    public int money = 0;
    public int start = -1;
    public int end = -1;
    public double multiplier = 1;
    public static Employee[] list;
    public static int employeeNum;
    public static void Init(String path) throws IOException {
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
