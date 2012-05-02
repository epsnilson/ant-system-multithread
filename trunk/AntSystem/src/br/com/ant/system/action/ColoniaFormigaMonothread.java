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

import java.util.ArrayList;
import java.util.List;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasColetorController;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

/**
 * Classe que executa o algoritmo em monothread.
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMonothread implements ColoniaFormigasActionInterface {

	private int						maximoIteracoes;
	private List<FormigaController>	formigas	= new ArrayList<FormigaController>();
	private PercursoController		percursoController;

	public ColoniaFormigaMonothread(List<Formiga> formigas, ASAlgoritmo algoritmo, PercursoController percurso) {
		this.percursoController = percurso;

		for (Formiga formiga : formigas) {
			this.formigas.add(new FormigaController(formiga, percursoController, algoritmo));
		}

		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());
	}

	/**
	 * Executa o algoritmo.
	 */
	public void action() {
		for (int i = 0; i < maximoIteracoes; i++) {
			for (FormigaController controller : formigas) {
				AntSystemUtil.getIntance().logar("Formiga: " + controller.getFormiga().getId());

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
				controller.adicionarFeromonioTrajeto();

				// Coletando dados estatisticos do trajeto da formiga.
				EstatisticasColetorController.getEstatisticaColetor().coletarEstatisticas(controller.getFormiga(), i);

				// Limpando os dados da formiga
				controller.clearFormiga();
			}
		}
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
