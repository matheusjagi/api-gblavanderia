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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
    private final SequenciamentoProcessoService sequenciamentoProcessoService;

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

    private void mockDados() {
        ordemProcessos = new ArrayList<>();
        maquinas = new ArrayList<>();

        ordemProcessos.addAll(List.of(new OrdemProcesso(1, 100, "Empresa A", LocalDate.parse("18/04/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
            new OrdemProcesso(2, 500, "Empresa B", LocalDate.parse("09/04/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
            new OrdemProcesso(3, 50, "Empresa C", LocalDate.parse("14/04/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")))));

        List<Processo> processosOP1 = List.of(new Processo(1, "LAVAR", 1700),
                new Processo(3, "TINGIR", 1700),
                new Processo(5, "SECAR", 1700),
                new Processo(6, "CENTRIFUGAR", 1700));

        List<Processo> processosOP2 = List.of(new Processo(1, "LAVAR", 1400),
                new Processo(7, "USE", 1400),
                new Processo(4, "LASER", 1400),
                new Processo(6, "CENTRIFUGAR", 1400));

        List<Processo> processosOP3 = List.of(new Processo(1, "LAVAR", 1300),
                new Processo(4, "LASER", 1300),
                new Processo(3, "TINGIR", 1300),
                new Processo(6, "CENTRIFUGAR", 1300));

        ordemProcessos.get(0).setProcessos(processosOP1);
        ordemProcessos.get(1).setProcessos(processosOP2);
        ordemProcessos.get(2).setProcessos(processosOP3);

        maquinas.addAll(List.of(new Maquina(1, "MAQUINA LAVAR", 100),
                new Maquina(4, "MAQUINA LASER", 60),
                new Maquina(6, "MAQUINA TINGIR", 130),
                new Maquina(8, "MAQUINA CENTRIFUGAR", 140),
                new Maquina(9, "MAQUINA USE", 100),
                new Maquina(10, "MAQUINA SECAR", 100)));

        maquinas.get(0).getProcessosQueRealiza().addAll(List.of(new Processo(1)));
        maquinas.get(1).getProcessosQueRealiza().addAll(List.of(new Processo(4)));
        maquinas.get(2).getProcessosQueRealiza().addAll(List.of(new Processo(3)));
        maquinas.get(3).getProcessosQueRealiza().addAll(List.of(new Processo(6)));
        maquinas.get(4).getProcessosQueRealiza().addAll(List.of(new Processo(7)));
        maquinas.get(5).getProcessosQueRealiza().addAll(List.of(new Processo(5)));
    }

    public void testeSequenciamento() {
        mockDados();
//        sequenciamentoProcessoService.sequenciaPorOrdemProcesso(maquinas, inicializaPopulacao(1));
        Cromossomo cromossomo = new Cromossomo();
        ordemProcessos.forEach(op -> cromossomo.getGenes().add(op));
        sequenciamentoProcessoService.sequenciaPorOrdemProcesso(maquinas, Collections.singletonList(cromossomo));
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
        ordemProcessos.forEach(op -> cromossomo.getGenes().add(op));

        cromossomo.getGenes().forEach(gen -> {
            do {
                sequenciamento.set(AlgoritimoUtil.sortearNumero(ordemProcessos.size() + 1));
            } while (verificaSequeciamentoIgual(sequenciamento, cromossomo));

            gen.setSequenciamento(sequenciamento.get());
        });

        ordenaCromossomoPorOrdemDeSequenciamento(cromossomo);

        return cromossomo;
    }

    private void ordenaCromossomoPorOrdemDeSequenciamento(Cromossomo cromossomo) {
        cromossomo.getGenes().sort(Comparator.comparing(OrdemProcesso::getSequenciamento));
    }

    private Boolean verificaSequeciamentoIgual(AtomicInteger sequenciamento, Cromossomo cromossomo) {
        return cromossomo.getGenes().stream()
                .anyMatch(obj -> Objects.equals(sequenciamento.get(), obj.getSequenciamento()));
    }

    private List<Cromossomo> inicializaPopulacao(Integer tamanhoPopulacao) {
        return Stream.generate(this::inicializaCromossomo).limit(tamanhoPopulacao).toList();
    }

}
