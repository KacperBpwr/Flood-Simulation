package riverSection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GuiRiver {
    private static IRiverOld river;

    public GuiRiver(IRiverOld river) {
        GuiRiver.river = river;
    }

    public static void createWindow() {
        JFrame frame = new JFrame("River");
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
        logPanel.setLayout(new GridLayout(10, 1));

        //  pola tekstowe
        JTextField riverPortField = createPlaceHolderTextField("Enter River name");
        JTextField inBasinPortField = createPlaceHolderTextField("Enter Input Basin name (basin that pours water into the river");
        JTextField envPortField = createPlaceHolderTextField("Enter Environment name");
        JTextField delayFieldField = createPlaceHolderTextField("Enter Delay");


        // Przycisk do przełączania widoku
        JButton switchButton = new JButton("Accept");

        // Obsługa kliknięcia guzika
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pobieranie wartości z pól tekstowych
                String riverPort = riverPortField.getText();

                String inBasinPort = inBasinPortField.getText();

                String envPort = envPortField.getText();



                String delay = delayFieldField.getText();

                if ( riverPort.isEmpty() || inBasinPort.isEmpty() || envPort.isEmpty() || delay.isEmpty()) {
                    JOptionPane.showMessageDialog(logPanel, "Make sure that every text field is not empty");
                    return;
                }


                try {
                    river.setRiverName(riverPort);
                    river.setInBasinName(inBasinPort);
                    river.setEnvName(envPort);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    river.registerInTailor();
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }


                try {
                    river.setDelay(delay);
                    river.sendWaterToBasin();
                } catch (RemoteException ex) {
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


        logPanel.add(riverPortField);
        logPanel.add(inBasinPortField);
        logPanel.add(envPortField);

        logPanel.add(delayFieldField);
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
        JPanel editPanel = new JPanel(new GridLayout(9,1));

        JLabel portLabel = new JLabel("River Port: "+river.getRiverName());
        JLabel inBasinLabel = new JLabel("In Basin Port : "+river.getInBasinName());
        JLabel envPortLabel = new JLabel("Environment Port : "+river.getEnvName());

        JLabel delayLabel = new JLabel("Delay : "+river.getDelay());
        JLabel rainfallLabel = new JLabel("Rainfall : "+river.getRainfall());
        JLabel totalwaterLabel = new JLabel("Total water: "+ river.realWaterAmount());
        JButton refreshButton = new JButton("Refresh");

        editPanel.add(portLabel);
        editPanel.add(inBasinLabel);
        editPanel.add(envPortLabel);
        editPanel.add(delayLabel);
        editPanel.add(rainfallLabel);
        editPanel.add(totalwaterLabel);
        editPanel.add(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    river.assingRiverToSmth();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (NotBoundException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        // Odświeżanie raingallLabel
        new Thread(() -> {
            while (true) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        rainfallLabel.setText("Rainfall: " + river.getRainfall());
                        totalwaterLabel.setText("Total water:" + river.realWaterAmount());
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


    public static void main(String[] args) throws RemoteException {
        IRiverOld river = new River();
        GuiRiver gui = new GuiRiver(river);
        createWindow();

    }

}
