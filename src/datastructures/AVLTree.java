
package datastructures;

import models.Radio;
import models.RadioType;
import java.util.ArrayList;
import java.util.List;

//AVL Tree - Self-balancing Binary Search Tree.
//Stores Radio objects keyed by their ID.
//Guarantees O(log n) for search, insert, and delete operations.
//Custom implementation from scratch.

public class AVLTree {

    // Node class
    private class Node {
        Radio data;
        Node left, right;
        int height;

        Node(Radio data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    public AVLTree() {
        root = null;
        size = 0;
    }

    // Get height of a node
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Get balance factor
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Update height of a node
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    // Right rotation (LL case)
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Left rotation (RR case)
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Insert a radio into the tree
    public void insert(Radio radio) {
        root = insertNode(root, radio);
        size++;
    }

    private Node insertNode(Node node, Radio radio) {
        // Standard BST insert
        if (node == null) {
            return new Node(radio);
        }

        if (radio.getId() < node.data.getId()) {
            node.left = insertNode(node.left, radio);
        } else if (radio.getId() > node.data.getId()) {
            node.right = insertNode(node.right, radio);
        } else {
            // Duplicate ID - update data
            node.data = radio;
            size--;
            return node;
        }

        // Update height
        updateHeight(node);

        // Get balance factor
        int balance = getBalance(node);

        // Left Left case
        if (balance > 1 && radio.getId() < node.left.data.getId()) {
            return rotateRight(node);
        }

        // Right Right case
        if (balance < -1 && radio.getId() > node.right.data.getId()) {
            return rotateLeft(node);
        }

        // Left Right case
        if (balance > 1 && radio.getId() > node.left.data.getId()) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Left case
        if (balance < -1 && radio.getId() < node.right.data.getId()) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Search by ID
    public Radio search(int id) {
        Node node = searchNode(root, id);
        return node == null ? null : node.data;
    }

    private Node searchNode(Node node, int id) {
        if (node == null || node.data.getId() == id) {
            return node;
        }

        if (id < node.data.getId()) {
            return searchNode(node.left, id);
        }
        return searchNode(node.right, id);
    }

    // Search by name (partial match, returns first match)
    public Radio searchByName(String name) {
        List<Radio> results = new ArrayList<>();
        searchByNameInOrder(root, name.toLowerCase(), results);
        return results.isEmpty() ? null : results.get(0);
    }

    // Search by name (returns all matches)
    public List<Radio> searchAllByName(String name) {
        List<Radio> results = new ArrayList<>();
        searchByNameInOrder(root, name.toLowerCase(), results);
        return results;
    }

    private void searchByNameInOrder(Node node, String name, List<Radio> results) {
        if (node == null)
            return;

        searchByNameInOrder(node.left, name, results);

        if (node.data.getModel().toLowerCase().contains(name) ||
                node.data.getBrand().toLowerCase().contains(name)) {
            results.add(node.data);
        }

        searchByNameInOrder(node.right, name, results);
    }

    // Delete a radio by ID
    public void delete(int id) {
        root = deleteNode(root, id);
        size--;
    }

    private Node deleteNode(Node node, int id) {
        if (node == null)
            return null;

        // Standard BST delete
        if (id < node.data.getId()) {
            node.left = deleteNode(node.left, id);
        } else if (id > node.data.getId()) {
            node.right = deleteNode(node.right, id);
        } else {
            // Node found - delete
            if (node.left == null && node.right == null) {
                return null;
            } else if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                // Node with two children: get inorder successor
                Node successor = getMinNode(node.right);
                node.data = successor.data;
                node.right = deleteNode(node.right, successor.data.getId());
            }
        }

        // Update height
        updateHeight(node);

        // Balance
        int balance = getBalance(node);

        // LL case
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // LR case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RR case
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // RL case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node getMinNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Update a radio
    public void update(Radio radio) {
        // Since ID might stay same, we delete and re-insert
        root = deleteNode(root, radio.getId());
        root = insertNode(root, radio);
    }

    // Get all radios in sorted order (in-order traversal)
    public List<Radio> getAll() {
        List<Radio> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Node node, List<Radio> result) {
        if (node == null)
            return;
        inOrderTraversal(node.left, result);
        result.add(node.data);
        inOrderTraversal(node.right, result);
    }

    // Get radios with quantity below threshold (range query)
    public List<Radio> getLowStock(int threshold) {
        List<Radio> result = new ArrayList<>();
        getLowStockInOrder(root, threshold, result);
        return result;
    }

    private void getLowStockInOrder(Node node, int threshold, List<Radio> result) {
        if (node == null)
            return;
        getLowStockInOrder(node.left, threshold, result);
        if (node.data.getQuantity() <= threshold) {
            result.add(node.data);
        }
        getLowStockInOrder(node.right, threshold, result);
    }

    // Filter by type
    public List<Radio> getByType(RadioType type) {
        List<Radio> result = new ArrayList<>();
        getByTypeInOrder(root, type, result);
        return result;
    }

    private void getByTypeInOrder(Node node, RadioType type, List<Radio> result) {
        if (node == null)
            return;
        getByTypeInOrder(node.left, type, result);
        if (node.data.getType() == type) {
            result.add(node.data);
        }
        getByTypeInOrder(node.right, type, result);
    }

    // Get next available ID
    public int getNextId() {
        if (root == null)
            return 1;
        List<Radio> all = getAll();
        int maxId = 0;
        for (Radio r : all) {
            if (r.getId() > maxId)
                maxId = r.getId();
        }
        return maxId + 1;
    }

    // Size
    public int size() {
        return size;
    }

    // Check if empty
    public boolean isEmpty() {
        return root == null;
    }

    // Clear tree
    public void clear() {
        root = null;
        size = 0;
    }
}