/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rxpert;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author najx
 */
public class Billing_Services extends javax.swing.JFrame {
 private static final String PATIENT_FILE = "src/maeclinicapp/patient.json";
    private static final String NURSE_FILE = "src/maeclinicapp/nurse.json";
    private static final String BILLS_FILE = "src/maeclinicapp/bills.json";
     // Corrected file paths
    // Service prices
    private static final double CONSULTATION_PRICE = 500.00;
    private static final double DIAGNOSTIC_PRICE = 350.00;
    private static final double MISCELLANEOUS_PRICE = 250.00;

    private double totalAmount = 0.0;
    private String currentBillId = null;

    public Billing_Services() {
        initComponents();
        customInit();
        loadPatients();
        loadNurses();
        loadServices();
        loadDoctors();
        loadBills();
        initializePaymentTypes();
    /**
     * Creates new form Appointment_Scheduling
     */ 
    
    }
private void loadPatients() {
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(PATIENT_FILE)) {
                JSONObject patientsJson = (JSONObject) parser.parse(reader);
                JSONArray patientList = (JSONArray) patientsJson.get("patientList");
                jComboBox21.removeAllItems();
                for (Object obj : patientList) {
                    JSONObject patient = (JSONObject) obj;
                    jComboBox21.addItem((String) patient.get("name"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void loadNurses() {
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(NURSE_FILE)) {
                JSONArray usersList = (JSONArray) parser.parse(reader);
                jComboBox23.removeAllItems();
                for (Object obj : usersList) {
                    JSONObject user = (JSONObject) obj;
                    if ("nurse".equals(user.get("role"))) {
                        jComboBox23.addItem((String) user.get("username"));
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading nurses: " + e.getMessage());
        }
    }

    private void loadDoctors() {
        // Assuming doctors are stored in nurse.json with role "doctor"
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(NURSE_FILE)) {
                JSONArray usersList = (JSONArray) parser.parse(reader);
                jComboBox22.removeAllItems();
                for (Object obj : usersList) {
                    JSONObject user = (JSONObject) obj;
                    if ("doctor".equals(user.get("role"))) {
                        jComboBox22.addItem((String) user.get("username"));
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    private void loadServices() {
        servicesComboBox.removeAllItems();
        servicesComboBox.addItem("Consultation");
        servicesComboBox.addItem("Diagnostic Tests");
        servicesComboBox.addItem("Miscellaneous");
    }

    private void initializePaymentTypes() {
        jComboBox24.removeAllItems();
        jComboBox24.addItem("Cash");
        jComboBox24.addItem("Card");
    }

    private void loadBills() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Bill ID", "Patient Name", "Nurse Name", "Doctor Name", "Department", "Services", "Total Amount", "Status"}
        );
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(BILLS_FILE)) {
                JSONObject billsJson = (JSONObject) parser.parse(reader);
                JSONArray bills = (JSONArray) billsJson.get("bills");
                for (Object obj : bills) {
                    JSONObject bill = (JSONObject) obj;
                    model.addRow(new Object[]{
                        bill.get("billId"),
                        bill.get("patientName"),
                        bill.get("nurseName"),
                        bill.get("doctorName"),
                        bill.get("department"),
                        bill.get("services"),
                        String.format("PHP %.2f", bill.get("totalAmount")),
                        bill.get("status")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
        }
        jTable1.setModel(model);
    }

    private double getServicePrice(String service) {
        switch (service) {
            case "Consultation":
                return CONSULTATION_PRICE;
            case "Diagnostic Tests":
                return DIAGNOSTIC_PRICE;
            case "Miscellaneous":
                return MISCELLANEOUS_PRICE;
            default:
                return 0.0;
        }
    }

    private double getPatientAge(String patientName) {
        try {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(PATIENT_FILE)) {
            JSONObject patientsJson = (JSONObject) parser.parse(reader);
            JSONArray patientList = (JSONArray) patientsJson.get("patientList");
            for (Object obj : patientList) {
                JSONObject patient = (JSONObject) obj;
                if (patient.get("name").equals(patientName)) {
                    String dobStr = (String) patient.get("dateOfBirth");
                    if (dobStr == null || dobStr.length() != 8) {
                        throw new IllegalArgumentException("Invalid date of birth format");
                    }
                    // Parse date of birth (format: yyyyMMdd)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate dob = LocalDate.parse(dobStr, formatter);
                    LocalDate currentDate = LocalDate.now();
                    // Calculate age
                    return Period.between(dob, currentDate).getYears();
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error retrieving patient age: " + e.getMessage());
    }
    return 0.0;
    }
    private String getPatientId(String patientName) {
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(PATIENT_FILE)) {
                JSONObject patientsJson = (JSONObject) parser.parse(reader);
                JSONArray patientList = (JSONArray) patientsJson.get("patientList");
                for (Object obj : patientList) {
                    JSONObject patient = (JSONObject) obj;
                    if (patient.get("name").equals(patientName)) {
                        return (String) patient.get("id");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving patient ID: " + e.getMessage());
        }
        return null;
    }

    private String getNurseId(String nurseUsername) {
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(NURSE_FILE)) {
                JSONArray usersList = (JSONArray) parser.parse(reader);
                for (Object obj : usersList) {
                    JSONObject user = (JSONObject) obj;
                    if (user.get("username").equals(nurseUsername) && "nurse".equals(user.get("role"))) {
                        return (String) user.get("id");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving nurse ID: " + e.getMessage());
        }
        return null;
    }

    private String getDoctorId(String doctorUsername) {
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(NURSE_FILE)) {
                JSONArray usersList = (JSONArray) parser.parse(reader);
                for (Object obj : usersList) {
                    JSONObject user = (JSONObject) obj;
                    if (user.get("username").equals(doctorUsername) && "doctor".equals(user.get("role"))) {
                        return (String) user.get("id");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving doctor ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        sideBarPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        DashboardButton = new javax.swing.JButton();
        PatientManagementButton = new javax.swing.JButton();
        AppointmentButton = new javax.swing.JButton();
        PatientRecordsButton = new javax.swing.JButton();
        BillingButton = new javax.swing.JButton();
        TaskButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel14 = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jTextField49 = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jComboBox21 = new javax.swing.JComboBox<>();
        jComboBox22 = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        jComboBox23 = new javax.swing.JComboBox<>();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jCheckBox8 = new javax.swing.JCheckBox();
        jTextField50 = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jComboBox24 = new javax.swing.JComboBox<>();
        jLabel98 = new javax.swing.JLabel();
        jTextField51 = new javax.swing.JTextField();
        servicesComboBox = new javax.swing.JComboBox<>();
        removeRow = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        serviceTable = new javax.swing.JTable();
        serviceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                // You can populate this section with your initial table data, e.g., { "Row 1", "Data 1" }
            },
            new String [] {
                "Column 1", "Column 2", "Column 3" // Replace with your column names
            }
        ));
        jButton15 = new javax.swing.JButton();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jCheckBox3 = new javax.swing.JCheckBox();
        jButton6 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.X_AXIS));

        sideBarPanel.setBackground(new java.awt.Color(0, 204, 204));
        sideBarPanel.setForeground(new java.awt.Color(255, 255, 255));
        sideBarPanel.setMaximumSize(new java.awt.Dimension(250, 1050));
        sideBarPanel.setMinimumSize(new java.awt.Dimension(250, 1024));
        sideBarPanel.setPreferredSize(new java.awt.Dimension(250, 1024));
        sideBarPanel.setLayout(new javax.swing.BoxLayout(sideBarPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setFont(new java.awt.Font("Helvetica Rounded", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("   RXpert");
        jLabel1.setMaximumSize(new java.awt.Dimension(250, 150));
        jLabel1.setMinimumSize(new java.awt.Dimension(250, 150));
        jLabel1.setPreferredSize(new java.awt.Dimension(250, 150));
        sideBarPanel.add(jLabel1);

        DashboardButton.setBackground(new java.awt.Color(0, 204, 204));
        DashboardButton.setFont(new java.awt.Font("Poppins", 0, 16)); // NOI18N
        DashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        DashboardButton.setText("Dashboard");
        DashboardButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        DashboardButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        DashboardButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(DashboardButton);

        PatientManagementButton.setBackground(new java.awt.Color(0, 204, 204));
        PatientManagementButton.setFont(new java.awt.Font("Poppins", 0, 16)); // NOI18N
        PatientManagementButton.setForeground(new java.awt.Color(255, 255, 255));
        PatientManagementButton.setText("Patient Management");
        PatientManagementButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        PatientManagementButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        PatientManagementButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(PatientManagementButton);

        AppointmentButton.setBackground(new java.awt.Color(0, 204, 204));
        AppointmentButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        AppointmentButton.setForeground(new java.awt.Color(255, 255, 255));
        AppointmentButton.setText("Appointment Scheduling");
        AppointmentButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        AppointmentButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        AppointmentButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(AppointmentButton);

        PatientRecordsButton.setBackground(new java.awt.Color(0, 204, 204));
        PatientRecordsButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        PatientRecordsButton.setForeground(new java.awt.Color(255, 255, 255));
        PatientRecordsButton.setText("Patient Records");
        PatientRecordsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        PatientRecordsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        PatientRecordsButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(PatientRecordsButton);

        BillingButton.setBackground(new java.awt.Color(0, 204, 204));
        BillingButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BillingButton.setForeground(new java.awt.Color(255, 255, 255));
        BillingButton.setText("Billing Services");
        BillingButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        BillingButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BillingButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(BillingButton);

        TaskButton.setBackground(new java.awt.Color(0, 204, 204));
        TaskButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        TaskButton.setForeground(new java.awt.Color(255, 255, 255));
        TaskButton.setText("Task Management");
        TaskButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        TaskButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TaskButton.setMaximumSize(new java.awt.Dimension(250, 50));
        sideBarPanel.add(TaskButton);

        getContentPane().add(sideBarPanel);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 50));
        jPanel2.setPreferredSize(new java.awt.Dimension(1328, 50));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBackground(new java.awt.Color(252, 255, 246));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 50, 50, 50));
        jPanel4.setMaximumSize(new java.awt.Dimension(1440, 1024));

        jTabbedPane1.setBackground(new java.awt.Color(38, 198, 218));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Poppins", 0, 16)); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel85.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(102, 102, 102));
        jLabel85.setText("Select Patient");

        jLabel87.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(102, 102, 102));
        jLabel87.setText("Department");

        jTextField47.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField47jTextField7ActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(102, 102, 102));
        jLabel88.setText("Select Doctor");

        jLabel89.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel89.setForeground(new java.awt.Color(102, 102, 102));
        jLabel89.setText("Admission Date");

        jTextField48.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField48jTextField9ActionPerformed(evt);
            }
        });

        jLabel90.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(102, 102, 102));
        jLabel90.setText("Admission Date");

        jTextField49.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField49jTextField10ActionPerformed(evt);
            }
        });

        jLabel91.setBackground(new java.awt.Color(38, 198, 218));
        jLabel91.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel91.setForeground(new java.awt.Color(255, 255, 255));
        jLabel91.setText("     Services");
        jLabel91.setOpaque(true);

        jLabel92.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel92.setForeground(new java.awt.Color(102, 102, 102));
        jLabel92.setText("Select Services");

        jComboBox21.setEditable(true);
        jComboBox21.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox21.setForeground(new java.awt.Color(102, 102, 102));
        jComboBox21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jComboBox21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox21ActionPerformed(evt);
            }
        });

        jComboBox22.setEditable(true);
        jComboBox22.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox22.setForeground(new java.awt.Color(102, 102, 102));
        jComboBox22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel93.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel93.setForeground(new java.awt.Color(102, 102, 102));
        jLabel93.setText("Select Nurse");

        jComboBox23.setEditable(true);
        jComboBox23.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox23.setForeground(new java.awt.Color(102, 102, 102));
        jComboBox23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel94.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(102, 102, 102));
        jLabel94.setText("Payment Type");

        jLabel95.setBackground(new java.awt.Color(38, 198, 218));
        jLabel95.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel95.setForeground(new java.awt.Color(255, 255, 255));
        jLabel95.setText("   Payment");
        jLabel95.setOpaque(true);

        jButton13.setBackground(new java.awt.Color(0, 204, 204));
        jButton13.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("Update");
        jButton13.setBorder(null);
        jButton13.setMinimumSize(new java.awt.Dimension(100, 40));
        jButton13.setPreferredSize(new java.awt.Dimension(100, 40));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jCheckBox8.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox8.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jCheckBox8.setForeground(new java.awt.Color(102, 102, 102));
        jCheckBox8.setText("Please Confirm");
        jCheckBox8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });

        jTextField50.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField50.setForeground(new java.awt.Color(102, 102, 102));
        jTextField50.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField50jTextField14ActionPerformed(evt);
            }
        });

        jLabel96.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel96.setForeground(new java.awt.Color(38, 198, 218));
        jLabel96.setText("$000,000.00");

        jLabel97.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel97.setForeground(new java.awt.Color(102, 102, 102));
        jLabel97.setText("Discount (%)");

        jComboBox24.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox24.setForeground(new java.awt.Color(102, 102, 102));
        jComboBox24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jComboBox24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox24jComboBox2ActionPerformed(evt);
            }
        });

        jLabel98.setBackground(new java.awt.Color(255, 255, 153));
        jLabel98.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(255, 102, 102));
        jLabel98.setText("      Please Fill Up All the Form");
        jLabel98.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 102, 102), 1, true));
        jLabel98.setOpaque(true);

        jTextField51.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField51.setForeground(new java.awt.Color(102, 102, 102));
        jTextField51.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField51.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField51ActionPerformed(evt);
            }
        });

        servicesComboBox.setEditable(true);
        servicesComboBox.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        servicesComboBox.setForeground(new java.awt.Color(102, 102, 102));
        servicesComboBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        removeRow.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        removeRow.setForeground(new java.awt.Color(38, 198, 218));
        removeRow.setText("Delete Row");
        removeRow.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(38, 198, 218)));
        removeRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRowActionPerformed(evt);
            }
        });

        serviceTable.setForeground(new java.awt.Color(102, 102, 102));
        serviceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Service"
            }
        ));
        serviceTable.setRowHeight(40);
        jScrollPane7.setViewportView(serviceTable);

        jButton15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton15.setForeground(new java.awt.Color(38, 198, 218));
        jButton15.setText("Add Service");
        jButton15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(38, 198, 218)));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jLabel99.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel99.setForeground(new java.awt.Color(102, 102, 102));
        jLabel99.setText("Advance Paid");

        jLabel100.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel100.setForeground(new java.awt.Color(102, 102, 102));
        jLabel100.setText("Total Amount :");

        jLabel102.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(102, 102, 102));
        jLabel102.setText("Total Due : ");

        jLabel103.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel103.setForeground(new java.awt.Color(38, 198, 218));
        jLabel103.setText("$000,000.00");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel92)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jCheckBox8)
                                .addGap(35, 35, 35)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(servicesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel97)
                            .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel99)
                            .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(79, 79, 79)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel94)
                                    .addComponent(jComboBox24, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(323, 323, 323))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel102)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel103)
                                .addGap(34, 34, 34))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel98, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(0, 29, Short.MAX_VALUE)
                                .addComponent(removeRow, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(720, 720, 720)
                                .addComponent(jLabel100)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel96)
                                .addGap(19, 19, 19))
                            .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel85)
                                    .addComponent(jLabel89)
                                    .addComponent(jLabel87)
                                    .addComponent(jTextField48)
                                    .addComponent(jTextField47)
                                    .addComponent(jComboBox21, 0, 525, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel88)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jComboBox23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel93)
                                    .addComponent(jLabel90)))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(17, 17, 17))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel93)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jComboBox22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel90))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel89)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel92)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(servicesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(removeRow, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel96)
                        .addComponent(jLabel100)))
                .addGap(18, 18, 18)
                .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(jLabel97)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField51, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(jLabel99)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField50, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox24, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(jLabel102))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox8)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel14);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add Payment", jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel6.setForeground(new java.awt.Color(102, 102, 102));

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(102, 102, 102));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Appointment Date", "Time Slot", "Reason for Visit"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(450);
        }

        jButton2.setBackground(new java.awt.Color(38, 198, 218));
        jButton2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(38, 198, 218));
        jButton2.setText("Previous");
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(38, 198, 218));
        jButton3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(38, 198, 218));
        jButton3.setText("Next");
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setForeground(new java.awt.Color(38, 198, 218));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("2");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel12.setOpaque(true);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setForeground(new java.awt.Color(38, 198, 218));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("3");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel13.setOpaque(true);

        jLabel14.setBackground(new java.awt.Color(38, 198, 218));
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("1");
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel14.setOpaque(true);

        jLabel24.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText(" Search Patient Payment History :");

        jTextField15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField15.setForeground(new java.awt.Color(102, 102, 102));
        jTextField15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField15ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(38, 198, 218));
        jButton5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(38, 198, 218));
        jButton5.setText("Search");
        jButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1151, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jButton3)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5)
                    .addComponent(jLabel24)
                    .addComponent(jButton2)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addGap(14, 14, 14))
        );

        jTabbedPane1.addTab("All Payments", jPanel6);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("Patient Name");

        jTextField4.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 102, 102));
        jLabel9.setText("Nurse");

        jTextField16.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField16ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(102, 102, 102));
        jLabel25.setText("Doctor");

        jTextField17.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField17ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(102, 102, 102));
        jLabel26.setText("Department");

        jTextField18.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField18ActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("Total Amount");

        jTextField19.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField19ActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(102, 102, 102));
        jLabel28.setText("Services");

        jTextField20.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jTextField20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTextField20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField20ActionPerformed(evt);
            }
        });

        jCheckBox3.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox3.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jCheckBox3.setForeground(new java.awt.Color(102, 102, 102));
        jCheckBox3.setText("Please Confirm");
        jCheckBox3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(0, 204, 204));
        jButton6.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Update");
        jButton6.setBorder(null);
        jButton6.setMinimumSize(new java.awt.Dimension(100, 40));
        jButton6.setPreferredSize(new java.awt.Dimension(100, 40));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel29.setBackground(new java.awt.Color(255, 255, 153));
        jLabel29.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 102, 102));
        jLabel29.setText("      Please Fill Up All the Form");
        jLabel29.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 102, 102), 1, true));
        jLabel29.setOpaque(true);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(jTextField18)
                    .addComponent(jTextField20)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jCheckBox3)
                        .addGap(35, 35, 35)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTextField17)
                    .addComponent(jTextField19)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27)
                            .addComponent(jLabel26))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox3)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Edit Payment", jPanel7);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 37, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4);

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         JOptionPane.showMessageDialog(this, "Previous page not implemented.");  // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      JOptionPane.showMessageDialog(this, "Next page not implemented.");  // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField15ActionPerformed
      jButton5ActionPerformed(evt);  // TODO add your handling code here:
    }//GEN-LAST:event_jTextField15ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
String patientName = jTextField4.getText().trim();
    if (patientName.isEmpty()) {
        // Clear fields if no patient name is entered
        currentBillId = null;
        jTextField16.setText("");
        jTextField17.setText("");
        jTextField18.setText("");
        jTextField19.setText("");
        jTextField20.setText("");
        return;
    }

    try {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(BILLS_FILE)) {
            JSONObject billsJson = (JSONObject) parser.parse(reader);
            JSONArray bills = (JSONArray) billsJson.get("bills");
            JSONObject latestBill = null;
            String latestDate = "";
            for (Object obj : bills) {
                JSONObject bill = (JSONObject) obj;
                if (((String) bill.get("patientName")).equalsIgnoreCase(patientName)) {
                    String dateAdded = (String) bill.get("date");
                    if (latestBill == null || dateAdded.compareTo(latestDate) > 0) {
                        latestBill = bill;
                        latestDate = dateAdded;
                    }
                }
            }

            if (latestBill != null) {
                // Store the bill ID for updating later
                currentBillId = (String) latestBill.get("billId");
                // Populate the fields with the latest bill details
                jTextField4.setText((String) latestBill.get("patientName")); // Patient Name
                jTextField16.setText((String) latestBill.get("nurseName")); // Nurse
                jTextField17.setText((String) latestBill.get("doctorName")); // Doctor
                jTextField19.setText((String) latestBill.get("department")); // Department
                jTextField18.setText((String) latestBill.get("services")); // Services
                jTextField20.setText(String.format("PHP %.2f", latestBill.get("totalAmount"))); // Total Amount (after discount)
            } else {
                // Clear fields if no bill is found
                currentBillId = null;
                jTextField16.setText("");
                jTextField17.setText("");
                jTextField18.setText("");
                jTextField19.setText("");
                jTextField20.setText("");
                JOptionPane.showMessageDialog(this, "No bill found for patient: " + patientName);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading bill details: " + e.getMessage());
    }
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField16ActionPerformed

    private void jTextField17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField17ActionPerformed

    private void jTextField18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField18ActionPerformed

    private void jTextField19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField19ActionPerformed

    private void jTextField20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField20ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    String patientName = jTextField4.getText().trim();
    String advancePaid = jTextField16.getText().trim();
    boolean confirmed = jCheckBox3.isSelected();

    // Validation
    if (patientName.isEmpty() || !confirmed) {
        JOptionPane.showMessageDialog(this, "Please enter patient name and confirm.");
        return;
    }

    if (currentBillId == null) {
        JOptionPane.showMessageDialog(this, "No bill selected to update. Please enter a valid patient name.");
        return;
    }

    try {
        double advancePaidValue = advancePaid.isEmpty() ? 0.0 : Double.parseDouble(advancePaid);
        if (advancePaidValue < 0) {
            JOptionPane.showMessageDialog(this, "Advance paid cannot be negative.");
            return;
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(BILLS_FILE)) {
            JSONObject billsJson = (JSONObject) parser.parse(reader);
            JSONArray bills = (JSONArray) billsJson.get("bills");
            boolean updated = false;
            for (Object obj : bills) {
                JSONObject bill = (JSONObject) obj;
                if (bill.get("billId").equals(currentBillId)) {
                    double totalAmount = ((Number) bill.get("totalAmount")).doubleValue();
                    double newAdvance = advancePaidValue;
                    if (newAdvance > totalAmount) {
                        JOptionPane.showMessageDialog(this, "Total advance paid cannot exceed total amount.");
                        return;
                    }
                    bill.put("advancePaid", newAdvance);
                    bill.put("totalDue", totalAmount - newAdvance);
                    bill.put("status", newAdvance >= totalAmount ? "paid" : "unpaid");
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                JOptionPane.showMessageDialog(this, "Bill not found.");
                return;
            }

            try (FileWriter writer = new FileWriter(BILLS_FILE)) {
                writer.write(billsJson.toJSONString());
                writer.flush();
            }

            JOptionPane.showMessageDialog(this, "Bill updated successfully!");
            // Reset form
            currentBillId = null;
            jTextField4.setText("");
            jTextField16.setText("");
            jTextField17.setText("");
            jTextField18.setText("");
            jTextField19.setText("");
            jTextField20.setText("");
            jCheckBox3.setSelected(false);
            loadBills();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid advance paid format.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating bill: " + e.getMessage());
    }

    
   // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBox24jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox24jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox24jComboBox2ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
   String patientSelection = (String) jComboBox21.getSelectedItem();
    String nurseSelection = (String) jComboBox23.getSelectedItem();
    String doctorSelection = (String) jComboBox22.getSelectedItem();
    String department = jTextField47.getText().trim();
    String admissionDate = jTextField48.getText().trim();
    String paymentType = (String) jComboBox24.getSelectedItem();
    String advancePaid = jTextField50.getText().trim();
    boolean confirmed = jCheckBox8.isSelected();

    // Validation
    if (patientSelection == null || nurseSelection == null || doctorSelection == null ||
        department.isEmpty() || admissionDate.isEmpty() || paymentType == null ||
        serviceTable.getRowCount() == 0 || !confirmed) {
        JOptionPane.showMessageDialog(this, "Please fill all required fields and confirm.");
        return;
    }

    // Get IDs
    String patientId = getPatientId(patientSelection);
    String nurseId = getNurseId(nurseSelection);
    String doctorId = getDoctorId(doctorSelection);
    if (patientId == null || nurseId == null || doctorId == null) {
        JOptionPane.showMessageDialog(this, "Error retrieving IDs.");
        return;
    }

    try {
        double advancePaidValue = advancePaid.isEmpty() ? 0.0 : Double.parseDouble(advancePaid);
        if (advancePaidValue < 0) {
            JOptionPane.showMessageDialog(this, "Advance paid cannot be negative.");
            return;
        }

        // Get services from table
        DefaultTableModel model = (DefaultTableModel) serviceTable.getModel();
        StringBuilder services = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            services.append(model.getValueAt(i, 0)).append(",");
        }
        String servicesString = services.toString().replaceAll(",$", "");

        // Calculate discount based on age
        double patientAge = getPatientAge(patientSelection);
        double ageBasedDiscount = (patientAge <= 18 || patientAge >= 60) ? 20.0 : 0.0;

        // Get additional discount from jTextField51
        double additionalDiscount = 0.0;
        try {
            String additionalDiscountStr = jTextField51.getText().trim();
            additionalDiscount = additionalDiscountStr.isEmpty() ? 0.0 : Double.parseDouble(additionalDiscountStr);
            if (additionalDiscount < 0 || additionalDiscount > 100) {
                JOptionPane.showMessageDialog(this, "Additional discount must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount format in Discount (%) field.");
            return;
        }

        // Calculate total discount (age-based + additional)
        double totalDiscount = ageBasedDiscount + additionalDiscount;
        if (totalDiscount > 100) {
            JOptionPane.showMessageDialog(this, "Total discount cannot exceed 100%.");
            return;
        }

        // Calculate total amount after discount
        double finalAmount = totalAmount * (1 - totalDiscount / 100);
        double totalDue = finalAmount - advancePaidValue;
        if (totalDue < 0) {
            JOptionPane.showMessageDialog(this, "Advance paid cannot exceed total amount after discount.");
            return;
        }

        // Update UI
        jTextField51.setText(String.format("%.2f", totalDiscount));
        jLabel96.setText(String.format("PHP %.2f", totalAmount)); // Total Amount (before discount)
        jLabel103.setText(String.format("PHP %.2f", totalDue));   // Total Due (after discount and advance)

        // Load or initialize bills.json
        JSONObject billsJson;
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(BILLS_FILE)) {
            billsJson = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            billsJson = new JSONObject();
            billsJson.put("bills", new JSONArray());
        }

        // Add new bill
        JSONArray bills = (JSONArray) billsJson.getOrDefault("bills", new JSONArray());
        JSONObject bill = new JSONObject();
        bill.put("billId", String.valueOf(bills.size() + 1));
        bill.put("patientId", patientId);
        bill.put("patientName", patientSelection);
        bill.put("nurseId", nurseId);
        bill.put("nurseName", nurseSelection);
        bill.put("doctorId", doctorId);
        bill.put("doctorName", doctorSelection);
        bill.put("department", department);
        bill.put("admissionDate", admissionDate);
        bill.put("services", servicesString);
        bill.put("amount", totalAmount); // Original amount before discount
        bill.put("discount", totalDiscount); // Total discount percentage
        bill.put("totalAmount", finalAmount); // Amount after discount
        bill.put("advancePaid", advancePaidValue);
        bill.put("totalDue", totalDue);
        bill.put("paymentType", paymentType);
        bill.put("status", totalDue == 0 ? "paid" : "unpaid");
        bill.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        bills.add(bill);
        billsJson.put("bills", bills);

        // Save to bills.json
        try (FileWriter writer = new FileWriter(BILLS_FILE)) {
            writer.write(billsJson.toJSONString());
            writer.flush();
        }

        JOptionPane.showMessageDialog(this, "Bill created successfully!");
        // Reset form
        jTextField47.setText("");
        jTextField48.setText("");
        jTextField50.setText("");
        jTextField51.setText("");
        jComboBox21.setSelectedIndex(-1);
        jComboBox22.setSelectedIndex(-1);
        jComboBox23.setSelectedIndex(-1);
        jComboBox24.setSelectedIndex(-1);
        servicesComboBox.setSelectedIndex(-1);
        jCheckBox8.setSelected(false);
        model.setRowCount(0);
        totalAmount = 0.0;
        jLabel96.setText("PHP 0.00");
        jLabel103.setText("PHP 0.00");
        loadBills();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid amount format.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error creating bill: " + e.getMessage());
    }


      // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed


   private void customInit() {
       setExtendedState(JFrame.MAXIMIZED_BOTH);  // Make the frame full screen
       
        jLabel1.setBackground(Color.white);
        jTabbedPane1.setFont(new Font("Poppins", Font.BOLD, 16));
        jTable1.setRowHeight(40);
        DashboardButton.addActionListener(e -> DashboardButton(e));
        AppointmentButton.addActionListener(e -> AppointmentButton(e));
        PatientRecordsButton.addActionListener(e -> PatientRecordsButton(e));
        setTabSize(jTabbedPane1, 0, new Dimension(175, 30));
        setTabSize(jTabbedPane1, 1, new Dimension(175, 30));
        setTabSize(jTabbedPane1, 2, new Dimension(175, 30));
        jTabbedPane1.revalidate();
        jTabbedPane1.repaint();
        applyGradient(sideBarPanel, new Color(0x26C6DA), new Color(0x92FFCD), false);
        setButtonBG(DashboardButton, AppointmentButton, PatientRecordsButton, PatientManagementButton, TaskButton, BillingButton);
        revalidate();
        repaint();
    }

    private void setTabSize(JTabbedPane tabbedPane, int index, Dimension size) {
        Font poppinsFont = new Font("Poppins", Font.PLAIN, 16);
        JLabel label = new JLabel(tabbedPane.getTitleAt(index));
        label.setPreferredSize(size);
        label.setMinimumSize(size);
        label.setMaximumSize(size);
        label.setFont(poppinsFont);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        tabbedPane.setTabComponentAt(index, label);
    }

    public void setButtonBG(JButton... buttons) {
        for (JButton button : buttons) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setForeground(Color.WHITE);
        }
    }

    private void applyGradient(JPanel panel, Color startColor, Color endColor, boolean isHorizontal) {
        panel.setOpaque(false);
        panel.setUI(new javax.swing.plaf.PanelUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                int width = c.getWidth();
                int height = c.getHeight();
                GradientPaint gp = isHorizontal
                    ? new GradientPaint(0, 0, startColor, width, 0, endColor)
                    : new GradientPaint(0, 0, startColor, 0, height, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
                super.update(g, c);
            }
        });
    }

    private void DashboardButton(ActionEvent evt) {
        Dashboard a = new Dashboard();
        a.setVisible(true);
        setVisible(false);
    }

    private void AppointmentButton(ActionEvent evt) {
        Appointment_Scheduling a = new Appointment_Scheduling();
        a.setVisible(true);
        setVisible(false);
    }

    private void PatientRecordsButton(ActionEvent evt) {
        Patient_Records a = new Patient_Records();
        a.setVisible(true);
        setVisible(false);
    }
    private void jTextField49jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField49jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField49jTextField10ActionPerformed

    private void jTextField48jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField48jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField48jTextField9ActionPerformed

    private void jTextField47jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField47jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField47jTextField7ActionPerformed

    private void removeRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRowActionPerformed
     int selectedRow = serviceTable.getSelectedRow();
    if (selectedRow != -1) {
        DefaultTableModel model = (DefaultTableModel) serviceTable.getModel();
        String service = (String) model.getValueAt(selectedRow, 0);
        totalAmount -= getServicePrice(service);
        model.removeRow(selectedRow);
        jLabel96.setText(String.format("PHP %.2f", totalAmount));

        // Recalculate total due with current discount and advance paid
        double totalDiscount = 0.0;
        try {
            String discountStr = jTextField51.getText().trim();
            totalDiscount = discountStr.isEmpty() ? 0.0 : Double.parseDouble(discountStr);
        } catch (NumberFormatException e) {
            totalDiscount = 0.0;
        }
        double advancePaidValue = 0.0;
        try {
            String advancePaidStr = jTextField50.getText().trim();
            advancePaidValue = advancePaidStr.isEmpty() ? 0.0 : Double.parseDouble(advancePaidStr);
        } catch (NumberFormatException e) {
            advancePaidValue = 0.0;
        }
        double finalAmount = totalAmount * (1 - totalDiscount / 100);
        double totalDue = finalAmount - advancePaidValue;
        jLabel103.setText(String.format("PHP %.2f", totalDue));
    } else {
        JOptionPane.showMessageDialog(this, "Please select a row to remove.");
    }

    

    }//GEN-LAST:event_removeRowActionPerformed
private void updateTotalDue() {
    double totalDiscount = 0.0;
    try {
        String discountStr = jTextField51.getText().trim();
        totalDiscount = discountStr.isEmpty() ? 0.0 : Double.parseDouble(discountStr);
    } catch (NumberFormatException e) {
        totalDiscount = 0.0;
    }
    double advancePaidValue = 0.0;
    try {
        String advancePaidStr = jTextField50.getText().trim();
        advancePaidValue = advancePaidStr.isEmpty() ? 0.0 : Double.parseDouble(advancePaidStr);
    } catch (NumberFormatException e) {
        advancePaidValue = 0.0;
    }
    double finalAmount = totalAmount * (1 - totalDiscount / 100);
    double totalDue = finalAmount - advancePaidValue;
    jLabel103.setText(String.format("PHP %.2f", totalDue));
}
    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
      String selectedService = (String) servicesComboBox.getSelectedItem();
    if (selectedService != null) {
        DefaultTableModel model = (DefaultTableModel) serviceTable.getModel();
        model.addRow(new Object[]{selectedService});
        totalAmount += getServicePrice(selectedService);
        jLabel96.setText(String.format("PHP %.2f", totalAmount));

        // Recalculate total due with current discount and advance paid
        double totalDiscount = 0.0;
        try {
            String discountStr = jTextField51.getText().trim();
            totalDiscount = discountStr.isEmpty() ? 0.0 : Double.parseDouble(discountStr);
        } catch (NumberFormatException e) {
            totalDiscount = 0.0;
        }
        double advancePaidValue = 0.0;
        try {
            String advancePaidStr = jTextField50.getText().trim();
            advancePaidValue = advancePaidStr.isEmpty() ? 0.0 : Double.parseDouble(advancePaidStr);
        } catch (NumberFormatException e) {
            advancePaidValue = 0.0;
        }
        double finalAmount = totalAmount * (1 - totalDiscount / 100);
        double totalDue = finalAmount - advancePaidValue;
        jLabel103.setText(String.format("PHP %.2f", totalDue));
    }




    // Add selected item as a new row

    }//GEN-LAST:event_jButton15ActionPerformed

    private void jTextField50jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField50jTextField14ActionPerformed
     updateTotalDue();   // TODO add your handling code here:
    }//GEN-LAST:event_jTextField50jTextField14ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
String searchQuery = jTextField15.getText().trim().toLowerCase();
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Bill ID", "Patient Name", "Nurse Name", "Doctor Name", "Department", "Services", "Total Amount", "Status"}
        );
        try {
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(BILLS_FILE)) {
                JSONObject billsJson = (JSONObject) parser.parse(reader);
                JSONArray bills = (JSONArray) billsJson.get("bills");
                for (Object obj : bills) {
                    JSONObject bill = (JSONObject) obj;
                    if (((String) bill.get("patientName")).toLowerCase().contains(searchQuery)) {
                        model.addRow(new Object[]{
                            bill.get("billId"),
                            bill.get("patientName"),
                            bill.get("nurseName"),
                            bill.get("doctorName"),
                            bill.get("department"),
                            bill.get("services"),
                            String.format("PHP %.2f", bill.get("totalAmount")),
                            bill.get("status")
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching bills: " + e.getMessage());
        }
        jTable1.setModel(model);
            // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox21ActionPerformed
      String patientSelection = (String) jComboBox21.getSelectedItem();
    if (patientSelection != null && !patientSelection.trim().isEmpty()) {
        // Calculate age-based discount
        double patientAge = getPatientAge(patientSelection);
        double ageBasedDiscount = (patientAge <= 18 || patientAge >= 60) ? 20.0 : 0.0;

        // Get the current additional discount from jTextField51
        double additionalDiscount = 0.0;
        try {
            String discountStr = jTextField51.getText().trim();
            additionalDiscount = discountStr.isEmpty() ? 0.0 : Double.parseDouble(discountStr);
        } catch (NumberFormatException e) {
            additionalDiscount = 0.0;
        }

        // Combine age-based and additional discount
        double totalDiscount = ageBasedDiscount + additionalDiscount;
        if (totalDiscount > 100) {
            totalDiscount = 100.0; // Cap discount at 100%
        }

        // Update the discount field
        jTextField51.setText(String.format("%.2f", totalDiscount));

        // Update total due
        updateTotalDue();
    }
  // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox21ActionPerformed

    private void jTextField51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField51ActionPerformed
updateTotalDue();        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField51ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Billing_Services.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Billing_Services.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Billing_Services.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Billing_Services.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Billing_Services().setVisible(true);
            }
        });
    }
    
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AppointmentButton;
    private javax.swing.JButton BillingButton;
    private javax.swing.JButton DashboardButton;
    private javax.swing.JButton PatientManagementButton;
    private javax.swing.JButton PatientRecordsButton;
    private javax.swing.JButton TaskButton;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JComboBox<String> jComboBox21;
    private javax.swing.JComboBox<String> jComboBox22;
    private javax.swing.JComboBox<String> jComboBox23;
    private javax.swing.JComboBox<String> jComboBox24;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JButton removeRow;
    private javax.swing.JTable serviceTable;
    private javax.swing.JComboBox<String> servicesComboBox;
    private javax.swing.JPanel sideBarPanel;
    // End of variables declaration//GEN-END:variables
}
