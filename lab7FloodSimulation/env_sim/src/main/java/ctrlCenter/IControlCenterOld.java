package ctrlCenter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface IControlCenterOld {


    void setControlCenterName(String controlCenterName);
    String getControlCenterName();
    void setTblModel(TableModel tblModel);

    void signInToTailorRegister() throws RemoteException, NotBoundException;

    void sendNewOutflowToBasin(String namen, int waterDischarge) throws RemoteException;
    void getWaterInflow()throws RemoteException;
    void getWaterOutflow()throws RemoteException;
    void getFillingPercentage() throws RemoteException;



}
