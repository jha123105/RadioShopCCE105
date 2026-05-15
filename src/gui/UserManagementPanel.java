package gui;

import database.DatabaseManager;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private User currentUser;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, toggleStatusButton, refreshButton;

    public UserManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        initComponents();
        setupLayout();
        loadUserData();
    }

    private void initComponents() {
        String[] columns = { "ID", "Username", "Full Name", "Email", "Role", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.setRowHeight(25);

        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        toggleStatusButton = new JButton("Toggle Status");
        refreshButton = new JButton("Refresh");

        styleButton(addButton, new Color(46, 204, 113));
        styleButton(editButton, new Color(52, 152, 219));
        styleButton(toggleStatusButton, new Color(241, 196, 15));
        styleButton(refreshButton, new Color(149, 165, 166));
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void setupLayout() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        toggleStatusButton.addActionListener(e -> toggleUserStatus());
        refreshButton.addActionListener(e -> loadUserData());
    }

    private void loadUserData() {
        tableModel.setRowCount(0);
        List<User> users = DatabaseManager.getAllUsers();

        for (User user : users) {
            Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void addUser() {
        JDialog dialog = createUserDialog(null);
        dialog.setVisible(true);
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        List<User> users = DatabaseManager.getAllUsers();
        User userToEdit = users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);

        if (userToEdit != null) {
            JDialog dialog = createUserDialog(userToEdit);
            dialog.setVisible(true);
        }
    }

    private void toggleUserStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to toggle status.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        // Prevent deactivating own account
        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot deactivate your own account.");
            return;
        }

        List<User> users = DatabaseManager.getAllUsers();
        User userToToggle = users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);

        if (userToToggle != null) {
            userToToggle.setActive(!userToToggle.isActive());
            DatabaseManager.updateUser(userToToggle);
            loadUserData();
            JOptionPane.showMessageDialog(this, "User status updated successfully!");
        }
    }

    private JDialog createUserDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                user == null ? "Add New User" : "Edit User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[] { "ADMIN", "STAFF" });

        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            roleCombo.setSelectedItem(user.getRole());
        }

        int row = 0;
        addFormField(dialog, gbc, row++, "Username:", usernameField);
        addFormField(dialog, gbc, row++, "Password:", passwordField);
        addFormField(dialog, gbc, row++, "Full Name:", fullNameField);
        addFormField(dialog, gbc, row++, "Email:", emailField);
        addFormField(dialog, gbc, row++, "Role:", roleCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton, new Color(46, 204, 113));
        styleButton(cancelButton, new Color(149, 165, 166));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.");
                return;
            }

            if (user == null) {
                int newId = DatabaseManager.getNextUserId();
                User newUser = new User(newId, username, password, fullName, email, role);
                DatabaseManager.saveUser(newUser);
            } else {
                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(role);
                DatabaseManager.updateUser(user);
            }

            loadUserData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "User saved successfully!");
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        return dialog;
    }

    private void addFormField(JDialog dialog, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(field, gbc);
    }
}