import javafx.util.Pair;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;

/**
 * This class takes a query after processing and ranks the documents in the corpus by relevance
 * and then return the most relevant documents
 */
public class Ranker {
    InversedFileReader reader;  //An object to pull data from the posting files
    Map<String,Integer> query; //The query after processing
    boolean semantics; //checks if the query needs semantic ranking
    boolean stem; //checks if the query has been stemmed
    String corpus;
    Map<String,Pair<Pair<String,Integer>, Map<Integer,Integer>>> queryInfo;
    Map<String,Integer> semanticWords;
    Double[] corpusData;
    Map<String,Double> docsBeforeSort;
    Double k;
    Double b;
    int semWordsSize;


    public Ranker(Map<String,Integer> query,String corpus,InversedFileReader reader,Double k,Double b) {
        this.query=query;
        semantics=false;
        this.corpus=corpus;
        queryInfo=new HashMap<>();
        this.reader=reader;
        semanticWords=new HashMap<>();
        stem=false;
        docsBeforeSort=new HashMap<>();
        this.k=k;
        this.b=b;
        semWordsSize=0;
    }

    /**
     * This function takes a path to the stop words
     * and then ranks the corpus by Okapi BM25 standards
     * @return The most relevant documents to the query
     * @throws IOException
     */
    public ArrayList<Map.Entry<String, Double>> rank() throws IOException{
        corpusData=reader.readInformationAboutCorpus();
        if(semantics)
            addSemanticWordsToQuery();
        Map<String,Double> bm=PreBM25();
        docsBeforeSort=bm;
        ArrayList<Map.Entry<String,Double>> sorted=new ArrayList<>();
        for(Map.Entry<String,Double> e:bm.entrySet()){
            sorted.add(e);
        }
        Comparator<Map.Entry<String, Double>> myComparator =
                new Comparator<Map.Entry<String, Double>>() {

                    @Override
                    public int compare(
                            Map.Entry<String, Double> e1,
                            Map.Entry<String, Double> e2) {

                        Double double1 = e1.getValue();
                        Double double2 = e2.getValue();
                        return double2.compareTo(double1);
                    }
                };
        Collections.sort(sorted,myComparator);
        while(sorted.size()>50){
            sorted.remove(50);
        }
        sorted.removeIf((Map.Entry<String,Double> val)->val.getValue()<=0.0);
        return sorted;
    }


    /**
     * Starts the preparation for the Okapi BM25 ranking formula by getting all the information needed from each document
     * from the posting files,and then calculates the rank by another function
     * @return The most relevant documents to the query
     */
    private Map<String,Double> PreBM25(){
        Map<Integer,Double> bm=new HashMap<>();
        bm=new HashMap<>();
        for (String term:query.keySet()) {
            Pair<Pair<String,Integer>, Map<Integer,Integer>> p=reader.readTermInformation(term);
            queryInfo.put(term,p);
            if(p==null)
                continue;
            Map<Integer,Integer> map = p.getValue() ;
            Integer[] keys = new Integer[map.keySet().size()];
            map.keySet().toArray(keys);
            for (Integer docno:keys) {
                bm.put(docno,null);
            }
        }
        HashMap<String,Double> docs=new HashMap<>();
        for(Integer docno:bm.keySet()){
            HashMap<String,String> docMeta=(HashMap)reader.DocToMetaData(docno);
            docs.put(docMeta.get("DocId"),calculateBM(docno,Double.parseDouble(docMeta.get("DocLen")),docMeta.get("Title")));
        }
        return docs;
    }

    /**
     * This function calculates the Okapi BM25 formula
     * @param docno The document number
     * @param docLen The document length
     * @return The rank of the document
     */
    private Double calculateBM(Integer docno,Double docLen,String title){
        Double idf=0.0;
        Double rest=0.0;
        Double sum=0.0;
        Double avgDocLen=corpusData[1]/corpusData[0];
        for(String term:query.keySet()){
            Pair<Pair<String,Integer>, Map<Integer,Integer>> p = queryInfo.get(term);
            if(p==null)
                continue;
            if(p.getValue().get(docno)==null)
                continue;
            Double docAmount=p.getValue().get(docno).doubleValue();
            idf=Math.log((corpusData[0]-p.getKey().getValue()+0.5)/(p.getKey().getValue()+0.5));
            rest=(docAmount*(k+1))/(docAmount+k*(1-b+b*docLen/avgDocLen));
            if(semanticWords.get(term)!=null)
                rest=rest/2;
            sum=sum+rest*idf;
            sum=sum*query.get(term);
//            if(title.contains(term))
//                sum+=10;
        }
        return sum;
    }


    /**
     * This function adds similar words to the query
     * @throws IOException
     */
    private void addSemanticWordsToQuery() throws IOException {
        String add="";
        for(String term:query.keySet()) {
            String st;
            File file = new File("semDataWords.txt");
            int count=0;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((st = br.readLine()) != null) {
                String[] similar = st.split("[ |]");
                if(similar[0].equals(term.toLowerCase())){
                    for (int i = 2; i <similar.length ; i++) {

                        if(similar[i].equals(""))
                            continue;
                        String[] divided=similar[i].split("=");
                        if(Double.parseDouble(divided[1])>0.6) {
                            add = add + " " + divided[0];
                            count++;
                        }
                        if(count>2)
                            break;
                    }
                }
            }
        }
        Parse parser=new Parse(corpus);
        if(stem)
            parser.TurnOnStem();
        else
            parser.TurnOffStem();
        Map<String,Integer> semanticParse=parser.parseIt(add);
        query.putAll(semanticParse);
        semanticWords=semanticParse;
        semWordsSize=query.size();
    }

    public void turnOnSemantics(){
        semantics=true;
    }

    public void turnOffSemantics(){
        semantics=false;
    }

    public void turnOnStem(){
        stem=true;
    }

    public void turnOffStem(){
        stem=false;
    }

    public Map<String, Double> getDocsBeforeSort() {
        return docsBeforeSort;
    }

    public int getSemWordsSize() {
        return semWordsSize;
    }
}
