package com.nounou.times.services;

import com.nounou.times.model.Absence;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.RapportMensuel;
import com.nounou.times.repository.AbsenceRepository;
import com.nounou.times.repository.GardeRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.RapportRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RapportService {

    @Inject
    RapportRepository rapportRepository;

    @Inject
    NounouRepository nounouRepository;

    @Inject
    GardeRepository gardeRepository;

    @Inject
    AbsenceRepository absenceRepository;

    public Optional<RapportMensuel> findById(Long id) {
        return rapportRepository.findByIdOptional(id);
    }

    public List<RapportMensuel> findAll() {
        return rapportRepository.listAll();
    }

    @Transactional
    public void save(RapportMensuel rapport) {
        rapportRepository.persist(rapport);
    }

    @Transactional
    public void update(RapportMensuel rapport) {
        rapportRepository.getEntityManager().merge(rapport);
    }

    @Transactional
    public void delete(Long id) {
        rapportRepository.deleteById(id);
    }

    public List<RapportMensuel> getRapportsMensuels(Long nounouId, YearMonth periode) {
        Nounou nounou = nounouRepository.findByIdOptional(nounouId)
                .orElseThrow(() -> new IllegalArgumentException("Nounou non trouvée"));

        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        List<RapportMensuel> existants = rapportRepository.findByNounouAndPeriode(nounouId, debut, fin);
        if (!existants.isEmpty()) return existants;

        return genererRapportMensuel(nounou, periode);
    }

    @Transactional
    public List<RapportMensuel> genererRapportMensuel(Nounou nounou, YearMonth periode) {
        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        List<Garde> gardes = gardeRepository.findByNounouAndPeriode(nounou.id, debut, fin);
        List<Absence> absences = absenceRepository.findByNounouAndPeriode(nounou.id, debut, fin);

        RapportMensuel rapport = new RapportMensuel();
        rapport.setNounou(nounou);
        rapport.setPeriode(periode);
        rapport.setDateGeneration(LocalDate.now());
        rapport.setNombreGardes((long) gardes.size());
        rapport.setNombreAbsences((long) absences.size());
        rapport.setHeuresTotales(gardes.stream()
                .mapToDouble(g -> g.getHeures() != null ? g.getHeures() : 0)
                .sum());

        rapportRepository.persist(rapport);
        return List.of(rapport);
    }

    public StreamingOutput downloadRapport(Long nounouId, Long rapportId) {
        RapportMensuel rapport = rapportRepository.findByIdOptional(rapportId)
                .filter(r -> r.getNounou().id.equals(nounouId))
                .orElseThrow(() -> new IllegalArgumentException("Rapport non trouvé ou non autorisé"));

        return output -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Rapport Mensuel");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Période");
                headerRow.createCell(1).setCellValue("Nombre de gardes");
                headerRow.createCell(2).setCellValue("Nombre d'absences");
                headerRow.createCell(3).setCellValue("Heures totales");

                Row dataRow = sheet.createRow(1);
                dataRow.createCell(0).setCellValue(rapport.getPeriode().toString());
                dataRow.createCell(1).setCellValue(rapport.getNombreGardes() != null ? rapport.getNombreGardes() : 0);
                dataRow.createCell(2).setCellValue(rapport.getNombreAbsences() != null ? rapport.getNombreAbsences() : 0);
                dataRow.createCell(3).setCellValue(rapport.getHeuresTotales() != null ? rapport.getHeuresTotales() : 0);

                workbook.write(output);
            }
        };
    }
}
