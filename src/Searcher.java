import jdk.nashorn.internal.objects.NativeUint8Array;

import java.util.Map;

public class Searcher {

    public String[] analazeQuery(String Query){
        Parse p = new Parse(null);
        Map<String, Integer>  queryWords = p.parseIt(Query);

        String[] results = new String[2];
        return results;
    }
}
