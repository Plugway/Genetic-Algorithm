import java.io.IOException;

public class ClientWeb {
    public static void Init(String path) throws IOException {
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
    private static int[][] availableVertices;
    private static int[][] timesToOther;
    private static Client[] clients;
    private static int clientNum;

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
