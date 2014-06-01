package cl.blackbird.server.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class PaperFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(formatDate(record.getMillis()))
                .append("] ")
                .append(record.getLevel().getLocalizedName())
                .append(": ")
                .append(formatMessage(record))
                .append(System.lineSeparator());

        if(record.getThrown() != null){
            try {
                StringWriter writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                record.getThrown().printStackTrace(printWriter);
                printWriter.close();
                builder.append(writer.toString());
                builder.append(System.lineSeparator());
            } catch (Exception e){
                //hacer nada
            }
        }

        return builder.toString();
    }

    private String formatDate(long millis){
        Date current = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(current);
    }
}