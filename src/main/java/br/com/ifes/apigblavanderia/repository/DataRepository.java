package br.com.ifes.apigblavanderia.repository;

import java.util.List;

public abstract class DataRepository<E> {

    abstract List<E> abasteceBaseDados();

    public String getUrlArquivoCSV(String nomeArquivo) {
        return String.format("src/main/java/br/com/ifes/apigblavanderia/repository/csv/%s", nomeArquivo);
    }
}
