import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
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

@Test
public void init() {
    String inputImagePath = "./TESTimage/test.jpg";
    File inputFile = new File(inputImagePath);
    BufferedImage inputImage = null;
    try {
        inputImage = ImageIO.read(inputFile);
    } catch (IOException e) {
        e.printStackTrace();
    }

       System.out.println( inputImage.getWidth());
       inputImage = resizeInputImageE(inputImage, 28,28, true);
       System.out.println(inputImage.getWidth());


        cutCut(inputImage, 1,7,16,9);



//    try {
//        ImageIO.write(resizeInputImageE(inputImage), "jpg", new File("./resized.jpg"));
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
}

    private static BufferedImage resizeInputImageE(BufferedImage inputImage, int width, int height, boolean shouldGreyscale){
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, width, height, null);
        g2d.dispose();

        if (shouldGreyscale) {
            RescaleOp rescaleOp = new RescaleOp(1.3f, -10, null);
            rescaleOp.filter(outputImage, outputImage);
        }

        return outputImage;
    }



//    @Test
//    public void fitImage(){
//        String inputImagePath = "./resized.jpg";
//        File inputFile = new File(inputImagePath);
//        BufferedImage inputImage = null;
//        try {
//            inputImage = ImageIO.read(inputFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        double[] inputArray = Main.getImagePixelsArray("./resized.jpg");
//        double[][] dataReshaped = new double[28][28];
//        for (int i=0, c=0; i<28; i++){
//            for (int j=0; j<28; j++, c++){
//                dataReshaped[i][j] = inputArray[c];
//            }
//        }
//        int top=0, left=0, right=0, bottom=0;
//
//        boolean pixFound = false;
//        for (int i=0; i<28; i++){
//            for (int j=0; j<28; j++){
//             if (dataReshaped[i][j]<72 && !pixFound){
//                 top=i;bottom=i;left=j;right=j;
//                 pixFound=true;
//             }
//
//             if (pixFound){
//                 if (dataReshaped[i][j]<72 && i<top){
//                     top = i;
//                 }
//                 if (dataReshaped[i][j]<72 && i>bottom){
//                     bottom = i;
//                 }
//                 if (dataReshaped[i][j]<72 && j<left){
//                     left = j;
//                 }
//                 if (dataReshaped[i][j]<72 && j>right){
//                     right = j;
//                 }
//             }
//            }
//        }
//
//        //TODO СДЕЛАТЬ ПРОВЕРКУ НА ПУСТОТУ ИЗОБРАЖЕННИЯ (если высота меньше, скажем, 4 пикселей и ширина < 1; или если вообще пикслеей не нешло (pix found false)
//
//        System.out.println(" top: " + top + " left: " + left + " bottom: " + bottom + " right: " + right);
//
//        int width = right - left;
//        int height = bottom - top;
//
//        if (width>12){
////            for (int i=top, h=0; i<bottom; i++, h++){
////                for (int j=left, w=0; j<right; j++, w++){
//                 cutCut(inputImage, top, left, bottom, right);
////                }
////            }
//        } else if (width+4>12){
//            double[][] imgPart = new double[height][width+4];
////            for (int i=top, h=0; i<bottom; i++, h++){
////                for (int j=left-2, w=0; j<right+2; j++, w++){
//                    cutCut(inputImage, top, left-2, bottom, right+2);
////                }
////            }
//        } else if (width + 8>12){
//        //    double[][] imgPart = new double[height][width+8];
////            for (int i=top, h=0; i<bottom; i++, h++){
////                for (int j=left-4, w=0; j<right+4; j++, w++){
//            cutCut(inputImage, top, left-4, bottom, right+4);
////                }
////            }
//        } else {
//            int addMe = (20-width)/2;
////            double[][] imgPart = new double[height][width+addMe*2];
////            for (int i=top, h=0; i<bottom; i++, h++){
////                for (int j=left-addMe, w=0; j<right+addMe; j++, w++){
//            cutCut(inputImage, top, left-addMe, bottom, right+addMe);
////                }
////            }
//        }
//    }
//

     static void cutCut(BufferedImage inputImage, int top, int left, int bottom, int right){
         BufferedImage dst = new BufferedImage(right-left+1, bottom-top+1, BufferedImage.TYPE_BYTE_GRAY);

         Graphics2D g = dst.createGraphics();
         g.drawImage(inputImage, -left, -top, 28, 28, null);
         g.dispose();

         BufferedImage outputImage =  resizeInputImageE(dst,20,20, false);
         BufferedImage newResized = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);

         Graphics graphics = newResized.getGraphics();
         graphics.setColor(Color.WHITE);
         graphics.fillRect(0, 0, 28, 28);
         graphics.setColor(Color.BLACK);
         graphics.drawImage(outputImage, 4, 4, null);
         graphics.dispose();

//         try {
//             ImageIO.write(newResized, "jpg", new File("./resized2.jpg"));
//         } catch (IOException e) {
//             e.printStackTrace();
//         }



    }

}