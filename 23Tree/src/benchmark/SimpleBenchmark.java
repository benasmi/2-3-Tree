package benchmark;


import core.Tree23;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import javax.xml.bind.ValidationException;

/**
 * @author eimutis
 */
public class SimpleBenchmark {

    public static final String FINISH_COMMAND = "                               ";

    private final Timekeeper timekeeper;
    ///private final String[] PUT_BENCHMARK = {"add23Tree", "addTreeSet", "addHashSet"};
    private final String[] PUT_BENCHMARK = {"contains23Tree", "containsTreeSet", "containsHashSet"};
    private final int[] COUNTS = {10000, 370000};
    
    ///private final int[] COUNTS = {370000};
   
    private final Tree23<String> myTree = new Tree23();
    private final TreeSet<String> treeSet = new TreeSet();
    private final HashSet<String> hashSet = new HashSet();
    
    /**
     * For console benchmark
     */
    public SimpleBenchmark() {
        timekeeper = new Timekeeper(COUNTS);
    }   
        

    /**
     * For Gui benchmark
     *
     * @param resultsLogger
     * @param semaphore
     */
    public SimpleBenchmark(BlockingQueue<String> resultsLogger, Semaphore semaphore) {
        semaphore.release();
        timekeeper = new Timekeeper(COUNTS, resultsLogger, semaphore);
    }

    public static void main(String[] args) {
        executeTest();
    }

    public static void executeTest() {
        // suvienodiname skaičių formatus pagal LT lokalę (10-ainis kablelis)
        Locale.setDefault(new Locale("LT"));
        System.out.println("Greitaveikos tyrimas:\n");
        new SimpleBenchmark().startBenchmark();
    }

    public void startBenchmark() {
        try {
            putBenchmark();
            //containsBenchmark();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void putBenchmark() throws InterruptedException {
        
        ArrayList<String> wordss = addFromFile("/Users/b/Desktop/2-3-Tree/23Tree/src/pkg23tree/words/words_alpha.txt");
        ArrayList<String> words1 = addFromFile("/Users/b/Desktop/2-3-Tree/23Tree/src/pkg23tree/words/words.txt");
        ArrayList<ArrayList<String>> allWords = new ArrayList();
        allWords.add(wordss);
        allWords.add(words1);
        
        for(ArrayList<String> words : allWords){
            myTree.clear();
            treeSet.clear();
            hashSet.clear();
            timekeeper.startAfterPause();
            timekeeper.start();
            for (int i = 0; i < words.size(); i++) {
                myTree.add(words.get(i));
            }
            timekeeper.finish(PUT_BENCHMARK[0]);
            for (int i = 0; i < words.size(); i++) {
              treeSet.add(words.get(i));
            }
            timekeeper.finish(PUT_BENCHMARK[1]);
            for (int i = 0; i < words.size(); i++) {
               hashSet.add(words.get(i));
            }
            timekeeper.finish(PUT_BENCHMARK[2]);
            timekeeper.seriesFinish();
            StringBuilder sb = new StringBuilder();
            timekeeper.logResult(sb.toString());
            timekeeper.logResult(FINISH_COMMAND);
        }
       
        
    }
    
     public void containsBenchmark() throws InterruptedException {
        
        ArrayList<String> wordss = addFromFile("/Users/b/Desktop/2-3-Tree/23Tree/src/pkg23tree/words/words_alpha.txt");
        ArrayList<String> words1 = addFromFile("/Users/b/Desktop/2-3-Tree/23Tree/src/pkg23tree/words/words.txt");
        ArrayList<ArrayList<String>> allWords = new ArrayList();
        allWords.add(wordss);
        allWords.add(words1);
        
        for(ArrayList<String> words : allWords){
            myTree.clear();
            treeSet.clear();
            hashSet.clear();
            
            for (int i = 0; i < words.size(); i++) {
                myTree.add(words.get(i));
            }
            for (int i = 0; i < words.size(); i++) {
              treeSet.add(words.get(i));
            }
            for (int i = 0; i < words.size(); i++) {
               hashSet.add(words.get(i));
            }
            
            timekeeper.startAfterPause();
            timekeeper.start();
            myTree.get("zwitterionic");
            timekeeper.finish(PUT_BENCHMARK[0]);
            treeSet.contains("zwitterionic");
            timekeeper.finish(PUT_BENCHMARK[1]);
            hashSet.contains("zwitterionic");
            timekeeper.finish(PUT_BENCHMARK[2]);
            timekeeper.seriesFinish();
            StringBuilder sb = new StringBuilder();
            timekeeper.logResult(sb.toString());
            timekeeper.logResult(FINISH_COMMAND);
        }
    }
    
    public ArrayList<String> addFromFile(String fileName){
        ArrayList<String> words = new ArrayList();
            try {
                words = new ArrayList();
                File file = new File(fileName);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null){
                    words.add(st);
                }   
            } catch (IOException ex) {
            }
          return words;
    }
}


