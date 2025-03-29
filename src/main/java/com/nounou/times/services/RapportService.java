package com.nounou.times.services;

import com.nounou.times.model.RapportMensuel;
import com.nounou.times.model.Nounou;
import com.nounou.times.model.Garde;
import com.nounou.times.model.Absence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

@ApplicationScoped
public class RapportService {

    @Transactional
    public List<RapportMensuel> getRapportsMensuels(Long nounouId, YearMonth periode) {
        Nounou nounou = Nounou.findById(nounouId);
        if (nounou == null) {
            throw new IllegalArgumentException("Nounou non trouvée");
        }

        // Récupérer les rapports existants
        List<RapportMensuel> rapportsExistants = RapportMensuel.list(
            "nounou.id = ?1 AND periode = ?2",
            nounouId, periode);
        
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
        List<Garde> gardes = Garde.list(
            "nounou.id = ?1 AND dateDebut >= ?2 AND dateFin <= ?3",
            nounou.getId(), debut.atStartOfDay(), fin.atTime(23, 59, 59));
            
        List<Absence> absences = Absence.list(
            "nounou.id = ?1 AND dateDebut >= ?2 AND dateFin <= ?3",
            nounou.getId(), debut.atStartOfDay(), fin.atTime(23, 59, 59));

        // Créer le rapport
        RapportMensuel rapport = new RapportMensuel();
        rapport.setNounou(nounou);
        rapport.setPeriode(periode);
        rapport.setDateGeneration(LocalDate.now());
        rapport.setNombreGardes((long) gardes.size());
        rapport.setNombreAbsences((long) absences.size());
        
        // Calculer les heures totales et autres statistiques
        calculerStatistiques(rapport, gardes, absences);

        rapport.persist();
        
        List<RapportMensuel> rapports = new ArrayList<>();
        rapports.add(rapport);
        return rapports;
    }

    public StreamingOutput downloadRapport(Long nounouId, Long rapportId) {
        RapportMensuel rapport = RapportMensuel.findById(rapportId);
        if (rapport == null || !rapport.getNounou().getId().equals(nounouId)) {
            throw new IllegalArgumentException("Rapport non trouvé ou non autorisé");
        }

        return output -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Rapport Mensuel");

                // Style pour les en-têtes
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Créer l'en-tête
                Row headerRow = sheet.createRow(0);
                createHeaderCell(headerRow, 0, "Période", headerStyle);
                createHeaderCell(headerRow, 1, "Nombre de gardes", headerStyle);
                createHeaderCell(headerRow, 2, "Nombre d'absences", headerStyle);
                createHeaderCell(headerRow, 3, "Heures totales", headerStyle);
                createHeaderCell(headerRow, 4, "Montant total", headerStyle);

                // Remplir les données
                Row dataRow = sheet.createRow(1);
                dataRow.createCell(0).setCellValue(rapport.getPeriode().toString());
                dataRow.createCell(1).setCellValue(rapport.getNombreGardes());
                dataRow.createCell(2).setCellValue(rapport.getNombreAbsences());
                dataRow.createCell(3).setCellValue(String.format("%.2f", rapport.getHeuresTotales()));
                dataRow.createCell(4).setCellValue(String.format("%.2f €", rapport.getMontantTotal()));

                // Ajuster la largeur des colonnes
                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Écrire le workbook dans le stream
                workbook.write(output);
            }
        };
    }

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void calculerStatistiques(RapportMensuel rapport, List<Garde> gardes, List<Absence> absences) {
        double heuresTotales = 0;
        double montantTotal = 0;
        
        for (Garde garde : gardes) {
            // Calculer les heures de garde
            double heuresGarde = calculerHeuresGarde(garde);
            heuresTotales += heuresGarde;
            
            // Calculer le montant
            double montantGarde = calculerMontantGarde(garde, heuresGarde);
            montantTotal += montantGarde;
        }

        rapport.setHeuresTotales(heuresTotales);
        rapport.setMontantTotal(montantTotal);
    }

    private double calculerHeuresGarde(Garde garde) {
        if (garde.getHeureDebut() == null || garde.getHeureFin() == null) {
            return 0;
        }

        // Calculer la durée en minutes
        long minutes = ChronoUnit.MINUTES.between(garde.getDate(), garde.getHeureFin());
        
        // Convertir en heures avec 2 décimales
        return Math.round((minutes / 60.0) * 100.0) / 100.0;
    }

    private double calculerMontantGarde(Garde garde, double heures) {
        // Tarif horaire de base (à personnaliser selon vos besoins)
        double tarifHoraire = garde.getNounou().getTarifHoraire();
        
        // Majoration pour les repas si inclus
        double majorationRepas = garde.isRepasInclus() ? garde.getNounou().getMajorationRepas() : 0;
        
        // Calculer le montant total
        return (tarifHoraire * heures) + majorationRepas;
    }
}
