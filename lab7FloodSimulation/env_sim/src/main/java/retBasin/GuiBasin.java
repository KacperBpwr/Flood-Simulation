package retBasin;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GuiBasin {
    private static IRetentionBasinOld basin;

    public GuiBasin(IRetentionBasinOld basin) {
        GuiBasin.basin = basin;
    }

    public static void createWindow() {
        JFrame frame = new JFrame("Basin");
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

        //  panele
        JPanel logPanel = logPanelContainer(mainPanel);

        mainPanel.add(logPanel, "logPanel");

        return mainPanel;
    }


    public static JPanel logPanelContainer(JPanel mainPanel) {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new GridLayout(5, 1));

        //  pola tekstowe
        JTextField basinPortField = createPlaceHolderTextField("Enter Basin name:");
        JTextField volumeField = createPlaceHolderTextField("Enter Basin Volume:");
        JTextField controlPortField = createPlaceHolderTextField("Enter Control name:");
        JTextField riverInputField = createPlaceHolderTextField("River name that flows to the basin :");


        // Przycisk do przełączania widoku
        JButton switchButton = new JButton("Accept");

        // Obsługa kliknięcia guzika
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pobieranie wartości z pól tekstowych
                String basinPort = basinPortField.getText();
                String basinVolume = volumeField.getText();
                String controlPort = controlPortField.getText();
                String riverInput = riverInputField.getText();


                if (basinPort.isEmpty() || basinVolume.isEmpty() || controlPort.isEmpty() || riverInput.isEmpty()) {
                    JOptionPane.showMessageDialog(logPanel, "Make sure that every text field is not empty");
                    return;
                }


                try {
                    basin.setBasinName(basinPort);
                    basin.setVolume(Integer.parseInt((basinVolume)));
                    basin.setCCname(controlPort);
                    basin.setRiverInName(riverInput);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    basin.signInToTailorRegister();
                    basin.assingBasinToCC();
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }

                JPanel editPanel = null;
                try {
                    editPanel = editPanelContainer();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                // przełączam widok
                mainPanel.add(editPanel, "editPanel");
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "editPanel");
            }
        });


        logPanel.add(basinPortField);


        logPanel.add(controlPortField);


        logPanel.add(volumeField);

        logPanel.add(riverInputField);
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

    public static JPanel editPanelContainer() throws RemoteException {
        JPanel editPanel = new JPanel(new GridLayout(7, 1));



        JLabel filingLabel = new JLabel("Filing: " + (basin.getFillingPercentageOLD() * 100) + "%");
        JLabel waterInsideLabel = new JLabel("Water Inside:" + basin.getWaterInside());
        JLabel waterOutflowLabel = new JLabel("Water Outflow: " + basin.getWaterOutflow());
        JLabel waterInflowLabel = new JLabel("Water Inflow: " + basin.getWaterInflow());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        basin.assingBasinToCC();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            });


        editPanel.add(filingLabel);
        editPanel.add(waterInsideLabel);
        editPanel.add(waterOutflowLabel);
        editPanel.add(waterInflowLabel);
        editPanel.add(refreshButton);

        // Odświeżanie rainfalllabel
        new Thread(() -> {
            while (true) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        filingLabel.setText("Filing: " + (basin.getFillingPercentageOLD() * 100) + "%");


                        waterInsideLabel.setText("WaterInside: " + basin.getWaterInside());


                        waterOutflowLabel.setText("WaterOutflow: " + basin.getWaterOutflow());


                        waterInflowLabel.setText("WaterInflow: " + basin.getWaterInflow());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return editPanel;
    }


    public static void main(String[] args) throws RemoteException, InterruptedException {
        IRetentionBasinOld basin = new RetentionBasin();
        GuiBasin gui = new GuiBasin(basin);
        createWindow();

    }
}
