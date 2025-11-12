package riverSection;


import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import tailor.ITailorAssIgner;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class River extends UnicastRemoteObject implements IRiverOld, IRiverSection {
    //    river
    private String riverName;
    private volatile int delay;
    private volatile int rainfall = 0;
    private volatile int waterAmount = 0;
    private volatile int waterFromBasinDischarge = 0;
    //    basin
    private String inBasinName;
    private IRetensionBasin basin;
    private String outBasinName;
    //    environment
    private String envName;
    //    controlCenter
    private String controlName;

    public River() throws RemoteException {
        super();
    }


    public void registerInTailor() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor it = (ITailor) registry.lookup("Tailor");
        it.register((IRiverSection) this, riverName);
    }


    @Override
    public boolean assingRiverToSmth() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailorAssIgner tailorAssIgner = (ITailorAssIgner) registry.lookup("Tailor");
        if (envName != null) {
            tailorAssIgner.assignRiverSectionToEnviroment(riverName, envName);
        }
        if (inBasinName != null) {
            tailorAssIgner.assignBasinInToRiver(inBasinName, riverName);
        }
        return true;
    }


    @Override
    public String getRainfall() {
        return Float.toString(this.rainfall);
    }


    @Override
    public void setDelay(String delay) {
        this.delay = Integer.parseInt(delay);
    }

    @Override
    public float getDelay() {
        return this.delay;
    }

    @Override
    public void setRiverName(String riverName) throws RemoteException {
        this.riverName = riverName;
    }

    @Override
    public String getRiverName() throws RemoteException {
        return this.riverName;
    }

    @Override
    public void setInBasinName(String inBasinName) throws RemoteException {
        this.inBasinName = inBasinName;
    }

    @Override
    public String getInBasinName() throws RemoteException {
        return this.inBasinName;
    }

    @Override
    public void setEnvName(String envName) throws RemoteException {
        this.envName = envName;
    }

    @Override
    public String getEnvName() throws RemoteException {
        return this.envName;
    }


    @Override
    public float realWaterAmount() {
        float realWaterAmount = 0;
        realWaterAmount = waterFromBasinDischarge + rainfall;
        return realWaterAmount;
    }


    @Override
    public void sendWaterToBasin() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                waterAmount = waterFromBasinDischarge + rainfall;

                System.out.println("water send to basin: "+waterAmount);
                if (basin != null) {
                    try {
                        basin.setWaterInflow(waterAmount, riverName);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }

//    bierze wode ze zbiornika
    @Override
    public void setRealDischarge(int realDischarge) throws RemoteException {
        waterFromBasinDischarge = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) throws RemoteException {
        this.rainfall = rainfall;
    }


//    PRZYPISANIE WYJŚCIA RZEKI DO ZBIORNIKA
    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException {
        this.basin = irb;
        this.outBasinName = name;
        System.out.println("Basin " + name + " assigned to River " + riverName);
    }
}