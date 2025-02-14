package br.com.ifes.apigblavanderia.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationProperties {
    
    @Value("${algoritmo-genetico.cruzamento.porcentagem}")
    private Integer porcentagemCruzamento;

    @Value("${algoritmo-genetico.mutacao.porcentagem}")
    private Integer porcentagemMutacao;

    @Value("${algoritmo-genetico.populacao.quantidade-pais-selecionados}")
    private Integer quantidadePaisSelecionados;

    @Value("${algoritmo-genetico.populacao.taxa-elitismo}")
    private Double taxaElitismo;

    @Value("${algoritmo-genetico.populacao.diversidade}")
    private Integer operadorDiversidade;

    @Value("${algoritmo-genetico.sequenciamento.data-execucao}")
    private String dataExecucaoSequenciamento;

    @Value("${app.datetime-format}")
    private String dateTimeFormat;

    public LocalDate getDataExecucaoSequenciamento() {
        return LocalDate.parse(dataExecucaoSequenciamento, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public DateTimeFormatter getDateTimeFormat() {
        return DateTimeFormatter.ofPattern(dateTimeFormat);
    }
}
