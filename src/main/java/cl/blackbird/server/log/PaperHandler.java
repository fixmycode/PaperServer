package cl.blackbird.server.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class PaperHandler extends Handler {
    public PaperHandler(){
        setFormatter(new PaperFormatter());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if(record.getLevel().intValue() > Level.WARNING.intValue()){
            System.err.print(getFormatter().format(record));
        } else {
            System.out.print(getFormatter().format(record));
        }
    }

    @Override
    public void flush() {
        System.out.flush();
        System.err.flush();
    }

    @Override
    public void close() throws SecurityException {

    }
}