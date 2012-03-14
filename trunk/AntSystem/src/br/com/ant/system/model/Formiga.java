package br.com.ant.system.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Formiga {
	private int				id;

	private Cidade			localizacaoCidadeAtual;
	private Cidade			localizacaoCidadeInicial;
	private Set<Cidade>		cidadesVisitadas	= new HashSet<Cidade>();
	private List<Caminho>	trajetoCidades		= new LinkedList<Caminho>();

	public Formiga(int number, Cidade localizacaoAtual) {
		if (localizacaoAtual == null) {
			throw new RuntimeException("Localizacao atual nao pode ser null");
		}

		this.cidadesVisitadas.add(localizacaoAtual);
		this.localizacaoCidadeAtual = localizacaoAtual;
		this.localizacaoCidadeInicial = localizacaoAtual;
		this.id = number;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cidade getLocalizacaoCidadeAtual() {
		return localizacaoCidadeAtual;
	}

	public void addCaminho(Caminho caminho) {
		this.cidadesVisitadas.add(caminho.getCidadeDestino());
		this.trajetoCidades.add(caminho);

		this.localizacaoCidadeAtual = caminho.getCidadeDestino();
	}

	public List<Caminho> getTrajetoCidades() {
		return trajetoCidades;
	}

	public void clear(Cidade localizacaoAtual) {
		this.trajetoCidades.clear();
		this.cidadesVisitadas.clear();

		this.localizacaoCidadeAtual = localizacaoAtual;
		this.localizacaoCidadeInicial = localizacaoAtual;
	}

	public boolean isCidadeVisitada(Cidade cidade) {
		return cidadesVisitadas.contains(cidade);
	}

	public Set<Cidade> getCidadesVisitadas() {
		return cidadesVisitadas;
	}

	public Cidade getLocalizacaoCidadeInicial() {
		return localizacaoCidadeInicial;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Numero:");
		buffer.append(id);
		buffer.append("\n");
		buffer.append("Trajetos: ");
		buffer.append(Arrays.toString(trajetoCidades.toArray()));
		buffer.append("\n");

		return buffer.toString();
	}

}
