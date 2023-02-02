package be.vdab.geld.console;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import be.vdab.geld.services.MensService;
import be.vdab.geld.services.SchenkingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    /*private final MensService mensService;
    public MyRunner(MensService mensService) {
        this.mensService = mensService;
    }
    @Override
    public void run(String... args) {
        var scanner = new Scanner(System.in);
        System.out.print("Naam: ");
        var naam = scanner.nextLine();
        System.out.print("Geld: ");
        var geld = scanner.nextBigDecimal();
        var nieuweMens = new Mens(0, naam, geld);
        var nieuweId = mensService.create(nieuweMens);
        System.out.println("Id van deze nieuw toegevoegde mens: " + nieuweId);
        System.out.println("Overzicht van alle mensen in de database:");
        mensService.findAll().forEach(mens ->
                System.out.println(mens.getNaam() + ":" + mens.getGeld()));
    }*/
    private final SchenkingService schenkingService;
    public MyRunner(SchenkingService schenkingService) {
        this.schenkingService = schenkingService;
    }
    @Override
    public void run(String... args) {
        var scanner = new Scanner(System.in);
        System.out.print("Id van mens: ");
        var vanMensId = scanner.nextInt();
        System.out.print("Id aan mens: ");
        var aanMensId = scanner.nextInt();
        System.out.print("Bedrag: ");
        var bedrag = scanner.nextBigDecimal();
        try {
            var schenking = new Schenking(vanMensId, aanMensId, bedrag);
            schenkingService.create(schenking);
            System.out.println("Schenking gelukt");
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        } catch (MensNietGevondenException ex) {
            System.err.println("Schenking mislukt.  Mens ontbreekt. Id: " + ex.getId());
        } catch (OnvoldoendeGeldException ex) {
            System.err.println("Schenking mislukt.  Onvoldoende geld.");
        }
    }
}
