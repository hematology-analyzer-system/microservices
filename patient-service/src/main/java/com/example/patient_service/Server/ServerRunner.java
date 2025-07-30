//package com.example.patient_service.Server;
//
//import com.example.patient_service.repository.PatientRepository;
//import com.example.patient_service.service.PatientGrpcService;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//
//
//public class ServerRunner {
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        Server server = ServerBuilder
//                .forPort(9090)
//                .addService(new P_Service())
//                .build();
//
//
//        server.start();
//
//        System.out.println("Server started, listening on " + 9090);
//
//        server.awaitTermination();
//    }
//}
