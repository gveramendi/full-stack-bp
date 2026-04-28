package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.service.ReportService;
import net.veramendi.fullstackbpapi.web.dto.report.FlatStatementRow;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reports", description = "Account-statement reports (JSON + base64 PDF)")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "Account statement for a client over a date range",
            description = "Returns totals per account, the movements within [from, to], and the same "
                        + "report rendered as a base64-encoded PDF in `pdfBase64`."
    )
    @GetMapping
    public ReportResponse accountStatement(
            @Parameter(description = "Client identifier", required = true) @RequestParam @NotBlank @NonNull String clientId,
            @Parameter(description = "Range start (inclusive, ISO yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Range end (inclusive, ISO yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return reportService.generate(clientId, from, to);
    }

    @Operation(
            summary = "Flat account statement (one row per movement)",
            description = "Returns a denormalized list where each row carries the client name, account "
                        + "metadata, the movement value and the running balance after the movement."
    )
    @GetMapping("/flat")
    public List<FlatStatementRow> flatAccountStatement(
            @Parameter(description = "Client identifier", required = true) @RequestParam @NotBlank @NonNull String clientId,
            @Parameter(description = "Range start (inclusive, ISO yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Range end (inclusive, ISO yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return reportService.generateFlat(clientId, from, to);
    }
}
