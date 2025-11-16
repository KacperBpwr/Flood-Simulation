package riverSection;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRiverOld extends Remote {

    void setDelay(String delay)throws RemoteException;
    void setRiverName(String riverName) throws RemoteException;
    String getRiverName() throws RemoteException;
    void setInBasinName(String inBasinName) throws RemoteException;
    String getInBasinName() throws RemoteException;
    void setEnvName(String envName) throws RemoteException;
    String getEnvName() throws RemoteException;

    float getDelay()throws RemoteException;
    String getRainfall()throws RemoteException;
    void sendWaterToBasin()throws RemoteException;

    float realWaterAmount()throws RemoteException;
    boolean assingRiverToSmth() throws RemoteException, NotBoundException;
    void registerInTailor() throws RemoteException, NotBoundException;
}
