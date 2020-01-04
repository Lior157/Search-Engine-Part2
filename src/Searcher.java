import javafx.util.Pair;
import jdk.nashorn.internal.objects.NativeUint8Array;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class Searcher {

    Path posting;
    Path Corpus;
    InversedFileReader reader;

    public Searcher(Path posting,Path corpus) {
        this.posting = posting;
        reader=new InversedFileReader(posting);
        this.Corpus=corpus;
    }

    public ArrayList<Map.Entry<String, Double>> analazeQuery(String Query) throws IOException {
        Parse p = new Parse(Corpus.toString());
        Map<String, Integer>  queryWords = p.parseIt(Query);
        Ranker ranker=new Ranker(queryWords,Corpus.toString(),reader);
        ranker.turnOnSemantics();
        ArrayList<Map.Entry<String, Double>> docs=ranker.rank(posting);
        return docs;
    }

    public ArrayList<Pair<String,Double>> getStrongestEntities(Integer docno){
        Map<String,Integer> entities=reader.DocToEntities(docno);
        ArrayList<Pair<String,Double>> entitiesSorted=new ArrayList<>();
        Map<String,String> terms=reader.DocToMetaData(docno);
        Double maxTF=Double.parseDouble(terms.get("max_tf"));
        Comparator<Pair<String, Double>> myComparator =
                new Comparator<Pair<String, Double>>() {

                    @Override
                    public int compare(
                            Pair<String, Double> e1,
                            Pair<String, Double> e2) {

                        Double double1 = e1.getValue();
                        Double double2 = e2.getValue();
                        return double1.compareTo(double2);
                    }
                };
        for(Map.Entry<String,Integer> e:entities.entrySet()){
            Double rank=0.5+0.5*(Double.valueOf(e.getValue()))/maxTF;
            entitiesSorted.add(new Pair(e.getKey(),rank));
        }
        Collections.sort(entitiesSorted,myComparator);
        while(entitiesSorted.size()>5){
            entitiesSorted.remove(5);
        }
        return entitiesSorted;
    }
}
