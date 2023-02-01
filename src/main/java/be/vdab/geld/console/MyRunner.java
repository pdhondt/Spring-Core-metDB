package be.vdab.geld.console;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.services.MensService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    private final MensService mensService;
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
    }
}
