import javafx.util.Pair;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;

public class Ranker {
    InversedFileReader reader;
    Map<String,Integer> query;
    boolean semantics;

    public Ranker(Map<String,Integer> query) {
        this.query=query;
        semantics=false;
    }

    public ArrayList<Map.Entry<Integer, Double>> rank(Path path){
        reader=new InversedFileReader(path);
        if(semantics)
            addSemanticWordsToQuery(null);
        Map<Integer,Double> bm=BM25();
        ArrayList<Map.Entry<Integer,Double>> sorted=new ArrayList<>();
        for(Map.Entry<Integer,Double> e:bm.entrySet()){
            sorted.add(e);
        }
        Comparator<Map.Entry<Integer, Double>> myComparator =
                new Comparator<Map.Entry<Integer, Double>>() {

                    @Override
                    public int compare(
                            Map.Entry<Integer, Double> e1,
                            Map.Entry<Integer, Double> e2) {

                        Double double1 = e1.getValue();
                        Double double2 = e2.getValue();
                        return double1.compareTo(double2);
                    }
                };
        Collections.sort(sorted,myComparator);
        while(sorted.size()>50){
            sorted.remove(50);
        }
        for(Map.Entry<Integer,Double> e:sorted){
            if(e.getValue()<=0)
                sorted.remove(e);
        }
        return sorted;
    }

    public Map<Integer,Double> BM25(){
        Map<Integer,Double> bm=new HashMap<>();
        bm=new HashMap<>();
        for (String term:query.keySet()) {
            Pair<Pair<String,Integer>, Map<Integer,Integer>> p=reader.readTermInformation(term);
            Map<Integer,Integer> map = p.getValue() ;
            Integer[] keys = new Integer[map.keySet().size()];
            map.keySet().toArray(keys);
            for (Integer docno:keys) {
                bm.put(docno,null);
            }
        }
        for(Integer docno:bm.keySet()){
            bm.put(docno,calculateBM(docno));
        }
        return bm;
    }

    public Double calculateBM(Integer docno){
        Double idf=0.0;
        Double rest=0.0;
        Double sum=0.0;
        Double[] corpusData=reader.readInformationAboutCorpus();
        Double avgDocLen=corpusData[1]/corpusData[0];
        for(String term:query.keySet()){
            Pair<Pair<String,Integer>, Map<Integer,Integer>> p = reader.readTermInformation(term);
            Double docAmount=(Double)p.getValue().get(docno).doubleValue();
            idf=Math.log((corpusData[0]-p.getKey().getValue()+0.5)/(p.getKey().getValue()+0.5));
            rest=(docAmount*2.5)/(docAmount+1.5*(0.25+0.75*Double.parseDouble(reader.DocToMetaData(docno).get("DocLen"))/avgDocLen));
            sum=sum+rest*idf;
        }
        return sum;
    }

    public void addSemanticWordsToQuery(String query){

    }

    public void turnOnSemantics(){
        semantics=true;
    }

    public void turnOffSemantics(){
        semantics=false;
    }

}
