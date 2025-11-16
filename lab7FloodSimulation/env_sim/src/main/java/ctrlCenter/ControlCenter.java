package ctrlCenter;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;
import retBasin.IRetentionBasinOld;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


public class ControlCenter extends UnicastRemoteObject implements IControlCenterOld, interfaces.IControlCenter {
    //    controlCenter
    private String controlCenterName;
    private TableModel tblModel;
    //    list of assigned basins
    private Map<String, IRetensionBasin> retentionBasins;

    public ControlCenter(Map<String, IRetensionBasin> map) throws RemoteException {
        this.retentionBasins = map;
    }


    public ControlCenter() throws RemoteException, NotBoundException {
        super();
    }

    @Override
    public void setTblModel(TableModel tblModel) {
        this.tblModel = tblModel;
    }

    @Override
    public void setControlCenterName(String controlCenterName) {
        this.controlCenterName = controlCenterName;
    }

    @Override
    public String getControlCenterName() {
        return controlCenterName;
    }

    @Override
    public void signInToTailorRegister() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor tailor = (ITailor) registry.lookup("Tailor");
        tailor.register((IControlCenter) this, controlCenterName);
    }


    @Override
    public void getWaterInflow() throws RemoteException {
        for (String key : retentionBasins.keySet()) {
            if (retentionBasins.get(key) instanceof IRetentionBasinOld retentionBasin) {
                System.out.println("do " + retentionBasins.get(key).toString());
                retentionBasin.getWaterInflow();
            }
            else System.out.println("not an instance of IRetentionBasinOld");
        }
    }

    @Override
    public void getWaterOutflow() throws RemoteException {
        if (!retentionBasins.isEmpty()) {
            for (String key : retentionBasins.keySet()) {
                IRetentionBasinOld retensionBasin = (IRetentionBasinOld) retentionBasins.get(key);
                System.out.println("do " + retensionBasin.toString());
                retensionBasin.getWaterOutflow();
            }
        } else System.out.println("retention basin map is empty");
    }

    @Override
    public void getFillingPercentage() throws RemoteException {
        if (!retentionBasins.isEmpty()) {
            for (String key : retentionBasins.keySet()) {
                IRetensionBasin retensionBasin = (IRetensionBasin) retentionBasins.get(key);
                retensionBasin.getFillingPercentage();
                tblModel.refreshTable();
            }
        } else System.out.println("No assigned retention basins");
    }


    @Override
    public void sendNewOutflowToBasin(String name, int waterDischarge) throws RemoteException {
        IRetensionBasin retentionBasin = retentionBasins.get(name);
        retentionBasin.setWaterDischarge(waterDischarge);
    }


    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException {
        if (irb == null) {
            System.out.println("No retention basin assigned to CC, got null");
        } else {
            retentionBasins.put(name, irb);
            tblModel.addRetensionBasin(name, irb);
            System.out.println("Retention basin " + name + " assigned to ControlCenter: " + controlCenterName);
        }
    }
}