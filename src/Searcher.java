import javafx.util.Pair;
import jdk.nashorn.internal.objects.NativeUint8Array;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * this class responsible on searching query using Ranker.
 * In addition , searching for most significant entities in each document.
 */
public class Searcher {

    Path posting;
    Path Corpus;
    InversedFileReader reader;
    boolean semantics;
    boolean stem;
    Ranker ranker;
    int originalQuerySize;


    public Searcher(Path posting,Path corpus) {
        this.posting = posting;
        reader=new InversedFileReader(posting);
        this.Corpus=corpus;
        semantics=false;
        stem=false;
    }


    /**
     *
     * @param Query for analyzing
     * @return DONCO of relevant documents and theirs rank .
     * @throws IOException
     */
    public ArrayList<Map.Entry<String, Double>> analyzeQuery(String Query,Double k,Double b,Double delta) throws IOException {
        Parse p = new Parse(Corpus.toString());
        if(stem)
            p.TurnOnStem();
        else
            p.TurnOffStem();
        Map<String, Integer>  queryWords = p.parseIt(Query);
        originalQuerySize=queryWords.size();
        ranker=new Ranker(queryWords,Corpus.toString(),reader,k,b,delta);
        if(stem)
            ranker.turnOnStem();
        else
            ranker.turnOffStem();
        if(semantics)
            ranker.turnOnSemantics();
        else
            ranker.turnOffSemantics();
        ArrayList<Map.Entry<String, Double>> docs=ranker.rank();
        return docs;
    }

    /**
     * receives a DOCNO and finds the most significant entities int the document
     * @param doc - DOCNO
     * @return Entities and their rank
     */
    public ArrayList<Pair<String,Double>> getStrongestEntities(String doc){
        Integer docno=reader.DocIdFromDoc(doc);
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

    /**
     * turn On Semantics using searcher
     */
    public void turnOnSemantics(){
        semantics=true;
    }
    /**
     * turn off Semantics using searcher
     */
    public void turnOffSemantics(){
        semantics=false;
    }
    /**
     * turn On Stemming in parser used by searcher
     */
    public void turnOnStem(){
        stem=true;
    }
    /**
     * turn off Stemming in parser used by searcher
     */
    public void turnOffStem(){
        stem=false;
    }

}
