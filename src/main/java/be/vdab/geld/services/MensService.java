package be.vdab.geld.services;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.dto.SchenkStatistiekPerMens;
import be.vdab.geld.repositories.MensRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MensService {
    private final MensRepository mensRepository;
    public MensService(MensRepository mensRepository) {
        this.mensRepository = mensRepository;
    }
    public List<Mens> findAll() {
        return mensRepository.findAll();
    }
    @Transactional
    public long create(Mens mens) {
        return mensRepository.create(mens);
    }
    public List<SchenkStatistiekPerMens> findSchenkStatistiekPerMens() {
        return mensRepository.findSchenkStatistiekPerMens();
    }
}
