package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.Minuto;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import br.com.ifes.apigblavanderia.domain.TempoTrabalhado;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SequenciamentoService {

    public static final Integer DAY = 1;
    public static final Integer WEEK = 5;
    public static final Integer HOUR_IN_MINUTES = 60;
    public static final LocalDate DATA_EXECUCAO_SEQUENCIAMENTO = LocalDate.of(2022, Month.MAY, 2);

    public void sequenciamentoPorOrdemDeProcesso(List<Maquina> maquinas, List<Cromossomo> populacao) {
        log.info("Realizando SEQUENCIAMENTO dos PROCESSOS...");

        populacao.forEach(cromossomo -> {
            cromossomo.getGenes().forEach(op -> {
                AtomicInteger terminoUltimoProcesso = new AtomicInteger(0);

                op.getProcessos().forEach(processo -> {
                    List<Maquina> maquinasRealizamProcesso = getMaquinasRealizamProcesso(maquinas, processo);

                    maquinasRealizamProcesso.stream()
                            .filter(mrp -> Objects.isNull(mrp.getTempoTrabalhado()))
                            .findAny()
                            .ifPresentOrElse(
                                (maquina) -> inicializaTrabalhoDaMaquina(processo, maquina, terminoUltimoProcesso),
                                () -> alocaProcesso(processo, maquinasRealizamProcesso, terminoUltimoProcesso)
                            );

                });

                setDataPrevistaDaOP(op);
            });

            setAvalicaoCromossomo(cromossomo);
            reinciaTempoTrabalhadoDasMaquinas(maquinas);
        });

        log.info("SEQUENCIAMENTO finalizado!");
    }

    private void reinciaTempoTrabalhadoDasMaquinas(List<Maquina> maquinas) {
        maquinas.forEach(maquina -> maquina.setTempoTrabalhado(new TempoTrabalhado(DATA_EXECUCAO_SEQUENCIAMENTO)));
    }

    public void setAvalicaoCromossomo(Cromossomo cromossomo) {
        cromossomo.setAvaliacao(cromossomo.getGenes().stream().mapToLong(OrdemProcesso::getDiasAtraso).sum());
    }

    private void setDataPrevistaDaOP(OrdemProcesso op) {
        Processo ultimoProcessoDaOP = op.getProcessos().get(op.getProcessos().size() - 1);
        TempoTrabalhado tempoTrabalhadoDaUltimaMaquinaAlocada = ultimoProcessoDaOP.getMaquinaAlocada().getTempoTrabalhado();
        op.setDataPrevistaEntrega(tempoTrabalhadoDaUltimaMaquinaAlocada.getDataInicio().plusDays(tempoTrabalhadoDaUltimaMaquinaAlocada.getTotalDiasTrabalhados()));

        if (op.getDataEntrega().isBefore(op.getDataPrevistaEntrega())) {
            op.setDiasAtraso(ChronoUnit.DAYS.between(op.getDataEntrega(), op.getDataPrevistaEntrega()));
        }
    }

    private void alocaProcesso(Processo processo, List<Maquina> maquinasRealizamProcesso, AtomicInteger terminoUltimoProcesso) {
        AtomicBoolean processoAlocado = new AtomicBoolean(false);

        while (!processoAlocado.get()) {
            maquinasRealizamProcesso.forEach(maquina -> {
                Integer minutosGastosComProcesso = getMinutosGastosComProcesso(processo.getQuantidadePecas(), maquina.getProducaoMaximaPorHora());
                tentaAlocarProcessoNaMaquina(processo, processoAlocado, maquina, minutosGastosComProcesso, terminoUltimoProcesso);

                if (!processoAlocado.get()) {
                    adicionaUmaSemanaDeTrabalhoNaMaquina(maquina);
                }
            });
        }
    }

    private void adicionaUmDiaDeTrabalhoNaMaquina(Maquina maquina) {
        maquina.getTempoTrabalhado().getTempo().addAll(AlgoritimoUtil.criaDiaTrabalhadoEmMinutos());
        maquina.getTempoTrabalhado().setTotalDiasTrabalhados(maquina.getTempoTrabalhado().getTotalDiasTrabalhados() + DAY);
    }

    private void adicionaUmaSemanaDeTrabalhoNaMaquina(Maquina maquina) {
        maquina.getTempoTrabalhado().getTempo().addAll(AlgoritimoUtil.criaSemanaTrabalhadaEmMinutos());
        maquina.getTempoTrabalhado().setTotalDiasTrabalhados(maquina.getTempoTrabalhado().getTotalDiasTrabalhados() + WEEK);
    }

    private void tentaAlocarProcessoNaMaquina(Processo processo, AtomicBoolean processoAlocado, Maquina maquina,
                                              Integer minutosGastosComProcesso, AtomicInteger terminoUltimoProcesso) {

        Integer tamanhoTempoTrabalhado = maquina.getTempoTrabalhado().getTempo().size() - 1;
        AtomicBoolean terminoProcessoMaiorQueTempo = new AtomicBoolean(true);

        maquina.getTempoTrabalhado().getTempo().stream()
                .skip(terminoUltimoProcesso.get())
                .filter(minuto -> !minuto.getUsado())
                .takeWhile(t -> terminoProcessoMaiorQueTempo.get() && !processoAlocado.get())
                .forEach(minuto -> {
                    Integer indexMinuto = maquina.getTempoTrabalhado().getTempo().indexOf(minuto);
                    Integer indexFinalProcesso = indexMinuto + minutosGastosComProcesso - 1;
                    terminoProcessoMaiorQueTempo.set(indexFinalProcesso <= tamanhoTempoTrabalhado);

                    if (terminoProcessoMaiorQueTempo.get()) {
                        List<Minuto> tempoProcesso = maquina.getTempoTrabalhado().getTempo()
                                .subList(indexMinuto, indexFinalProcesso);

                        if (tempoProcesso.stream().noneMatch(Minuto::getUsado)) {
                            setMinutosTrabalhados(maquina, indexMinuto, indexFinalProcesso, terminoUltimoProcesso);
                            processo.setMaquinaAlocada(maquina);
                            processoAlocado.set(true);
                        }
                    }
                });
    }

    private void inicializaTrabalhoDaMaquina(Processo processo, Maquina maquina, AtomicInteger terminoUltimoProcesso) {
        maquina.setTempoTrabalhado(new TempoTrabalhado(DATA_EXECUCAO_SEQUENCIAMENTO));
        Integer minutosGastosComProcesso = getMinutosGastosComProcesso(processo.getQuantidadePecas(), maquina.getProducaoMaximaPorHora());

        if (terminoUltimoProcesso.get() == 0) {
            setMinutosTrabalhados(maquina, 0, minutosGastosComProcesso - 1, terminoUltimoProcesso);
        } else {
            setMinutosTrabalhados(maquina, terminoUltimoProcesso.get(), terminoUltimoProcesso.get() + minutosGastosComProcesso - 1, terminoUltimoProcesso);
        }

        processo.setMaquinaAlocada(maquina);
    }

    private int getMinutosGastosComProcesso(Integer quantidadePecas, Integer producaoMaximaPorHora) {
        return (HOUR_IN_MINUTES * quantidadePecas) / producaoMaximaPorHora;
    }

    private void setMinutosTrabalhados(Maquina maquina, Integer indexComecoProcesso, Integer indexFinalProcesso, AtomicInteger terminoUltimoProcesso) {
        while (indexFinalProcesso > maquina.getTempoTrabalhado().getTempo().size()) {
            adicionaUmDiaDeTrabalhoNaMaquina(maquina);
        }

        IntStream.range(indexComecoProcesso, indexFinalProcesso)
                .forEach(index -> maquina.getTempoTrabalhado().getTempo().set(index, new Minuto(true)));

        terminoUltimoProcesso.set(indexFinalProcesso + 1);
    }

    private List<Maquina> getMaquinasRealizamProcesso(List<Maquina> maquinas, Processo processo) {
        return maquinas.stream()
                .filter(maquina ->
                    maquina.getProcessosQueRealiza()
                            .stream()
                            .anyMatch(processoRealizado -> processoRealizado.equals(processo.getId()))
                )
                .toList();
    }
}
