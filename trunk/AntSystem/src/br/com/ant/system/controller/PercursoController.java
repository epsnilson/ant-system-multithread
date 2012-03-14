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

		  private List<Cidade>			   cidades	   = new ArrayList<Cidade>();
		  private Map<Cidade, List<Caminho>> mapPercurso   = new HashMap<Cidade, List<Caminho>>();

		  private List<Caminho>			  melhorTrajeto = new LinkedList<Caminho>();
		  private float					  totalMelhorDistancia;

		  public void addPercurso(Caminho distancia) {
					if (!cidades.contains(distancia.getCidadeOrigem())) {
							  cidades.add(distancia.getCidadeOrigem());
					}

					List<Caminho> distancias;
					if (mapPercurso.containsKey(distancia.getCidadeOrigem())) {
							  distancias = mapPercurso.get(distancia.getCidadeOrigem());
							  distancias.add(distancia);
					} else {
							  distancias = new ArrayList<Caminho>();
							  distancias.add(distancia);
							  mapPercurso.put(distancia.getCidadeOrigem(), distancias);
					}
		  }

		  public List<Cidade> getCidades() {
					return cidades;
		  }

		  public Map<Cidade, List<Caminho>> getMapPercurso() {
					return mapPercurso;
		  }

		  public List<Caminho> getAlternativas(Cidade cidade) {
					return mapPercurso.get(cidade);
		  }

		  public boolean isAllFinished(Formiga formiga) {
					if (cidades.size() == formiga.getCidadesVisitadas().size()) {
							  return true;
					} else {
							  return false;
					}
		  }

		  public List<Caminho> getMelhorTrajeto() {
					return melhorTrajeto;
		  }

		  public float getTotalMelhorDistancia() {
					return totalMelhorDistancia;
		  }

		  public void setMelhorTrajeto(List<Caminho> melhorTrajeto) {
					this.melhorTrajeto.clear();
					for (int i = 0; i < melhorTrajeto.size(); i++) {
							  Caminho c = melhorTrajeto.get(i);
							  this.melhorTrajeto.add(c);
					}
		  }

		  public void setTotalMelhorDistancia(float totalMelhorDistancia) {
					this.totalMelhorDistancia = totalMelhorDistancia;
		  }

}
