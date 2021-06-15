
package br.com.via.api.model.response;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nome",
    "idFormaPagamento",
    "quantidadeParcelas",
    "taxaJurosAoMes",
    "valorParcela",
    "valorTotal"
})
public class OpcaoParcelamento {

    @JsonProperty("nome")
    private String nome;
    @JsonProperty("idFormaPagamento")
    private Integer idFormaPagamento;
    @JsonProperty("quantidadeParcelas")
    private Integer quantidadeParcelas;
    @JsonProperty("taxaJurosAoMes")
    private Double taxaJurosAoMes;
    @JsonProperty("valorParcela")
    private Double valorParcela;
    @JsonProperty("valorTotal")
    private Double valorTotal;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdFormaPagamento() {
        return idFormaPagamento;
    }

    public void setIdFormaPagamento(Integer idFormaPagamento) {
        this.idFormaPagamento = idFormaPagamento;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public Double getTaxaJurosAoMes() {
        return taxaJurosAoMes;
    }

    public void setTaxaJurosAoMes(Double taxaJurosAoMes) {
        this.taxaJurosAoMes = taxaJurosAoMes;
    }

    public Double getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(Double valorParcela) {
        this.valorParcela = valorParcela;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }


}
