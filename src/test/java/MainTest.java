import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void rotateAndAddData (){
        ArrayList<String> trainingDataList = new ArrayList<>();
        trainingDataList.add("7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,84,185,159,151,60,36,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,222,254,254,254,254,241,198,198,198,198,198,198,198,198,170,52,0,0,0,0,0,0,0,0,0,0,0,0,67,114,72,114,163,227,254,225,254,254,254,250,229,254,254,140,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,17,66,14,67,67,67,59,21,236,254,106,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,83,253,209,18,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,22,233,255,83,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,129,254,238,44,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,59,249,254,62,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,133,254,187,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,205,248,58,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,126,254,182,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,75,251,240,57,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19,221,254,166,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,203,254,219,35,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,38,254,254,77,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,31,224,254,115,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,133,254,254,52,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,61,242,254,254,52,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,121,254,254,219,40,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,121,254,207,18,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");

        ArrayList<String> toAddPlus = new ArrayList<>();   //для того, чтобы данные были более разрежены, не было такого, что все цифры повторяются 2 раза
        ArrayList<String> toAddMinus = new ArrayList<>();
        for (String record: trainingDataList) {

            List<String> allTrainingValues = Arrays.asList(record.split("\\s*,\\s*"));

            ArrayList<Position> plusPositions = new ArrayList<>();
            ArrayList<Position> minusPositions = new ArrayList<>();

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
                        if (dataFlippedPlus[tempPosition.getI()][tempPosition.getJ()]!=0){
                            System.out.println("!= 0 !!!!!!!!!!!!! PLUS");
                        }
                        dataFlippedPlus[tempPosition.getI()][tempPosition.getJ()] = dataReshaped[i][j];
                    }


                    tempPosition = rotatePixel(new Position(i, j), false);
                    if (tempPosition.getI()>27 || tempPosition.getI()<0 || tempPosition.getJ()>27 || tempPosition.getJ()<0){
                        continue;
                    } else {
                        if (dataFlippedMinus[tempPosition.getI()][tempPosition.getJ()]!=0){
                            System.out.println("!= 0 !!!!!!!!!!!!! MINUS");
                        }
                        dataFlippedMinus[tempPosition.getI()][tempPosition.getJ()] = dataReshaped[i][j];
                    }

                }
            }

            arrayElementsToStringList(dataFlippedPlus, toAddPlus, allTrainingValues.get(0));
            arrayElementsToStringList(dataFlippedMinus, toAddMinus, allTrainingValues.get(0));
        }


        trainingDataList.addAll(toAddPlus);
        trainingDataList.addAll(toAddMinus);

    }


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

    public static void arrayElementsToStringList(int[][] twoDimArray, ArrayList<String> list, String target) {

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



    @Test
    public void arrayElementsToStringList() throws IOException {
        double value = 0.4587516989212365478851236812121223213;
        System.out.println(value);
        System.out.println();


        String stroka = "-8.230478518947536E-4";
        double exp = Double.parseDouble(stroka);
        System.err.println("!!!!!!!!!!!!!!");
        System.out.println(exp);
        System.out.println(exp*100000);



        double[][] array = new double[4][4];
        for (int i =0; i<4; i++){
            for (int j =0; j<4; j++){
                value+=7.425;
                array[i][j]=value;
            }
        }

//        File wIHFile = new File("deleteMe.csv");
//        if (wIHFile.exists() && wIHFile.isFile())
//        {
//            wIHFile.delete();
//        }


//        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("deleteMe.csv"), true));
//
//        for (int i =0; i<4; i++){
//            for (int j =0; j<4; j++){
//                if (j!=3)
//                writer.write(array[i][j]+ ",");
//                else writer.write(String.valueOf(array[i][j]));
//            }
//            writer.write("\r");
//        }
//        writer.flush();

    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }


}