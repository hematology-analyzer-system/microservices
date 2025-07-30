package com.example.demo.Client;

import com.example.grpc.patient.PatientRequest;
import com.example.grpc.patient.PatientResponse;
import com.example.grpc.patient.PatientServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientRunner {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        PatientServiceGrpc.PatientServiceBlockingStub stub = PatientServiceGrpc.newBlockingStub(channel);

        PatientRequest myRequest = PatientRequest.newBuilder()
                .setId(1)
                .build();

        System.out.println(myRequest.getId());

        PatientResponse myResponse = stub.getPatientById(myRequest);

        System.out.println("this is response: " + myResponse);

        channel.shutdown();
    }
}
