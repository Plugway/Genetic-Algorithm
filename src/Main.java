import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    private static String ClientsPath = "./Clients.txt";
    private static String EmployeesPath = "./Employees.txt";
    public static int salarySetting = 5000; //зарплата за клиента
    public static int breakCounterSetting = 3000; //если предыдущий результат лучше то убавляется на 1, иначе возвращается
    public static int crossoverPosibility = 80; //Вероятность кроссинговера, иначе - мутации.
    public static double employeeMultiplierAdd = 0.5;
    public static void main(String[] args) {

        var startTime = System.currentTimeMillis();
        System.out.print("Чтение клиентов - ");

        try{
            ClientWeb.Init(ClientsPath);
        } catch (Exception e) {
            System.out.println("Ошибка чтения клиентов: "+e.getMessage());
            throw new Error();
        }

        System.out.println((System.currentTimeMillis() - startTime)/1000.0 + " сек.");
        startTime = System.currentTimeMillis();
        System.out.print("Чтение сотрудников - ");

        try {
            Employee.Init(EmployeesPath);
        } catch (IOException e) {
            System.out.println("Ошибка чтения сотрудников: "+e.getMessage());
            throw new Error();
        }

        System.out.println((System.currentTimeMillis() - startTime)/1000.0 + " сек.");
        startTime = System.currentTimeMillis();
        System.out.println("\nРабота алгоритма: ");

        var population1 = Specimen.getRandomPopulation(50);
        var fitnessOfPopulation = new int[population1.length];
        var averageFitness = 0;
        var fitnessSum = 0;
        var maxFitness = Integer.MIN_VALUE;
        var minFitness = Integer.MAX_VALUE;
        for (var i = 0; i < population1.length; i++){
            fitnessOfPopulation[i] = Specimen.getFitness(population1[i]);
            fitnessSum += fitnessOfPopulation[i];
            if (maxFitness < fitnessOfPopulation[i])
                maxFitness = fitnessOfPopulation[i];
            if(minFitness > fitnessOfPopulation[i])
                minFitness = fitnessOfPopulation[i];
        }
        var possibilityFitnessSum = 0;
        for (var i = 0; i < fitnessOfPopulation.length; i++){
            possibilityFitnessSum += fitnessOfPopulation[i] - minFitness;
        }
        averageFitness = fitnessSum/population1.length;

        var population2 = new Specimen[50];
        var breakCounter = breakCounterSetting;
        var prevMaxFitness = Integer.MIN_VALUE;
        var bestFitness = Integer.MIN_VALUE;
        var worstFitness = Integer.MAX_VALUE;
        var iterationCounter = 0;
        Specimen bestSpec = population1[0];
        while (true){
            iterationCounter++;
            if (iterationCounter%100 == 0)
                System.out.print("\rСредняя приспособленность: "+averageFitness +
                        "; Лучшая приспособленность: " + bestFitness + "; Поколение: " + iterationCounter);
            if (bestFitness >= maxFitness){  //prevMaxFitness >= maxFitness
                breakCounter--;
            } else {
                breakCounter = breakCounterSetting;
            }
            if (bestFitness < maxFitness){
                bestFitness = maxFitness;
                var fitnessOfPopulationList = Arrays.stream(fitnessOfPopulation).boxed().collect(Collectors.toList());
                bestSpec = population1[fitnessOfPopulationList.indexOf(maxFitness)];
            }
            if (worstFitness > minFitness)
                worstFitness = minFitness;
            if (breakCounter == 0)
                break;

            TreeMap<Double, Specimen>  map = new TreeMap<>();
            double total = 0.0d;
            for (var i = 0; i < population1.length; i++){
                map.put(total+=(fitnessOfPopulation[i]-minFitness)*1.0/possibilityFitnessSum, population1[i]);
            }
            Random generator = new Random();
            for (var i = 0; i < population2.length; i++){
                population2[i] = map.ceilingEntry(generator.nextDouble()).getValue();
            }

            population1 = Specimen.mutationOrCrossover(population2);

            fitnessSum = 0;
            possibilityFitnessSum = 0;
            prevMaxFitness = maxFitness;
            maxFitness = Integer.MIN_VALUE;
            minFitness = Integer.MAX_VALUE;
            for (var i = 0; i < population1.length; i++){
                fitnessOfPopulation[i] = Specimen.getFitness(population1[i]);
                fitnessSum += fitnessOfPopulation[i];
                if(minFitness > fitnessOfPopulation[i])
                    minFitness = fitnessOfPopulation[i];
                if (maxFitness < fitnessOfPopulation[i])
                    maxFitness = fitnessOfPopulation[i];
            }
            for (var i = 0; i < fitnessOfPopulation.length; i++){
                possibilityFitnessSum += fitnessOfPopulation[i] - minFitness;
            }
            averageFitness = fitnessSum/population1.length;
        }
        bestSpec = Specimen.truncateSpecimen(bestSpec);
        System.out.println();
        Specimen.printSolution(bestSpec, "Худшая приспособленность: "+worstFitness+
                "; Текущая приспособленность: "+bestFitness + "; Средняя приспособленность: " + averageFitness);
        System.out.println("\nВремя работы алгоритма: "+(System.currentTimeMillis() - startTime)/1000.0 + " сек.");
    }
    public static String getFileContent(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
