package com.example.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.main.dto.AgendamentoRequest;
import com.example.main.exception.RecursoNaoEncontradoException;
import com.example.main.exception.RegraNegocioException;
import com.example.main.model.Agendamento;
import com.example.main.model.Servico;
import com.example.main.model.StatusAgendamento;
import com.example.main.model.Usuario;
import com.example.main.repository.AgendamentoRepository;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario;
    private Servico servico;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");

        servico = new Servico();
        servico.setId(1L);
        servico.setNome("Corte Masculino");

        dataHora = LocalDateTime.of(2030, 1, 10, 14, 30);
    }

    @Nested
    @DisplayName("Ao cadastrar um agendamento")
    class Cadastrar {

        @Test
        @DisplayName("deve salvar quando o horário estiver livre")
        void deveSalvarQuandoHorarioLivre() {
            // Arrange (preparação)
            AgendamentoRequest request = new AgendamentoRequest(1L, 1L, dataHora);
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(servicoService.buscarPorId(1L)).thenReturn(servico);
            when(agendamentoRepository.existsByDataHoraAndStatus(dataHora, StatusAgendamento.AGENDADO)).thenReturn(false);
            when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

            // Act (ação)
            Agendamento resultado = agendamentoService.cadastrar(request);

            // Assert (verificação)
            assertThat(resultado.getUsuario()).isEqualTo(usuario);
            assertThat(resultado.getServico()).isEqualTo(servico);
            assertThat(resultado.getDataHora()).isEqualTo(dataHora);
            assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.AGENDADO);
            verify(agendamentoRepository).save(any(Agendamento.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando o horário já estiver ocupado")
        void deveLancarExcecaoQuandoHorarioOcupado() {
            AgendamentoRequest request = new AgendamentoRequest(1L, 1L, dataHora);
            when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
            when(servicoService.buscarPorId(1L)).thenReturn(servico);
            when(agendamentoRepository.existsByDataHoraAndStatus(dataHora, StatusAgendamento.AGENDADO)).thenReturn(true);

            assertThatThrownBy(() -> agendamentoService.cadastrar(request))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Horário indisponível");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando o usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            AgendamentoRequest request = new AgendamentoRequest(99L, 1L, dataHora);
            when(usuarioService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

            assertThatThrownBy(() -> agendamentoService.cadastrar(request))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Usuário não encontrado");

            verify(agendamentoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Ao cancelar um agendamento")
    class Cancelar {

        @Test
        @DisplayName("deve mudar o status para CANCELADO quando estiver AGENDADO")
        void deveCancelarAgendamentoAtivo() {
            Agendamento agendamento = criarAgendamento(StatusAgendamento.AGENDADO);
            when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

            Agendamento resultado = agendamentoService.cancelar(1L);

            assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando o agendamento já estiver cancelado")
        void deveLancarExcecaoQuandoJaCancelado() {
            Agendamento agendamento = criarAgendamento(StatusAgendamento.CANCELADO);
            when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

            assertThatThrownBy(() -> agendamentoService.cancelar(1L))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessage("Apenas agendamentos ativos podem ser cancelados");
        }

        @Test
        @DisplayName("deve lançar exceção quando o agendamento estiver concluído")
        void deveLancarExcecaoQuandoConcluido() {
            Agendamento agendamento = criarAgendamento(StatusAgendamento.CONCLUIDO);
            when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

            assertThatThrownBy(() -> agendamentoService.cancelar(1L))
                    .isInstanceOf(RegraNegocioException.class);

            assertThat(agendamento.getStatus()).isEqualTo(StatusAgendamento.CONCLUIDO);
        }

        @Test
        @DisplayName("deve lançar exceção quando o agendamento não existir")
        void deveLancarExcecaoQuandoNaoExistir() {
            when(agendamentoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> agendamentoService.cancelar(99L))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Agendamento não encontrado");
        }
    }

    private Agendamento criarAgendamento(StatusAgendamento status) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setUsuario(usuario);
        agendamento.setServico(servico);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(status);
        return agendamento;
    }
}
