package org.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MensagemResource: atua como o Receiver no modelo send/receive.
 *
 * O Quarkus registra esta classe como um endpoint HTTP via JAX-RS (RESTEasy).
 * Cada método representa uma operação de "receive": o servidor aguarda
 * uma requisição, processa e devolve uma resposta ao Sender (cliente/Postman).
 */
@Path("/mensagens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MensagemResource {

    /** Armazenamento em memória sem banco de dados */
    private static final List<Mensagem> repositorio = new ArrayList<>();

    /** Gerador de IDs auto-incrementais thread-safe */
    private static final AtomicLong contador = new AtomicLong(1);

    // -----------------------------------------------------------------------
    // GET /mensagens: retorna todas as mensagens (200 OK)
    // -----------------------------------------------------------------------
    @GET
    public List<Mensagem> listarTodas() {
        return repositorio;
    }

    // -----------------------------------------------------------------------
    // GET /mensagens/{id}: busca por ID (200 OK | 404 Not Found)
    // -----------------------------------------------------------------------
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        return repositorio.stream()
                .filter(m -> m.id.equals(id))
                .findFirst()
                .map(m -> Response.ok(m).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"erro\":\"Mensagem com id " + id + " não encontrada.\"}")
                        .build());
    }

    // -----------------------------------------------------------------------
    // POST /mensagens: cria nova mensagem (201 Created)
    // -----------------------------------------------------------------------
    @POST
    public Response enviarMensagem(Mensagem entrada) {
        if (entrada == null || entrada.remetente == null || entrada.conteudo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\":\"Campos 'remetente' e 'conteudo' são obrigatórios.\"}")
                    .build();
        }

        Mensagem nova = new Mensagem(contador.getAndIncrement(), entrada.remetente, entrada.conteudo);
        repositorio.add(nova);

        // 201 Created: recurso foi criado com sucesso no servidor
        return Response.status(Response.Status.CREATED).entity(nova).build();
    }

    // -----------------------------------------------------------------------
    // DELETE /mensagens/{id}: remove por ID (200 OK | 404 Not Found)
    // -----------------------------------------------------------------------
    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        boolean removido = repositorio.removeIf(m -> m.id.equals(id));

        if (removido) {
            return Response.ok("{\"mensagem\":\"Mensagem " + id + " removida com sucesso.\"}").build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"erro\":\"Mensagem com id " + id + " não encontrada.\"}")
                .build();
    }
}
