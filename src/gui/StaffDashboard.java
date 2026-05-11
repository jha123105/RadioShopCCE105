package gui;

import models.User;

import javax.swing.*;
import java.awt.*;

public class StaffDashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public StaffDashboard(User user) {
        this.currentUser = user;

        setTitle("RadioWave Electronics - Staff Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        setupMenuBar();

        add(tabbedPane);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add tabs - Staff has limited access
        tabbedPane.addTab("View Inventory", new InventoryPanel(false));
        tabbedPane.addTab("Process Sales", new SalesPanel(currentUser, false));
        tabbedPane.addTab("Sales Reports", new ReportsPanel(false));
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "RadioWave Electronics Management System\n" +
                        "Version 1.0\n\n" +
                        "© 2024 RadioWave Electronics",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}