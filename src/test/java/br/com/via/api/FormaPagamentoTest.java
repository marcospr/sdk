package br.com.via.api;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.via.api.client.ApiException;
import br.com.via.api.model.response.OpcoesParcelamentoDTO;

public class FormaPagamentoTest {
	private static FormaPagamentoApi pagamentoApi;
	
	@BeforeAll
	public static void init() {
		pagamentoApi = new FormaPagamentoApi();
	}
	
	@Test
	public void getOpcoesParcelamentoSucess() throws ApiException{
		try {
			OpcoesParcelamentoDTO opcoesParcelamento = pagamentoApi.getOpcoesParcelamento("1", "5940", "57.822.975/0001-12", "1000");
			assertNotNull(opcoesParcelamento);
			assertEquals(new Double(1000.0), opcoesParcelamento.getData().get(0).getValorParcela());;
		}catch (ApiException e) {
			printErrorApi(e, "testGetOpcoesParcelamentoSucess");
			fail("Falha. Uma exce��o ApiException n�o deveria ser lan�ada!");
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}
	
	//ERRO FORA DO PADR�O(objeto vem todo vazio)
	@Test
	public void getOpcoesParcelamentoFailure() throws ApiException{
		try {
			OpcoesParcelamentoDTO opcoesParcelamento = pagamentoApi.getOpcoesParcelamento("8", "5940", "57.822.975/0001-12", "1000");
			assertNotNull(opcoesParcelamento);
			assertTrue(opcoesParcelamento.getData().isEmpty());
		}catch (ApiException e) {
			printErrorApi(e, "testGetOpcoesParcelamentoSucess");
			fail("Falha. Uma exce��o ApiException n�o deveria ser lan�ada!");
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}
	
	private void printErrorApi(ApiException e, String method) {
		System.out.println(String.format("ApiException %s \nCode: %s \nMessage: %s \nBody: %s \nHeaders: %s", method,
				e.getCode(), e.getMessage(), e.getResponseBody(), e.getResponseHeaders()));
	}

}
