# RadioWave Electronics Management System

A portable and base radio inventory and sales management system developed for the **CCE 105/L – Data Structures and Algorithms** course.  

This project demonstrates the transition from a baseline (naive) implementation using linear data structures to an optimized system using advanced data structures and algorithms.

---

## Features

- **Inventory Management** – Add, edit, delete, and search radios by ID or name
- **Sales Processing** – Record customer purchases with automatic stock updates
- **Reports & Analytics** – Generate sales summaries, inventory status reports, low-stock alerts, and top-selling radios
- **Restock Prioritization** – Identify the most critical item to restock using a priority queue
- **User Management** – Manage staff accounts with role-based access control

---

## Branches

| Branch | Description |
|--------|-------------|
| [`baseline`](https://github.com/jha123105/RadioShopCCE105/tree/baseline) | Initial naive implementation using `ArrayList`, linear search, and Bubble Sort with O(n) / O(n²) complexity |
| [`optimized`](https://github.com/jha123105/RadioShopCCE105/tree/optimized) | Improved implementation using a custom **AVL Tree**, **Min-Heap**, **Merge Sort**, and **HashMap** for O(log n), O(1), and O(n log n) operations |

> Both branches implement the same system features. The only difference is the underlying data structures and algorithms used.

---

## Data Structures & Algorithms (Optimized Branch)

| Structure / Algorithm | Type | Implementation | Purpose |
|----------------------|------|----------------|---------|
| **AVL Tree** | Self-balancing Binary Search Tree | Custom (from scratch) | Fast inventory search, insertion, and deletion – O(log n) |
| **Min-Heap** | Priority Queue | Custom (from scratch) | Restock prioritization – O(1) peek |
| **Merge Sort** | Divide and Conquer | Custom (from scratch) | Efficient sales report sorting – O(n log n) |
| **HashMap** | Hash Table | `java.util.HashMap` | User authentication – O(1) average lookup |

---

## Performance Improvements

All benchmarks were tested using identical datasets containing 100, 500, and 1000 items across both branches.

| Operation | Baseline (O(n) / O(n²)) | Optimized (O(log n) / O(1) / O(n log n)) | Improvement |
|-----------|--------------------------|-------------------------------------------|-------------|
| Search by ID | 0.059 ms | 0.003 ms | **95.8% Faster** |
| Sort Sales | 146.23 ms | 4.83 ms | **96.7% Faster** |
| Restock Peek | 0.135 ms | 0.002 ms | **98.7% Faster** |

---

## Technologies Used

- **Language:** Java 17+
- **UI Framework:** Java Swing
- **Persistence:** File-based storage using pipe-delimited `.txt` files
- **Build Tool:** None (manual compilation or IDE-based execution)

---

## How to Run

### Prerequisites

- Java JDK 8 or higher
- Any Java IDE such as IntelliJ IDEA, Eclipse, or VS Code

---

### 1. Clone the Repository

```bash
git clone https://github.com/jha123105/RadioShopCCE105.git
cd RadioShopCCE105
```

---

### 2. Switch to a Branch

```bash
git checkout baseline
```

or

```bash
git checkout optimized
```

---

### 3. Compile and Run

```bash
javac -d bin src/**/*.java
java -cp bin Main
```

---

## Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Staff | staff | staff123 |

---

## Project Structure (Optimized Branch)

```text
src/
├── Main.java
├── benchmark/
│   └── BenchmarkRunner.java        # Performance benchmark tests
├── datastructures/
│   ├── AVLTree.java                # Custom AVL tree implementation
│   ├── MinHeap.java                # Custom min-heap implementation
│   └── MergeSort.java              # Custom merge sort implementation
├── models/
│   ├── Radio.java
│   ├── RadioType.java
│   ├── Sale.java
│   └── User.java
├── database/
│   ├── DatabaseManager.java        # Data access and management
│   └── FileHandler.java            # File I/O utilities
└── gui/
    ├── LoginFrame.java
    ├── AdminDashboard.java
    ├── StaffDashboard.java
    ├── InventoryPanel.java
    ├── SalesPanel.java
    ├── ReportsPanel.java
    └── UserManagementPanel.java
```

---

## Authors

- Josh A. – BSIT
- Ryu S. – BSIT
- Hanz Q. – BSIT

**University of Mindanao**  
CCE 105/L – Data Structures and Algorithms  

**Instructor:** Benjie B.

---

## License

This project is intended for academic purposes only.
