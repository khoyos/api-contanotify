package co.java.app.contanotify.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FechaLegible {

    public static String convertirFechaISOPorFechaLegible(String fechaIso){

        // Parsear la fecha a LocalDateTime
        LocalDateTime fecha = LocalDateTime.parse(fechaIso);

        // Formateador legible en español
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "ES")
        );

        // Formatear y mostrar
       return fecha.format(formatter);
    }
}
