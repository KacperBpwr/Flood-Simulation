package env;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface IEnvOld {

    void signInToTailorRegister() throws RemoteException, NotBoundException;


    void receiveRainfall(float rainfall)throws RemoteException;
    void sendRainfall() throws RemoteException;
    String getName() throws RemoteException;
    void setName(String name) throws RemoteException;
}
