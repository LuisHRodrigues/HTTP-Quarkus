package org.acme;

import java.time.LocalDateTime;

/**
 * Representa uma mensagem trafegada entre processos distribuídos.
 * Equivale ao "pacote de dados" no modelo send/receive.
 */
public class Mensagem {

    public Long id;
    public String remetente;
    public String conteudo;
    public LocalDateTime timestamp;

    /** Construtor padrão exigido pelo Jackson para desserialização JSON */
    public Mensagem() {}

    public Mensagem(Long id, String remetente, String conteudo) {
        this.id        = id;
        this.remetente = remetente;
        this.conteudo  = conteudo;
        this.timestamp = LocalDateTime.now();
    }
}
