module env.sim {
    requires javafx.base;
    requires java.desktop;
    //requires transitive lab7FloodSimulation;
    requires floodlib;
    exports tailor;
    exports riverSection;
    exports retBasin;
    exports env;
    exports ctrlCenter;
}