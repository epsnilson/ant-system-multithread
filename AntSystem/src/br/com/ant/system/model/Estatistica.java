package br.com.ant.system.model;

import java.util.LinkedList;
import java.util.List;

public class Estatistica {
	private int				formigaId;
	private long			tempoGasto;
	private double			distanciaPercorrida;
	private Cidade			cidadeInicial;

	private List<Caminho>	caminhoPercorrido	= new LinkedList<Caminho>();

	public int getFormigaId() {
		return formigaId;
	}

	public void setFormigaId(int formigaId) {
		this.formigaId = formigaId;
	}

	public long getTempoGasto() {
		return tempoGasto;
	}

	public void setTempoGasto(long tempoGasto) {
		this.tempoGasto = tempoGasto;
	}

	public double getDistanciaPercorrida() {
		return distanciaPercorrida;
	}

	public void setDistanciaPercorrida(double distanciaPercorrida) {
		this.distanciaPercorrida = distanciaPercorrida;
	}

	public List<Caminho> getCaminhoPercorrido() {
		return caminhoPercorrido;
	}

	public void setCaminhoPercorrido(List<Caminho> caminhoPercorrido) {
		this.caminhoPercorrido = caminhoPercorrido;
	}

	public Cidade getCidadeInicial() {
		return cidadeInicial;
	}

	public void setCidadeInicial(Cidade cidadeInicial) {
		this.cidadeInicial = cidadeInicial;
	}

}