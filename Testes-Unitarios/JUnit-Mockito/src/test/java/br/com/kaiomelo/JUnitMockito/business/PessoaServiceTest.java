package br.com.kaiomelo.JUnitMockito.business;

import br.com.kaiomelo.JUnitMockito.insfrastructure.PessoaRepository;
import br.com.kaiomelo.JUnitMockito.insfrastructure.entity.Pessoa;
import br.com.kaiomelo.JUnitMockito.insfrastructure.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    @InjectMocks
    PessoaService service;

    @Mock
    PessoaRepository repository;

    Pessoa pessoa;

    @BeforeEach
    public void setUp(){
        pessoa = new Pessoa("Angelica", "12358569852", "Desenvolvedora", 30, "Sao Paulo", "Rua das Cruzes", 54);
    }

    @Test
    void deveBuscarPessoaPorCPFComSucesso(){
        when(repository.findPessoa(pessoa.getCpf())).thenReturn(Collections.singletonList(pessoa));

        List<Pessoa> pessoas = service.buscaPessoasPorCpf(pessoa.getCpf());
        assertEquals(Collections.singletonList(pessoa), pessoas);
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void naoDeveChamaroRepositoryCasoParametroCPFNulo(){
        final BusinessException e = assertThrows(BusinessException.class, () -> {
            service.buscaPessoasPorCpf(null);
        });

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Erro ao buscar pessoa por Cpf = null"));
        assertThat(e.getCause(), notNullValue());
        assertThat(e.getCause().getMessage(), is("cpf é obrigatório!"));
        verifyNoInteractions(repository);
    }

    @Test
    void deveAcionarExceptionQuandoRepositoryFalhar(){
        when(repository.findPessoa(pessoa.getCpf())).thenThrow(new RuntimeException("falha ao buscar pessoas por cpf!"));

        final BusinessException e = assertThrows(BusinessException.class, () -> {
            service.buscaPessoasPorCpf(pessoa.getCpf());
        });

        assertThat(e.getMessage(), is(format("Erro ao buscar pessoa por cpf = %s", pessoa.getCpf())));
        assertThat(e.getCause().getClass(), is(RuntimeException.class));
        assertThat(e.getCause().getMessage(), is("falha ao buscar pessoa por cpf!"));
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);
    }
}
