package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class MutacaoService {

    public void mutacao(Cromossomo cromossomo) {
        Integer tamanhoGenes = cromossomo.getGenes().size();

        IntStream.range(0, tamanhoGenes).forEach(index -> {
            if (AlgoritimoUtil.sortearPorcentagem() <= 1) {
                AlgoritimoUtil.trocaPosicaoDeSequencimentoDosGenes(cromossomo, tamanhoGenes);
            }
        });

        AlgoritimoUtil.ordenaCromossomoPorOrdemDeSequenciamento(cromossomo);
    }
}
