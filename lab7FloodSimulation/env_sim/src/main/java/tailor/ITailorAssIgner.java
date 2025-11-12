package tailor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITailorAssIgner extends Remote {

    void assignRetentionBasinToControlCenter(String retentionBasinName, String controlCenterName) throws RemoteException;

    void assignRiverSectionToEnviroment(String riverSectionName, String enviromentName) throws RemoteException;

    void assignRiverOutToBasin(String riverSectionName, String basinName) throws RemoteException;

    void assignBasinInToRiver(String basinName, String riverSectionName) throws RemoteException;


}
