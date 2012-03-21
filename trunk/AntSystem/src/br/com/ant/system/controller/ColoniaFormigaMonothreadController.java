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

import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;

/**
 * Classe que executa o algoritmo em monothread.
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMonothreadController {

	private static final int		MAXIMO_INTERACOES	= 1000;
	private List<FormigaController>	formigas;
	private Logger					logger				= Logger.getLogger(this.getClass());
	private PercursoController		percursoController;
	private FeromonioController		feromonioController;

	public ColoniaFormigaMonothreadController(List<FormigaController> formigas, ASAlgoritmo algoritmo, PercursoController controller) {
		this.formigas = formigas;
		this.percursoController = controller;
		this.feromonioController = new FeromonioController(algoritmo, percursoController);

		algoritmo.inicializarFeromonio(controller.getCaminhosDisponiveis(), controller.getCidadesPercurso().size());
	}

	public void run() {
		logger.info("Iniciando a execu��o do Algoritmo...");
		logger.info("Maximo Interacoes: " + MAXIMO_INTERACOES);
		logger.info("Quantidade de formigas: " + formigas.size());
		logger.info("Quantidade de cidades: " + percursoController.getCidadesPercurso());

		for (int i = 0; i < MAXIMO_INTERACOES; i++) {
			logger.info("************** Iteracao N. " + i + " ******************");
			for (FormigaController controller : formigas) {
				logger.info("Formiga: " + controller.getFormiga().getId());

				// Setando o tempo inicial
				if (controller.getFormiga().getTempoInicial() == 0) {
					controller.getFormiga().setTempoInicial(System.currentTimeMillis());
				}

				// Recupera o melhor trajeto que a formiga pode escolher
				controller.escolherPercurso();

				// Verifica se a formiga ja percorreu todas as cidades.
				if (percursoController.isFinalizouPercurso(controller.getFormiga())) {
					// Setando o tempo final do percurso
					controller.getFormiga().setTempoFinal(System.currentTimeMillis());

					// Adiciona Feromonio ao trajeto percorrido pela formiga
					feromonioController.adicionarFeromonioTrajeto(controller.getFormiga());

					EstatisticasControler.getInstance().coletarEstatisticas(controller.getFormiga());
					// Limpando os dados da formiga
					controller.clearFormiga();

				}
			}
		}

		logger.info(EstatisticasControler.getInstance().printEstatisticas());

	}

}
