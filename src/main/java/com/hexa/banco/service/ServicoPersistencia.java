package com.hexa.banco.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexa.banco.model.Conta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServicoPersistencia {
    private ObjectMapper mapper = new ObjectMapper();
    private File arquivo = new File("dados.json");

    public void salvar(List<Conta> listaDeContas) throws IOException {
        mapper.writeValue(arquivo, listaDeContas);
    }

    public List<Conta> carregar() throws IOException {
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(arquivo, new TypeReference<List<Conta>>() {});
    }
}
