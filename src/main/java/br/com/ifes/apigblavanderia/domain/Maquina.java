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

    private String nome;

    private Integer producaoMaximaPorHora;

    private List<Processo> processosQueRealiza = new ArrayList<>();

    private TempoTrabalhado tempoTrabalhado;

    public Maquina(Integer id, String nome, Integer producaoMaximaPorHora) {
        this.id = id;
        this.nome = nome;
        this.producaoMaximaPorHora = producaoMaximaPorHora;
    }
}
