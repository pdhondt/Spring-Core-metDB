package be.vdab.geld.services;

import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.repositories.MensRepository;
import be.vdab.geld.repositories.SchenkingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SchenkingService {
    private final SchenkingRepository schenkingRepository;
    private final MensRepository mensRepository;

    public SchenkingService(SchenkingRepository schenkingRepository, MensRepository mensRepository) {
        this.schenkingRepository = schenkingRepository;
        this.mensRepository = mensRepository;
    }
    @Transactional
    public void create(Schenking schenking) {
        var vanMens = mensRepository.findAndLockById(schenking.getVanMensId())
                .orElseThrow(() -> new MensNietGevondenException(schenking.getVanMensId()));
        var aanMens = mensRepository.findAndLockById(schenking.getAanMensId())
                .orElseThrow(() -> new MensNietGevondenException(schenking.getAanMensId()));
        vanMens.schenk(aanMens, schenking.getBedrag());
        mensRepository.update(vanMens);
        mensRepository.update(aanMens);
        schenkingRepository.create(schenking);
    }
}
