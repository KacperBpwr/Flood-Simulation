package tailor;

import interfaces.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;

public class Tailor implements ITailor, ITailorAssIgner {
    private Map<String, Remote> ccMap = new HashMap<>();
    private Map<String, Remote> envMap = new HashMap<>();
    private Map<String, Remote> basinMap = new HashMap<>();
    private Map<String, Remote> rsMap = new HashMap<>();

    @Override
    public boolean register(Remote r, String name) throws RemoteException {

        if (r instanceof IControlCenter) {
            if (!ccMap.containsKey(name)) {
                ccMap.put(name, r);
                System.out.println("registration of control center named: " + name);
                return true;
            } else return false;
        } else if (r instanceof IEnvironment) {
            if (!envMap.containsKey(name)) {
                envMap.put(name, r);
                System.out.println("registration of Environment named: " + name);
                return true;
            } else return false;
        } else if (r instanceof IRetensionBasin) {
            if (!basinMap.containsKey(name)) {
                basinMap.put(name, r);
                System.out.println("registration of Retension Basin named: " + name);
                return true;
            } else return false;
        } else if (r instanceof IRiverSection) {
            if (!rsMap.containsKey(name)) {
                rsMap.put(name, r);
                System.out.println("registration of River Section named: " + name);
                return true;
            } else return false;
        }
        return false;
    }

    @Override
    public void assignRetentionBasinToControlCenter(String retentionBasinName, String controlCenterName) throws RemoteException {
        IControlCenter cc = (IControlCenter) ccMap.get(controlCenterName);
        IRetensionBasin rb = (IRetensionBasin) basinMap.get(retentionBasinName);
        if (rb != null) {

            cc.assignRetensionBasin(rb, retentionBasinName);
            System.out.println("Retension Basin:" + retentionBasinName + " assigned to control center: " + controlCenterName);

        } else System.out.println("Retention Basin not found, assignRetensionBasinToControlCenter");
    }


    @Override
    public void assignRiverSectionToEnviroment(String riverSectionName, String enviromentName) throws RemoteException {
        IEnvironment env = (IEnvironment) envMap.get(enviromentName);
        IRiverSection rs = (IRiverSection) rsMap.get(riverSectionName);
        if (rs != null) {
            env.assignRiverSection(rs, riverSectionName);
            System.out.println("River Section:" + riverSectionName + " assigned to environment: " + enviromentName);
        } else System.out.println("River Section not found, assignRiverSectionToEnviroment");
    }

    @Override
    public void assignRiverOutToBasin(String riverSectionName, String basinName) throws RemoteException {
        IRetensionBasin rB = (IRetensionBasin) basinMap.get(basinName);
        IRiverSection rs = (IRiverSection) rsMap.get(riverSectionName);
        if (rs != null) {

            rs.assignRetensionBasin(rB, basinName);
            System.out.println("Retension Basin:" + basinName + " assigned to river: " + riverSectionName);

        } else System.out.println("River Section not found, assignRiverSectionToBasin");
    }

    @Override
    public void assignBasinInToRiver(String basinName, String riverSectionName) throws RemoteException {
        IRetensionBasin rb = (IRetensionBasin) basinMap.get(basinName);
        IRiverSection rs = (IRiverSection) rsMap.get(riverSectionName);
        if (rb != null) {
            rb.assignRiverSection(rs, riverSectionName);
            System.out.println("River Section:" + riverSectionName + " assigned to basin: " + basinName);
        } else System.out.println("Retention Basin not found, assignBasinInToRiver");
    }


    // służy do wyrejestrowywania namiastek
    @Override
    public boolean unregister(Remote r) throws RemoteException {
        return false;
    }


//    GUI SECTION


    private JFrame frame;
    private JTable ccTable;
    private JTable envTable;
    private JTable basinTable;
    private JTable rsTable;


    private void initializeGUI() {
        frame = new JFrame("Tailor Map Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));

        ccTable = createTable();
        envTable = createTable();
        basinTable = createTable();
        rsTable = createTable();

        frame.add(new JScrollPane(ccTable));
        frame.add(new JScrollPane(envTable));
        frame.add(new JScrollPane(basinTable));
        frame.add(new JScrollPane(rsTable));

        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private JTable createTable() {
        JTable table = new JTable(new DefaultTableModel(new Object[]{"Name", "Remote Object"}, 0));
        return table;
    }

    private void startRefreshingData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    updateTable(ccTable, ccMap);
                    updateTable(envTable, envMap);
                    updateTable(basinTable, basinMap);
                    updateTable(rsTable, rsMap);
                });
            }
        }, 0, 1000); // Odświeżanie co 1 sekundę
    }

    private void updateTable(JTable table, Map<String, ?> map) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Czyszczenie tabeli
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    public static void main(String[] args) {
        Tailor tailor = new Tailor();
        tailor.initializeGUI();
        tailor.startRefreshingData();
        try {
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(tailor, 0);
            Registry r = LocateRegistry.createRegistry(2000);
            r.rebind("Tailor", it);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
