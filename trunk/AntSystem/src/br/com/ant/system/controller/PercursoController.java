package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;

public class PercursoController {

	private List<Cidade>				cidadesPercurso	= new ArrayList<Cidade>();
	private Map<Cidade, List<Caminho>>	mapPercurso		= new HashMap<Cidade, List<Caminho>>();

	private List<Caminho>				melhorTrajeto	= new LinkedList<Caminho>();
	private double						menorDistanciaPercorrida;

	public void addPercurso(Cidade cidadeOrigem, Cidade cidadeDestino, float distancia) {
		Caminho caminho = new Caminho(cidadeOrigem, cidadeDestino, distancia);
		Caminho caminhoInverso = new Caminho(cidadeDestino, cidadeOrigem, distancia);

		if (!cidadesPercurso.contains(cidadeOrigem)) {
			cidadesPercurso.add(cidadeOrigem);
		}

		if (!cidadesPercurso.contains(cidadeDestino)) {
			cidadesPercurso.add(cidadeDestino);
		}

		this.addtoMapPercurso(cidadeOrigem, caminho);
		this.addtoMapPercurso(cidadeDestino, caminhoInverso);
	}

	private void addtoMapPercurso(Cidade cidadeOrigem, Caminho caminho) {
		List<Caminho> caminhos;
		if (mapPercurso.containsKey(cidadeOrigem)) {
			caminhos = mapPercurso.get(cidadeOrigem);
			caminhos.add(caminho);
		} else {
			caminhos = new ArrayList<Caminho>();
			caminhos.add(caminho);
			mapPercurso.put(cidadeOrigem, caminhos);
		}
	}

	public List<Cidade> getCidadesPercurso() {
		return cidadesPercurso;
	}

	public Map<Cidade, List<Caminho>> getMapPercurso() {
		return mapPercurso;
	}

	public List<Caminho> getAlternativas(Cidade cidade) {
		return mapPercurso.get(cidade);
	}

	public boolean isFinalizouPercurso(Formiga formiga) {
		boolean terminado = false;
		if (cidadesPercurso.size() == formiga.getCidadesVisitadas().size()) {
			if (formiga.getLocalizacaoCidadeAtual().equals(formiga.getLocalizacaoCidadeInicial())) {
				terminado = true;
			}
		}

		return terminado;
	}

	public List<Caminho> getMelhorTrajeto() {
		return melhorTrajeto;
	}

	public double getMenorDistanciaPercorrida() {
		return menorDistanciaPercorrida;
	}

	public void setMelhorTrajeto(List<Caminho> melhorTrajeto) {
		this.melhorTrajeto.clear();
		for (int i = 0; i < melhorTrajeto.size(); i++) {
			Caminho c = melhorTrajeto.get(i);
			this.melhorTrajeto.add(c);
		}
	}

	public void setMenorDistanciaPercorrida(double menorDistanciaPercorrida) {
		this.menorDistanciaPercorrida = menorDistanciaPercorrida;
	}

}
