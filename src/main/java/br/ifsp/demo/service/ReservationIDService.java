package br.ifsp.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Pattern;

public class ReservationIDService {

    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^H-\\d{20}$");
    private static final String prefix = "H-";

    public static String generate(LocalDateTime timeStamp){
        String formatedTimeStamp = timeStamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        var sbRandomPart = new StringBuilder();
        for (int i = 0; i < 6; i++){
            sbRandomPart.append(new Random().nextInt(10));
        }

        return "H-" + formatedTimeStamp + sbRandomPart.toString();
    }

    private static boolean isValid(String id) {
        return VALID_ID_PATTERN.matcher(id).matches();
    }

    public static boolean validate(String id) {
        if (!isValid(id)) {
            throw new IllegalArgumentException("Invalid Reservation ID format");
        }
        return true;
    }
}

