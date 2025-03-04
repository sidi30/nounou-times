package com.nounou.times.services;

import com.nounou.times.model.RapportMensuel;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Absence;
import com.nounou.times.repository.RapportRepository;
import com.nounou.times.repository.NounouRepository;
import com.nounou.times.repository.GardeRepository;
import com.nounou.times.repository.AbsenceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

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

    public List<RapportMensuel> getRapportsMensuels(Long nounouId, YearMonth periode) {
        Nounou nounou = nounouRepository.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        // Récupérer les rapports existants
        List<RapportMensuel> rapportsExistants = rapportRepository.findByNounouAndPeriode(nounouId, debut, fin);
        
        // Si des rapports existent déjà pour cette période, les retourner
        if (!rapportsExistants.isEmpty()) {
            return rapportsExistants;
        }

        // Sinon, générer un nouveau rapport
        return genererRapportMensuel(nounou, periode);
    }

    @Transactional
    public List<RapportMensuel> genererRapportMensuel(Nounou nounou, YearMonth periode) {
        LocalDate debut = periode.atDay(1);
        LocalDate fin = periode.atEndOfMonth();

        // Récupérer les données nécessaires
        List<Garde> gardes = gardeRepository.findByNounouAndPeriode(nounou.getId(), debut, fin);
        List<Absence> absences = absenceRepository.findByNounouAndPeriode(nounou.getId(), debut, fin);

        // Créer le rapport
        RapportMensuel rapport = new RapportMensuel();
        rapport.setNounou(nounou);
        rapport.setPeriode(periode);
        rapport.setDateGeneration(LocalDate.now());
        rapport.setNombreGardes((long) gardes.size());
        rapport.setNombreAbsences((long) absences.size());
        
        // Calculer les heures totales et autres statistiques
        calculerStatistiques(rapport, gardes, absences);

        rapportRepository.save(rapport);
        
        List<RapportMensuel> rapports = new ArrayList<>();
        rapports.add(rapport);
        return rapports;
    }

    public StreamingOutput downloadRapport(Long nounouId, Long rapportId) {
        RapportMensuel rapport = rapportRepository.findById(rapportId);
        if (rapport == null || !rapport.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Rapport non trouvé ou non autorisé");
        }

        return output -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Rapport Mensuel");

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Période");
                headerRow.createCell(1).setCellValue("Nombre de gardes");
                headerRow.createCell(2).setCellValue("Nombre d'absences");
                headerRow.createCell(3).setCellValue("Heures totales");

                // Remplir les données
                Row dataRow = sheet.createRow(1);
                dataRow.createCell(0).setCellValue(rapport.getPeriode().toString());
                dataRow.createCell(1).setCellValue(rapport.getNombreGardes());
                dataRow.createCell(2).setCellValue(rapport.getNombreAbsences());
                dataRow.createCell(3).setCellValue(rapport.getHeuresTotales());

                // Écrire le workbook dans le stream
                workbook.write(output);
            }
        };
    }

    private void calculerStatistiques(RapportMensuel rapport, List<Garde> gardes, List<Absence> absences) {
        double heuresTotales = 0;
        
        for (Garde garde : gardes) {
            // Calculer les heures de garde
            double heuresGarde = calculerHeuresGarde(garde);
            heuresTotales += heuresGarde;
        }

        rapport.setHeuresTotales(heuresTotales);
    }

    private double calculerHeuresGarde(Garde garde) {
        // Logique de calcul des heures de garde
        // À adapter selon vos besoins spécifiques
        return garde.getHeures() != null ? garde.getHeures() : 0;
    }
}
