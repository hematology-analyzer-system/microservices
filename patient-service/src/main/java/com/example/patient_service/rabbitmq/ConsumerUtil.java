///* Used to provide utilities for PatientRabbitMQConsumer and PatientRabbitMQProducer */
//
//package com.example.patient_service.rabbitmq;
//
//import lombok.experimental.UtilityClass;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@UtilityClass
//public class ConsumerUtil {
//    // Util to parseLocalDateTime
//    public LocalDateTime parseLocalDateTime(Object rawDateTime) {
//        if (rawDateTime instanceof List<?> dateTimeList) {
//            // if raw input DateTime is an ArrayList
//            // Jackson có thể serialize LocalDateTime thành array [YYYY,MM,DD,HH,MM,SS,NNN]
//            return LocalDateTime.of(
//                    (Integer) dateTimeList.get(0),
//                    (Integer) dateTimeList.get(1),
//                    (Integer) dateTimeList.get(2),
//                    (Integer) dateTimeList.get(3),
//                    (Integer) dateTimeList.get(4),
//                    (Integer) dateTimeList.get(5)
//            );
//        } else {
//            // Otherwise
//            return LocalDateTime.parse(rawDateTime.toString());
//        }
//    }
//
//    // Util to parseLocalDate
//    public LocalDate parseLocalDate(Object rawDate) {
//        // if raw input Date is an ArrayList
//        if (rawDate instanceof List<?> dateTimeList) {
//            return LocalDate.of(
//                    (Integer) dateTimeList.get(0),
//                    (Integer) dateTimeList.get(1),
//                    (Integer) dateTimeList.get(2)
//            );
//        } else {
//            // Otherwise
//            return LocalDate.parse(rawDate.toString());
//        }
//    }
//}
