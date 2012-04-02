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
package br.com.ant.system.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

/**
 * Classe que executa o algoritmo em monothread.
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMonothread implements ColoniaFormigasActionInterface {

	private int						maximoIteracoes;
	private List<FormigaController>	formigas		= new ArrayList<FormigaController>();
	private Logger					logger			= Logger.getLogger(this.getClass());
	private PercursoController		percursoController;
	private FeromonioController		feromonioController;

	private SimpleDateFormat		horaFormat		= new SimpleDateFormat("HH:mm:ss:SSS");
	private SimpleDateFormat		minutoFormat	= new SimpleDateFormat("mm:ss:SSS");

	public ColoniaFormigaMonothread(List<Formiga> formigas, ASAlgoritmo algoritmo, PercursoController percurso) {
		this.percursoController = percurso;
		this.feromonioController = new FeromonioController(algoritmo, percursoController);

		for (Formiga formiga : formigas) {
			this.formigas.add(new FormigaController(formiga, percursoController, algoritmo));
		}

		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());
	}

	/**
	 * Executa o algoritmo.
	 */
	public void action() {
		logger.info("Iniciando a execução do Algoritmo...");
		logger.info("Maximo Interacoes: " + maximoIteracoes);
		logger.info("Quantidade de formigas: " + formigas.size());
		logger.info("Quantidade de cidades: " + percursoController.getCidadesPercurso().size());

		EstatisticasControler.getInstance().setNumeroIteracoes(maximoIteracoes);
		EstatisticasControler.getInstance().setHorarioInicial(System.currentTimeMillis());
		logger.info("Horario Inicial: " + horaFormat.format(EstatisticasControler.getInstance().getHorarioInicial()));

		for (int i = 0; i < maximoIteracoes; i++) {
			logger.info("************** Iteracao N. " + i + " ******************");
			for (FormigaController controller : formigas) {
				logger.info("Formiga: " + controller.getFormiga().getId());

				// Setando o tempo inicial
				controller.getFormiga().setTempoInicial(System.currentTimeMillis());

				/*
				 * Fica em loop ate a formiga finalizar o caminho completo..
				 */
				do {
					// Recupera o melhor trajeto que a formiga pode escolher
					controller.escolherPercurso();
				} while (!percursoController.isFinalizouPercurso(controller.getFormiga()));

				// Setando o tempo final do percurso
				controller.getFormiga().setTempoFinal(System.currentTimeMillis());

				// Adiciona Feromonio ao trajeto percorrido pela formiga
				feromonioController.adicionarFeromonioTrajeto(controller.getFormiga());

				// Coletando dados estatisticos do trajeto da formiga.
				EstatisticasControler.getInstance().coletarEstatisticas(controller.getFormiga());

				// Limpando os dados da formiga
				controller.clearFormiga();
			}
		}

		EstatisticasControler.getInstance().setHorarioFinal(System.currentTimeMillis());

		// Exibindo dados da estatisticos.
		EstatisticasControler.getInstance().loggerEstatisticas();

		logger.info("Horario Final: " + horaFormat.format(EstatisticasControler.getInstance().getHorarioFinal()));
		logger.info("Tempo de execucao: " + minutoFormat.format(EstatisticasControler.getInstance().getTempoExecucao()));
	}

	@Override
	public void setMaximoIteracoes(int maximoIteracoes) {
		this.maximoIteracoes = maximoIteracoes;
	}

	@Override
	public int getMaximoIteracoes() {
		return maximoIteracoes;
	}
}
