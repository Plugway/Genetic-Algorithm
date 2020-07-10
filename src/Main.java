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
    private static String ConfigPath = "./Config.txt";
    private static String ClientsGraphPath = "./ClientsGraph.txt";
    private static String GraphPath = "./graph.graphml";
    public static int salarySetting = 5000; //зарплата за клиента
    public static int breakCounterSetting = 3000; //если предыдущий результат лучше то убавляется на 1, иначе возвращается
    public static int crossoverPossibility = 80; //Вероятность кроссинговера, иначе - мутации.
    public static double employeeMultiplierAdd = 0.5;
    public static int populationSize = 50;
    public static boolean htmlGraph = true;

    public static void main(String[] args) {

        var startTime = System.currentTimeMillis();
        System.out.print("Чтение конфигурации - ");     //Чтение конфигурации

        try {
            InitConfig();
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла конфигурации: "+e.getMessage());
            throw new Error();
        }

        System.out.println((System.currentTimeMillis() - startTime)/1000.0 + " сек.");
        startTime = System.currentTimeMillis();
        System.out.print("Чтение клиентов - ");         //Чтение клиентов

        try{
            if (htmlGraph)
                ClientWeb.Init2(GraphPath, ClientsGraphPath);
            else
                ClientWeb.Init(ClientsPath);
        } catch (Exception e) {
            System.out.println("Ошибка чтения клиентов: "+e.getMessage());
            e.printStackTrace();
            throw new Error();
        }

        System.out.println((System.currentTimeMillis() - startTime)/1000.0 + " сек.");
        startTime = System.currentTimeMillis();
        System.out.print("Чтение сотрудников - ");      //Чтение сотрудников

        try {
            Employee.Init(EmployeesPath);
        } catch (IOException e) {
            System.out.println("Ошибка чтения сотрудников: "+e.getMessage());
            throw new Error();
        }

        System.out.println((System.currentTimeMillis() - startTime)/1000.0 + " сек.");
        startTime = System.currentTimeMillis();
        System.out.println("\nРабота алгоритма: ");

        var population1 = Specimen.getRandomPopulation(populationSize);     //Создаем случайную популяцию из n особей, где n - четное
        var fitnessOfPopulation = new int[population1.length];              //Массив приспособленности популяции
        var averageFitness = 0;                                             //Средняя приспособленность
        var fitnessSum = 0;                                                 //Сумма приспособленностей
        var maxFitness = Integer.MIN_VALUE;                                 //Максимальная приспособленность
        var minFitness = Integer.MAX_VALUE;                                 //Минимальная
        for (var i = 0; i < population1.length; i++){                       //Считаем приспособленность поколения 0
            fitnessOfPopulation[i] = Specimen.getFitness(population1[i]);
            fitnessSum += fitnessOfPopulation[i];
            if (maxFitness < fitnessOfPopulation[i])
                maxFitness = fitnessOfPopulation[i];
            if(minFitness > fitnessOfPopulation[i])
                minFitness = fitnessOfPopulation[i];
        }
        var possibilityFitnessSum = 0;                                      //Сумма приспособленностей для расчета вероятности
        for (var i = 0; i < fitnessOfPopulation.length; i++){               //Считаем
            possibilityFitnessSum += fitnessOfPopulation[i] - minFitness;
        }
        averageFitness = fitnessSum/population1.length;                     //Средняя п.

        var population2 = new Specimen[populationSize];                     //2 популяция
        var breakCounter = breakCounterSetting;                             //Счетчик остановки
        var prevMaxFitness = Integer.MIN_VALUE;
        var bestFitness = Integer.MIN_VALUE;                                //Макс. п. за все время
        var worstFitness = Integer.MAX_VALUE;                               //Мин. п. за все время
        var iterationCounter = 0;                                           //Счетчик итераций
        Specimen bestSpec = population1[0];

        while (true){                                                       //Основной цикл
            iterationCounter++;
            if (iterationCounter%100 == 0)                                  //Обновляем информацию раз в 100 поколений
                System.out.print("\rСредняя приспособленность: "+averageFitness +
                        "; Лучшая приспособленность: " + bestFitness + "; Поколение: " + iterationCounter);
            if (bestFitness >= maxFitness){  //prevMaxFitness >= maxFitness //Условия уменьшения счетчика
                breakCounter--;
            } else {                                                        //Иначе, возвращаем значение на стандартное
                breakCounter = breakCounterSetting;
            }
            if (bestFitness < maxFitness){                                  //Записываем лучшую особь за все время
                bestFitness = maxFitness;
                var fitnessOfPopulationList = Arrays.stream(fitnessOfPopulation).boxed().collect(Collectors.toList());
                bestSpec = population1[fitnessOfPopulationList.indexOf(maxFitness)];
            }
            if (worstFitness > minFitness)
                worstFitness = minFitness;
            if (breakCounter == 0)                                          //Если счетчик == 0, то завершаем работу
                break;
            //
            TreeMap<Double, Specimen>  map = new TreeMap<>();               //Выбираем особей для создания нового поколения
            double total = 0.0d;                                            //методом рулетки
            for (var i = 0; i < population1.length; i++){
                map.put(total+=(fitnessOfPopulation[i]-minFitness)*1.0/possibilityFitnessSum, population1[i]);
            }
            Random generator = new Random();
            for (var i = 0; i < population2.length; i++){
                population2[i] = map.ceilingEntry(generator.nextDouble()).getValue();
            }
            //
            population1 = Specimen.mutationOrCrossover(population2);        //Создаем новое поколение

            fitnessSum = 0;
            possibilityFitnessSum = 0;
            prevMaxFitness = maxFitness;
            maxFitness = Integer.MIN_VALUE;
            minFitness = Integer.MAX_VALUE;
            for (var i = 0; i < population1.length; i++){                   //Считаем приспособленность поколения n
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
        bestSpec = Specimen.truncateSpecimen(bestSpec);                     //Сокращаем пути лучшей особи
        System.out.println();                                               //Печатаем результат
        Specimen.printSolution(bestSpec, "Худшая приспособленность: "+worstFitness+
                "; Текущая приспособленность: "+bestFitness + "; Средняя приспособленность: " + averageFitness);
        System.out.println("\nВремя работы алгоритма: "+(System.currentTimeMillis() - startTime)/1000.0 + " сек.");
    }
    public static String getFileContent(String path) throws IOException {   //Считывает текст из файла
        return new String(Files.readAllBytes(Paths.get(path)));
    }
    public static void InitConfig() throws IOException {                    //Конфигурация
        var config = getFileContent(ConfigPath).split("\n");
        for (var i = 0; i < config.length; i++){
            config[i] = config[i].split(";")[0];
        }
        salarySetting = Integer.parseInt(config[0]);
        breakCounterSetting = Integer.parseInt(config[1]);
        crossoverPossibility = Integer.parseInt(config[2]);
        employeeMultiplierAdd = Double.parseDouble(config[3]);
        populationSize = Integer.parseInt(config[4]);
        htmlGraph = config[5].equals("1");
    }
}
