package br.com.viavarejo.api;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.viavarejo.api.client.ApiException;
import br.com.viavarejo.api.model.request.ConfirmacaoReqDTO;
import br.com.viavarejo.api.model.request.CriarPedidoRequest;
import br.com.viavarejo.api.model.request.DestinatarioDto;
import br.com.viavarejo.api.model.request.EnderecoEntregaDto;
import br.com.viavarejo.api.model.request.EntregaDadosDto;
import br.com.viavarejo.api.model.request.PedidoCarrinho;
import br.com.viavarejo.api.model.request.PedidoProdutoDto;
import br.com.viavarejo.api.model.request.Produtos;
import br.com.viavarejo.api.model.response.CalculoCarrinho;
import br.com.viavarejo.api.model.response.CriarPedidoDTO;
import br.com.viavarejo.api.model.response.PedidoParceiroData;

public class PedidoApiTest {
	private PedidoApi pedidoApi;
	private PedidoApi pedidoApiCB;

	@Before
	public void init() {
		pedidoApi = new PedidoApi("http://api-integracao-extra.hlg-b2b.net", "H9xO4+R8GUy+18nUCgPOlg==");
		pedidoApiCB = new PedidoApi("http://api-integracao-casasbahia.hlg-b2b.net", "H9xO4+R8GUy+18nUCgPOlg==");
	}

	@Test
	public void testPostPedidoCarrinhoWithSucess() {
		Produtos produto = new Produtos();
		produto.setCodigo(8935731);
		produto.setQuantidade(1);
		produto.setIdLojista(15);

		PedidoCarrinho pedidoCarrinho = new PedidoCarrinho();

		pedidoCarrinho.setIdCampanha(5940);
		pedidoCarrinho.setCnpj("57.822.975/0001-12");
		pedidoCarrinho.setCep("01525000");
		pedidoCarrinho.setProdutos(Arrays.asList(produto));

		CalculoCarrinho calculoCarrinho;
		try {
			calculoCarrinho = pedidoApi.postPedidosCarrinho(pedidoCarrinho);
			Assert.assertEquals(8935731L, calculoCarrinho.getData().getProdutos().get(0).getIdSku().longValue());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	public void testGetDadosPedidoParceiroWithSucess() {
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("request.idCompra", "229277332");
		queryParams.put("request.cnpj", "57.822.975/0001-12");
		queryParams.put("request.idCampanha", "3139");
		queryParams.put("request.idPedidoParceiro", "55221211252116");

		PedidoParceiroData pedido;
		try {
			pedido = pedidoApiCB.getDadosPedidoParceiro(queryParams);
			Assert.assertEquals(229277332, pedido.getData().getPedido().getCodigoPedido());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	public void testPatchPedidosCancelamentoOrConfirmacao() {
		Map<String, String> variableParams = new HashMap<>();
		variableParams.put("idCompra", "229559524");

		ConfirmacaoReqDTO dto = new ConfirmacaoReqDTO();
		dto.setIdCampanha(5984);
		dto.setIdPedidoParceiro(123123);
		dto.setConfirmado(false);
		dto.setIdPedidoMktplc("1-01");
		dto.setCancelado(true);
		dto.setMotivoCancelamento("teste");
		dto.setParceiro("BANCO INTER");

		Response response;
		try {
			response = pedidoApi.patchPedidosCancelamentoOrConfirmacao(dto, variableParams);
			Assert.assertNotNull(response);
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	public void testGetNotaFiscalPedidoWithSucess() {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("idCompra", "247473612");
		pathParams.put("idCompraEntrega", "91712686");
		pathParams.put("formato", "PDF");

		String response;
		try {
			response = pedidoApi.getNotaFiscalPedido(pathParams);
			Assert.assertNotNull("Response nulo", response);
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	public void testPostCriarPedido() {
		// Produto
		PedidoProdutoDto produto = new PedidoProdutoDto();
		produto.setIdLojista(10037);
		produto.setCodigo(8935731);
		produto.setQuantidade(1);
		produto.setPremio(0);
		produto.setPrecoVenda(29.90);
		List<PedidoProdutoDto> produtos = Arrays.asList(produto);

		// endereco Entrega
		EnderecoEntregaDto enderecoEntrega = new EnderecoEntregaDto();
		enderecoEntrega.setCep("01525-000");
		enderecoEntrega.setEstado("SP");
		enderecoEntrega.setLogradouro("rua da se");
		enderecoEntrega.setCidade("S�o Paulo");
		enderecoEntrega.setNumero(63);
		enderecoEntrega.setReferencia("teste");
		enderecoEntrega.setBairro("bairro se");
		enderecoEntrega.setComplemento("teste");
		enderecoEntrega.setTelefone("22333333");
		enderecoEntrega.setTelefone("22333335");
		enderecoEntrega.setTelefone("22333336");

		// destinatario
		DestinatarioDto destinatario = new DestinatarioDto();
		destinatario.setNome("teste");
		destinatario.setCpfCnpj("435.375.660-50");
		destinatario.setEmail("teste@teste.com");

		// dados entrega
		EntregaDadosDto dadosEntrega = new EntregaDadosDto();
		dadosEntrega.setValorFrete(4.53);

		// pedido
		CriarPedidoRequest pedido = new CriarPedidoRequest();
		pedido.setProdutos(Arrays.asList(produto));
		pedido.setEnderecoEntrega(enderecoEntrega);
		pedido.setDestinatario(destinatario);
		pedido.setDadosEntrega(dadosEntrega);
		pedido.setCampanha(3139);
		pedido.setCnpj("57.822.975/0001-12");
		pedido.setPedidoParceiro(122266121);
		pedido.setValorFrete(4.53);
		pedido.setAguardarConfirmacao(true);
		pedido.setOptantePeloSimples(true);

		CriarPedidoDTO criarPedidoDTO;
		try {
			criarPedidoDTO = pedidoApi.postCriarPedido(pedido);
			Assert.assertNotNull(criarPedidoDTO);
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	private String printErrorApi(ApiException e, String method) {
		return String.format(
				"Falha. Uma exce��o ApiException n�o deveria ser lan�ada!\nApiException %s \nCode: %s \nMessage: %s \nBody: %s \nHeaders: %s",
				method, e.getCode(),
				e.getMessage(), e.getResponseBody(), e.getResponseHeaders());
	}
}
