import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class NeuralNetwork {
    int iNodes; //input
    int hNodes; //hidden
    int oNodes; //output
    double lRate; //learningRate

    double[][] wIH; // weights Inputs - Hidden
    double[][] wHO; // weights Hidden - Outputs
    double[][] wIHTransposed; // weights Inputs - Hidden
    double[][] wHOTransposed; // weights Hidden - Outputs
    double[][] wIHTransposedUpdated; // weights Inputs - Hidden updated
    double[][] wHOTransposedUpdated; // weights Hidden - Outputs updated
    double[] inputsList;  //взходные данные
    double[] targetsList; //то, что мы хотим получить
    double[][] inputsListTransposed;  //взходные данные
    double[][] hiddenInputsTransposed;  //входные данные на скрытый слой(уже повернутый)
    double[][] hiddenOutputsTransposed;  //вЫходные данные ИЗ скрытый слой(уже повернутый)
    double[][] outNeuronsInputsTransposed; //входные данные на выходной слой (уже транспонированные)
    double[][] outNeuronsOutputsTransposed; //вЫходные данные ИЗ выходной слой (уже транспонированные)


    //TODO  //это из книги, объяснение того, как выбирать первоначальный рандом весов, но я сделал кривенько, но работает, так что нормуль
//    The rule of thumb these mathematicians arrive at
//    is that the weights are initialised randomly sampling
//    from a range that is roughly the inverse of the square
//    root of the number of links into a node.

//    So if each node has 3 links into it, the initial weights
//    should be in the range 1/(√3) = 0.577. If each node has 100
//    incoming links, the weights should be in the range 1/(√100) = 0.1.

    NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, double learningRate) {
        this.iNodes = inputNodes;
        this.hNodes = hiddenNodes;
        this.oNodes = outputNodes;
        this.lRate = learningRate;

        this.inputsList = new double[inputNodes];
        this.targetsList = new double[outputNodes];

        this.wIH = new double[inputNodes][hiddenNodes];
        this.wHO = new double[hiddenNodes][outputNodes];
        fillRandomWeights(wIH, hiddenNodes);
        fillRandomWeights(wHO, outputNodes);
        wIHTransposed = transpose2DimMatrix(wIH);
        wHOTransposed = transpose2DimMatrix(wHO);
    }

    //учим сеть
    public void train(double[] inputsList, double[] targetsList) {
        double[][] localInputsListTransposed;  //взходные данные
        double[][] localHiddenInputsTransposed;  //входные данные на скрытый слой(уже повернутый)
        double[][] localHiddenOutputsTransposed;  //вЫходные данные ИЗ скрытый слой(уже повернутый)
        double[][] localOutNeuronsInputsTransposed; //входные данные на выходной слой (уже транспонированные)
        double[][] localFinalOutputsTransposed; //выхзодыные даныне localOutNeuronsOutputsTransposed
        double[][] localTargetsListTransposed; // правильных результатов для тестов

        double[][] localOutputErrors; //ошибка
        double[][] localHiddenErrors; //ошибки на скрытых слоях

        //конвертируем список входных данных и правильных результатов для тестов в 2д массив
        localInputsListTransposed = transpose2DimMatrix(transpose1DimMatrix(inputsList));
        localTargetsListTransposed = transpose2DimMatrix(transpose1DimMatrix(targetsList));
        //считаем сигналы на скрытом слое
        localHiddenInputsTransposed = multiplyWeightsNInputs(wIHTransposed, localInputsListTransposed);
        localHiddenOutputsTransposed = activationFunction(localHiddenInputsTransposed);
        //считаем сигналы на выходном слое
        localOutNeuronsInputsTransposed = multiplyWeightsNInputs(wHOTransposed, localHiddenOutputsTransposed);
        localFinalOutputsTransposed = activationFunction(localOutNeuronsInputsTransposed);
        //считаем ошибку (таргет - полученное)
        localOutputErrors = getErrorTargetMinusActualTransposed(localFinalOutputsTransposed, localTargetsListTransposed);
        //распределяем ошибки
        localHiddenErrors = multiplyWeightsNInputs(transpose2DimMatrix(wHOTransposed), localOutputErrors);
        //обновляем веса сктырых - выходных
        wHOTransposed = updateWeights(lRate, wHOTransposed, localOutputErrors, localFinalOutputsTransposed, localHiddenOutputsTransposed);
        //обновляем веса входных - скрытых
        wIHTransposed = updateWeights(lRate, wIHTransposed, localHiddenErrors, localHiddenOutputsTransposed, localInputsListTransposed);
    }

    //вычисляем результат сети
    public double[][] query(double[] inputsList) {
        double[][] localInputsListTransposed;  //взходные данные
        double[][] localHiddenInputsTransposed;  //входные данные на скрытый слой(уже повернутый)
        double[][] localHiddenOutputsTransposed;  //вЫходные данные ИЗ скрытый слой(уже повернутый)
        double[][] localOutNeuronsInputsTransposed; //входные данные на выходной слой (уже транспонированные)
        double[][] localOutNeuronsOutputsTransposed; //выхзодыные даныне
        //конвертируем список входных данных в 2д массив
        localInputsListTransposed = transpose2DimMatrix(transpose1DimMatrix(inputsList));
        //считаем что приходит на нейроны скртого слоя
        localHiddenInputsTransposed = multiplyWeightsNInputs(wIHTransposed, localInputsListTransposed);
        //считаем что выходит из скрытых нейронов
        localHiddenOutputsTransposed = activationFunction(localHiddenInputsTransposed);
        //вычисляем сигналы на выходной слой
        localOutNeuronsInputsTransposed = multiplyWeightsNInputs(wHOTransposed, localHiddenOutputsTransposed);
        //считаем что выходит из выходного слоя
        localOutNeuronsOutputsTransposed = activationFunction(localOutNeuronsInputsTransposed);
        return localOutNeuronsOutputsTransposed;
    }

    //создаем рандомные веса для матриц
    private void fillRandomWeights(double[][] matrix, int rightLayerNodesCount) {
        double buf;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                do {
                    double random1 = Math.random() * Math.pow(rightLayerNodesCount, -0.5);
                    buf = Math.random() * 2 * random1 - random1;
                } while (buf == 0 || buf > 0.5 || buf < -0.5); //TODO тут стоит переделать, это было временным решением, но оно работает, так что трогать не буду, однажды переделаю
                matrix[i][j] = buf;
            }
        }
    }

    //вывод матрицы 2д
    private void print2DimMatrix(double[][] matrix) {
        System.out.println("MATRIX: ");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "  ");
                // System.out.print(" i:" + i + " j:" + j+"  ");
            }
            System.out.println();

        }
    }

    //вывод матрицы 1д
    private void print1DimMatrix(double[] matrix) {
        System.out.println("MATRIX: ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(matrix[i] + "  ");
            System.out.println();

        }
    }

    //функция активации сигмоид
    private static double sigmoid(double x) {
        return (1 / (1 + Math.pow(Math.E, (-1 * x))));
    }

    //применяем фуннкцию активации
    private double[][] activationFunction(double[][] inputs) {
        double[][] outputs = new double[inputs.length][inputs[0].length];
        for (int i = 0; i < inputs.length; i++) {
            outputs[i][0] = sigmoid(inputs[i][0]);
        }
        return outputs;
    }

    //транспонируем 2д матрицу
    private double[][] transpose2DimMatrix(double[][] m) {
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    //транспонируем 1д матрицу, //TODO стоило бы прямо в этом методе еще раз транспонировать (уже через 2д) ее перед возвратом, но я уже везде это сделал в ручную
    private double[][] transpose1DimMatrix(double[] m) {
        double[][] temp = new double[1][m.length];
        for (int i = 0; i < m.length; i++)
            temp[0][i] = m[i];

        return temp;
    }

//это шпаргалка по умножению матриц
//For matrix multiplication to take place, the number of columns of first matrix must be equal to the number of rows of second matrix. In our example, i.e.
//
//c1 = r2
//Also, the final product matrix is of size r1 x c2, i.e.
//
//product[r1][c2]

    //умножение матриц
    public double[][] multiplyWeightsNInputs(double[][] weightsTransposed, double[][] inputsTransposed) {
        double[] product = new double[weightsTransposed.length];
        double sum;
        for (int i = 0; i < weightsTransposed.length; i++) {
            sum = 0;
            for (int j = 0; j < weightsTransposed[i].length; j++) {
                sum += weightsTransposed[i][j] * inputsTransposed[j][0];
            }
            product[i] = sum;
        }

        return transpose2DimMatrix(transpose1DimMatrix(product));
    }

    private double[][] getErrorTargetMinusActualTransposed(double[][] actual, double[][] target) {

        double[][] newArrayTransposed = new double[target.length][target[0].length];
        for (int i = 0; i < actual.length; i++) {
            newArrayTransposed[i][0] = target[i][0] - actual[i][0];
        }

        return newArrayTransposed;
    }

    //корректировка весов синапсов
    private double[][] updateWeights(double learningRate, double[][] weightsMatrix, double[][] errorsRight, double[][] outputsRight, double[][] outputsLeft) {
        //тут, короче, должна была быть одна формула, но оно немножко не работало, пришлось немножко декомпозировать
        double[][] firstResult = first(errorsRight, outputsRight);
        double[][] secondResult = second(firstResult, outputsRight);
        double[][] thirdResult = third(secondResult, outputsLeft);
        double[][] fourthResult = fourth(thirdResult);
        double[][] fifthResult = fifth(fourthResult, weightsMatrix);                                  //правый и левый слои это типа ну как на картинке :)  входной - скрытый - выходной , что-то вроде этого, на картинке еще есть узлы и связи(нейроны и синапсы)
        //формула, кстати: весаСинапсов += скоростьОбучения * произведениеМатриц(ошибкиПравогоСлоя*выходныеДанныеПравогоСлоя*(1-выходныеДанныеПравогоСлоя),(выходныеДанныеЛевогоСлоя.Транспонированные))
        return fifthResult;
    }

    private double[][] first(double[][] rightError, double[][] rightOutput) {
        double[][] newArray = new double[rightError.length][rightError[0].length];
        for (int i = 0; i < rightError.length; i++) {
            newArray[i][0] = rightError[i][0] * rightOutput[i][0];
        }
        return newArray;
    }

    private double[][] second(double[][] firstResult, double[][] finalOutput) {
        double[][] minusArray = new double[finalOutput.length][finalOutput[0].length];
        double[][] partArray = new double[finalOutput.length][finalOutput[0].length];
        for (int i = 0; i < finalOutput.length; i++) {
            minusArray[i][0] = 1 - finalOutput[i][0];
        }

        for (int i = 0; i < finalOutput.length; i++) {
            minusArray[i][0] *= firstResult[i][0];
        }
        return minusArray;
    }

    private double[][] third(double[][] secondResult, double[][] hiddenOutput) {
        double[][] transposedArray = transpose2DimMatrix(hiddenOutput);

        double[][] arrayToReturn = dotMatrices(secondResult, transposedArray);  //тут все вот так с лишними переменными потому что я все это дебажил и выводил, принты убрал, а переменные остались
        return arrayToReturn;

    }

    //это дополнительный метод перемножения матриц, сделанный правильно (костыльный вариант выше, не подходил, пришлось делать еще раз)
    public double[][] dotMatrices(double[][] firstMatrix, double[][] secondMatrix) {

        double[][] product = new double[firstMatrix.length][secondMatrix[0].length];
        for (int i = 0; i < firstMatrix.length; i++) {
            for (int j = 0; j < secondMatrix[0].length; j++) {
                for (int k = 0; k < firstMatrix[0].length; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }
        return product;
    }

    private double[][] fourth(double[][] thirdResult) {
        double[][] toReturn = new double[thirdResult.length][thirdResult[0].length];
        for (int i = 0; i < thirdResult.length; i++) {
            for (int j = 0; j < thirdResult[0].length; j++) {
                toReturn[i][j] = this.lRate * thirdResult[i][j];
            }
        }
        return toReturn;
    }


    private double[][] fifth(double[][] fourthResult, double[][] weights) {
        double[][] toReturn = new double[fourthResult.length][fourthResult[0].length];

        for (int i = 0; i < fourthResult.length; i++) {
            for (int j = 0; j < fourthResult[0].length; j++) {
                toReturn[i][j] = weights[i][j] + fourthResult[i][j];
            }
        }
        return toReturn;
    }


/**ЭТА ЧАСТЬ ОТВЕЧАЕТ ЗА ОБРАТНЫЙ ПРОЦЕСС, ПОПРОСИМ СЕТЬ ВЫДАТЬ НАМ, ПО ЕЕ МНЕНИЮ, ИДЕАЛЬНУЮ ЦИФРУ */

    /**
     * The logit function is defined as logit(p) = log(p/(1-p)).
     * Note that logit(0) = -inf, logit(1) = inf, and logit(p) for p<0 or p>1 yields nan.
     */
    private static double logit(double x) {
        return (Math.log(x / (1 - x)));
    }

    //применяем фуннкцию активации
    private double[][] inverseActivationFunction(double[][] inputs) {
        double[][] outputs = new double[inputs.length][inputs[0].length];
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[0].length; j++) {
                outputs[i][j] = logit(inputs[i][j]);
            }
        }
        return outputs;
    }


    public double[][] backQuery(double[] targetsList) {

        double[][] localFinalOutputs = transpose2DimMatrix(transpose1DimMatrix(targetsList));
        double[][] localFinalInputs = inverseActivationFunction(localFinalOutputs);
        double[][] localHiddenOutputs = dotMatrices(transpose2DimMatrix(wHOTransposed), localFinalInputs);
        localHiddenOutputs = scaleBackQuery(localHiddenOutputs);
        double[][] localHiddenInputs = inverseActivationFunction(localHiddenOutputs);
        double[][] localInputs = dotMatrices(transpose2DimMatrix(wIHTransposed), localHiddenInputs);
        localInputs = scaleBackQuery(localInputs);

        return localInputs;

    }


    private double[][] scaleBackQuery(double[][] outputs){
        double[][] buf = new double[outputs.length][outputs[0].length];
        double min = outputs[0][0];
        double max = outputs[0][0];
        for (int i=0; i<outputs.length; i++){
            for (int j=0; j<outputs[i].length; j++){
                if (outputs[i][j]>max) {
                    max = outputs[i][j];
                }
                if (outputs[i][j]<min) {
                    min = outputs[i][j];
                }
            }
        }

        max += Math.abs(min);
        for (int i=0; i<outputs.length; i++){
            for (int j=0; j<outputs[i].length; j++){
                outputs[i][j] = ((outputs[i][j]+Math.abs(min))/max * 0.98) + 0.01;
            }
        }
        return outputs;
    }
//сохраняем память сети на диск
    void saveWeights(){
        BufferedWriter writerWih = null;
        BufferedWriter writerWho = null;
        try {
            File wIHFile = new File("./wIHTransposedMemory.csv");
            File wHOFile = new File("./wHOTransposedMemory.csv");
            if (wIHFile.exists() && wIHFile.isFile()){
                try {
                    wIHFile.delete();
                    wIHFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                wIHFile.createNewFile();
            }

            if (wHOFile.exists() && wHOFile.isFile()){
                try {
                    wHOFile.delete();
                    wHOFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                wHOFile.createNewFile();
            }


            writerWih = new BufferedWriter(new FileWriter(new File("./wIHTransposedMemory.csv"), true));
            writerWho = new BufferedWriter(new FileWriter(new File("./wHOTransposedMemory.csv"), true));

            for (int i =0; i<wIHTransposed.length; i++){
                for (int j =0; j<wIHTransposed[i].length; j++){
                    if (j!=wIHTransposed[i].length-1)
                        writerWih.write(wIHTransposed[i][j]+ ",");
                    else writerWih.write(String.valueOf(wIHTransposed[i][j]));
                }
                writerWih.write("\r");
            }
            writerWih.flush();


            for (int i =0; i<wHOTransposed.length; i++){
                for (int j =0; j<wHOTransposed[i].length; j++){
                    if (j!=wHOTransposed[i].length-1)
                        writerWho.write(wHOTransposed[i][j]+ ",");
                    else writerWho.write(String.valueOf(wHOTransposed[i][j]));
                }
                writerWho.write("\r");
            }
            writerWho.flush();

        } catch (IOException e) {
            System.err.println("Не удается создать/открыть/записать (в) файл с указанным именем! " + e);
        }
    }
//загружаем память с диска
     boolean loadWeights(){
            File wIHFile = new File("./wIHTransposedMemory.csv");
            File wHOFile = new File("./wHOTransposedMemory.csv");
            if (wIHFile.exists() && wIHFile.isFile() && wHOFile.exists() && wHOFile.isFile()){
                try {
                    ArrayList<String> wIHStrings = Main.readCSVfile(wIHFile.getPath());
                    ArrayList<String> wHOStrings = Main.readCSVfile(wHOFile.getPath());
                    if (wIHStrings.size()!=hNodes || wHOStrings.size()!=oNodes){
                        System.err.println("wIHStrings.size()!=hNodes || wHOStrings.size()!=oNodes         НАЧИНАЕТСЯ ОБУЧЕНИЕ");
                        return false;
                    }

                    for (int i=0; i<wIHStrings.size(); i++) {
                        List<String> recordString = Arrays.asList(wIHStrings.get(i).split("\\s*,\\s*"));

                        if (recordString.size()!=iNodes){
                            System.err.println("recordString.size()!= количеству ВХОДНЫХ НЕЙРОНОВ, начинается обучение");
                            return false;
                        }

                        for (int j=0; j<recordString.size(); j++){
                            wIHTransposed[i][j] = Double.parseDouble(recordString.get(j));
                        }
                    }

                    for (int i=0; i<wHOStrings.size(); i++) {
                        List<String> recordString = Arrays.asList(wHOStrings.get(i).split("\\s*,\\s*"));

                        if (recordString.size()!=hNodes){
                            System.err.println("recordString.size()!= количеству СКРЫТЫХ НЕЙРОНОВ, начинается обучение");
                            return false;
                        }


                        for (int j=0; j<recordString.size(); j++){
                            wHOTransposed[i][j] = Double.parseDouble(recordString.get(j));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
            }
            }
            else {
                System.err.println("ПАМЯТЬ НЕ НАЙДЕНА, НАЧИНАЕТСЯ ОБУЧЕНИЕ");
                return false;
            }
        System.err.println("ПАМЯТЬ ЗАГРУЖЕНА");
            return true;
    }



}


