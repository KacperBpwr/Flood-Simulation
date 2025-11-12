package ctrlCenter;

import interfaces.IRetensionBasin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class GuiCC {

    private static IControlCenterOld controlCenter;
    private static TableModel tableModel;

    public GuiCC(IControlCenterOld controlCenter, TableModel tableModel) {
        GuiCC.controlCenter = controlCenter;
        GuiCC.tableModel = tableModel;

    }

    public static void createWindow() {
        JFrame frame = new JFrame("Control Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        JPanel mainPanel = mainPanelContainer();
        frame.add(mainPanel);


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static JPanel mainPanelContainer() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());

        JPanel logPanel = logPanelContainer(mainPanel);

        mainPanel.add(logPanel, "logPanel");
        return mainPanel;
    }


    public static JPanel logPanelContainer(JPanel mainPanel) {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new GridLayout(2, 1));

        JTextField controlPortField = createPlaceHolderTextField("Control Center name:");


        // Przycisk do przełączania widoku
        JButton switchButton = new JButton("Accept");

        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String controlPort = controlPortField.getText();


                if (controlPort.isEmpty()) {
                    JOptionPane.showMessageDialog(logPanel, "Make sure that every text field is not empty");
                    return;
                }

                controlCenter.setControlCenterName(controlPort);
                try {
                    controlCenter.signInToTailorRegister();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (NotBoundException ex) {
                    throw new RuntimeException(ex);
                }
                startRefreshingData();

                JPanel editPanel = editPanelContainer();

                // zmiana widoku
                mainPanel.add(editPanel, "editPanel");
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "editPanel");
            }
        });

        logPanel.add(controlPortField);

        logPanel.add(switchButton);

        return logPanel;
    }

    public static JTextField createPlaceHolderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Usunięcie tekstu placeholder po kliknięciu
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Przywrócenie placeholdera, jeśli pole jest puste
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    public static JPanel editPanelContainer() {
        JPanel editPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JLabel label = new JLabel("Control Center port: " + controlCenter.getControlCenterName());
        topPanel.add(label);

        JTextField basinPort = createPlaceHolderTextField("Basin name:");
        JTextField basinOutflow = createPlaceHolderTextField("Basin Outflow:");
        JButton acceptButton = new JButton("Accept");
        bottomPanel.add(basinPort);
        bottomPanel.add(basinOutflow);
        bottomPanel.add(acceptButton);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pobierz wartości z pól tekstowych
                String basinPortbuf = basinPort.getText();
                String basinOutbuf = basinOutflow.getText();

                try {
                    controlCenter.sendNewOutflowToBasin(basinPortbuf,Integer.parseInt(basinOutbuf));
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                // Wyczyść pola tekstowe
                basinPort.setText("");
                basinOutflow.setText("");
            }
        });

        editPanel.add(topPanel, BorderLayout.NORTH);
        editPanel.add(scrollPane, BorderLayout.CENTER);
        editPanel.add(bottomPanel, BorderLayout.SOUTH);

        return editPanel;
    }

    private static void startRefreshingData() {
        Timer timer = new Timer(1000, e -> {
            try {
                System.out.println("wykonuje Water inflow");
                controlCenter.getWaterInflow();
                System.out.println("wykonuje Water outflow");
                controlCenter.getWaterOutflow();
                System.out.println("wykonuje filling percentage");
                controlCenter.getFillingPercentage();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        timer.start();
    }


    public static void main(String[] args) throws RemoteException, NotBoundException {
        Map<String, IRetensionBasin> retensionBasins = new HashMap<String, IRetensionBasin>();

        TableModel tb = new TableModel(retensionBasins);
        IControlCenterOld controlCenter = new ControlCenter(retensionBasins);

        controlCenter.setTblModel(tb);
        GuiCC gui = new GuiCC(controlCenter, tb);
        createWindow();

    }

}
