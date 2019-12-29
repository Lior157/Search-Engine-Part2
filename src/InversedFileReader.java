import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InversedFileReader {

    private Path inversedFilesFolder;

    public InversedFileReader(Path inversedFilesFolder) {
        this.inversedFilesFolder = inversedFilesFolder;
    }
    public Double[] readInformationAboutCorpus(){
        Double[] result = new Double[2];
        try {
            Path path = Paths.get(inversedFilesFolder + "//@InformationAboutCorpus.txt");
            String text = new String(Files.readAllBytes(path));
            String[] splitedLinesInput = text.split("\n");
            result[0] = Double.parseDouble(splitedLinesInput[0]);
            result[1] = Double.parseDouble(splitedLinesInput[1]);
        }catch (Exception e){}
        return result ;
    }
    public Pair<Pair<String,Integer>, Map<Integer,Integer>> readTermInformation(String term){
        String fileName;
        char firstTermLetter = term.charAt(0) ;
        if(firstTermLetter >=65 && firstTermLetter<=90){
            firstTermLetter = (char) (((int) firstTermLetter) + 22) ;
            fileName = "@"+firstTermLetter+".txt";
        }if(firstTermLetter >= 97 && firstTermLetter <= 122){
            fileName = "@"+firstTermLetter+".txt";
        }else{
            fileName = "number&sign.txt";
        }
        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            Path path = Paths.get(inversedFilesFolder + "//"+fileName);
            objReader = new BufferedReader(new FileReader(path.toString()));
            while ((strCurrentLine = objReader.readLine()) != null) {
                if(strCurrentLine.startsWith(term)){
                    System.out.println(term.length());
                    System.out.println(strCurrentLine.indexOf("="));
                   if( term.length()-strCurrentLine.indexOf("=") == -1 ) {
                       String[] termElements = strCurrentLine.substring( term.length()+4).split("[ \\|\\}\\{\\,\\[\\]]+");

                       Pair<String,Integer> term_df = new Pair<>(term , Integer.parseInt(termElements[0].substring(termElements[0].indexOf("=")+1))) ;
                       String[] doc_amount;
                       Map<Integer , Integer> doc_amount_map = new HashMap<>();
                       for(int i=1 ; i < termElements.length ; i++){
                           doc_amount = termElements[i].split("=");
                           doc_amount_map.put(Integer.parseInt(doc_amount[0]),Integer.parseInt(doc_amount[1]));
                       }
                       return new Pair<>(term_df , doc_amount_map);
                   }
                }
            }
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
        return null;
    }

    /**
     *
     * @param DocID
     * @return Map of pairs <Entity , numberOfAppearence>
     */
    public  Map<String,Integer> DocToEntities(Integer DocID){
        String fileName = "DocToEntities.txt";
        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            Path path = Paths.get(inversedFilesFolder + "//"+fileName);
            objReader = new BufferedReader(new FileReader(path.toString()));
            while ((strCurrentLine = objReader.readLine()) != null) {
                if(strCurrentLine.startsWith(DocID.toString()+"=")){

                    String[] termElements = strCurrentLine.substring( strCurrentLine.indexOf("=")+2 , strCurrentLine.length()-1 ).split(",");

                    String[] doc_amount;
                    Map<String , Integer> doc_amount_map = new HashMap<>();
                    for(int i=0 ; i < termElements.length ; i++){
                        doc_amount = termElements[i].split("=");
                        if(doc_amount[0].startsWith(" ")){ doc_amount[0] = doc_amount[0].substring(1);}
                        if(doc_amount[0].endsWith(" ")){ doc_amount[0] = doc_amount[0].substring(0,doc_amount[0].length()-1);}
                        doc_amount_map.put(doc_amount[0] ,Integer.parseInt(doc_amount[1]));
                    }
                    return doc_amount_map;

                }
            }
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
        return null;
    }


    public  Map<String,String> DocToMetaData(Integer DocID){
        String fileName = "id_toDoc.txt";
        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            Path path = Paths.get(inversedFilesFolder + "//"+fileName);
            objReader = new BufferedReader(new FileReader(path.toString()));
            while ((strCurrentLine = objReader.readLine()) != null) {
                if(strCurrentLine.startsWith(DocID.toString()+"=")){

                    String[] termElements = strCurrentLine.substring( strCurrentLine.indexOf("=")+1 ).split("[|]");
                    Map<String , String> doc_amount_map = new HashMap<>();
                    doc_amount_map.put("Title" ,termElements[0]);
                    doc_amount_map.put("Date" ,termElements[1]);
                    doc_amount_map.put("DocId" ,termElements[2]);
                    doc_amount_map.put("max_tf" ,termElements[4]);
                    doc_amount_map.put("qw" ,termElements[6]);
                    doc_amount_map.put("DocLen" ,termElements[8]);
                    return doc_amount_map;

                }
            }
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
        return null;
    }
}

