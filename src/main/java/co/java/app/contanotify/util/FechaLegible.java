package co.java.app.contanotify.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class FechaLegible {

    private static final String[] MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

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

    public static String formatoPeriodo(LocalDate desde, LocalDate hasta) {
        Period p = Period.between(desde, hasta);
        StringBuilder sb = new StringBuilder("Han pasado ");

        if (p.getYears() > 0) sb.append(p.getYears()).append(" año(s) ");
        if (p.getMonths() > 0) sb.append(p.getMonths()).append(" mes(es) ");
        if (p.getDays() > 0) sb.append(p.getDays()).append(" día(s)");

        if (p.isZero()) return "Es hoy";
        return sb.toString().trim();
    }

    public static int obtenerMes(String fecha) {
        try {
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDate date = LocalDate.parse(fecha, formateador);
            return date.getMonthValue();
        } catch (Exception e) {
            return 0; // Si el formato no es válido o está vacío
        }
    }

    // Retorna el nombre del mes en español
    public static String nombreMes(int numeroMes) {
        return MESES[numeroMes - 1];
    }

    public static String nombreMesAbreviado(int numeroMes) {
        String nombre = MESES[numeroMes - 1];
        return nombre.substring(0, Math.min(3, nombre.length()));
    }

    // (Opcional) Para ordenar correctamente por nombre
    public static int obtenerNumeroMesPorNombre(String nombreMes) {
        for (int i = 0; i < MESES.length; i++) {
            if (MESES[i].equalsIgnoreCase(nombreMes)) {
                return i + 1;
            }
        }
        return 0;
    }

    public static int obtenerAnio(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha vacía o nula");
        }

        // Intentamos parsear con formato ISO (yyyy-MM-dd)
        try {
            return LocalDate.parse(fecha).getYear();
        } catch (DateTimeParseException e) {
            // Intentamos con formato latino (dd/MM/yyyy)
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                return LocalDate.parse(fecha, formatter).getYear();
            } catch (DateTimeParseException e2) {
                // Intentamos con formato alterno (MM/dd/yyyy)
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    return LocalDate.parse(fecha, formatter).getYear();
                } catch (DateTimeParseException e3) {
                    throw new IllegalArgumentException("Formato de fecha no reconocido: " + fecha);
                }
            }
        }
    }

}
