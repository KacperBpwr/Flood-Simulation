package env;

import interfaces.IRiverSection;
import interfaces.ITailor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import interfaces.IEnvironment;

public class Environment extends UnicastRemoteObject implements IEnvOld, IEnvironment {
    private Map<String, IRiverSection> rivers = new HashMap<String, IRiverSection>();

    private volatile float rainfall;
    private String envName;

    protected Environment() throws RemoteException {
        super();
    }

    @Override
    public void signInToTailorRegister() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor tailor = (ITailor) registry.lookup("Tailor");
        tailor.register((IEnvironment) this, envName);
    }

    @Override
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
        rivers.put(name, irs);
        System.out.println("River " + name + " assigned to " + envName);
    }

    @Override
    public String getName() throws RemoteException {
        return envName;
    }

    @Override
    public void setName(String name) throws RemoteException {
        envName = name;
    }


    @Override
    public void receiveRainfall(float rainfall) {
        this.rainfall = rainfall;
    }


    @Override
    public void sendRainfall() throws RemoteException {
        if (!rivers.isEmpty()) {
            for (Map.Entry<String, IRiverSection> entry : rivers.entrySet()) {
                rivers.get(entry.getKey()).setRainfall((int) rainfall);
            }
        }
    }


}
