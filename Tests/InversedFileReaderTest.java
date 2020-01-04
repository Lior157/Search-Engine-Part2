import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class InversedFileReaderTest {

    private InversedFileReader fd ;
    @Before
    public void init() {
        Path path = Paths.get("D:\\מסמכים\\לימודים\\שנה ג\\איחזור\\fbi10\\withoutStemPostingFiles");
        fd = new InversedFileReader( path );
    }
    @Test
    public void readInformationAboutCorpus() {
        Double[] res = fd.readInformationAboutCorpus();
        System.out.println("number of docs:"+res[0]+", all docs length:"+res[1]);
    }

    @Test
    public void readTermInformation() {
        Pair<Pair<String,Integer>, Map<Integer,Integer>> p = fd.readTermInformation("pachyderm");
        System.out.println("term:"+p.getKey().getKey() +", df = " + p.getKey().getValue());
        Map<Integer,Integer> map = p.getValue() ;
        Integer[] keys = new Integer[map.keySet().size()];
        map.keySet().toArray(keys);
        for( Integer doc_id : keys ){
            System.out.println("doc num:"+doc_id +", amount:"+map.get(doc_id));
        }
    }

    @Test
    public void DocToEntities(){
        Map<String,Integer> p = fd.DocToEntities(2998);
        String[] keys = new String[p.keySet().size()];
        p.keySet().toArray(keys);
        for( String entity : keys ){
            System.out.println("entity:"+entity +", amount:"+p.get(entity));
        }
    }

    @Test
    public void  DocToMetaData(){
        Map<String ,String > MetaData = fd.DocToMetaData(360429);
        System.out.println("title:"+MetaData.get("Title"));
        System.out.println("Date:"+MetaData.get("Date"));
        System.out.println("DocId:"+MetaData.get("DocId"));
        System.out.println("max_tf:"+MetaData.get("max_tf"));
        System.out.println("qw:"+MetaData.get("qw"));
        System.out.println("DocLen:"+MetaData.get("DocLen"));
    }
}