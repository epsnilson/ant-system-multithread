package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.model.Percurso;
import br.com.ant.system.model.Percurso.Distancia;

public class AntController {
		  private static final int   MAXIMO_INTERACOES = 50;
		  private static final float FEROMONIO_INICIAL = 0.0F;
		  private static final float MINIMO_FEROMONIO  = 2F;

		  private List<Formiga>	  formigas;
		  private Percurso		   percurso;

		  public AntController(List<Formiga> formigas, Percurso percurso) {
					this.formigas = formigas;
					this.percurso = percurso;
		  }

		  /**
		   * Inicia o execução do algoritmo
		   */
		  public void inicializar() {

					for (int i = 0; i < MAXIMO_INTERACOES; i++) {
							  for (Formiga formiga : formigas) {
										// Recupera as alternativas para o trajeto de cada formiga
										List<Distancia> alternativas = percurso.getAlternativas(formiga.getLocalizacaoAtual().getNumber());

										// Recupera o melhor trajeto que a formiga pode escolher
										Cidade caminhoEscolhido = escolherPercurso(formiga, alternativas);

										// atualiza a localização atual da formiga e o estado da cidade.
										formiga.setLocalizacaoAtual(caminhoEscolhido);
										caminhoEscolhido.setVisitado(formiga);

										// Verifica se a formiga ja percorreu todas as cidades.
										if (percurso.isAllFinished(formiga)) {
												  // Adiciona Feromonio ao trajeto percorrido pela formiga
												  adicionarFeromonioTrajeto(formiga);
										}
							  }
					}

		  }

		  private void adicionarFeromonioTrajeto(Formiga formiga) {
					// Recupera o trajeto efetuado pela formiga
					List<Cidade> trajetosFormigas = formiga.getTrajetoCidades();
					for (int i = 0; i < trajetosFormigas.size(); i++) {
							  Cidade c = trajetosFormigas.get(i);
							  // Recupera as distancias dos percursos
							  List<Distancia> distancias = percurso.getMapPercurso().get(c.getNumber());

							  float distancia = 0;
							  forDistancias: for (Distancia d : distancias) {
										if (trajetosFormigas.get(i + 1) != null && d.getCidadeDestino().equals(trajetosFormigas.get(i + 1))) {
												  distancia += d.getDistancia();
												  // Atualiza o feromonio
												  d.getFeromonio().addFeromonio();

												  // Atualiza as estatiscas dos melhores caminhos
												  if (percurso.getTotalMelhorDistancia() > distancia || percurso.getTotalMelhorDistancia() == 0) {
															percurso.setTotalMelhorDistancia(distancia);
															percurso.setMelhorTrajeto(trajetosFormigas);
												  }

												  break forDistancias;
										}
							  }
					}
		  }

		  public Cidade escolherPercurso(Formiga formiga, List<Distancia> alternativas) {
					List<Integer> cidadesDisponiveis = new ArrayList<Integer>();
					List<Integer> cidades = new ArrayList<Integer>();
					float maiorFeromonio = FEROMONIO_INICIAL;
					Distancia distanciaMaiorFeromonio = null;

					for (Distancia d : alternativas) {
							  cidades.add(d.getCidadeDestino().getNumber());

							  if (!d.getCidadeDestino().isVisitado(formiga)) {
										cidadesDisponiveis.add(d.getCidadeDestino().getNumber());

										if (d.getFeromonio().getFeromonio() >= MINIMO_FEROMONIO && d.getFeromonio().getFeromonio() > maiorFeromonio) {
												  maiorFeromonio = d.getFeromonio().getFeromonio();
												  distanciaMaiorFeromonio = d;
										}

							  }
					}

					Cidade cidadeEscolhida;
					if (distanciaMaiorFeromonio == null) {
							  int caminhoEscolhido;
							  if (!cidadesDisponiveis.isEmpty()) {
										caminhoEscolhido = cidadesDisponiveis.get(0 + (int) ((cidadesDisponiveis.size() - 0) * Math.random()));
							  } else {
										caminhoEscolhido = cidades.get(0 + (int) ((cidades.size() - 0) * Math.random()));
							  }

							  cidadeEscolhida = percurso.getCidades().get(caminhoEscolhido);

					} else {
							  cidadeEscolhida = distanciaMaiorFeromonio.getCidadeDestino();
					}

					return cidadeEscolhida;
		  }

		  public static void main(String[] args) {
					System.out.println(0 + (int) ((10 - 0) * Math.random()));
		  }
}
