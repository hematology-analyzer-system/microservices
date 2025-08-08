package com.example.demo.Client;

import com.example.grpc.patient.PatientServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class ClientRunner {

    private static final String HOST = "patient-service";
    private static final int PORT = 9091;

    private static final ManagedChannel channel;
    @Getter
    private static final PatientServiceGrpc.PatientServiceBlockingStub stub;

    static {
        channel = ManagedChannelBuilder
                .forAddress(HOST, PORT)
                .usePlaintext()
                .build();

        stub = PatientServiceGrpc.newBlockingStub(channel);
    }

    public static void shutdown() {
        channel.shutdown();
        try {
            if(!channel.awaitTermination(5, TimeUnit.SECONDS)){
                System.err.println("Channel did not terminate in time.");
            }
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while shutting down gRPC channel.");
        }
    }
}
