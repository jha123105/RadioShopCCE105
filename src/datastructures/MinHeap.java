package datastructures;

import models.Radio;
import java.util.ArrayList;
import java.util.List;

// Min-Heap implemented as a priority queue for restock prioritization.
// Stores Radio objects ordered by stock quantity (lowest first).
// Root always contains the radio with lowest stock.
// Custom implementation from scratch using array-based complete binary tree.

public class MinHeap {

    private List<Radio> heap;

    public MinHeap() {
        heap = new ArrayList<>();
    }

    // Get parent index
    private int parent(int i) {
        return (i - 1) / 2;
    }

    // Get left child index
    private int leftChild(int i) {
        return 2 * i + 1;
    }

    // Get right child index
    private int rightChild(int i) {
        return 2 * i + 2;
    }

    // Check if index has parent
    private boolean hasParent(int i) {
        return i > 0;
    }

    // Check if index has left child
    private boolean hasLeftChild(int i) {
        return leftChild(i) < heap.size();
    }

    // Check if index has right child
    private boolean hasRightChild(int i) {
        return rightChild(i) < heap.size();
    }

    // Swap two elements
    private void swap(int i, int j) {
        Radio temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    // Get size
    public int size() {
        return heap.size();
    }

    // Check if empty
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Peek at minimum without removing - O(1)
    public Radio peek() {
        if (isEmpty())
            return null;
        return heap.get(0);
    }

    // Insert a radio into the heap - O(log n)
    public void insert(Radio radio) {
        // Check if radio already exists in heap
        int existingIndex = findIndex(radio.getId());
        if (existingIndex >= 0) {
            // Update existing
            heap.set(existingIndex, radio);
            heapifyUp(existingIndex);
            heapifyDown(existingIndex);
            return;
        }

        heap.add(radio);
        heapifyUp(heap.size() - 1);
    }

    // Find index of radio by ID - O(n)
    private int findIndex(int radioId) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getId() == radioId) {
                return i;
            }
        }
        return -1;
    }

    // Heapify up - bubble element up to correct position - O(log n)
    private void heapifyUp(int index) {
        while (hasParent(index)) {
            int parentIdx = parent(index);
            if (heap.get(index).getQuantity() < heap.get(parentIdx).getQuantity()) {
                swap(index, parentIdx);
                index = parentIdx;
            } else {
                break;
            }
        }
    }

    // Extract minimum (remove root) - O(log n)
    public Radio extractMin() {
        if (isEmpty())
            return null;

        Radio min = heap.get(0);
        Radio last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }

        return min;
    }

    // Heapify down - sink element to correct position - O(log n)
    private void heapifyDown(int index) {
        while (hasLeftChild(index)) {
            int smallerChildIdx = leftChild(index);

            if (hasRightChild(index) &&
                    heap.get(rightChild(index)).getQuantity() < heap.get(leftChild(index)).getQuantity()) {
                smallerChildIdx = rightChild(index);
            }

            if (heap.get(index).getQuantity() < heap.get(smallerChildIdx).getQuantity()) {
                break;
            }

            swap(index, smallerChildIdx);
            index = smallerChildIdx;
        }
    }

    // Update priority of a radio in the heap - O(n) find + O(log n) heapify
    public void updatePriority(Radio radio) {
        int index = findIndex(radio.getId());
        if (index >= 0) {
            heap.set(index, radio);
            heapifyUp(index);
            heapifyDown(index);
        } else {
            insert(radio);
        }
    }

    // Remove a specific radio from heap
    public void remove(int radioId) {
        int index = findIndex(radioId);
        if (index < 0)
            return;

        Radio last = heap.remove(heap.size() - 1);
        if (index < heap.size()) {
            heap.set(index, last);
            heapifyUp(index);
            heapifyDown(index);
        }
    }

    // Build heap from list of radios - O(n)
    public void buildHeap(List<Radio> radios) {
        heap.clear();
        heap.addAll(radios);

        // Start from last non-leaf node and heapify down
        int startIdx = parent(heap.size() - 1);
        for (int i = startIdx; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    // Get all items in heap (not in sorted order)
    public List<Radio> getAll() {
        return new ArrayList<>(heap);
    }

    // Get items in priority order (destroys heap structure)
    public List<Radio> getPriorityOrder() {
        List<Radio> result = new ArrayList<>();
        List<Radio> tempHeap = new ArrayList<>(heap);

        while (!isEmpty()) {
            result.add(extractMin());
        }

        // Restore heap
        heap = tempHeap;
        return result;
    }

    // Clear heap
    public void clear() {
        heap.clear();
    }
}