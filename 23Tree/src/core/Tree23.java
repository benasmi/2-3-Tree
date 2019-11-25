package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class Tree23<T extends Comparable<T>> implements ITree23<T> {

    private Node root;             
    public int size;          
    private boolean inRecursion = false;
    private static boolean ESCAPE_RECURSION = false;
    private static final int ROOT_IS_BIGGER = 1;
    private static final int ROOT_IS_SMALLER = -1;

    private boolean addition;     

    public Tree23() {

        this.root = new Node();
        size = 0;
    }

    public Tree23(Collection<T> elements) {
        this.root = new Node();
        this.size = 0;
    }

    public void inOrder(String keyword, DefaultListModel model, JList list) {

        if (keyword.length() > 1) {
            if (root == null) {
                System.out.println("The tree is empty");
            } else {
                try {
                    inOrderI(root, keyword.toLowerCase(), model, list);
                } catch (EscapeRecursionException e) {
                    System.out.println("Bega is rekursijos");
                }
            }
        }

    }

    //Norint optimizuoti paiešką, galime ją nutraukti rekusijoje metant klaidą.
    public void cancelRecursion() {
        if (inRecursion) {
            ESCAPE_RECURSION = true;
        }
    }

    public void continueRecursion() {
        inRecursion = false;
        ESCAPE_RECURSION = false;
    }

    //Apvaikštome visą medį
    private void inOrderI(Node current, String keyword, DefaultListModel model, JList list) throws EscapeRecursionException {
        if (current != null) {
            if (current.isLeaf()) {
                addIfContains(keyword, current.leftElement.toString(), model);
                list.setModel(model);
                if (current.rightElement != null) {
                    addIfContains(keyword, current.rightElement.toString(), model);
                }
            } else {
                inOrderI(current.left, keyword, model, list);
                addIfContains(keyword, current.leftElement.toString(), model);
                list.setModel(model);
                inOrderI(current.mid, keyword, model, list);
                if (current.rightElement != null) {
                    if (!current.isLeaf()) {
                        addIfContains(keyword, current.rightElement.toString(), model);
                        list.setModel(model);
                    }
                    inOrderI(current.right, keyword, model, list);
                }
            }
        }
    }

    //Pridedam jeigu tinka
    private void addIfContains(String keyword, String target, DefaultListModel model) {
        if (target.startsWith(keyword)) {
            model.addElement(highlightedString(target, keyword));
        }
    }

    //Pridedam elementu į medį iš failo
    public void addFromFile(String fileName) {
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                add((T) st);
            }
        } catch (IOException ex) {
            Logger.getLogger(Tree23.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean add(T element) {
        size++;
        addition = false;

        if (root == null || root.getLeftElement() == null) { // first case

            if (root == null) {
                root = new Node();
            }
            root.setLeftElement(element);
            addition = true;
        } else {
            Node newRoot = addElementI(root, element); // Immersion
            if (newRoot != null) {
                root = newRoot;
            }
        }

        if (!addition) {
            size--;
        }
        return addition;
    }

    @Override
    public boolean get(T element) {
        if (root == null || element == null) {
            return false;
        }
        return getI(root, element);
    }

    //Patamsinam žodžio dalį.
    private String highlightedString(String word, String target) {
        String temp = word.substring(target.length(), word.length());
        return "<html><font color=black><b>" + target + "</b></font>" + "<font color=black>" + temp + "</font></html>";
    }

    private boolean getI(Node node, T element) {
        int cmpLeft = element.compareTo(node.leftElement);
        if (cmpLeft == 0) {
            return true;
        } else {
            if (node.is3Node()) {
                int cmpRight = element.compareTo(node.rightElement);
                if (cmpRight == 0) {
                    return true;
                }

                if (cmpLeft > 0 && cmpRight < 0 && node.mid != null) {
                    return getI(node.mid, element);
                }

                if (cmpLeft < 0 && node.left != null) {
                    return getI(node.left, element);
                }

                if (cmpRight > 0 && node.right != null) {
                    return getI(node.right, element);
                }
            } else {
                if (cmpLeft > 0 && node.mid != null) {
                    return getI(node.mid, element);
                } else if (cmpLeft < 0 && node.left != null) {
                    return getI(node.left, element);
                }
            }
        }
        return false;
    }

    private Node addElementI(Node current, T element) {

        Node newParent = null;
        if (!current.isLeaf()) {
            Node sonAscended = null;
            if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
                sonAscended = addElementI(current.left, element);
                if (sonAscended != null) {
                    if (current.is2Node()) {

                        current.rightElement = current.leftElement;
                        current.leftElement = sonAscended.leftElement;
                        current.right = current.mid;
                        current.mid = sonAscended.mid;
                        current.left = sonAscended.left;
                    } else {

                        Node rightCopy = new Node(current.rightElement, null, current.mid, current.right);
                        newParent = new Node(current.leftElement, null, sonAscended, rightCopy);
                    }
                }

            } else if (current.is2Node() || (current.is3Node() && current.rightElement.compareTo(element) >= ROOT_IS_BIGGER)) {

                sonAscended = addElementI(current.mid, element);
                if (sonAscended != null) { // A new split

                    if (current.is2Node()) {

                        current.rightElement = sonAscended.leftElement;
                        current.right = sonAscended.mid;
                        current.mid = sonAscended.left;
                    } else {

                        Node left = new Node(current.leftElement, null, current.left, sonAscended.left);
                        Node mid = new Node(current.rightElement, null, sonAscended.mid, current.right);
                        newParent = new Node(sonAscended.leftElement, null, left, mid);
                    }
                }
            } else if (current.is3Node() && current.rightElement.compareTo(element) <= ROOT_IS_SMALLER) {

                sonAscended = addElementI(current.right, element);
                if (sonAscended != null) { // Split, the right element goes up
                    Node leftCopy = new Node(current.leftElement, null, current.left, current.mid);
                    newParent = new Node(current.rightElement, null, leftCopy, sonAscended);
                }
            }
        } else {
            addition = true;
            if (current.is2Node()) {

                if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
                    current.rightElement = current.leftElement;
                    current.leftElement = element;
                } else if (current.leftElement.compareTo(element) <= ROOT_IS_SMALLER) {
                    current.rightElement = element;
                }
            } else {
                newParent = split(current, element);
            }
        }
        return newParent;
    }

    private Node split(Node current, T element) {
        Node newParent = null;
        if (current.leftElement.compareTo(element) >= ROOT_IS_BIGGER) {
            Node left = new Node(element, null);
            Node right = new Node(current.rightElement, null);
            newParent = new Node(current.leftElement, null, left, right);
        } else if (current.leftElement.compareTo(element) <= ROOT_IS_SMALLER) {
            if (current.rightElement.compareTo(element) >= ROOT_IS_BIGGER) {
                Node left = new Node(current.leftElement, null);
                Node right = new Node(current.rightElement, null);
                newParent = new Node(element, null, left, right);
            } else {
                Node left = new Node(current.leftElement, null);
                Node right = new Node(element, null);
                newParent = new Node(current.rightElement, null, left, right);
            }
        }

        return newParent;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
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

            return rightElement == null;
        }

        private boolean is3Node() {

            return rightElement != null;
        }

    }
}
