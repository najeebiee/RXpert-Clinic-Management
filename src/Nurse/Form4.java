/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Nurse;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;/**
 *
 * @author USER
 */
public class Form4 extends TransitionsForm {
private String nurseId;
    private JPanel mainPanel;
    private JPanel notificationsPanel;
    private JLabel titleLabel;
    private JLabel notificationCountLabel;
    private JLabel lastCheckedLabel;
    private JLabel loadingLabel;
    private JProgressBar loadingBar;
    private JButton refreshButton;
    private JButton clearAllButton;
    private JButton markAllReadButton;
    private JComboBox<String> filterComboBox;
    private JComboBox<String> sortComboBox;
    private JTextField searchField;
    private JScrollPane notificationsScrollPane;
    private javax.swing.Timer refreshTimer;
    private javax.swing.Timer animationTimer;
    private Map<String, Long> knownBillIds;
    private long lastBillsCheckTime;
    private boolean isUILoaded = false;
    private boolean isInitialLoad = true; // New flag for initial load
    private List<JPanel> notificationEntries = new ArrayList<>();
    private boolean isCheckingBills = false;

    public Form4() {
        knownBillIds = new HashMap<>();
        lastBillsCheckTime = 0;
        initComponents();
        initPlaceholderUI();
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
        System.out.println("Form4: Nurse ID set to " + nurseId);
        if (!isUILoaded) {
            initDynamicComponents();
            isUILoaded = true;
        }
        startRefreshTimer();
        checkForNewBills();
        // Add a small delay to ensure file write is complete before loading
        new javax.swing.Timer(500, e -> {
            loadNotifications();
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }

    private void initPlaceholderUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        JPanel placeholderPanel = new JPanel(new GridBagLayout());
        placeholderPanel.setBackground(new Color(40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel loadingLabel = new JLabel("Loading... Please wait");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loadingLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        placeholderPanel.add(loadingLabel, gbc);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setForeground(new Color(0, 153, 153));
        gbc.gridy = 1;
        placeholderPanel.add(progressBar, gbc);

        add(placeholderPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
 private void initDynamicComponents() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 40), 0, getHeight(), new Color(60, 60, 60));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 40, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(40, 40, 40));
        titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        notificationCountLabel = new JLabel("");
        notificationCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        notificationCountLabel.setForeground(new Color(255, 99, 71));
        titlePanel.add(notificationCountLabel);

        lastCheckedLabel = new JLabel("Last checked: Never");
        lastCheckedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastCheckedLabel.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(lastCheckedLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(new Color(40, 40, 40));

        loadingLabel = new JLabel("");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setForeground(Color.LIGHT_GRAY);
        controlsPanel.add(loadingLabel);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setPreferredSize(new Dimension(100, 20));
        loadingBar.setForeground(new Color(0, 153, 153));
        loadingBar.setVisible(false);
        controlsPanel.add(loadingBar);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBackground(new Color(50, 50, 50));
        searchField.setForeground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchField.setText("Search notifications...");
        searchField.setForeground(Color.LIGHT_GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search notifications...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search notifications...");
                    searchField.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        searchField.addActionListener(e -> {
            isInitialLoad = false; // User interaction, disable initial load bypass
            loadNotifications();
        });
        controlsPanel.add(new JLabel("Search:") {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }});
        controlsPanel.add(searchField);

        filterComboBox = new JComboBox<>(new String[]{"All", "Unread", "Billing", "System", "Info"});
        filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterComboBox.setBackground(new Color(50, 50, 50));
        filterComboBox.setForeground(Color.WHITE);
        filterComboBox.setSelectedItem("All"); // Ensure default is "All"
        filterComboBox.addActionListener(e -> {
            isInitialLoad = false; // User interaction, disable initial load bypass
            loadNotifications();
        });
        controlsPanel.add(new JLabel("Filter:") {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }});
        controlsPanel.add(filterComboBox);

        sortComboBox = new JComboBox<>(new String[]{"Newest First", "Oldest First"});
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sortComboBox.setBackground(new Color(50, 50, 50));
        sortComboBox.setForeground(Color.WHITE);
        sortComboBox.addActionListener(e -> {
            isInitialLoad = false; // User interaction, disable initial load bypass
            loadNotifications();
        });
        controlsPanel.add(new JLabel("Sort:") {{
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }});
        controlsPanel.add(sortComboBox);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(0, 153, 153));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            lastBillsCheckTime = 0;
            checkForNewBills();
            // Add a small delay to ensure file write is complete
            new javax.swing.Timer(500, evt -> {
                isInitialLoad = false; // Refresh is a user action
                loadNotifications();
                ((javax.swing.Timer) evt.getSource()).stop();
            }).start();
        });
        controlsPanel.add(refreshButton);

        markAllReadButton = new JButton("Mark All Read");
        markAllReadButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        markAllReadButton.setBackground(new Color(255, 165, 0));
        markAllReadButton.setForeground(Color.WHITE);
        markAllReadButton.setFocusPainted(false);
        markAllReadButton.addActionListener(e -> markAllAsRead());
        controlsPanel.add(markAllReadButton);

        clearAllButton = new JButton("Clear All");
        clearAllButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clearAllButton.setBackground(new Color(220, 53, 69));
        clearAllButton.setForeground(Color.WHITE);
        clearAllButton.setFocusPainted(false);
        clearAllButton.addActionListener(e -> clearAllNotifications());
        controlsPanel.add(clearAllButton);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        notificationsPanel = new JPanel();
        notificationsPanel.setBackground(new Color(40, 40, 40));
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        notificationsScrollPane = new JScrollPane(notificationsPanel);
        notificationsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notificationsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        notificationsScrollPane.setBackground(new Color(40, 40, 40));
        notificationsScrollPane.setBorder(null);
        mainPanel.add(notificationsScrollPane, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void startRefreshTimer() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            return;
        }
        refreshTimer = new javax.swing.Timer(30000, e -> {
            checkForNewBills();
            // Add a small delay to ensure file write is complete
            new javax.swing.Timer(500, evt -> {
                loadNotifications();
                ((javax.swing.Timer) evt.getSource()).stop();
            }).start();
        });
        refreshTimer.start();
        System.out.println("Form4: Refresh timer started");
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.stop();
            System.out.println("Form4: Refresh timer stopped");
        }
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    private void showLoadingIndicator(boolean show) {
        isCheckingBills = show;
        loadingLabel.setText(show ? "Checking for new bills..." : "");
        loadingBar.setVisible(show);
        refreshButton.setEnabled(!show);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void checkForNewBills() {
        if (nurseId == null) {
            System.out.println("Form4: Skipping checkForNewBills because nurseId is null");
            return;
        }

        showLoadingIndicator(true);

        try {
            String billsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\maeclinicapp\\bills.json";
            File billsFile = new File(billsFilePath);
            if (!billsFile.exists()) {
                System.out.println("Form4: bills.json does not exist");
                return;
            }

            long lastModified = billsFile.lastModified();
            System.out.println("Form4: bills.json last modified: " + lastModified + ", last check time: " + lastBillsCheckTime);
            boolean forceCheck = lastBillsCheckTime == 0;

            if (!forceCheck && lastModified <= lastBillsCheckTime) {
                System.out.println("Form4: No changes in bills.json since last check");
                return;
            }
            lastBillsCheckTime = lastModified;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a");
            lastCheckedLabel.setText("Last checked: " + LocalDateTime.now().format(formatter));

            JSONParser parser = new JSONParser();
            JSONObject billsJson = (JSONObject) parser.parse(new FileReader(billsFile));
            JSONArray bills = (JSONArray) billsJson.get("bills");

            String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
            File notificationsFile = new File(notificationsFilePath);
            JSONObject notificationsJson;
            JSONArray notificationsArray;
            if (notificationsFile.exists()) {
                notificationsJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
                notificationsArray = (JSONArray) notificationsJson.get("notifications");
            } else {
                notificationsJson = new JSONObject();
                notificationsArray = new JSONArray();
                notificationsJson.put("notifications", notificationsArray);
            }

            boolean newBillFound = false;
            for (Object obj : bills) {
                JSONObject bill = (JSONObject) obj;
                String billId = (String) bill.get("billId");
                String billNurseId = (String) bill.get("nurseId");
                String status = (String) bill.get("status");
                String patientName = (String) bill.get("patientName");
                Double amount = (Double) bill.get("amount");
                String date = (String) bill.get("date");

                System.out.println("Form4: Checking bill: billId=" + billId + ", nurseId=" + billNurseId + ", status=" + status);

                Long lastProcessed = knownBillIds.get(billId);
                boolean isNewBill = lastProcessed == null;
                boolean isModified = !isNewBill && lastProcessed < lastModified;

                if ((isNewBill || isModified) && nurseId.equals(billNurseId)) {
                    String message;
                    String type = "billing";
                    String priority = "High";
                    if (isNewBill) {
                        message = "New bill added for patient " + patientName + ": $" + amount;
                    } else {
                        message = "Bill updated for patient " + patientName + ": $" + amount + " (Status: " + status + ")";
                        priority = "Medium";
                    }

                    JSONObject newNotification = new JSONObject();
                    newNotification.put("id", "N" + UUID.randomUUID().toString().substring(0, 8));
                    newNotification.put("nurseId", nurseId);
                    newNotification.put("message", message);
                    newNotification.put("timestamp", date);
                    newNotification.put("type", type);
                    newNotification.put("priority", priority);
                    newNotification.put("read", false);

                    notificationsArray.add(newNotification);
                    newBillFound = true;
                    System.out.println("Form4: Generated notification for billId " + billId + ": " + newNotification.toJSONString());
                    System.out.println("Form4: Simulating notification sound: *Ding*");
                }
                knownBillIds.put(billId, lastModified);
            }

            if (newBillFound) {
                try (FileWriter file = new FileWriter(notificationsFilePath)) {
                    notificationsJson.put("notifications", notificationsArray);
                    file.write(notificationsJson.toJSONString());
                    file.flush();
                }
                System.out.println("Form4: Saved new notifications to " + notificationsFilePath);
                // Log the contents of the file after saving
                JSONObject savedJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
                JSONArray savedArray = (JSONArray) savedJson.get("notifications");
                System.out.println("Form4: After saving, notifications.json contains " + (savedArray != null ? savedArray.size() : 0) + " notifications: " + savedArray);
            } else {
                System.out.println("Form4: No new or modified bills found for nurseId " + nurseId);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Form4: Error checking for new bills: " + e.getMessage());
        } finally {
            showLoadingIndicator(false);
        }
    }

    private void loadNotifications() {
        if (nurseId == null) {
            System.out.println("Form4: Nurse ID is null, cannot load notifications");
            return;
        }

        try {
            String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
            JSONParser parser = new JSONParser();

            File notificationsFile = new File(notificationsFilePath);
            System.out.println("Form4: Notifications file exists: " + notificationsFile.exists());

            JSONObject notificationsJson;
            JSONArray notificationsArray;
            if (notificationsFile.exists()) {
                notificationsJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
                notificationsArray = (JSONArray) notificationsJson.get("notifications");
            } else {
                notificationsJson = new JSONObject();
                notificationsArray = new JSONArray();
                notificationsJson.put("notifications", notificationsArray);
                try (FileWriter file = new FileWriter(notificationsFilePath)) {
                    file.write(notificationsJson.toJSONString());
                    file.flush();
                }
            }

            System.out.println("Form4: Loaded " + (notificationsArray != null ? notificationsArray.size() : 0) + " notifications from file: " + notificationsArray);

            notificationEntries.clear();
            notificationsPanel.removeAll();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a");

            List<JSONObject> filteredNotifications = new ArrayList<>();
            String filter = (String) filterComboBox.getSelectedItem();
            String searchText = searchField.getText().toLowerCase();

            // Fix: Treat placeholder text as empty search on initial load
            if (searchText.equals("search notifications...")) {
                searchText = "";
            }
            // On initial load, bypass search filter to show all notifications
            boolean applySearchFilter = !isInitialLoad;

            if (notificationsArray != null) {
                for (Object obj : notificationsArray) {
                    JSONObject notification = (JSONObject) obj;
                    String notifNurseId = (String) notification.get("nurseId");
                    String type = (String) notification.get("type");
                    Boolean read = (Boolean) notification.get("read");
                    String message = (String) notification.get("message");

                    System.out.println("Form4: Filtering notification: nurseId=" + notifNurseId + ", type=" + type + ", read=" + read + ", message=" + message);

                    if (nurseId.equals(notifNurseId)) {
                        boolean matchesFilter = "All".equals(filter) ||
                            ("Unread".equals(filter) && !read) ||
                            (type != null && type.equalsIgnoreCase(filter));
                        boolean matchesSearch = !applySearchFilter || searchText.isEmpty() || (message != null && message.toLowerCase().contains(searchText));

                        if (matchesFilter && matchesSearch) {
                            filteredNotifications.add(notification);
                            System.out.println("Form4: Notification passed filter: " + notification.toJSONString());
                        } else {
                            System.out.println("Form4: Notification filtered out: filter=" + filter + ", searchText=" + (applySearchFilter ? searchText : "bypassed"));
                        }
                    }
                }
            }

            String sortOrder = (String) sortComboBox.getSelectedItem();
            filteredNotifications.sort((a, b) -> {
                String timeA = (String) a.get("timestamp");
                String timeB = (String) b.get("timestamp");
                try {
                    LocalDateTime dateA = LocalDateTime.parse(timeA, inputFormatter);
                    LocalDateTime dateB = LocalDateTime.parse(timeB, inputFormatter);
                    return "Newest First".equals(sortOrder) ? dateB.compareTo(dateA) : dateA.compareTo(dateB);
                } catch (DateTimeParseException e) {
                    return 0;
                }
            });

            boolean hasNotifications = false;
            int unreadCount = 0;

            for (JSONObject notification : filteredNotifications) {
                String id = (String) notification.get("id");
                String message = (String) notification.get("message");
                String timestamp = (String) notification.get("timestamp");
                String type = (String) notification.get("type");
                String priority = (String) notification.get("priority");
                Boolean read = (Boolean) notification.get("read");

                if (!read) {
                    unreadCount++;
                }

                String formattedTime = "Unknown Time";
                if (timestamp != null) {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
                        formattedTime = dateTime.format(outputFormatter);
                    } catch (DateTimeParseException e) {
                        System.err.println("Form4: Invalid time format for notification: " + timestamp);
                    }
                }

                JPanel notificationEntry = new JPanel(new BorderLayout(10, 5));
                Color priorityColor = switch (priority != null ? priority : "Medium") {
                    case "High" -> new Color(255, 99, 71);
                    case "Low" -> new Color(0, 191, 255);
                    default -> new Color(255, 215, 0);
                };
                notificationEntry.setBackground(new Color(40, 40, 40));
                notificationEntry.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(priorityColor, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                notificationEntry.setOpaque(true);
                notificationEntry.setPreferredSize(new Dimension(0, 100));
                notificationEntry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

                JPanel iconPanel = new JPanel(new BorderLayout());
                iconPanel.setBackground(new Color(40, 40, 40));
                JLabel iconLabel = new JLabel();
                iconLabel.setPreferredSize(new Dimension(40, 40));
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                iconLabel.setText("!");
                iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                iconLabel.setForeground("billing".equalsIgnoreCase(type) ? new Color(255, 215, 0) : new Color(0, 191, 255));
                iconLabel.setBorder(BorderFactory.createLineBorder("billing".equalsIgnoreCase(type) ? new Color(255, 215, 0) : new Color(0, 191, 255), 2));
                iconPanel.add(iconLabel, BorderLayout.CENTER);
                if (!read) {
                    JLabel unreadDot = new JLabel("●");
                    unreadDot.setForeground(new Color(255, 99, 71));
                    unreadDot.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    iconPanel.add(unreadDot, BorderLayout.EAST);
                }
                notificationEntry.add(iconPanel, BorderLayout.WEST);

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(new Color(40, 40, 40));

                JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                messagePanel.setBackground(new Color(40, 40, 40));
                JLabel messageLabel = new JLabel("<html>" + message + "</html>");
                messageLabel.setFont(new Font("Segoe UI", read ? Font.PLAIN : Font.BOLD, 14));
                messageLabel.setForeground(Color.WHITE);
                messagePanel.add(messageLabel);

                JLabel priorityLabel = new JLabel("[" + priority + "]");
                priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                priorityLabel.setForeground(priorityColor);
                messagePanel.add(priorityLabel);

                JLabel typeLabel = new JLabel("Type: " + type);
                typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                typeLabel.setForeground(Color.LIGHT_GRAY);

                JLabel timeLabel = new JLabel(formattedTime);
                timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                timeLabel.setForeground(Color.LIGHT_GRAY);

                textPanel.add(messagePanel);
                textPanel.add(typeLabel);
                textPanel.add(timeLabel);
                notificationEntry.add(textPanel, BorderLayout.CENTER);

                JPanel actionButtonPanel = new JPanel();
                actionButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                actionButtonPanel.setBackground(new Color(40, 40, 40));

                JButton readButton = new JButton(read ? "Mark Unread" : "Mark Read");
                readButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                readButton.setBackground(read ? new Color(255, 165, 0) : new Color(0, 153, 153));
                readButton.setForeground(Color.WHITE);
                readButton.setFocusPainted(false);
                readButton.addActionListener(e -> toggleReadStatus(notification, readButton, notificationEntry));

                JButton dismissButton = new JButton("Dismiss");
                dismissButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dismissButton.setBackground(new Color(220, 53, 69));
                dismissButton.setForeground(Color.WHITE);
                dismissButton.setFocusPainted(false);
                dismissButton.addActionListener(e -> dismissNotification(notification, notificationEntry));

                actionButtonPanel.add(readButton);
                actionButtonPanel.add(dismissButton);
                notificationEntry.add(actionButtonPanel, BorderLayout.EAST);

                notificationEntry.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                notificationEntry.addMouseListener(new MouseAdapter() {
                    Color originalBackground = notificationEntry.getBackground();
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        notificationEntry.setBackground(new Color(50, 50, 50));
                        notificationEntry.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(priorityColor.brighter(), 2),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)
                        ));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        notificationEntry.setBackground(originalBackground);
                        notificationEntry.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(priorityColor, 2),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)
                        ));
                    }
                });
                notificationEntry.setToolTipText("<html>Notification ID: " + id + "<br>Type: " + type + "<br>Priority: " + priority + "<br>Timestamp: " + formattedTime + "</html>");

                notificationEntries.add(notificationEntry);
                hasNotifications = true;
            }

            notificationCountLabel.setText(unreadCount > 0 ? " (" + unreadCount + ")" : "");

            if (!hasNotifications) {
                JLabel noNotificationsLabel = new JLabel("No notifications for Nurse ID " + nurseId + ".");
                noNotificationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noNotificationsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noNotificationsLabel.setForeground(Color.WHITE);
                notificationsPanel.add(noNotificationsLabel);
            } else {
                animateNotifications();
            }

            notificationsPanel.revalidate();
            notificationsPanel.repaint();
            notificationsScrollPane.revalidate();
            notificationsScrollPane.repaint();
            // Force a full UI refresh
            mainPanel.revalidate();
            mainPanel.repaint();
            System.out.println("Form4: Displayed notifications: " + (hasNotifications ? "Yes" : "None"));
        } catch (IOException | ParseException e) {
            JLabel errorLabel = new JLabel("Error loading notifications: " + e.getMessage());
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(Color.WHITE);
            notificationsPanel.add(errorLabel);
            System.err.println("Form4: Error: " + e.getMessage());
        }
    }

    private void animateNotifications() {
        for (JPanel entry : notificationEntries) {
            entry.setVisible(false);
            notificationsPanel.add(entry);
        }

        final int[] index = {0};
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationTimer = new javax.swing.Timer(100, e -> {
            if (index[0] < notificationEntries.size()) {
                notificationEntries.get(index[0]).setVisible(true);
                notificationsPanel.revalidate();
                notificationsPanel.repaint();
                index[0]++;
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
            }
        });
        animationTimer.start();
    }

    private void toggleReadStatus(JSONObject notification, JButton readButton, JPanel notificationEntry) {
        if (nurseId == null) {
            System.out.println("Form4: Cannot toggle read status because nurseId is null");
            return;
        }
        Boolean currentReadStatus = (Boolean) notification.get("read");
        notification.put("read", !currentReadStatus);
        readButton.setText(currentReadStatus ? "Mark Read" : "Mark Unread");
        readButton.setBackground(currentReadStatus ? new Color(0, 153, 153) : new Color(255, 165, 0));
        saveNotifications();
        loadNotifications();
    }

    private void dismissNotification(JSONObject notification, JPanel notificationEntry) {
        if (nurseId == null) {
            System.out.println("Form4: Cannot dismiss notification because nurseId is null");
            return;
        }
        String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
        try {
            JSONParser parser = new JSONParser();
            File notificationsFile = new File(notificationsFilePath);
            JSONObject notificationsJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
            JSONArray notificationsArray = (JSONArray) notificationsJson.get("notifications");

            notificationsArray.remove(notification);
            try (FileWriter file = new FileWriter(notificationsFilePath)) {
                notificationsJson.put("notifications", notificationsArray);
                file.write(notificationsJson.toJSONString());
                file.flush();
            }

            notificationsPanel.remove(notificationEntry);
            notificationEntries.remove(notificationEntry);
            notificationsPanel.revalidate();
            notificationsPanel.repaint();
            loadNotifications();
            System.out.println("Form4: Dismissed notification ID: " + notification.get("id"));
        } catch (IOException | ParseException e) {
            System.err.println("Form4: Error dismissing notification: " + e.getMessage());
        }
    }

    private void markAllAsRead() {
        if (nurseId == null) {
            System.out.println("Form4: Cannot mark all as read because nurseId is null");
            return;
        }
        String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
        try {
            JSONParser parser = new JSONParser();
            File notificationsFile = new File(notificationsFilePath);
            JSONObject notificationsJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
            JSONArray notificationsArray = (JSONArray) notificationsJson.get("notifications");

            for (Object obj : notificationsArray) {
                JSONObject notification = (JSONObject) obj;
                String notifNurseId = (String) notification.get("nurseId");
                if (nurseId.equals(notifNurseId)) {
                    notification.put("read", true);
                }
            }

            try (FileWriter file = new FileWriter(notificationsFilePath)) {
                notificationsJson.put("notifications", notificationsArray);
                file.write(notificationsJson.toJSONString());
                file.flush();
            }

            loadNotifications();
            System.out.println("Form4: Marked all notifications as read for nurseId " + nurseId);
        } catch (IOException | ParseException e) {
            System.err.println("Form4: Error marking all as read: " + e.getMessage());
        }
    }

    private void clearAllNotifications() {
        if (nurseId == null) {
            System.out.println("Form4: Cannot clear notifications because nurseId is null");
            return;
        }
        String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
        try {
            JSONObject notificationsJson = new JSONObject();
            JSONArray notificationsArray = new JSONArray();
            notificationsJson.put("notifications", notificationsArray);

            try (FileWriter file = new FileWriter(notificationsFilePath)) {
                file.write(notificationsJson.toJSONString());
                file.flush();
            }

            notificationsPanel.removeAll();
            notificationEntries.clear();
            JLabel noNotificationsLabel = new JLabel("No notifications for Nurse ID " + nurseId + ".");
            noNotificationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noNotificationsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noNotificationsLabel.setForeground(Color.WHITE);
            notificationsPanel.add(noNotificationsLabel);
            notificationsPanel.revalidate();
            notificationsPanel.repaint();
            loadNotifications();
            System.out.println("Form4: Cleared all notifications for nurseId " + nurseId);
        } catch (IOException e) {
            System.err.println("Form4: Error clearing notifications: " + e.getMessage());
        }
    }

    private void saveNotifications() {
        if (nurseId == null) {
            System.out.println("Form4: Cannot save notifications because nurseId is null");
            return;
        }
        String notificationsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\Nurse\\notifications.json";
        try {
            JSONParser parser = new JSONParser();
            File notificationsFile = new File(notificationsFilePath);
            JSONObject notificationsJson = (JSONObject) parser.parse(new FileReader(notificationsFile));
            JSONArray notificationsArray = (JSONArray) notificationsJson.get("notifications");

            try (FileWriter file = new FileWriter(notificationsFilePath)) {
                notificationsJson.put("notifications", notificationsArray);
                file.write(notificationsJson.toJSONString());
                file.flush();
            }
            System.out.println("Form4: Saved notifications to " + notificationsFilePath);
        } catch (IOException | ParseException e) {
            System.err.println("Form4: Error saving notifications: " + e.getMessage());
        }
    }

   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 357, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
