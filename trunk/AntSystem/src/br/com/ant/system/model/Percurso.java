package br.com.ant.system.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Percurso {

		  private Map<Integer, Cidade>		  cidades	 = new HashMap<Integer, Cidade>();
		  private Map<Integer, List<Distancia>> mapPercurso = new HashMap<Integer, List<Distancia>>();

		  private List<Cidade>				  melhorTrajeto;
		  private float						 totalMelhorDistancia;

		  public void addPercurso(Distancia distancia) {
					cidades.put(distancia.getCidadeOrigem().getNumber(), distancia.getCidadeOrigem());

					List<Distancia> distancias;
					if (mapPercurso.containsKey(distancia.getCidadeOrigem().getNumber())) {
							  distancias = mapPercurso.get(distancia.getCidadeOrigem().getNumber());
							  distancias.add(distancia);
					} else {
							  distancias = new ArrayList<Percurso.Distancia>();
							  distancias.add(distancia);
							  mapPercurso.put(distancia.getCidadeOrigem().getNumber(), distancias);
					}
		  }

		  public Map<Integer, Cidade> getCidades() {
					return cidades;
		  }

		  public Map<Integer, List<Distancia>> getMapPercurso() {
					return mapPercurso;
		  }

		  public List<Distancia> getAlternativas(int cidade) {
					return mapPercurso.get(cidade);
		  }

		  public boolean isAllFinished(Formiga formiga) {
					for (Iterator<Integer> it = cidades.keySet().iterator(); it.hasNext(); ) {  
							  Integer key = it.next();
							  Cidade c = cidades.get(key);

							  if (!c.isVisitado(formiga)) {
										return false;
							  }
					}

					return true;
		  }

		  public List<Cidade> getMelhorTrajeto() {
					return melhorTrajeto;
		  }

		  public float getTotalMelhorDistancia() {
					return totalMelhorDistancia;
		  }

		  public void setMelhorTrajeto(List<Cidade> melhorTrajeto) {
					this.melhorTrajeto = melhorTrajeto;
		  }

		  public void setTotalMelhorDistancia(float totalMelhorDistancia) {
					this.totalMelhorDistancia = totalMelhorDistancia;
		  }

		  public static class Distancia {
					private Cidade	cidadeOrigem;
					private Cidade	cidadeDestino;
					private Feromonio feromonio;
					private float	 distancia;

					public Distancia(Cidade cidadeOrigem, Cidade cidadeDestino, float distancia) {
							  this.cidadeOrigem = cidadeOrigem;
							  this.cidadeDestino = cidadeDestino;

							  this.distancia = distancia;
							  this.feromonio = new Feromonio();
					}

					public void setDistancia(float distancia) {
							  this.distancia = distancia;
					}

					public float getDistancia() {
							  return distancia;
					}

					public Feromonio getFeromonio() {
							  return feromonio;
					}

					public Cidade getCidadeOrigem() {
							  return cidadeOrigem;
					}

					public void setCidadeOrigem(Cidade cidadeOrigem) {
							  this.cidadeOrigem = cidadeOrigem;
					}

					public Cidade getCidadeDestino() {
							  return cidadeDestino;
					}

					public void setCidadeDestino(Cidade cidadeDestino) {
							  this.cidadeDestino = cidadeDestino;
					}

					public void setFeromonio(Feromonio feromonio) {
							  this.feromonio = feromonio;
					}

		  }

}
