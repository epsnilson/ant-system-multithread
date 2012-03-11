package br.com.ant.system.model;

import java.util.LinkedList;
import java.util.List;

public class Formiga {
		  private int		  number;
		  private List<Cidade> trajetoCidades = new LinkedList<Cidade>();

		  public Formiga(int number, Cidade localizacaoAtual) {
					this.setLocalizacaoAtual(localizacaoAtual);
					this.number = number;
		  }

		  public int getNumber() {
					return number;
		  }

		  public void setNumber(int number) {
					this.number = number;
		  }

		  public Cidade getLocalizacaoAtual() {
					return trajetoCidades.get(trajetoCidades.size() - 1);
		  }

		  public void setLocalizacaoAtual(Cidade localizacaoAtual) {
					trajetoCidades.add(localizacaoAtual);
		  }

		  public List<Cidade> getTrajetoCidades() {
					return trajetoCidades;
		  }
}
