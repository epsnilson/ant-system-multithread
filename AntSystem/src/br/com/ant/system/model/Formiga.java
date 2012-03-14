package br.com.ant.system.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Formiga {
		  private int			 number;
		  private Cidade		  localizacaoCidadeAtual;
		  private Set<Cidade>	 cidadesVisitadas = new HashSet<Cidade>();
		  private List<Caminho> trajetoCidades   = new LinkedList<Caminho>();

		  public Formiga(int number, Cidade localizacaoAtual) {
					if (localizacaoAtual == null) {
							  throw new RuntimeException("Localizacao atual nao pode ser null");
					}

					this.cidadesVisitadas.add(localizacaoAtual);
					this.localizacaoCidadeAtual = localizacaoAtual;
					this.number = number;
		  }

		  public int getNumber() {
					return number;
		  }

		  public void setNumber(int number) {
					this.number = number;
		  }

		  public void setLocalizacaoCidadeAtual(Cidade localizacaoCidadeAtual) {
					this.localizacaoCidadeAtual = localizacaoCidadeAtual;
		  }

		  public Cidade getLocalizacaoCidadeAtual() {
					return localizacaoCidadeAtual;
		  }

		  public void addCaminho(Caminho caminho) {
					cidadesVisitadas.add(caminho.getCidadeDestino());
					trajetoCidades.add(caminho);
					
					this.setLocalizacaoCidadeAtual(caminho.getCidadeDestino());
		  }

		  public List<Caminho> getTrajetoCidades() {
					return trajetoCidades;
		  }

		  public void clear() {
					trajetoCidades.clear();
					cidadesVisitadas.clear();
		  }

		  public boolean isCidadeVisitada(Cidade cidade) {
					return cidadesVisitadas.contains(cidade);
		  }

		  public Set<Cidade> getCidadesVisitadas() {
					return cidadesVisitadas;
		  }

		  @Override
		  public String toString() {
					StringBuffer buffer = new StringBuffer();
					buffer.append("Numero:");
					buffer.append(number);
					buffer.append("\n");
					buffer.append("Trajetos: ");
					buffer.append(Arrays.toString(trajetoCidades.toArray()));
					buffer.append("\n");

					return buffer.toString();
		  }

}
