package retBasin;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import tailor.ITailorAssIgner;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class RetentionBasin extends UnicastRemoteObject implements IRetentionBasinOld, IRetensionBasin {
    private String basinName;
    private String controlCenterName;
    private String riverINName;
    private IRiverSection riversOutput;
    private String riversOutputName;

    private int volume;
    private volatile int waterOutFlow;
    private int waterInFlow;
    private int waterInside;



    public RetentionBasin() throws RemoteException {
        super();
        new Thread(() ->{
            try {
                dischargeWater();
                System.out.println("odesłano wode");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    @Override
    public boolean signInToTailorRegister() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor tailor = (ITailor) registry.lookup("Tailor");
        tailor.register((IRetensionBasin) this, basinName);
        return true;
    }

    @Override
    public boolean assingBasinToCC() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailorAssIgner tailorAssIgner = (ITailorAssIgner) registry.lookup("Tailor");
        tailorAssIgner.assignRetentionBasinToControlCenter(basinName, controlCenterName);
        if (riverINName != null) {
            tailorAssIgner.assignRiverOutToBasin(riverINName, basinName);
        }
        return true;
    }


    @Override
    // ustawia namiastę wychodzącego odcinka rzecznego;
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
        this.riversOutput = irs;
        this.riversOutputName = name;
        System.out.println("Assigning river section " + name);
    }


    public void dischargeWater() throws RemoteException {
        while (true){
            if (riversOutput != null) {
                if (waterInside <10){
                    waterOutFlow = 0;
                }
                if (waterInside >= volume){
                    waterOutFlow = 10;
                }
                riversOutput.setRealDischarge(waterOutFlow);
                waterInside -= waterOutFlow;
                System.out.println("wysyła się woda "+waterOutFlow);
                System.out.println("water isnside"+ waterInside);

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public boolean setBasinName(String name) {
        basinName = name;
        return true;
    }

    @Override
    public boolean setCCname(String ccname) {
        controlCenterName = ccname;
        return true;
    }

    @Override
    public boolean setVolume(int volume) {
        this.volume = volume;
        return true;
    }

    @Override
    public float getWaterInside() {
        return waterInside;
    }

    @Override
    public boolean setRiverInName(String name) throws RemoteException {
        riverINName = name;
        return true;
    }

    @Override
    public float getFillingPercentageOLD() {
        return (float) waterInside / volume;
    }

    @Override
    public float getWaterOutflow() {
        return waterOutFlow;
    }

    @Override
    public float getWaterInflow() {
        return waterInFlow;
    }

    @Override
    // zwraca informację o zrzucie wody,
    public int getWaterDischarge() throws RemoteException {
        return waterOutFlow;
    }

    @Override
    // zwraca informację o wypełnieniu zbiornika w procentach,
    public long getFillingPercentage() throws RemoteException {
        return (long) (waterInside / volume);
    }

    @Override
    // ustawiania wielkości zrzutu wody,
    public void setWaterDischarge(int waterDischarge) throws RemoteException {
        waterOutFlow = waterDischarge;
    }

    @Override
    // ustawia wielkość napływu wody z odcinka rzecznego o nazwie name
    public void setWaterInflow(int waterInflow, String name) throws RemoteException {
        this.waterInFlow = waterInflow;
        waterInside += waterInflow;
        System.out.println("water inflow received" + waterInflow + " " + name);
    }
}
