package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.ASAlgoritmo;
import br.com.ant.system.util.AntSystemUtil;

public class FormigaController {
	private static final int	MAXIMO_INTERACOES	= 50;

	private List<Formiga>		formigas;
	private PercursoController	percursoController;
	private ASAlgoritmo			algoritmo;

	public FormigaController(List<Formiga> formigas, PercursoController percurso, ASAlgoritmo algoritmo) {
		this.formigas = formigas;
		this.percursoController = percurso;
		this.algoritmo = algoritmo;
	}

	/**
	 * Inicia o execução do algoritmo
	 */
	public void executarAlgoritmo() {
		System.out.println("Maximo Interacoes: " + MAXIMO_INTERACOES);
		for (int i = 0; i < MAXIMO_INTERACOES; i++) {
			for (Formiga formiga : formigas) {
				// Recupera as alternativas para o trajeto de cada formiga
				List<Caminho> alternativas = percursoController.getAlternativas(formiga.getLocalizacaoCidadeAtual());

				// Recupera o melhor trajeto que a formiga pode escolher
				Caminho caminhoEscolhido = escolherPercurso(formiga, alternativas);

				// atualiza a localização atual da formiga e o estado da cidade.
				formiga.addCaminho(caminhoEscolhido);

				// Verifica se a formiga ja percorreu todas as cidades.
				if (percursoController.isFinalizouPercurso(formiga)) {
					// Adiciona Feromonio ao trajeto percorrido pela formiga
					adicionarFeromonioTrajeto(formiga);
					System.out.println("Chegou ao destino: ");
					System.out.println(formiga.toString());
					this.clearFormiga(formiga);

				}
			}
		}

		System.out.println("Melhor Trajeto: ");
		System.out.println(Arrays.toString(percursoController.getMelhorTrajeto().toArray()));
		System.out.println("Menor caminho: " + percursoController.getMenorDistanciaPercorrida());

	}

	private void adicionarFeromonioTrajeto(Formiga formiga) {
		// Recupera o trajeto efetuado pela formiga
		List<Caminho> trajetosFormigas = formiga.getTrajetoCidades();

		for (Caminho c : trajetosFormigas) {
			// Recupera a nova quantidade de feromonio atualizado.
			double novaQntFeromonio = algoritmo.atualizarFeromonio(c.getFeromonio().getQntFeromonio(), formiga.getDistanciaPercorrida());
			c.getFeromonio().setQntFeromonio(novaQntFeromonio);
		}

		// TODO: Atualiza as estatiscas dos melhores caminhos
		if (percursoController.getMenorDistanciaPercorrida() == 0 || percursoController.getMenorDistanciaPercorrida() > formiga.getDistanciaPercorrida()) {
			percursoController.setMenorDistanciaPercorrida(formiga.getDistanciaPercorrida());
			percursoController.setMelhorTrajeto(formiga.getTrajetoCidades());
		}
	}

	public Caminho escolherPercurso(Formiga formiga, List<Caminho> todasAlternativas) {
		List<Caminho> caminhosDisponiveis = new ArrayList<Caminho>();

		for (Caminho c : todasAlternativas) {
			if (!formiga.isCidadeVisitada(c.getCidadeDestino())) {
				caminhosDisponiveis.add(c);
			}
		}

		Caminho caminhoEscolhido;
		/*
		 * Ira escolher um caminho de uma cidade ainda nao visitada, ou sera escolhida uma cidade ja
		 * visitada se caso já tiver visitado todas as cidades.
		 */
		if (!caminhosDisponiveis.isEmpty()) {
			caminhoEscolhido = algoritmo.escolherCaminho(caminhosDisponiveis);
		} else {
			caminhoEscolhido = algoritmo.escolherCaminho(todasAlternativas);
		}

		return caminhoEscolhido;
	}

	private void clearFormiga(Formiga formiga) {
		Cidade localizacaoAtual = percursoController.getCidades().get(AntSystemUtil.getIntance().getAleatorio(0, percursoController.getCidades().size() - 1));
		formiga.clear(localizacaoAtual);
	}
}
