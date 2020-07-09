import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Specimen {
    public Specimen(String[] chromosomes, int bitInGen){
        this.chromosomes = chromosomes;
        this.bitInGen = bitInGen;
    }
    private int bitInGen;
    private String[] chromosomes;
    public int getBitInGen(){
        return bitInGen;
    }
    public String[] getChromosomes(){
        return chromosomes;
    }

    public static void printSolution(Specimen specimen, String addition){
        StringBuilder res = new StringBuilder();
        var intChromo = getIntChromosomes(specimen);
        for(var k = 0; k < intChromo.length; k++){
            res.append("\nСотрудник_").append(k + 1).append(":");
            for (var l = 0; l < intChromo[k].length; l++){
                res.append("----Клиент_").append(intChromo[k][l] + 1);
            }
        }
        System.out.print(res.append("\n").append(addition).toString());
    }
    public static int getFitness(Specimen specimen){
        var intChromos = getIntChromosomes(specimen);
        var fitness = 0;
        for(var k = 0; k < intChromos.length; k++){
            if (intChromos[k].length > 0 && Employee.list[k].start == intChromos[k][0]){
                Employee.list[k].multiplier+=Main.employeeMultiplierAdd;
            }
            if (intChromos[k].length > 0 && Employee.list[k].end == intChromos[k][intChromos[k].length-1]){
                Employee.list[k].multiplier+=Main.employeeMultiplierAdd;
            }
            var curTime = 0;
            for (var l = 0; l < intChromos[k].length; l++){
                var money = 0;
                var client = ClientWeb.getClients()[intChromos[k][l]];
                if (l > 0)
                    curTime += ClientWeb.getTimesToOther()[intChromos[k][l-1]][intChromos[k][l]];
                if (curTime <= client.getTime()){
                    money = (int)(Main.salarySetting*Employee.list[k].multiplier);
                } else {
                    money = (int)((Main.salarySetting -
                            curTime + client.getTime())*Employee.list[k].multiplier);
                }
                if (!client.getVisited() || client.getMoneyCount() < money){
                    Employee.list[client.getEmployeeNum()].money -= client.getMoneyCount();
                    client.setMoneyCount(money);
                    client.setVisited(true);
                    client.setEmployeeNum(k);
                    Employee.list[k].money += money;
                }
            }
        }
        for (var i = 0; i < Employee.employeeNum; i++){
            fitness += Employee.list[i].money;
            Employee.list[i].money = 0;
            Employee.list[i].multiplier = 1;
        }
        for (var i = 0; i < ClientWeb.getClientNum(); i++){
            var client = ClientWeb.getClients()[i];
            client.setVisited(false);
            client.setMoneyCount(0);
        }
        return fitness;
    }

    public static Specimen[] mutationOrCrossover(Specimen[] population){
        var res = new Specimen[population.length];
        var generator = new Random();
        for (var i = 0; i < population.length; i+=2){
            var spec1 = population[i];
            var spec2 = population[i+1];
            var newChromosomes3 = new String[spec1.chromosomes.length];
            var newChromosomes4 = new String[spec1.chromosomes.length];
            for (var j = 0; j < spec1.chromosomes.length; j++){
                var choose = generator.nextInt(100)+1;
                if (choose < Main.crossoverPosibility){
                    var cut = generator.nextInt(spec1.chromosomes[j].length());
                    newChromosomes3[j] = spec1.chromosomes[j].substring(0, cut)+spec2.chromosomes[j].substring(cut);
                    newChromosomes4[j] = spec2.chromosomes[j].substring(0, cut)+spec1.chromosomes[j].substring(cut);
                } else {
                    var mutBit =  generator.nextInt(spec1.chromosomes[j].length());
                    newChromosomes3[j] = spec1.chromosomes[j].substring(0, mutBit)+
                            generator.nextInt(2)+spec1.chromosomes[j].substring(mutBit+1);
                    mutBit = generator.nextInt(spec1.chromosomes[j].length());
                    newChromosomes4[j] = spec2.chromosomes[j].substring(0, mutBit)+
                            generator.nextInt(2)+spec2.chromosomes[j].substring(mutBit+1);
                }
            }
            var spec3 = new Specimen(newChromosomes3, spec1.bitInGen);
            var spec4 = new Specimen(newChromosomes4, spec2.bitInGen);
            res[i] = fixChromosome(spec3);
            res[i+1] = fixChromosome(spec4);
        }
        return res;
    }

    private static Integer[][] getIntChromosomes(Specimen specimen){
        var chromosomes = specimen.getChromosomes();
        var intChromo = new Integer[Employee.employeeNum][];//[ClientWeb.getClientNum()*2];
        for (var j = 0; j < chromosomes.length; j++){
            var chromosome = chromosomes[j].split("(?<=\\G.{"+ specimen.getBitInGen() +"})");
            intChromo[j] = new Integer[chromosome.length];
            for (var i = 0; i < chromosome.length; i++){
                intChromo[j][i] = Integer.parseInt(chromosome[i], 2);
            }
        }
        return intChromo;
    }

    private static Specimen fixChromosome(Specimen specimen){
        var intChromo = getIntChromosomes(specimen);
        for (var j = 0; j < intChromo.length; j++){
            for (var i = 0; i < intChromo[j].length-1; i++){
                if (intChromo[j][i+1]>=ClientWeb.getClientNum())
                    intChromo[j][i+1] = new Random().nextInt(ClientWeb.getClientNum());
                if (intChromo[j][i]>=ClientWeb.getClientNum())
                    intChromo[j][i] = new Random().nextInt(ClientWeb.getClientNum());
                if(ClientWeb.getTimesToOther()[intChromo[j][i]][intChromo[j][i+1]] == -1){
                    if (i == 0){
                        var waysList = getAvailableVertices(intChromo[j][i+1]);
                        intChromo[j][i] = waysList.get(0);
                    } else if (i == intChromo[j].length-2){
                        var waysList = getAvailableVertices(intChromo[j][i]);
                        intChromo[j][i+1] = waysList.get(0);
                    } else {
                        var waysList0 = getAvailableVertices(intChromo[j][i-1]);
                        var waysList = getAvailableVertices(intChromo[j][i]);
                        var waysList1 = getAvailableVertices(intChromo[j][i+1]);
                        var waysList2 = getAvailableVertices(intChromo[j][i+2]);
                        var assertPathChanged = false;
                        for (var k = 0; k < waysList.size(); k++){
                            if (waysList2.indexOf(waysList.get(k)) != -1){
                                intChromo[j][i+1] = waysList.get(k);
                                assertPathChanged = true;
                                break;
                            }
                        }
                        if (!assertPathChanged){
                            for (var k = 0; k < waysList1.size(); k++){
                                if (waysList0.indexOf(waysList1.get(k)) != -1){
                                    intChromo[j][i] = waysList1.get(k);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        normalizeArray(intChromo);
        var resChromos = new String[intChromo.length];
        for (var i = 0; i < intChromo.length; i++){
            StringBuilder resChromo = new StringBuilder();
            for(var k = 0; k < intChromo[i].length; k++){
                var curGen = Integer.toBinaryString(intChromo[i][k]);
                while (curGen.length() < specimen.bitInGen){
                    curGen = "0"+curGen;
                }
                resChromo.append(curGen);
            }
            resChromos[i] = resChromo.toString();
        }
        return new Specimen(resChromos, specimen.bitInGen);
    }

    private static List<Integer> getAvailableVertices(int vNum){
        var waysList = Arrays.stream(ClientWeb.getAvailableVertices()[vNum])
                .filter((s) -> s != -1).boxed().collect(Collectors.toList());
        Collections.shuffle(waysList);
        return waysList;
    }

    public static Specimen[] getRandomPopulation(int numOfSpec){
        var res = new Specimen[numOfSpec];
        for (var i = 0; i < numOfSpec; i++){
            var chromosomes = new Integer[Employee.employeeNum][ClientWeb.getClientNum()*2];//? *2 is optimal?
            var generator = new Random();
            for (var j = 0; j < chromosomes.length; j++){
                for (var k = 0; k < chromosomes[j].length; k++){
                    chromosomes[j][k] = generator.nextInt(ClientWeb.getClientNum());
                }
            }
            normalizeArray(chromosomes);
            var bitInGen = getBitInGen(ClientWeb.getClientNum()-1);
            var resChromos = new String[chromosomes.length];
            for (var j = 0; j < chromosomes.length; j++){
                resChromos[j] = "";
                for(var k = 0; k < chromosomes[j].length; k++){
                    StringBuilder curGen = new StringBuilder(Integer.toBinaryString(chromosomes[j][k]));
                    while (curGen.length() < bitInGen){
                        curGen.insert(0, "0");
                    }
                    resChromos[j] = resChromos[j] + curGen.toString();
                }
            }
            res[i] = new Specimen(resChromos, bitInGen);
        }
        return res;
    }

    public static Specimen truncateSpecimen(Specimen specimen){
        var fitness = getFitness(specimen);
        var intChromo = getIntChromosomes(specimen);
        var intChromo2 = new int[intChromo.length][];
        var removeCounters = new int[intChromo.length];

        for (var k = 0; k < removeCounters.length; k++){
            for (var l = 0; l < intChromo[0].length; l++){
                for (var i = 0; i < intChromo.length; i++){
                    intChromo2[i] = new int[intChromo[i].length-removeCounters[i]];
                    for (var j = 0; j < intChromo2[i].length; j++){
                        intChromo2[i][j] = intChromo[i][j];
                    }
                }
                var resChromos = new String[intChromo2.length];
                for (var j = 0; j < intChromo2.length; j++){
                    resChromos[j] = "";
                    for(var i = 0; i < intChromo2[j].length; i++){
                        StringBuilder curGen = new StringBuilder(Integer.toBinaryString(intChromo2[j][i]));
                        while (curGen.length() < specimen.bitInGen){
                            curGen.insert(0, "0");
                        }
                        resChromos[j] = resChromos[j] + curGen.toString();
                    }
                }
                if (fitness > getFitness(new Specimen(resChromos, specimen.bitInGen))){
                    removeCounters[k]--;
                    break;
                }
                removeCounters[k]++;
            }
        }
        for (var i = 0; i < removeCounters.length; i++){
            intChromo2[i] = new int[intChromo[i].length-removeCounters[i]];
            for (var j = 0; j < intChromo2[i].length; j++){
                intChromo2[i][j] = intChromo[i][j];
            }
        }
        var resChromos = new String[intChromo2.length];
        for (var j = 0; j < intChromo2.length; j++){
            resChromos[j] = "";
            for(var i = 0; i < intChromo2[j].length; i++){
                StringBuilder curGen = new StringBuilder(Integer.toBinaryString(intChromo2[j][i]));
                while (curGen.length() < specimen.bitInGen){
                    curGen.insert(0, "0");
                }
                resChromos[j] = resChromos[j] + curGen.toString();
            }
        }
        return new Specimen(resChromos, specimen.bitInGen);
    }

    private static int getBitInGen(int maxNum){
        return (int)Math.ceil(Math.log(maxNum)/Math.log(2));
    }
    private static void normalizeArray(Integer[][] chromosomes){
        for (var j = 0; j < chromosomes.length; j++){
            for (var i = 0; i < chromosomes[j].length-1; i++){
                if(ClientWeb.getTimesToOther()[chromosomes[j][i]][chromosomes[j][i+1]] == -1){
                    var waysList = getAvailableVertices(chromosomes[j][i]);
                    chromosomes[j][i+1] = waysList.get(0);
                }
            }
        }
    }
}
