package com.asseco.assecotask.userGenerator.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class PersonalNumberGenerator {

    public String generatePersonalIdentificationNumber() {

        long randomFiveLastDigits = ThreadLocalRandom.current().nextLong(10000, 100000);

        LocalDate birthDate = generateRandomBirthDate();

        String birthDateStr = birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"));

        return birthDateStr + randomFiveLastDigits;
    }

    public static LocalDate generateRandomBirthDate() {

        LocalDate minBirthDate = LocalDate.of(1960, 1, 1);
        LocalDate maxBirthDate = LocalDate.now();

        long minDay = minBirthDate.toEpochDay();
        long maxDay = maxBirthDate.toEpochDay();

        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay + 1);

        return LocalDate.ofEpochDay(randomDay);
    }


}
