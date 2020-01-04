

import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class dataProssecingSemanticCorpus {
    private Path PostingFiles ;
    private Path AllDataMetrix;
    private Path outputPath ;

    public dataProssecingSemanticCorpus(Path postingFiles, Path allDataMetrix , Path outputPath) {
        PostingFiles = postingFiles;
        AllDataMetrix = allDataMetrix;
        this.outputPath = outputPath;
    }
    public void startProssecing(){
    //    String fileName = "DocToEntities.txt";
        InversedFileReader inversedFileReader = new InversedFileReader(PostingFiles) ;
        BufferedReader objReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader(AllDataMetrix.toString()));
            int index = 0 ;
            String term ;
            boolean stop = false;
            while (!stop) {
                try {
                    if((strCurrentLine = objReader.readLine()) != null) {
                     //   System.out.println(strCurrentLine);
                        term = strCurrentLine.substring(0, strCurrentLine.indexOf(" "));
                      //  System.out.println(term);
                        if (inversedFileReader.searchIfExistTerm(term)) {
                            sb.append(strCurrentLine + "\n");
                            System.out.println(strCurrentLine);
                            index++;
                        }
                    }else{
                        stop = true;
                    }
                    if (index >= 10000 || stop ) {
                        System.out.println("cycle");
                        byte allVocInBytes[] = sb.toString().getBytes();
                        try (OutputStream out = new BufferedOutputStream(
                                Files.newOutputStream(outputPath, CREATE, APPEND))) {
                            out.write(allVocInBytes, 0, allVocInBytes.length);
                            out.flush();
                        } catch (IOException x) {
                            System.err.println(x);
                        }
                        sb = new StringBuilder();
                        index = 0;
                    }
                }catch (Exception e){e.getStackTrace();}
            }
          //  System.out.println(strCurrentLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private double absolutVals[];
    public void calcNclosestWords(Path outputPathResult , int lengthOfInput){
        absolutVals = new double[lengthOfInput];
        //    String fileName = "DocToEntities.txt";
        BufferedReader objReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader(outputPath.toString()));
            PriorityQueue<Pair<String,Double>> pq = new PriorityQueue<>(new Comparator<Pair<String, Double>>() {
                @Override
                public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
                    if(o1.getValue() < o2.getValue()){
                        return 1;
                    }
                    if(o1.getValue() > o2.getValue()){
                        return -1;
                    }
                    return 0;
                }
            });
            int index = 0;
            String term;
            int indexCycle = 0;
            boolean firstRun = true ;
            while ((strCurrentLine = objReader.readLine()) != null) {
                BufferedReader objReader2 = new BufferedReader(new FileReader(outputPath.toString()));
                String strCurrentLine2;
                String[] Vector1  = strCurrentLine.split(" ");
                while ((strCurrentLine2 = objReader2.readLine()) != null) {
                    String[] Vector2  = strCurrentLine2.split(" ");
                    try {
                        if(Vector1.length != Vector2.length){
                            System.out.println("error");
                        }
                        else {
                            double[] vectorD1 = new  double[Vector1.length] ;
                            for(int i =1 ; i< Vector1.length ; i++)
                                vectorD1[i] = Double.parseDouble(Vector1[i]) ;
                            double[] vectorD2 = new  double[Vector1.length] ;
                            for(int i =1 ; i< Vector2.length ; i++)
                                vectorD2[i] = Double.parseDouble(Vector2[i]) ;
                            pq.add(new Pair<>( Vector2[0] , cosineSimilarity(vectorD1,vectorD2))) ;
                            if(pq.size() > 500){
                                Pair<String  , Double>[] tempPd = new Pair[pq.size()];
                                pq.toArray(tempPd);
                                pq.removeAll(pq);
                                for(int i=0; i<10 ;i++){
                                    pq.add(tempPd[i]);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
                sb.append(Vector1[0]+" = ");
                for(int i=0 ; i<10 ; i++){
                    Pair<String,Double> ele = pq.poll();
                    sb.append(ele.getKey()+"="+ ele.getValue() +" | ");
                }
                sb.append("\n");
                index++;
                if (index >= 100) {
                    System.out.println(100*indexCycle++);
                    byte allVocInBytes[] = sb.toString().getBytes();
                    try (OutputStream out = new BufferedOutputStream(
                            Files.newOutputStream(outputPathResult, CREATE, APPEND))) {
                        out.write(allVocInBytes, 0, allVocInBytes.length);
                        out.flush();
                    } catch (IOException x) {
                        System.err.println(x);
                    }
                    sb = new StringBuilder();
                    index = 0;
                }
            }
                System.out.println(strCurrentLine);
            } catch(IOException e){
                e.printStackTrace();
            } finally{
                try {
                    if (objReader != null)
                        objReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        byte allVocInBytes[] = sb.toString().getBytes();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(outputPathResult, CREATE, APPEND))) {
            out.write(allVocInBytes, 0, allVocInBytes.length);
            out.flush();
        } catch (IOException x) {
            System.err.println(x);
        }
    }
    public double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    public double cosineSimilarity(double[] vectorA, double[] vectorB ,double normA ,double normB) {
        double dotProduct = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    public void calcNclosestWordsNewV(Path outputPathResult , int lengthOfInput){
        absolutVals = new double[lengthOfInput];
        //    String fileName = "DocToEntities.txt";
        BufferedReader objReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader(outputPath.toString()));
            PriorityQueue<Pair<String,Double>> pq = new PriorityQueue<>(new Comparator<Pair<String, Double>>() {
                @Override
                public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
                    if(o1.getValue() < o2.getValue()){
                        return 1;
                    }
                    if(o1.getValue() > o2.getValue()){
                        return -1;
                    }
                    return 0;
                }
            });
            int index = 0;
            String term;

            boolean firstRun = true ;
            Pair<String ,   Pair<double[] , Double>>[]  data = new Pair[lengthOfInput] ;
            try {
                while ((strCurrentLine = objReader.readLine()) != null) {
                    System.out.println("f" + index);
                    String[] Vector1 = strCurrentLine.split(" ");
                    double[] vectorD1 = new double[Vector1.length];
                    for (int i = 1; i < Vector1.length; i++)
                        vectorD1[i] = Double.parseDouble(Vector1[i]);
                    double normB = 0;
                    for (int i = 0; i < vectorD1.length; i++) {
                        normB += Math.pow(vectorD1[i], 2);
                    }
                    data[index] = new Pair<String, Pair<double[], Double>>(Vector1[0], new Pair<>(vectorD1, normB));
                    index++;
                }
            }catch (Exception e){e.getStackTrace();}
            System.out.println("D");

            for (int i=0 ; i <  data.length ; i++){
              //  System.out.println(i);
                for (int j=0 ; j < data.length ; j++){
                    if(data[i].getKey().equals(data[j].getKey())){
                        continue;
                    }
                    double resSim = cosineSimilarity(data[i].getValue().getKey(),data[j].getValue().getKey(), data[i].getValue().getValue(),data[j].getValue().getValue());
                    pq.add(new Pair<>( data[j].getKey() , resSim)) ;
                    if(pq.size() > 500){
                        Pair<String  , Double>[] tempPd = new Pair[pq.size()];
                        pq.toArray(tempPd);
                        pq.removeAll(pq);
                        for(int h=0; h<10 ;h++){
                            pq.add(tempPd[h]);
                        }
                    }
                }
                sb.append(data[i].getKey()+" = ");
                for(int h=0 ; h<10 ; h++){
                    Pair<String,Double> ele = pq.remove();
                    sb.append(ele.getKey()+"="+ ele.getValue() +" | ");
                }
                sb.append("\n");
               // index++;
              //  if (index >= 100) {

                    System.out.println(i);
                byte allVocInBytes[] = sb.toString().getBytes();
                try (OutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(outputPathResult, CREATE, APPEND))) {
                    out.write(allVocInBytes, 0, allVocInBytes.length);
                    out.flush();
                } catch (IOException x) {
                    System.err.println(x);
                }
                sb = new StringBuilder();
                //    index = 0;
             //   }
            }

        } catch(IOException e){
            e.printStackTrace();
        } finally{
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

//        byte allVocInBytes[] = sb.toString().getBytes();
//        try (OutputStream out = new BufferedOutputStream(
//                Files.newOutputStream(outputPathResult, CREATE, APPEND))) {
//            out.write(allVocInBytes, 0, allVocInBytes.length);
//            out.flush();
//        } catch (IOException x) {
//            System.err.println(x);
//        }
    }
}
