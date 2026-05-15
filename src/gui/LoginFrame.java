package gui;

import database.DatabaseManager;
import models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;

    public LoginFrame() {
        setTitle("RadioWave Electronics - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupLayout();
        setupListeners();

        DatabaseManager.initializeDefaultData();
    }

    private void initComponents() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");

        // Style buttons
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setOpaque(false);

        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.black);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setOpaque(false);

    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("RadioWave Electronics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Portable & Base Radio Sales System");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5);
        mainPanel.add(subtitleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> performLogin());
        exitButton.addActionListener(e -> System.exit(0));

        // Enter key in password field triggers login
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = DatabaseManager.authenticateUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Welcome " + user.getFullName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open appropriate dashboard
            if (user.getRole().equals("ADMIN")) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new StaffDashboard(user).setVisible(true);
            }

            dispose(); // Close login window
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
}