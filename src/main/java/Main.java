import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    //список тренингов
    private static ArrayList<String> trainingDataList;
    //список тестов
    private static ArrayList<String> testDataList;

        /** ЭТО ВАЖНО! ТУТ УКАЗЫВАЮТСЯ ПУТИ К ТЕСТАМ И ТРЕНИНГАМ **/
        private static final String TESTPATH = "./mnist_test.csv";  //ТЕСТЫ  mnist_test_10 - маленький тест, mnist_test - большой
        private static final String TRAINPATH = "./mnist_train.csv"; //ТРЕНИРОВОЧНЫЕ ДАННЫЕ mnist_train_100 - короткий тренинг mnist_train - полный
        public static void main(String[] args){
        int inputNodes = 784;   //количество входных нейронов
        int hiddenNodes = 200;  //количество скрытых нейронов
        int outputNodes = 10;   //количество выходных нейронов
        int epochs = 2;         //количество эпох
        double learningRate = 0.1;  //скорость обучения
        //инициализируем сеть
        NeuralNetwork network = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes, learningRate);

        boolean isTrained = network.loadWeights();

        if (!isTrained) {

            System.err.println("ОБУЧЕНИЕ НАЧАЛОСЬ...");
            try {
                trainingDataList = readCSVfile(TRAINPATH);
            } catch (IOException e) {
                System.err.println("Файл с данными для обучения не существует! ");
                System.out.println(e);
                System.err.println("Проверьте путь!");
            }
            //вращаем и добавляем дополнительные данные для обучения
            System.err.println("TRAINING DATA BEFORE rotations: " + trainingDataList.size());
            rotateAndAddData();
            System.err.println("TRAINING DATA AFTER rotations: " + trainingDataList.size());
            for (int i = 0; i < epochs; i++) {
                for (String record : trainingDataList) {
                    //парсим нашу строку из тренинга
                    List<String> allTrainingDataValues = Arrays.asList(record.split("\\s*,\\s*"));
                    //значения хранятся в диапазоне от 0 до 255, изменяем под наш диапазон (от 0.01 до 1)
                    //первое значение в строке это ответ(ну типа то, что там нарисовано, нарисована семерка, значит первый элемент 7 и тд), поэтому игнорируем первый элемент
                    for (int j = 1; j < allTrainingDataValues.size(); j++) {
                        network.inputsList[j - 1] = (Double.parseDouble(allTrainingDataValues.get(j)) / 255.0 * 0.99) + 0.01;
                    }
                    //обнуляем все цели(0=0.01, чтобы не возникло проблем при корректировке весов)
                    for (int j = 0; j < outputNodes; j++) {
                        network.targetsList[j] = 0.01;
                    }
                    //высталяем в целях 0.99 как наиболее вероятный результат(у нас числа от 0 до 9, поэтому просто получаем первый элемент нашего ЦСВ, он будет и индексом элемента)
                    network.targetsList[Integer.parseInt(allTrainingDataValues.get(0))] = 0.99;
                    //запускаем обучение
                    network.train(network.inputsList, network.targetsList);
                }
            }

            network.saveWeights();
            System.err.println("ОБУЧЕНИЕ ОКОНЧЕНО");
/*** */
        }
            //открываем файл с тестами
        try {
            testDataList= readCSVfile(TESTPATH);
        } catch (IOException e) {
            System.err.println("Файл с данными для тестов не существует! ");
            System.out.println(e);
            System.err.println("Проверьте путь! или распакуйте архив MNIST.zip если еще не сделали этого");
            return;
        }

        System.out.println("тестирование...");
        getScorecard(network);
        System.out.println("тестирование окончено");
/*** */


        System.out.println("рукописи 28*28 пикселей");
        System.out.println();
        //бесконечный цикл для ввода своих изображений
        while (true) {
            try {
                System.err.println("Укажите путь к изображению (28x28 pix): ");
                Scanner scanner = new Scanner(System.in);
                String path = scanner.nextLine();
                if (path.equals("backquery") || path.equals("back query") || path.equals("backQuery") || path.equals("Backquery") || path.equals("Back Query")){
                    System.out.println("Введите цифру 0-9: ");
                    int label=19;
                    while (label>9 || label<0) {
                        try{
                            label = Integer.parseInt(scanner.nextLine());
                        }catch(NumberFormatException e){
                            System.out.println("Некорректное значение. Couldnt parse");
                        }
                        if (label>9 || label<0){
                            System.out.println("Некорректное значение, цифра должна быть в диапазоне от нуля до девяти");
                        }
                    }
                    double[] targets = new double[outputNodes];

                    for (int i=0; i<targets.length; i++){
                        targets[i] = 0.01;
                    }

                    targets[label]=0.99;

                    System.out.println("Targets: ");
                    for (double i : targets){
                        System.out.print(i + " ");
                    }

                    double[][] imageData = network.backQuery(targets);
                    System.out.println();
                    System.out.println("Пиксели цифры " + label + ": ");
                    for (int i=0; i<imageData.length; i++){
                        for (int j=0; j<imageData[i].length; j++){
                            System.out.print(imageData[i][j] + ", ");
                        }
                    }
                    System.out.println();
                } else
                getOwnAnswer(network, path);  //запускаем обработку изображения и вычисление значения
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    //конвертируем изображение в массив
    private static double[] getImagePixelsArray(String imagePath)  {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Укажите корректный путь к файлу!");
            System.out.println(e);
        }
            if (image!=null) {
                int imWidth = image.getWidth();
                int imHeight = image.getHeight();

                if (imWidth != 28 && imHeight != 28) {
                    System.err.println("РАЗМЕР ИЗОБРАЖЕНИЯ ДОЛЖЕН БЫТЬ 28x28");
                    return null;
                }
                double[] pixelsArray = new double[imWidth * imHeight];
                for (int y = 0, z = 0; y < imHeight; y++)
                    for (int x = 0; x < imWidth; x++, z++)
                        pixelsArray[z] = image.getRaster().getSampleDouble(x, y, 0);

                return pixelsArray;
            } else return null;
    }
        //обработка собственного изображения и вычисление значения
    private static void getOwnAnswer(NeuralNetwork network, String path) throws IOException {
        double[] imagePixelsTemp = getImagePixelsArray(path);
        if (imagePixelsTemp==null){
            return;
        }
        double[] imagePixelsNormalized = new double[imagePixelsTemp.length];
        double[] imagePixels = new double[imagePixelsNormalized.length];
       for (int z=0; z<imagePixelsTemp.length; z++){
           imagePixelsNormalized[z] = 255.0 - imagePixelsTemp[z]; //мы получили пиксели в формате 255 -- полностью белый, 0 - черный. в MNIST все наоборот, поэтому "нормализуем" из
       }
        for (int z=0; z<imagePixelsNormalized.length; z++){
            imagePixels[z] = (imagePixelsNormalized[z]/255.0 * 0.99) + 0.01;  //превращаем диапазон (0-255 в 0.01-1)
        }
        //начинаем вычисление
        double[][] result = network.query(imagePixels);

        int max =0; //выбираем максимальный элемент
        System.out.println("Вероятности: ");
        for (int j=0; j<result.length; j++){
            System.out.println(j + ": " + result[j][0]);
            if (result[j][0]>result[max][0]){
                max=j;
            }
        }

        System.err.println("Значение: " + max);

    }
            //рассчитываем точность сети после теста
    private static void getScorecard(NeuralNetwork network){

        ArrayList<Integer> scorecard = new ArrayList<Integer>();
        for (int i=0; i<testDataList.size(); i++){
            List<String> testDataValues = Arrays.asList(testDataList.get(i).split("\\s*,\\s*"));  //парсим
            int correctLabel = Integer.parseInt(testDataValues.get(0)); //берем первый элемент(выше объяснял) это наш правильный ответ

            double[] tempInputs = new double[testDataValues.size()-1];
                    //все по старой схеме
            for (int z = 1; z< testDataValues.size(); z++){
                tempInputs[z-1] = (Double.parseDouble(testDataValues.get(z)) / 255.0 * 0.99) + 0.01; //превращаем диапазон (0-255 в 0.01-1)
            }

            double[][] result = network.query(tempInputs);
            int maxValue=0;
            for (int j=0; j<result.length; j++){
                if (result[j][0]>result[maxValue][0]){
                    maxValue=j;
                }
            }
                //если сеть угадала, добавляем 1, иначе 0
            if (maxValue== correctLabel){
                scorecard.add(1);
            } else scorecard.add(0);

        }
            //считаем точность в процентах
        double sum = 0;
        for (int record: scorecard){
            sum+=record;
        }
        double performance = sum / scorecard.size();

        System.out.println("Тесты пройдены с вероятностью: " + String.format("%(.2f", (performance*100))+"%");


    }

            //читаем CSV файл, возвращает список строк
    static ArrayList<String> readCSVfile (String filePath) throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath));

        String strLine;
        while ((strLine = csvReader.readLine()) != null){
            list.add(strLine);
        }
        csvReader.close();
        return list;
    }


//вращаем тренинговые данные на +/-10 градусов
    private static void rotateAndAddData (){
            ArrayList<String> toAddPlus = new ArrayList<>();   //для того, чтобы данные были более разрежены, не было такого, что все цифры повторяются 2 раза
            ArrayList<String> toAddMinus = new ArrayList<>();
            for (String record: trainingDataList) {

                List<String> allTrainingValues = Arrays.asList(record.split("\\s*,\\s*"));


                int[][] dataReshaped = new int[28][28];
                int[][] dataFlippedPlus = new int[28][28];
                int[][] dataFlippedMinus = new int[28][28];
                for (int i=0; i<dataFlippedPlus.length; i++){
                    for (int j=0; j<dataFlippedPlus[i].length; j++){
                        dataFlippedPlus[i][j]= 0;
                        dataFlippedMinus[i][j]= 0;
                    }
                }

                int c=1;
                for (int i=0; i<28; i++){
                    for (int j=0; j<28; j++, c++){
                        dataReshaped[i][j] = Integer.parseInt(allTrainingValues.get(c));
                    }
                }

                Position tempPosition;

                for (int i=0; i<dataReshaped.length; i++){              //TODO сделать нормально, типа сразу проверку и изменение позиций, подумать над этим
                    for (int j=0; j<dataReshaped[i].length; j++){

                        tempPosition = rotatePixel(new Position(i, j), true);
                        if (tempPosition.getI()>27 || tempPosition.getI()<0 || tempPosition.getJ()>27 || tempPosition.getJ()<0){
                            continue;
                        } else {
                            dataFlippedPlus[tempPosition.getI()][tempPosition.getJ()] = dataReshaped[i][j];
                        }


                        tempPosition = rotatePixel(new Position(i, j), false);
                        if (tempPosition.getI()>27 || tempPosition.getI()<0 || tempPosition.getJ()>27 || tempPosition.getJ()<0){
                            continue;
                        } else {
                            dataFlippedMinus[tempPosition.getI()][tempPosition.getJ()] = dataReshaped[i][j];
                        }

                    }
                }

                arrayElementsToStringList(dataFlippedPlus, toAddPlus, allTrainingValues.get(0));
                arrayElementsToStringList(dataFlippedMinus, toAddMinus, allTrainingValues.get(0));
            }

                //TODO еще раз проверить, все ли правильно добавляет
        trainingDataList.addAll(toAddPlus);
        trainingDataList.addAll(toAddMinus);

    }

//вращаем пиксель
    private static Position rotatePixel(Position defPosition, boolean plus){
        double tempI;
        double tempJ;

        Position tempPosition = new Position(defPosition.getI(), defPosition.getJ());

        tempPosition.setI(tempPosition.getI()-13);
        tempPosition.setJ(tempPosition.getJ()-13);

        if (plus) {
            ///+10градусов
            tempI = (tempPosition.getI() * 0.984) + (tempPosition.getJ() * 0.173);
            tempJ = -1 * (tempPosition.getI() * 0.173) + (tempPosition.getJ() * 0.984);
        } else {
            ///-10градусов
            tempI = (tempPosition.getI() * 0.984) - (tempPosition.getJ() * 0.173);
            tempJ = (tempPosition.getI() * 0.173) + (tempPosition.getJ() * 0.984);
        }
        ///13-13 - точка центра
        tempI+=13;
        tempJ+=13;
        ///округляем
        int iPlus = (int) Math.round(tempI);
        int jPlus = (int) Math.round(tempJ);

        return new Position(iPlus, jPlus);
    }
//конвертируем двумерный массив в строку
    private static void arrayElementsToStringList(int[][] twoDimArray, ArrayList<String> list, String target) {

        ArrayList<String> temp = new ArrayList<>();
        temp.add(target);
        for (int i=0; i<twoDimArray.length; i++){
            for (int j=0; j<twoDimArray[i].length; j++){
                temp.add(String.valueOf(twoDimArray[i][j]));
            }
        }
        String resultPlus = temp.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        list.add(resultPlus);
    }

}
