package se.liu.ida.tdp024.account.rest;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(scanBasePackages = {
        "se.liu.ida.tdp024.account.util",   // Hjälpklasser HTTPHelper, KafkaLogger)
        "se.liu.ida.tdp024.account.data",   // Datalager hanterar databasen
        "se.liu.ida.tdp024.account.logic",  // Affärslogik behandlar regler och data
        "se.liu.ida.tdp024.account.rest"    // REST-API tar emot HTTP-anrops
})





////
public class Application {

    // main() är startpunkten – programmet börjar här.
    public static void main(String[] args) {

        // Skapar en SpringApplication-instans som startar hela Spring Boot-miljön
        SpringApplication app = new SpringApplication(Application.class);

        // Stänger av den stora "Spring" text grejen sen 
        app.setBannerMode(Banner.Mode.OFF);

        // Kör själva applikationen – laddar alla komponenter (controllers, services, etc.)
        app.run(args);

        // Skriver ut så du jag vet att servern körs
        System.out.println("\n===============================================");
        System.out.println("   Account REST Service is up and running!   ");
        System.out.println("   Visit: http://localhost:8080/account-rest/");
        System.out.println("===============================================\n");
    }
}
