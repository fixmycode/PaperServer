package cl.blackbird.server.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PaperLog {
    private final static Logger LOGGER = Logger.getLogger(PaperLog.class.getName());
    private static FileHandler fileHandler = null;
    private static PaperHandler paperHandler = null;

    public static void init(){
        PaperFormatter formatter = new PaperFormatter();
        try {
            fileHandler = new FileHandler("paper_server.log", true);
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.FINEST);
        } catch (IOException e){
            System.err.println("No se pudo crear archivo de registro.");
            e.printStackTrace();
        }

        paperHandler = new PaperHandler();
        paperHandler.setFormatter(formatter);
        paperHandler.setLevel(Level.CONFIG);

        LOGGER.addHandler(paperHandler);
        LOGGER.addHandler(fileHandler);
        LOGGER.setUseParentHandlers(false);
    }
}
