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
package br.com.ant.system.multithread.controller;

import java.util.concurrent.CountDownLatch;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasColetorController;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

public class FormigaThreadExecutor implements Runnable {

	private Formiga				formiga;
	private PercursoController	percurso;
	private int					maxIteracoes;
	private FormigaController	formigaController;

	CountDownLatch				cdl;

	public FormigaThreadExecutor(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo, int maxIteracoes, CountDownLatch cdl) {
		this.formiga = formiga;
		this.percurso = percurso;
		this.maxIteracoes = maxIteracoes;
		this.cdl = cdl;

		this.formigaController = new FormigaController(formiga, percurso, algoritmo);
	}

	@Override
	public void run() {
		do {
			this.executarAlgoritmoBuscaCaminho();

			// Atualizando a quantidade de feromonio do trajeto da formiga.
			formigaController.adicionarFeromonioTrajeto();

			// Coletando informacoes para estatisticas.
			EstatisticasColetorController.getEstatisticaColetor().coletarEstatisticas(formiga, formiga.getQntIteracaoExecutadas());

			// Limpando os dados da formiga.
			Cidade localizacaoAtual = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percurso.getCidadesPercurso().size() - 1));
			// Cidade localizacaoAtual = formiga.getLocalizacaoCidadeInicial();
			formiga.clear(localizacaoAtual);

			int qntIteracoesExecutadas = formiga.getQntIteracaoExecutadas();
			formiga.setQntIteracaoExecutadas(qntIteracoesExecutadas + 1);
		} while (formiga.getQntIteracaoExecutadas() < maxIteracoes);

		// Diminuindo o contador de threads. (CountDownLatch)
		cdl.countDown();
	}

	public void executarAlgoritmoBuscaCaminho() {
		// Setando o tempo inicial
		formigaController.getFormiga().setTempoInicial(System.currentTimeMillis());

		do {
			// Percorrendo as cidades.
			formigaController.escolherPercurso();
		} while (!percurso.isFinalizouPercurso(formigaController.getFormiga()));

		// Setando o tempo Final
		formigaController.getFormiga().setTempoFinal(System.currentTimeMillis());
	}
}
