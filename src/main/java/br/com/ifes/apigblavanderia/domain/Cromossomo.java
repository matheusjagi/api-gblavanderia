package br.com.ifes.apigblavanderia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cromossomo implements Serializable {

    private List<OrdemProcesso> genes = new ArrayList<>();

    private Long avaliacao;

    public Cromossomo(Cromossomo pai) {
        pai.getGenes().forEach(gen -> this.getGenes().add(gen.clone()));
        this.avaliacao = pai.getAvaliacao();
    }
}
