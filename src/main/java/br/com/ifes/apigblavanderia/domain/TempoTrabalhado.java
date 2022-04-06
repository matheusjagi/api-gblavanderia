package br.com.ifes.apigblavanderia.domain;

import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TempoTrabalhado {

    private List<Minuto> tempo = AlgoritimoUtil.criaDiaTrabalhadoEmMinutos();

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private Integer totalDiasTrabalhados = 1;

    public TempoTrabalhado(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
}
