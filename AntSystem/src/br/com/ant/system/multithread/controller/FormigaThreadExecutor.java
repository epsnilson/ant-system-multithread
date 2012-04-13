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
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

public class FormigaThreadExecutor implements Runnable {

	private Formiga				formiga;

	private PercursoController	percurso;
	private ASAlgoritmo			algoritmo;

	private int					maxIteracoes;

	FeromonioController			feromonioController;

	CountDownLatch				cdl;

	public FormigaThreadExecutor(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo, int maxIteracoes, CountDownLatch cdl) {
		this.formiga = formiga;
		this.percurso = percurso;
		this.algoritmo = algoritmo;
		this.maxIteracoes = maxIteracoes;
		this.cdl = cdl;

		feromonioController = new FeromonioController(algoritmo, percurso);
	}

	@Override
	public void run() {
		FormigaMultiThreadController controller = new FormigaMultiThreadController(this.formiga, percurso, algoritmo);

		do {
			Formiga formiga = controller.call();

			// Atualizando a quantidade de feromonio do trajeto da formiga.
			feromonioController.adicionarFeromonioTrajeto(formiga);

			// Coletando informacoes para estatisticas.
			EstatisticasControler.getInstance().coletarEstatisticas(formiga);

			// Limpando os dados da formiga.
			Cidade localizacaoAtual = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percurso.getCidadesPercurso().size() - 1));
			formiga.clear(localizacaoAtual);

			int qntIteracoesExecutadas = formiga.getQntIteracaoExecutadas();
			formiga.setQntIteracaoExecutadas(qntIteracoesExecutadas + 1);
		} while (formiga.getQntIteracaoExecutadas() < maxIteracoes);

		// Diminuindo o contador de threads.
		cdl.countDown();
	}
}
