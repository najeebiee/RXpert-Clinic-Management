/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Nurse;import java.io.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class GradientPanel extends JPanel {
    private Color startColor, endColor, hoverStartColor, hoverEndColor;
    private boolean isHovered = false;

    public GradientPanel(Color startColor, Color endColor, Color hoverStartColor, Color hoverEndColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.hoverStartColor = hoverStartColor;
        this.hoverEndColor = hoverEndColor;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color1 = isHovered ? hoverStartColor : startColor;
        Color color2 = isHovered ? hoverEndColor : endColor;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        g2d.setColor(new Color(0, 206, 209));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
    }
}

// AppointmentCard: Modern card for appointments
class AppointmentCard extends GradientPanel {
    public AppointmentCard(String title, String time, String patientName, String doctorId, MouseListener clickListener) {
        super(new Color(255, 255, 255), new Color(230, 245, 255), new Color(200, 230, 255), new Color(220, 240, 255));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 120));
        setMaximumSize(new Dimension(200, 120));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(0, 206, 209));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(new Color(50, 50, 50));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel patientLabel = new JLabel(patientName);
        patientLabel.setForeground(new Color(0, 0, 0));
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(5));
        add(timeLabel);
        add(Box.createVerticalStrut(5));
        add(patientLabel);
        add(Box.createVerticalGlue());

        setToolTipText("Doctor ID: " + (doctorId != null ? doctorId : "Unknown"));
        addMouseListener(clickListener);
    }
}

// ReceiptCard: Modern card for receipts
class ReceiptCard extends GradientPanel {
    public ReceiptCard(String title, String date, String patientName, String billId, MouseListener clickListener, int height) {
        super(new Color(255, 255, 255), new Color(230, 245, 255), new Color(200, 230, 255), new Color(220, 240, 255));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, height));
        setMaximumSize(new Dimension(150, height));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(0, 206, 209));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dateLabel = new JLabel(date);
        dateLabel.setForeground(new Color(50, 50, 50));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel patientLabel = new JLabel(patientName);
        patientLabel.setForeground(new Color(0, 0, 0));
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(5));
        add(dateLabel);
        add(Box.createVerticalStrut(5));
        add(patientLabel);
        add(Box.createVerticalGlue());

        setToolTipText("Receipt #" + billId);
        addMouseListener(clickListener);
    }
}
/**
 *
 * @author USER
 */
public class Form extends TransitionsForm { 
    
    private String nurseId;

 

    public Form() {
        initComponents();
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
        System.out.println("Form: Nurse ID set to " + nurseId);
        loadAppointments();
    }

 private void loadAppointments() {
        try {
            String appointmentsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\maeclinicapp\\appointments.json";
            String patientsFilePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\maeclinicapp\\patient.json";
            JSONParser parser = new JSONParser();

            File patientsFile = new File(patientsFilePath);
            File appointmentsFile = new File(appointmentsFilePath);
            System.out.println("Form: Patients file exists: " + patientsFile.exists());
            System.out.println("Form: Appointments file exists: " + appointmentsFile.exists());

            JSONObject patientsJson = (JSONObject) parser.parse(new FileReader(patientsFile));
            JSONArray patientList = (JSONArray) patientsJson.get("patientList");
            HashMap<String, String> patientNames = new HashMap<>();
            if (patientList != null) {
                for (Object obj : patientList) {
                    JSONObject patient = (JSONObject) obj;
                    String id = (String) patient.get("id");
                    String name = (String) patient.get("name");
                    if (id != null && name != null) {
                        patientNames.put(id, name);
                    }
                }
            }
            System.out.println("Form: Loaded " + patientNames.size() + " patients");

            JSONObject appointmentsJson = (JSONObject) parser.parse(new FileReader(appointmentsFile));
            JSONArray appointments = (JSONArray) appointmentsJson.get("appointments");
            boolean hasAppointments = false;

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, h:mm a");

            if (appointments != null && nurseId != null) {
                for (Object obj : appointments) {
                    JSONObject appointment = (JSONObject) obj;
                    String apptNurseId = (String) appointment.get("nurseId");
                    if (nurseId.equals(apptNurseId)) {
                        String patientId = (String) appointment.get("patientId");
                        String appointmentTime = (String) appointment.get("appointmentTime");
                        String doctorId = (String) appointment.get("doctorId");
                        String patientName = patientNames.getOrDefault(patientId, "Unknown Patient (" + patientId + ")");

                        String formattedTime = "Unknown Time";
                        if (appointmentTime != null) {
                            try {
                                LocalDateTime dateTime = LocalDateTime.parse(appointmentTime, inputFormatter);
                                formattedTime = dateTime.format(outputFormatter);
                            } catch (DateTimeParseException e) {
                                System.err.println("Form: Invalid time format for appointment: " + appointmentTime);
                            }
                        }

                        AppointmentCard appointmentCard = new AppointmentCard(
                            "APPOINTMENT",
                            formattedTime,
                            patientName,
                            doctorId,
                            new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    showFullAppointment(appointment, patientName);
                                }
                            }
                        );
                        appointmentsGridPanel.add(appointmentCard);
                        hasAppointments = true;
                    }
                }
            }

            if (!hasAppointments) {
                JLabel noAppointmentsLabel = new JLabel("No appointments assigned for Nurse ID " + nurseId + ".");
                noAppointmentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noAppointmentsLabel.setForeground(new Color(0, 206, 209));
                appointmentsGridPanel.add(noAppointmentsLabel);
            }

            appointmentsGridPanel.revalidate();
            appointmentsGridPanel.repaint();
            System.out.println("Form: Displayed appointments: " + (hasAppointments ? "Yes" : "None"));
        } catch (IOException | ParseException e) {
            JLabel errorLabel = new JLabel("Error loading appointments: " + e.getMessage());
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(new Color(0, 206, 209));
            appointmentsGridPanel.add(errorLabel);
            System.err.println("Form: Error: " + e.getMessage());
        }
    }

    private void showFullAppointment(JSONObject appointment, String patientName) {
        JDialog appointmentDialog = new JDialog();
        appointmentDialog.setTitle("Appointment Details");
        appointmentDialog.setSize(400, 350);
        appointmentDialog.setLayout(new BorderLayout());
        appointmentDialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 206, 209), 0, getHeight(), new Color(32, 178, 170));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Icon appointmentIcon = loadIcon("/icons/appointment.png", "[Appointment]");
        Icon calendarIcon = loadIcon("/icons/calendar.png", "[Calendar]");
        Icon clockIcon = loadIcon("/icons/clock.png", "[Clock]");
        Icon userIcon = loadIcon("/icons/user.png", "[User]");
        Icon doctorIcon = loadIcon("/icons/doctor.png", "[Doctor]");
        Icon nurseIcon = loadIcon("/icons/nurse.png", "[Nurse]");

        String appointmentTime = (String) appointment.get("appointmentTime");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedDate = "Unknown Date";
        String formattedTime = "Unknown Time";
        if (appointmentTime != null) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(appointmentTime, inputFormatter);
                formattedDate = dateTime.format(dateFormatter);
                formattedTime = dateTime.format(timeFormatter);
            } catch (DateTimeParseException e) {
                System.err.println("Form: Invalid time format for appointment: " + appointmentTime);
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Appointment Details", appointmentIcon, JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        detailsPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        detailsPanel.add(Box.createVerticalStrut(10), gbc);

        JLabel dateIconLabel = new JLabel(calendarIcon);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        detailsPanel.add(dateIconLabel, gbc);

        JLabel dateField = new JLabel("Date:");
        dateField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dateField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        detailsPanel.add(dateField, gbc);

        JLabel dateValue = new JLabel(formattedDate);
        dateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateValue.setForeground(Color.WHITE);
        gbc.gridx = 2;
        detailsPanel.add(dateValue, gbc);

        JLabel timeIconLabel = new JLabel(clockIcon);
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(timeIconLabel, gbc);

        JLabel timeField = new JLabel("Time:");
        timeField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        detailsPanel.add(timeField, gbc);

        JLabel timeValue = new JLabel(formattedTime);
        timeValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeValue.setForeground(Color.WHITE);
        gbc.gridx = 2;
        detailsPanel.add(timeValue, gbc);

        JLabel patientIconLabel = new JLabel(userIcon);
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(patientIconLabel, gbc);

        JLabel patientField = new JLabel("Patient:");
        patientField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        detailsPanel.add(patientField, gbc);

        JLabel patientValue = new JLabel(patientName);
        patientValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientValue.setForeground(Color.WHITE);
        gbc.gridx = 2;
        detailsPanel.add(patientValue, gbc);

        JLabel doctorIconLabel = new JLabel(doctorIcon);
        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsPanel.add(doctorIconLabel, gbc);

        JLabel doctorField = new JLabel("Doctor ID:");
        doctorField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        doctorField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        detailsPanel.add(doctorField, gbc);

        String doctorId = (String) appointment.get("doctorId");
        JLabel doctorValue = new JLabel(doctorId != null ? doctorId : "Unknown Doctor");
        doctorValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doctorValue.setForeground(Color.WHITE);
        gbc.gridx = 2;
        detailsPanel.add(doctorValue, gbc);

        JLabel nurseIconLabel = new JLabel(nurseIcon);
        gbc.gridx = 0;
        gbc.gridy = 6;
        detailsPanel.add(nurseIconLabel, gbc);

        JLabel nurseField = new JLabel("Nurse ID:");
        nurseField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nurseField.setForeground(Color.WHITE);
        gbc.gridx = 1;
        detailsPanel.add(nurseField, gbc);

        JLabel nurseValue = new JLabel(nurseId);
        nurseValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nurseValue.setForeground(Color.WHITE);
        gbc.gridx = 2;
        detailsPanel.add(nurseValue, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(255, 255, 255));
        closeButton.setForeground(new Color(0, 206, 209));
        closeButton.setBorder(BorderFactory.createLineBorder(new Color(0, 206, 209), 2));
        closeButton.addActionListener(e -> appointmentDialog.dispose());
        buttonPanel.add(closeButton);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        detailsPanel.add(buttonPanel, gbc);

        appointmentDialog.add(detailsPanel, BorderLayout.CENTER);
        appointmentDialog.setVisible(true);
    }

    private Icon loadIcon(String path, String fallbackText) {
        try {
            URL imageURL = getClass().getResource(path);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Form: Warning: " + path + " not found");
                return new ImageIcon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        g.drawString(fallbackText, x, y + 12);
                    }
                    @Override
                    public int getIconWidth() { return 20; }
                    @Override
                    public int getIconHeight() { return 20; }
                };
            }
        } catch (Exception e) {
            System.err.println("Form: Error loading " + path + ": " + e.getMessage());
            return new ImageIcon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g.drawString(fallbackText, x, y + 12);
                }
                @Override
                public int getIconWidth() { return 20; }
                @Override
                public int getIconHeight() { return 20; }
            };
        }
    }

   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        appointmentsGridPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(153, 153, 153));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Your Appointments");
        add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        appointmentsGridPanel.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
        jScrollPane2.setViewportView(appointmentsGridPanel);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appointmentsGridPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
