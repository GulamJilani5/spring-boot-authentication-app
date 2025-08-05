package AuthCentral.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static int calculateAge(String dob) {
        LocalDate birthDate = LocalDate.parse(dob, FORMATTER);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // We can add more generic date utilities here

}

