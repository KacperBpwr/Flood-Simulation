package env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GuiEnv {
    private static IEnvOld env;

    public GuiEnv(IEnvOld env) {
        GuiEnv.env = env;
    }

    public static void createWindow() {
        JFrame frame = new JFrame("Environment");
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
        logPanel.setLayout(new GridLayout(3,1));

        // pola tekstowe
        JTextField port = new JTextField(10);

        JButton switchButton = new JButton("Accept");

        // Obsługa kliknięcia guzika
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pobieranie wartości z pól tekstowych
                String portValue = port.getText();

                if (portValue.isEmpty()) {
                    JOptionPane.showMessageDialog(logPanel, "name must be provided!");
                    return;
                }

                try {
                    env.setName(portValue);
                    env.signInToTailorRegister();
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }

                JPanel editPanel = null;
                try {
                    editPanel = editPanelContainer();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                // Dodajemy panel do mainPanel i przełączamy widok
                mainPanel.add(editPanel, "editPanel");
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "editPanel");
            }
        });

        logPanel.add(new JLabel("Env name:"));
        logPanel.add(port);

        logPanel.add(switchButton);

        return logPanel;
    }

    public static JPanel editPanelContainer() throws RemoteException {
        JPanel editPanel = new JPanel(new GridLayout(6,1));

        JLabel portLabel = new JLabel("Port: "+(env.getName()));

        JLabel oldLabel = new JLabel();
        JLabel descriptionLabel = new JLabel("New rainfall:");
        JTextField latest = new JTextField(5);

        JButton updateButton = new JButton("Update Rainfall");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Pobierz nową wartość opadów z pola tekstowego
                    String bufLatest = latest.getText();
                    float newRainfall = Float.parseFloat(bufLatest);

                    env.receiveRainfall(newRainfall);
                    env.sendRainfall();
                    // Zaktualizuj wyświetlanie aktualnych opadów
                    oldLabel.setText("Current Rainfall: \n" + newRainfall);
                } catch (NumberFormatException | RemoteException ex) {
                    JOptionPane.showMessageDialog(editPanel, "Please enter a valid number!");
                }
            }
        });
        editPanel.add(portLabel);
        editPanel.add(oldLabel);
        editPanel.add(descriptionLabel);
        editPanel.add(latest);
        editPanel.add(updateButton);

        return editPanel;
    }

    public static void main(String[] args) throws RemoteException {
        IEnvOld env = new Environment();
        GuiEnv gui = new GuiEnv(env);
        createWindow();

    }
}
