/**
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ant.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;
import br.com.ant.system.util.NumberUtil;

public class FormigaController {
	private static final int	MAXIMO_INTERACOES	= 50;

	private List<Formiga>		formigas;
	private PercursoController	percursoController;
	private ASAlgoritmo			algoritmo;

	private Logger				logger				= Logger.getLogger(this.getClass());

	public FormigaController(List<Formiga> formigas, PercursoController percurso, ASAlgoritmo algoritmo) {
		this.formigas = formigas;
		this.percursoController = percurso;
		this.algoritmo = algoritmo;

		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());

	}

	/**
	 * Inicia o execução do algoritmo
	 */
	public void executarAlgoritmo() {
		logger.info("Iniciando a execução do Algoritmo...");
		logger.info("Maximo Interacoes: " + MAXIMO_INTERACOES);
		logger.info("Quantidade de formigas: " + formigas.size());
		logger.info("Quantidade de cidades: " + percursoController.getCidadesPercurso());

		for (int i = 0; i < MAXIMO_INTERACOES; i++) {
			logger.info("************** Iteracao N. " + i + " ******************");
			for (Formiga formiga : formigas) {
				logger.info("Formiga: " + formiga.getId());
				// Recupera as alternativas para o trajeto de cada formiga
				List<Caminho> alternativas = percursoController.getAlternativas(formiga.getLocalizacaoCidadeAtual());

				// Recupera o melhor trajeto que a formiga pode escolher
				Caminho caminhoEscolhido = this.escolherPercurso(formiga, alternativas);

				// atualiza a localização atual da formiga e o estado da cidade.
				formiga.addCaminho(caminhoEscolhido);

				// Verifica se a formiga ja percorreu todas as cidades.
				if (percursoController.isFinalizouPercurso(formiga)) {
					// Adiciona Feromonio ao trajeto percorrido pela formiga
					this.adicionarFeromonioTrajeto(formiga);
					this.clearFormiga(formiga);

				}
			}
		}

		logger.info("Melhor Trajeto: " + Arrays.toString(percursoController.getMelhorTrajeto().toArray()));
		logger.info("Menor caminho: " + NumberUtil.getInstance().doubleToString(percursoController.getMenorDistanciaPercorrida()));

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
		logger.debug("Limpando as informações da formiga");

		Cidade localizacaoAtual = percursoController.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percursoController.getCidadesPercurso().size() - 1));
		logger.debug("Nova Localizacao Inicial: " + localizacaoAtual.getNome());

		formiga.clear(localizacaoAtual);
	}
}
