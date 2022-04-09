package br.com.ifes.apigblavanderia.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Maquina {

    private Integer id;

    private String descricao;

    private Integer producaoMaximaPorHora;

    private List<Processo> processosQueRealiza = new ArrayList<>();

    private TempoTrabalhado tempoTrabalhado;

    public Maquina(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
}
