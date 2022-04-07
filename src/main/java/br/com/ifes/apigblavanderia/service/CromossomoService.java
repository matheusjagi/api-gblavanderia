package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import br.com.ifes.apigblavanderia.repository.MaquinaRepository;
import br.com.ifes.apigblavanderia.repository.OrdemProcessoRepository;
import br.com.ifes.apigblavanderia.repository.ProcessoRepository;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CromossomoService {

    private final OrdemProcessoRepository ordemProcessoRepository;
    private final ProcessoRepository processoRepository;
    private final MaquinaRepository maquinaRepository;
    private final SequenciamentoService sequenciamentoService;

    private List<OrdemProcesso> ordemProcessos = new ArrayList<>();
    private List<Processo> processos = new ArrayList<>();
    private List<Maquina> maquinas = new ArrayList<>();

    public void abasteceTodasBasesDeDados() {
        processos = processoRepository.abasteceBaseDados();
        ordemProcessos = ordemProcessoRepository.abasteceBaseDados();
        maquinas = maquinaRepository.abasteceBaseDados();

        ordemProcessos.forEach(op -> op.setProcessos(getProcessosOP(op.getId())));
        maquinas.forEach(op -> op.setProcessosQueRealiza(getProcessosMaquina(op.getId())));

        log.info("Bases de dados preenchidas com sucesso!");
    }

    private List<Processo> getProcessosOP(Integer ordemProcessoId) {
        return processos.stream()
                .filter(processo -> processo.getOrdemProcessoId().equals(ordemProcessoId))
                .collect(Collectors.toList());
    }

    private List<Processo> getProcessosMaquina(Integer maquinaId) {
        return processos.stream()
                .filter(processo -> processo.getMaquinaId().equals(maquinaId))
                .collect(Collectors.toList());
    }

    private Cromossomo inicializaCromossomo() {
        AtomicInteger sequenciamento = new AtomicInteger();

        Cromossomo cromossomo = new Cromossomo();
        ordemProcessos.forEach(op -> cromossomo.getGenes().add(op.clone()));

        cromossomo.getGenes().forEach(gen -> {
            do {
                sequenciamento.set(AlgoritimoUtil.sortearNumero(1, ordemProcessos.size()));
            } while (verificaSequeciamentoIgual(sequenciamento, cromossomo));

            gen.setSequenciamento(sequenciamento.get());
        });

        AlgoritimoUtil.ordenaCromossomoPorOrdemDeSequenciamento(cromossomo);

        return cromossomo;
    }

    private Boolean verificaSequeciamentoIgual(AtomicInteger sequenciamento, Cromossomo cromossomo) {
        return cromossomo.getGenes().stream()
                .anyMatch(obj -> Objects.equals(sequenciamento.get(), obj.getSequenciamento()));
    }

    private List<Cromossomo> inicializaPopulacao(Integer tamanhoPopulacao) {
        return Stream.generate(this::inicializaCromossomo).limit(tamanhoPopulacao).toList();
    }

}
