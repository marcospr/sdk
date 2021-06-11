package br.com.via.api;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.com.via.api.client.ApiException;
import br.com.via.api.model.request.CartaoCreditoDadosDto;
import br.com.via.api.model.request.CartaoCreditoDadosValidacaoDto;
import br.com.via.api.model.request.ConfirmacaoReqDTO;
import br.com.via.api.model.request.CriacaoPedidoRequest;
import br.com.via.api.model.request.DestinatarioDto;
import br.com.via.api.model.request.EnderecoCobrancaDto;
import br.com.via.api.model.request.EnderecoEntregaDto;
import br.com.via.api.model.request.EntregaDadosDto;
import br.com.via.api.model.request.PagamentoComplementarDto;
import br.com.via.api.model.request.PedidoCarrinho;
import br.com.via.api.model.request.PedidoProdutoDto;
import br.com.via.api.model.request.Produtos;
import br.com.via.api.model.response.CalculoCarrinho;
import br.com.via.api.model.response.ConfirmacaoDTO;
import br.com.via.api.model.response.CriacaoPedidoDTO;
import br.com.via.api.model.response.PedidoParceiroData;
import br.com.via.api.security.Encryptor;

/**
 * Classe de testes para as URI's dos Pedidos do B2B.</br>
 * � importante que os metodos sejam executados na ordem estabelecida, pois</br>
 * alguns metodos de testes possuem dependencia dos resultados dos anteriores.
 * 
 * @author Marcos Pinheiro da Rocha
 *
 */
@TestMethodOrder(OrderAnnotation.class)
class PedidoApiTest {

	/** Instancia do client API. */
	private static PedidoApi pedidoApi;

	/** Token. */
	private static final String AUTHORIZATION_TOKEN = "H9xO4+R8GUy+18nUCgPOlg==";

	/** Host do servico do Extra. */
	private static final String HOST_EXTRA = "http://api-integracao-extra.hlg-b2b.net";

	/** Host do servico das Casas Bahia. */
	// private static final String HOST_CASAS_BAHIA = "";

	/** Host do servico do Ponto Frio. */
	// private static final String HOST_PONTO = "";

	/** CEP padrao dos testes */
	private static final String CEP = "01525000";

	/** Id Lojista padrao dos testes. */
	private static final int ID_LOJISTA = 15;

	/** CPF FICTICIO PARA TESTES */
	private static final String CPF_DESTINATARIO = "435.375.660-50";

	/** CNPJ padrao dos testes. */
	private static final String CNPJ = "57.822.975/0001-12";

	/** Id Campanha padrao dos testes. */
	private static final int ID_CAMPANHA = 5940;

	/** Atributo do Id Sku para criacao do primeiro Pedido. */
	private static final Integer ID_SKU_CRIACAO_PEDIDO = 8935731;

	/** Atributo do Id Sku para criacao do segundo Pedido com cartao de credito. */
	private static final Integer ID_SKU_CRIACAO_PEDIDO_COM_CARTAO = 9342200;

	/** Tipo de Forma de pagamento cart�o Visa. */
	//private static final int CARTAO_VISA = 2;

	/** Tipo de Forma de pagamento cart�o Master. */
	private static final int CARTAO_MASTER = 3;

	/** Numero de cartao de credito Master mascarado. */
	private static final String NUMERO_CARTAO_MASTER_MASCARADO = "515590XXXXXX0001";

	/** Numero de cartao de credito Master ficticio. */
	private static final String NUMERO_CARTAO_MASTER = "5155901222280001";

	/**
	 * Atributo global utilizado para guardar o primeiro pedido criado para ser
	 * utilizado nos demais testes.
	 */
	private static DadosPedidoHelper pedidoHelper;

	/**
	 * Atributo global utilizado para guardar o segundo pedido criado com Cartao
	 * Credito para ser utilizado nos demais testes.
	 */
	private static DadosPedidoHelper pedidoHelperComCartao;

	/**
	 * Chave p�blica 2048 bits utilizada para criptografia dos dados do cart�o.</br>
	 * Pode ser obtida pelo URI Rest abaixo.
	 * 
	 * @see http://api-integracao-casasbahia.hlg-b2b.net/swagger/ui/index#!/Seguranca/Seguranca_ObterChave
	 * 
	 */
	private static final String CHAVE_PUBLICA = "MIIENTCCAx2gAwIBAgIJAJ5ApEGl2oaIMA0GCSqGSIb3DQEBBQUAMIGwMQswCQYDVQQGEwJCUjELMAkGA1UECAwCU1AxFDASBgNVBAcMC1NBTyBDQUVUQU5PMRMwEQYDVQQKDApWSUEgVkFSRUpPMSAwHgYDVQQLDBdTRUdVUkFOQ0EgREEgSU5GT1JNQUNBTzEOMAwGA1UEAwwFUFJPWFkxNzA1BgkqhkiG9w0BCQEWKHRpLnNlZ3VyYW5jYS5pbmZvcm1hY2FvQHZpYXZhcmVqby5jb20uYnIwHhcNMTgwODE2MTIzNjQ2WhcNMjEwODE1MTIzNjQ2WjCBsDELMAkGA1UEBhMCQlIxCzAJBgNVBAgMAlNQMRQwEgYDVQQHDAtTQU8gQ0FFVEFOTzETMBEGA1UECgwKVklBIFZBUkVKTzEgMB4GA1UECwwXU0VHVVJBTkNBIERBIElORk9STUFDQU8xDjAMBgNVBAMMBVBST1hZMTcwNQYJKoZIhvcNAQkBFih0aS5zZWd1cmFuY2EuaW5mb3JtYWNhb0B2aWF2YXJlam8uY29tLmJyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqObNb7KAP09WsV9h76Dw3tj2qa3l97K+slfzLkOBvi0xjacuKCnvsMSGEBosvWY/qNmSLE1YaoyFt7ZaeOiALKh2AFckJRM+/zvQzqi6cPnW0cGsEE/9WO48Fgh894pKjHpukATFb9tBYGTBEW46AH2WiAR735KEnDfFAHG//pkLKriPWEZBr9tf4gdNvyJ/ybs5JrBRU1RKE9MM7qnMkCouKTPwY/lS/2Xb1IYkyZulCf3Uyl7zpB6hQUhprS1R5meRocpGgHJCFfiWD/uXa5nREuGuQxcImwzvf+enwT6CooRoM2rN6IQWSY+uQ64dhSt4FMajZFmHVpLfUIOjEwIDAQABo1AwTjAdBgNVHQ4EFgQUZ22K62aMm/lI5LfblgINPvz8ae8wHwYDVR0jBBgwFoAUZ22K62aMm/lI5LfblgINPvz8ae8wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAj23IDXLPkQpFDbgAtgKuO9N66o61edbJ1+BMjdSsfO0vMVpmBDlKdinxlh509/qJm/WLYswKkKOi7VHojBSV5HyrO5YGCSJFvVGJqF4JUxy7GrWTHqgwcylmX5B5lNd5aMIxwG6AF4o2cp6IPe+Uwaroa8kLTrtM0eRgAInHbQA7MXbvOZY+pzE4s6jFbA1O321zVg4C4Y3C4e30yf9YJNK5XjUP26duvwGqQrZg49ZU3W/t6GYY1kQhSeBG0FPg2GOIHX03WPZpaJ7i1uCv6Ial07pxDxqcT8oCJalY9tW9sv7zBJRaJgTIf5oz5jElb9kWd2D6XwaGB5PJfD6CTQ==";

	/** Atributo auxiliar para os testes de criacao de pedido. */
	private static DadosCartaoHelper dadosCartaoHelper;

	@BeforeAll
	public static void init() {
		pedidoApi = new PedidoApi(HOST_EXTRA, AUTHORIZATION_TOKEN);
		dadosCartaoHelper = new DadosCartaoHelper(new Encryptor(CHAVE_PUBLICA), "Jose da Silva", NUMERO_CARTAO_MASTER,
				"1234", "2045", "12");
	}

	@Test
	@Order(1)
	void testPostCalcularCarrinhoParaCriacaoPedido() {
		Produtos produto = new Produtos();
		produto.setCodigo(ID_SKU_CRIACAO_PEDIDO);
		produto.setQuantidade(1);
		produto.setIdLojista(ID_LOJISTA);

		PedidoCarrinho pedidoCarrinho = new PedidoCarrinho();

		pedidoCarrinho.setIdCampanha(ID_CAMPANHA);
		pedidoCarrinho.setCnpj(CNPJ);
		pedidoCarrinho.setCep(CEP);
		pedidoCarrinho.setProdutos(Arrays.asList(produto));

		CalculoCarrinho calculoCarrinho;
		try {
			calculoCarrinho = pedidoApi.postCalcularCarrinho(pedidoCarrinho);
			Assert.assertTrue(calculoCarrinho.getData().getProdutos().get(0).getValorTotalFrete().doubleValue() > 0.0);

			// preparacao do objeto que sera utilizado nos demais testes
			pedidoHelper = preparePedido(calculoCarrinho);

		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCalcularCarrinho"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(2)
	void testPostCalcularCarrinhoParaCriacaoPedidoComCartao() {
		Produtos produto = new Produtos();
		produto.setCodigo(ID_SKU_CRIACAO_PEDIDO_COM_CARTAO);
		produto.setQuantidade(1);
		produto.setIdLojista(ID_LOJISTA);

		PedidoCarrinho pedidoCarrinho = new PedidoCarrinho();

		pedidoCarrinho.setIdCampanha(ID_CAMPANHA);
		pedidoCarrinho.setCnpj(CNPJ);
		pedidoCarrinho.setCep(CEP);
		pedidoCarrinho.setProdutos(Arrays.asList(produto));

		CalculoCarrinho calculoCarrinho;
		try {
			calculoCarrinho = pedidoApi.postCalcularCarrinho(pedidoCarrinho);
			Assert.assertTrue(calculoCarrinho.getData().getProdutos().get(0).getValorTotalFrete().doubleValue() > 0.0);

			// preparacao do objeto que sera utilizado nos demais testes
			pedidoHelperComCartao = preparePedido(calculoCarrinho);

		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCalcularCarrinhoParaCriacaoPedidoComCartao"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(3)
	void testPostCriarPedido() {
		// Produto
		PedidoProdutoDto produto = new PedidoProdutoDto();
		produto.setIdLojista(ID_LOJISTA);
		produto.setCodigo(pedidoHelper.getIdSku());
		produto.setQuantidade(1);
		produto.setPremio(0);
		produto.setPrecoVenda(pedidoHelper.getPrecoVenda());
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
		destinatario.setCpfCnpj(CPF_DESTINATARIO);
		destinatario.setEmail("teste@teste.com");

		// dados entrega
		EntregaDadosDto dadosEntrega = new EntregaDadosDto();
		dadosEntrega.setValorFrete(pedidoHelper.valorFrete);

		// pedido
		CriacaoPedidoRequest pedido = new CriacaoPedidoRequest();
		pedido.setProdutos(produtos);
		pedido.setEnderecoEntrega(enderecoEntrega);
		pedido.setDestinatario(destinatario);
		pedido.setDadosEntrega(dadosEntrega);
		pedido.setCampanha(ID_CAMPANHA);
		pedido.setCnpj(CNPJ);
		int idPedidoParceiro = geraPedidoParceiroId();
		pedido.setPedidoParceiro(idPedidoParceiro);
		pedido.setValorFrete(pedidoHelper.getValorFrete());
		pedido.setAguardarConfirmacao(true);
		pedido.setOptantePeloSimples(true);

		CriacaoPedidoDTO criacaoPedidoDTO;
		try {
			criacaoPedidoDTO = pedidoApi.postCriarPedido(pedido);
			double expectedValue = pedidoHelper.getTotalPedido();
			Assert.assertEquals(expectedValue, criacaoPedidoDTO.getData().getValorTotalPedido(), 0.01);

			// complementa dados do Pedido para utilizar nos outros metodos
			pedidoHelper.setIdPedido(criacaoPedidoDTO.getData().getCodigoPedido());
			pedidoHelper.setIdPedidoParceiro(criacaoPedidoDTO.getData().getPedidoParceiro());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedido"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(4)
	void testPostCriarPedidoPagCartao() {
		// Produto
		PedidoProdutoDto produto = new PedidoProdutoDto();
		produto.setIdLojista(ID_LOJISTA);
		produto.setCodigo(pedidoHelperComCartao.getIdSku());
		produto.setQuantidade(1);
		produto.setPrecoVenda(pedidoHelperComCartao.getPrecoVenda());
		List<PedidoProdutoDto> produtos = Arrays.asList(produto);

		// endereco Entrega
		EnderecoEntregaDto enderecoEntrega = new EnderecoEntregaDto();
		enderecoEntrega.setCep(CEP);
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
		destinatario.setCpfCnpj(CPF_DESTINATARIO);
		destinatario.setEmail("teste@teste.com");

		// pedido
		CriacaoPedidoRequest pedido = new CriacaoPedidoRequest();
		pedido.setCampanha(ID_CAMPANHA);
		pedido.setCnpj(CNPJ);
		pedido.setPedidoParceiro(geraPedidoParceiroId());
		pedido.setValorFrete(pedidoHelperComCartao.getValorFrete());
		pedido.setAguardarConfirmacao(true);
		pedido.setOptantePeloSimples(true);
		pedido.setPossuiPagtoComplementar(true);

		// pagamentos complementares
		PagamentoComplementarDto pagamentoComplementarDto = new PagamentoComplementarDto();
		pagamentoComplementarDto.setIdFormaPagamento(CARTAO_MASTER); // 2-Visa 3-Master

		// dados cartao credito
		CartaoCreditoDadosDto cartaoCreditoDadosDto = new CartaoCreditoDadosDto();
		cartaoCreditoDadosDto.setNome(dadosCartaoHelper.getEncryptedName());
		cartaoCreditoDadosDto.setNumero(dadosCartaoHelper.getEncryptedNumber());
		cartaoCreditoDadosDto.setCodigoVerificador(dadosCartaoHelper.getEncryptedVerifyCode());
		cartaoCreditoDadosDto.setValidadeAno(dadosCartaoHelper.getEncryptedValidateYear());
		cartaoCreditoDadosDto.setValidadeMes(dadosCartaoHelper.getEncryptedValidateMonth());
		cartaoCreditoDadosDto.setQuantidadeParcelas(1);

		pagamentoComplementarDto.setDadosCartaoCredito(cartaoCreditoDadosDto);

		// dados Cartao Credito Validacao
		CartaoCreditoDadosValidacaoDto cartaoCreditoDadosValidacaoDto = new CartaoCreditoDadosValidacaoDto();
		cartaoCreditoDadosValidacaoDto.setNome(dadosCartaoHelper.getNome());
		cartaoCreditoDadosValidacaoDto.setNumeroMascarado(NUMERO_CARTAO_MASTER_MASCARADO);

		cartaoCreditoDadosValidacaoDto.setQtCaracteresCodigoVerificador("4");
		cartaoCreditoDadosValidacaoDto.setValidadeAno(dadosCartaoHelper.getAnoValidade());
		cartaoCreditoDadosValidacaoDto.setValidadeMes(dadosCartaoHelper.getMesValidade());

		pagamentoComplementarDto.setDadosCartaoCreditoValidacao(cartaoCreditoDadosValidacaoDto);

		// pagamento complementar
		pagamentoComplementarDto.setValorComplementar(30.0);
		pagamentoComplementarDto.setValorComplementarComJuros(30.0);

		// dados entrega
		EntregaDadosDto dadosEntrega = new EntregaDadosDto();
		dadosEntrega.setValorFrete(pedidoHelperComCartao.getValorFrete());

		// endereco cobranca
		EnderecoCobrancaDto enderecoCobranca = new EnderecoCobrancaDto();
		enderecoCobranca.setCep("01546090");
		enderecoCobranca.setEstado("SP");
		enderecoCobranca.setLogradouro("Rua Rodrigues Bastista");
		enderecoCobranca.setCidade("S�o Paulo");
		enderecoCobranca.setNumero(63);
		enderecoCobranca.setReferencia("teste");
		enderecoCobranca.setBairro("Vila Teodoro");
		enderecoCobranca.setComplemento("teste");
		enderecoCobranca.setTelefone("22333333");
		enderecoCobranca.setTelefone("22333335");
		enderecoCobranca.setTelefone("22333336");

		pedido.setProdutos(produtos);
		pedido.setEnderecoEntrega(enderecoEntrega);
		pedido.setDestinatario(destinatario);
		pedido.setDadosEntrega(dadosEntrega);
		pedido.setEnderecoCobranca(enderecoCobranca);
		pedido.setPagtosComplementares(Arrays.asList(pagamentoComplementarDto));
		pedido.setValorTotalPedido(pedidoHelperComCartao.getTotalPedido());
		pedido.setValorTotalComplementar(30.0);
		pedido.setValorTotalComplementarComJuros(30.0);

		CriacaoPedidoDTO criacaoPedidoDTO;
		try {
			criacaoPedidoDTO = pedidoApi.postCriarPedido(pedido);
			double valueExpected = pedidoHelperComCartao.getTotalPedido();
			Assert.assertEquals(valueExpected, criacaoPedidoDTO.getData().getValorTotalPedido(), 0);

			// complementa dados do Pedido para utilizar nos outros metodos
			pedidoHelperComCartao.setIdPedido(criacaoPedidoDTO.getData().getCodigoPedido());
			pedidoHelperComCartao.setIdPedidoParceiro(criacaoPedidoDTO.getData().getPedidoParceiro());

		} catch (ApiException e) {
			fail(printErrorApi(e, "testPostCriarPedidoPagCartao"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(5)
	void testPatchPedidosCancelamento() {
		Map<String, String> variableParams = new HashMap<>();
		variableParams.put("idCompra", pedidoHelper.getIdPedido().toString());

		ConfirmacaoReqDTO dto = new ConfirmacaoReqDTO();
		dto.setIdCampanha(ID_CAMPANHA);
		dto.setIdPedidoParceiro(pedidoHelper.getIdPedidoParceiro());
		dto.setCancelado(true);
		dto.setConfirmado(false);
		dto.setIdPedidoMktplc("1-01");
		dto.setMotivoCancelamento("teste");
		dto.setParceiro("BANCO INTER");

		ConfirmacaoDTO confirmacaoDto;
		try {
			confirmacaoDto = pedidoApi.patchPedidosCancelamentoOrConfirmacao(dto, variableParams);
			Assert.assertTrue(confirmacaoDto.getData().getPedidoCancelado());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPatchPedidosCancelamento"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(6)
	void testPatchPedidosConfirmacao() {
		Map<String, String> variableParams = new HashMap<>();
		variableParams.put("idCompra", pedidoHelperComCartao.getIdPedido().toString());

		ConfirmacaoReqDTO dto = new ConfirmacaoReqDTO();
		dto.setIdCampanha(ID_CAMPANHA);
		dto.setIdPedidoParceiro(pedidoHelperComCartao.getIdPedidoParceiro());
		dto.setConfirmado(true);

		ConfirmacaoDTO confirmacaoDto;
		try {
			confirmacaoDto = pedidoApi.patchPedidosCancelamentoOrConfirmacao(dto, variableParams);
			Assert.assertTrue(confirmacaoDto.getData().getPedidoConfirmado());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testPatchPedidosConfirmacao"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(7)
	void testGetDadosPedidoParceiro() {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("idCompra", pedidoHelper.getIdPedido().toString());

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("request.idCompra", pedidoHelper.getIdPedido().toString());
		queryParams.put("request.cnpj", CNPJ);
		queryParams.put("request.idCampanha", String.valueOf(ID_CAMPANHA));
		queryParams.put("request.idPedidoParceiro", pedidoHelper.getIdPedidoParceiro().toString());

		PedidoParceiroData pedido;
		try {
			pedido = pedidoApi.getDadosPedidoParceiro(pathParams, queryParams);
			Assert.assertEquals(pedidoHelper.getIdPedido().intValue(), pedido.getData().getPedido().getCodigoPedido());
		} catch (ApiException e) {
			fail(printErrorApi(e, "testGetDadosPedidoParceiro"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	@Test
	@Order(8)
	void testGetNotaFiscalPedidoPdf() {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("idCompra", "247473612");
		pathParams.put("idCompraEntrega", "91712686");
		pathParams.put("formato", "PDF");

		String response;
		try {
			response = pedidoApi.getNotaFiscalPedido(pathParams);
			Assert.assertNotNull("Response nulo", response);
		} catch (ApiException e) {
			fail(printErrorApi(e, "testGetNotaFiscalPedidoPdf"));
		} catch (Exception e) {
			fail("Falha. Uma exce��o n�o deveria ser lan�ada!\n" + e.getMessage());
		}
	}

	private String printErrorApi(ApiException e, String method) {
		return String.format(
				"Falha. Uma exce��o ApiException n�o deveria ser lan�ada!\nApiException %s \nCode: %s \nMessage: %s \nBody: %s \nHeaders: %s",
				method, e.getCode(), e.getMessage(), e.getResponseBody(), e.getResponseHeaders());
	}

	private DadosPedidoHelper preparePedido(CalculoCarrinho calculoCarrinho) {
		DadosPedidoHelper pedidoHelper = new DadosPedidoHelper();
		pedidoHelper.setIdSku(calculoCarrinho.getData().getProdutos().get(0).getIdSku());
		pedidoHelper.setPrecoVenda(calculoCarrinho.getData().getProdutos().get(0).getValorUnitario());
		pedidoHelper.setValorFrete(calculoCarrinho.getData().getProdutos().get(0).getValorTotalFrete());
		return pedidoHelper;
	}

	private int geraPedidoParceiroId() {
		int idPedidoParceiro = new Random().nextInt(65536);
		idPedidoParceiro = idPedidoParceiro < 0 ? idPedidoParceiro * -1 : idPedidoParceiro;
		return idPedidoParceiro;
	}

	/**
	 * Classe interna auxil�iar utilizada para guardar os dados os pedidos gerados
	 * para serem utilizados nos outros metodos dependentes.
	 * 
	 * @author Marcos
	 *
	 */
	private static class DadosPedidoHelper {
		private Integer idPedido;
		private Integer idPedidoParceiro;
		private Integer idSku;
		private double valorFrete;
		private double precoVenda;

		public double getTotalPedido() {
			return valorFrete + precoVenda;
		}

		public void setIdSku(Integer idSku) {
			this.idSku = idSku;
		}

		public Integer getIdSku() {
			return idSku;
		}

		public void setValorFrete(double valorFrete) {
			this.valorFrete = valorFrete;
		}

		public double getValorFrete() {
			return valorFrete;
		}

		public void setPrecoVenda(double precoVenda) {
			this.precoVenda = precoVenda;
		}

		public double getPrecoVenda() {
			return precoVenda;
		}

		public Integer getIdPedido() {
			return idPedido;
		}

		public void setIdPedido(Integer idPedido) {
			this.idPedido = idPedido;
		}

		public void setIdPedidoParceiro(Integer idPedidoParceiro) {
			this.idPedidoParceiro = idPedidoParceiro;
		}

		public Integer getIdPedidoParceiro() {
			return idPedidoParceiro;
		}

	}

	/**
	 * Classe auxiliara para dados do cartao credito.
	 * 
	 * @author Marcos P. da Rocha
	 *
	 */
	private static class DadosCartaoHelper {

		private Encryptor encryptor;
		private String nome;
		private String numero;
		private String codigoVerificador;
		private String anoValidade;
		private String mesValidade;

		public DadosCartaoHelper(Encryptor encryptor, String nome, String numero, String codigoVerificador,
				String anoValidade,
				String mesValidade) {
			this.encryptor = encryptor;
			this.nome = nome;
			this.numero = numero;
			this.codigoVerificador = codigoVerificador;
			this.anoValidade = anoValidade;
			this.mesValidade = mesValidade;
		}

		public String getEncryptedName() {
			return encryptor.encript(nome);
		}

		public String getEncryptedNumber() {
			return encryptor.encript(numero);
		}

		public String getEncryptedVerifyCode() {
			return encryptor.encript(codigoVerificador);
		}

		public String getEncryptedValidateYear() {
			return encryptor.encript(anoValidade);
		}

		public String getEncryptedValidateMonth() {
			return encryptor.encript(mesValidade);
		}

		public String getNome() {
			return nome;
		}

		public String getAnoValidade() {
			return anoValidade;
		}

		public String getMesValidade() {
			return mesValidade;
		}

	}
}
