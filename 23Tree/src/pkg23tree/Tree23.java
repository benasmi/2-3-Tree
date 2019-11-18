
package pkg23tree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tree23<T extends Comparable<T>> {

	private Node root;              // The root of the tree	
	private int size;              // Number of elements inside of the tree
	        
	private static final int    ROOT_IS_BIGGER = 1;
	private static final int    ROOT_IS_SMALLER = -1;

	private boolean addition;       // A flag to know if the last element has been added correctly or not

	public Tree23() {
		
		this.root = new Node();
		size = 0;
	}

    public Tree23(Collection<T> elements) {
        this.root = new Node();
        this.size = 0;
    }
    
    public void addFromFile(String fileName){
            try {
                File file = new File(fileName);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null){
                    add((T) st);
                }   } catch (IOException ex) {
                Logger.getLogger(Tree23.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println(size);
            
    }
    
    


	/**
	 * Adds a new element to the tree keeping it balanced.
	 *
	 * @param element The element to add
	 *
	 * @return If the element has been added (true) or not because it already exists (false)
	 */
	public boolean add(T element) {
		size++;
		addition = false;

		if(root == null || root.getLeftElement() == null) { // first case

			if(root == null) root = new Node();
			root.setLeftElement(element);
			addition = true;
		}
		else {
			Node newRoot = addElementI(root, element); // Immersion
			if(newRoot != null) root = newRoot;
		}

		if(!addition) size--;
		return addition;
	}
        
        
        
        public boolean get(T element){
            if(root == null || element == null){
                return false;
            }       
            return getI(root, element);
        }
        
           
        
        private boolean getI(Node node, T element){
            int cmpLeft = element.compareTo(node.leftElement);
            if(cmpLeft == 0){
                return true;
            }else{
                //Tikrinu ta ior ta
                if(node.is3Node()){
                    int cmpRight = element.compareTo(node.rightElement);
                    if(cmpRight == 0){
                        return true;
                    }
                    
                    if(cmpLeft > 0 && cmpRight<0 && node.mid != null){
                        return getI(node.mid, element);
                    }
                    
                    if(cmpLeft < 0 && node.left != null){
                        return getI(node.left, element);
                    }
                    
                    if(cmpRight > 0 && node.right != null){
                        return getI(node.right, element);
                    }
                }else{
                    if(cmpLeft>0 && node.mid != null){
                        return getI(node.mid, element);
                    }else if(cmpLeft<0 && node.left != null){
                        return getI(node.left, element);
                    }
                }
            }
           return false;
        }
        
	private Node addElementI(Node current, T element) {

		Node newParent = null;

		// We aren't in the deepest level yet
		if(!current.isLeaf()) {
		    Node sonAscended = null;
			// The new element is smaller than the left element
			if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
				sonAscended = addElementI(current.left, element);
				// Case sonAscended != null --> the element has been added on a 3-node (there were 2 elements)
				if (sonAscended != null) { // A new node comes from the left branch
					// The new element, in this case, is always less than the current.left
					if (current.is2Node()) {

						current.rightElement    = current.leftElement;  // shift the current left element to the right
						current.leftElement     = sonAscended.leftElement;
						current.right           = current.mid;
						current.mid             = sonAscended.mid;
						current.left            = sonAscended.left;
					}
					else { // In this case we have a new split, so the current element in the left will go up

						// We copy the right part of the subtree
						Node rightCopy = new Node(current.rightElement, null, current.mid, current.right);
						// Now we create the new "structure", pasting the right part
						newParent = new Node(current.leftElement, null, sonAscended, rightCopy);
					}
				}
				// Case: the ascended element is bigger than the left element and less than the right element
			} else if (current.is2Node() || (current.is3Node() && current.rightElement.compareTo(element) >= ROOT_IS_BIGGER)) {

				sonAscended = addElementI(current.mid, element);
				if (sonAscended != null) { // A new split

					// The right element is empty, so we can set the ascended element in the left and the existing left element into the right
					if (current.is2Node()) {

						current.rightElement    = sonAscended.leftElement;
						current.right           = sonAscended.mid;
						current.mid             = sonAscended.left;
					}
					else { // Another case we have to split again

						Node left 	= new Node(current.leftElement, null, current.left, sonAscended.left);
						Node mid 	= new Node(current.rightElement, null, sonAscended.mid, current.right);
						newParent 	= new Node(sonAscended.leftElement, null, left, mid);
					}
				}
				// The new element is bigger than the right element
			} else if (current.is3Node() && current.rightElement.compareTo(element) <= ROOT_IS_SMALLER) {

				sonAscended = addElementI(current.right, element);
				if (sonAscended != null) { // Split, the right element goes up
					Node leftCopy   = new Node(current.leftElement, null, current.left, current.mid);
					newParent       = new Node(current.rightElement, null, leftCopy,sonAscended);
				}
			}
		}
		else { // We are in the deepest level
			addition = true;
                        if (current.is2Node()) { // an easy case, there is not a right element
				// if the current left element is bigger than the new one --> we shift the left element to the right
				if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
					current.rightElement    = current.leftElement;
					current.leftElement     = element;
				}
				// if the new element is bigger, we add it in the right directly
				else if (current.leftElement.compareTo(element) <= ROOT_IS_SMALLER) 
                                    current.rightElement = element;
			}
			// Case 3-node: there are 2 elements in the node and we want to add another one. We have to split the node
			else newParent = split(current, element);
		}

		return newParent;
	}

	private Node split(Node current, T element) {
        Node newParent = null;
        // Current left up
        if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
            Node left   = new Node(element, null);
            Node right  = new Node(current.rightElement, null);
            newParent   = new Node(current.leftElement, null, left, right);
        } else if (current.leftElement.compareTo(element) <= ROOT_IS_SMALLER) {
            //New element up
            if (current.rightElement.compareTo(element) >= ROOT_IS_BIGGER) {
                Node left   = new Node(current.leftElement, null);
                Node right  = new Node(current.rightElement, null);
                newParent   = new Node(element, null, left, right);
            } else { // Right element up
                Node left   = new Node(current.leftElement, null);
                Node right  = new Node(element, null);
                newParent   = new Node(current.rightElement, null, left, right);
            }
        }

        return newParent;
    }

    private class Node {
		
		private Node left;
		private Node mid;
		private Node right;
		private T leftElement;
		private T rightElement;

                private Node() {
			
			left = null;
			mid = null;
			right = null;
			leftElement = null;
			rightElement = null;
		}

    
		private Node(T leftElement, T rightElement) {		
			this.leftElement = leftElement;
			this.rightElement = rightElement;
			left = null;
			mid = null;
			right = null;
		}

		private Node(T leftElement, T rightElement, Node left, Node mid) {
			
			this.leftElement = leftElement;
			this.rightElement = rightElement;			
			this.left = left;
			this.mid = mid;
		}

                private T getLeftElement() {
			
			return leftElement;
		}
		
		private void setLeftElement(T element) {
			
			this.leftElement = element;
		}
		
		private boolean isLeaf() {

			return left == null && mid == null && right == null;
		}

		private boolean is2Node() {

		    return rightElement == null; // also, right node is null but this will be always true if rightElement == null
                }

                private boolean is3Node() {

		    return rightElement != null; // also, right node is not null but this will be always true if rightElement <> null
                }
	
	}
}
