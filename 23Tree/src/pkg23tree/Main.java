/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg23tree;

/**
 *
 * @author b
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
     Tree23<String> tree = new Tree23<>();
     tree.addFromFile("/Users/b/Desktop/2-3-Tree/23Tree/src/pkg23tree/words.txt");
     
     /*
     tree.add("a");
     tree.add("b");
     tree.add("c");
     tree.add("d");
     tree.add("e");
     tree.add("f");
*/
     System.out.println(tree.get("knifsadasdasde"));
    
 
    }
    
   
    
    
    
}
