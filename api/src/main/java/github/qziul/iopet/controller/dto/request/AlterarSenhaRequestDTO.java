package github.qziul.iopet.controller.dto.request;

public record AlterarSenhaRequestDTO(
        String senhaAtual,
        String novaSenha
) {}
