/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Nurse;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import org.json.simple.JSONArray;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author USER
 */
public class nurseDashboard extends javax.swing.JFrame {
    private String nurseId;
   private Form form;
   

    /**
     * Creates new form Main
     */
    public nurseDashboard(String username) {
          initComponents();
           nurseId = getNurseId(username);
      if (nurseId == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error: Nurse ID not found for username " + username);
            dispose();
            return;
        }
        System.out.println("nurseDashboard: Initialized with nurseId " + nurseId);
       form = new Form();
        form.setNurseId(nurseId);
  
     
       
        getContentPane().setBackground(new Color(250, 250, 250));
navigationBar1.addItem(new ImageIcon(getClass().getResource("/icons/basta1.png")));
navigationBar1.addItem(new ImageIcon(getClass().getResource("/icons/basta2.png")));
navigationBar1.addItem(new ImageIcon(getClass().getResource("/icons/homepage.png")));
navigationBar1.addItem(new ImageIcon(getClass().getResource("/icons/basta4.png")));
navigationBar1.addItem(new ImageIcon(getClass().getResource("/icons/basta3.png")));
 navigationBar1.addEvent(new EventNavigationBar() {
    @Override
    public void beforeSelected(int index) {
                System.out.println("nurseDashboard: Navigating to index " + index);
                if (index == 0) {
                    Form form = new Form();
                    form.setNurseId(nurseId);
                    panelTransaction1.display(form, navigationBar1.getAnimator());
                } else if (index == 1) {
                    panelTransaction1.display(new Form2(nurseId), navigationBar1.getAnimator());
                } else if (index == 2) {
                    Form3 form3 = new Form3();
                    form3.setNurseId(nurseId);
                    panelTransaction1.display(form3, navigationBar1.getAnimator());
                } else if (index == 3) {
                    Form4 form4 = new Form4();
                    form4.setNurseId(nurseId); // Ensure nurseId is set for Form4
                    panelTransaction1.display(form4, navigationBar1.getAnimator());
                } else if (index == 4) {
                    panelTransaction1.display(new Form5(), navigationBar1.getAnimator());
                }
                panelTransaction1.revalidate();
                panelTransaction1.repaint();

                // Resize the window to fit the new content
                pack();
                setLocationRelativeTo(null); // Re-center the window
            
            }

    @Override
    public void afterSelected(int index) {
    }
});
        NavigationBackgroundColor nb = new NavigationBackgroundColor();
      
        nb.apply(getContentPane());
nb.addColor(0, new Color(58, 180, 234)); 
nb.addColor(0, new Color(50, 160, 210)); 
nb.addColor(0, new Color(42, 140, 190)); 
nb.addColor(0, new Color(34, 120, 170)); 
nb.addColor(0, new Color(26, 100, 150)); 
        navigationBar1.setnavigationBackgroundColor(nb);
    }

    
    private String getNurseId(String username) {
          String filePath = "C:\\Users\\najx\\Downloads\\RxPert\\RxPert\\src\\maeclinicapp\\nurse.json";
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray users = (JSONArray) parser.parse(reader);
            for (Object obj : users) {
                JSONObject user = (JSONObject) obj;
                if (username.equals(user.get("username"))) {
                    return (String) user.get("id");
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("nurseDashboard: Error getting nurse ID: " + e.getMessage());
        }
        return null;
    }
    public void navigateToForm(String formName) {
        int index = -1;
        switch (formName) {
            case "Form":
                index = 0;
                break;
            case "Form2":
                index = 1;
                break;
            case "Form3":
                index = 2;
                break;
            case "Form4":
                index = 3;
                break;
            case "Form5":
                index = 4;
                break;
            default:
                System.err.println("nurseDashboard: Unknown form name: " + formName);
                return;
        }
        if (index != -1) {
            System.out.println("nurseDashboard: Navigating to form: " + formName + " at index " + index);
            navigationBar1.initSelectedIndex(index);
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

        panelTransaction1 = new Nurse.PanelTransitions();
        navigationBar1 = new Nurse.NavigationBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout navigationBar1Layout = new javax.swing.GroupLayout(navigationBar1);
        navigationBar1.setLayout(navigationBar1Layout);
        navigationBar1Layout.setHorizontalGroup(
            navigationBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        navigationBar1Layout.setVerticalGroup(
            navigationBar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navigationBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTransaction1, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelTransaction1, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navigationBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
navigationBar1.initSelectedIndex(2);
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(nurseDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new nurseDashboard("nurse").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Nurse.NavigationBar navigationBar1;
    private Nurse.PanelTransitions panelTransaction1;
    // End of variables declaration//GEN-END:variables
}
