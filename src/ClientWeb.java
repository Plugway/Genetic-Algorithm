import java.io.IOException;

public class ClientWeb {
    public static void Init(String path) throws IOException {               //Инициализация клиентов
        var clientsContent = Main.getFileContent(path)
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .split(";");
        clientNum = clientsContent.length;
        clients = new Client[clientNum];
        timesToOther = new int[clientNum][clientNum];
        availableVertices = new int[clientNum][clientNum];

        for (var i = 0; i < clients.length; i++){
            var clientInfo = clientsContent[i].split(" ");
            clients[i] = new Client();
            clients[i].setTime(Integer.parseInt(clientInfo[0]));
            timesToOther[i][i] = -1;
            availableVertices[i][i] = -1;
            for (var k = 1; k < clients.length-i; k++) {
                if (clientInfo[k].equals("-")){
                    timesToOther[i+k][i] = -1;
                    availableVertices[i+k][i] = -1;
                    availableVertices[i][i+k] = -1;
                } else {
                    timesToOther[i+k][i] = Integer.parseInt(clientInfo[k]);
                    availableVertices[i+k][i] = i;
                    availableVertices[i][i+k] = i+k;
                }
                timesToOther[i][i+k] = timesToOther[i+k][i];
            }
        }
    }

    public static void Init2(String pathGraph, String pathValues) throws IOException {
        var clientsValues = Main.getFileContent(pathValues)
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .split(";");
        var clientsContent = Main.getFileContent(pathGraph)
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .split("<edge");
        clientNum = clientsValues.length;
        clients = new Client[clientNum];
        timesToOther = new int[clientNum][clientNum];
        availableVertices = new int[clientNum][clientNum];

        for (var i = 0; i < clients.length; i++){
            clients[i] = new Client();
            clients[i].setTime(Integer.parseInt(clientsValues[i]));
        }
        var vertDifference = Integer.parseInt(clientsContent[0]
                .substring(clientsContent[0].indexOf("\" id=\"")+6, clientsContent[0].indexOf("\" mainText=\"")));
        for (var i = 1; i < clientsContent.length; i++){
            var s = clientsContent[i];
            var firstVert = Integer.parseInt(s.substring(s.indexOf("source=\"")+8, s.indexOf("\" target=\"")))-vertDifference;
            var lastVert = Integer.parseInt(s.substring(s.indexOf("\" target=\"")+10, s.indexOf("\" isDirect=\"")))-vertDifference;
            var weight = Integer.parseInt(s.substring(s.indexOf("weight=\"")+8, s.indexOf("\" useWeight=\"")));
            timesToOther[firstVert][lastVert] = weight;
            timesToOther[lastVert][firstVert] = weight;
            availableVertices[firstVert][lastVert] = lastVert;
            availableVertices[lastVert][firstVert] = firstVert;
        }
        for (var i = 0; i < timesToOther.length; i++){
            for (var j = 0; j < timesToOther[i].length; j++){
                if (timesToOther[i][j] == 0){
                    timesToOther[i][j] = -1;
                    availableVertices[i][j] = -1;
                }
            }
        }
    }


    private static int[][] availableVertices;                               //Есть ли путь из i в j или нет
    private static int[][] timesToOther;                                    //Стоимость ребра между i и j
    private static Client[] clients;                                        //Все клиенты
    private static int clientNum;                                           //Число клиентов

    public static int[][] getAvailableVertices(){ return availableVertices; }
    public static int getClientNum(){
        return clientNum;
    }
    public static int[][] getTimesToOther(){
        return timesToOther;
    }
    public static Client[] getClients(){
        return clients;
    }
}
