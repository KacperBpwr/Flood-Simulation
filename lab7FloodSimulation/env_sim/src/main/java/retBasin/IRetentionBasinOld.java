package retBasin;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRetentionBasinOld extends Remote {

    boolean signInToTailorRegister() throws RemoteException, NotBoundException;
    boolean assingBasinToCC() throws RemoteException, NotBoundException;

    boolean setBasinName(String name)throws RemoteException;
    boolean setCCname(String ccname)throws RemoteException;
    boolean setVolume(int volume)throws RemoteException;

    float getFillingPercentageOLD()throws RemoteException;
    float getWaterInflow()throws RemoteException;

    float getWaterOutflow()throws RemoteException;

    float getWaterInside()throws RemoteException;
    boolean setRiverInName(String name)throws RemoteException;



}
