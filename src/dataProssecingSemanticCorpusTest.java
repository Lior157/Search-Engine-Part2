//
//import javafx.util.Pair;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//
//
//public class dataProssecingSemanticCorpusTest {
//
//
//   public static void main(String[] args)  {
//        Path PostingFiles = Paths.get("D:\\מסמכים\\לימודים\\שנה ג\\איחזור\\fbi10\\withoutStemPostingFiles");
//        Path AllDataMetrix = Paths.get("D:\\glove.6B\\glove.6B.100d.txt");
//        Path outputPath = Paths.get("D:\\מסמכים\\לימודים\\שנה ג\\איחזור\\fbi10\\semData.txt");
//       Path outputSimmilarWordsPath = Paths.get("D:\\מסמכים\\לימודים\\שנה ג\\איחזור\\fbi10\\semDataWords.txt");
//      //  startProssecing( PostingFiles ,  AllDataMetrix , outputPath);
//        calcNclosestWords(PostingFiles ,  AllDataMetrix , outputPath , outputSimmilarWordsPath);
//    }
//
////    public static  void startProssecing(Path PostingFiles , Path AllDataMetrix ,Path outputPath) {
////        try {
////            dataProssecingSemanticCorpus ds =  new dataProssecingSemanticCorpus(PostingFiles ,AllDataMetrix ,outputPath);
////            ds.startProssecing();
////
////        }catch (Exception e) {e.getStackTrace();}
////    }
//    public static  void calcNclosestWords(Path PostingFiles , Path AllDataMetrix , Path outputPath , Path outputSimmilarWordsPath) {
//        try {
//            dataProssecingSemanticCorpus ds =  new dataProssecingSemanticCorpus(PostingFiles ,AllDataMetrix ,outputPath);
//           // ds.calcNclosestWords(outputSimmilarWordsPath);
//            ds.calcNclosestWordsNewV( outputSimmilarWordsPath , 249631 );
//
//        }catch (Exception e) {e.getStackTrace();}
//    }
//
//}