package datastructures;

import models.Sale;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//  Merge Sort algorithm for sorting sales records.
//  Guarantees O(n log n) time complexity in all cases.
//  Stable sort - preserves order of equal elements.
//  Custom implementation from scratch.

public class MergeSort {

    // Sort sales by total price (descending).

    public static void sortByPrice(List<Sale> sales) {
        if (sales == null || sales.size() <= 1)
            return;
        mergeSort(sales, 0, sales.size() - 1, (a, b) -> Double.compare(b.getTotalPrice(), a.getTotalPrice()));
    }

    // Sort sales by date (newest first).

    public static void sortByDate(List<Sale> sales) {
        if (sales == null || sales.size() <= 1)
            return;
        mergeSort(sales, 0, sales.size() - 1, (a, b) -> b.getSaleDate().compareTo(a.getSaleDate()));
    }

    // Sort sales by quantity (descending).

    public static void sortByQuantity(List<Sale> sales) {
        if (sales == null || sales.size() <= 1)
            return;
        mergeSort(sales, 0, sales.size() - 1, (a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
    }

    // Sort sales using custom comparator.

    public static void sort(List<Sale> sales, Comparator<Sale> comparator) {
        if (sales == null || sales.size() <= 1)
            return;
        mergeSort(sales, 0, sales.size() - 1, comparator);
    }

    // Recursive merge sort implementation.

    private static void mergeSort(List<Sale> sales, int left, int right, Comparator<Sale> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Recursively sort left and right halves
            mergeSort(sales, left, mid, comparator);
            mergeSort(sales, mid + 1, right, comparator);

            // Merge the sorted halves
            merge(sales, left, mid, right, comparator);
        }
    }

    // Merge two sorted subarrays.

    private static void merge(List<Sale> sales, int left, int mid, int right, Comparator<Sale> comparator) {
        // Sizes of subarrays
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Create temporary arrays
        List<Sale> leftArr = new ArrayList<>(n1);
        List<Sale> rightArr = new ArrayList<>(n2);

        // Copy data to temp arrays
        for (int i = 0; i < n1; i++) {
            leftArr.add(sales.get(left + i));
        }
        for (int j = 0; j < n2; j++) {
            rightArr.add(sales.get(mid + 1 + j));
        }

        // Merge temp arrays back
        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (comparator.compare(leftArr.get(i), rightArr.get(j)) <= 0) {
                sales.set(k, leftArr.get(i));
                i++;
            } else {
                sales.set(k, rightArr.get(j));
                j++;
            }
            k++;
        }

        // Copy remaining elements from left array
        while (i < n1) {
            sales.set(k, leftArr.get(i));
            i++;
            k++;
        }

        // Copy remaining elements from right array
        while (j < n2) {
            sales.set(k, rightArr.get(j));
            j++;
            k++;
        }
    }
}