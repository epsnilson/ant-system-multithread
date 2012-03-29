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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.multithread.controller.FormigaMultiThreadController;
import br.com.ant.system.multithread.controller.SimpleThreadFactory;
import br.com.ant.system.util.AntSystemUtil;

/**
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMultithread implements ColoniaFormigasActionInterface {

	private static final int			CAPACIDADE_MAXIMA_FORMIGAS	= 1000;

	private int							maximoIteracoes;
	private PercursoController			percurso;

	private AtomicBoolean				stop						= new AtomicBoolean(false);

	private ASAlgoritmo					algoritmo;

	private Set<Formiga>				formigas					= new HashSet<Formiga>();

	private ArrayBlockingQueue<Formiga>	queueFormigaExecution		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);
	private ArrayBlockingQueue<Formiga>	queueAuxServicesUpdate		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);

	private ExecutorService				executor					= Executors.newCachedThreadPool(new SimpleThreadFactory());

	private Logger						logger						= Logger.getLogger(this.getClass());

	public ColoniaFormigaMultithread(PercursoController percursoController, ASAlgoritmo algoritmo) {
		this.percurso = percursoController;
		this.algoritmo = algoritmo;

		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());
	}

	@Override
	public void action() {
		executor.execute(new ControladorGeral());
	}

	@Override
	public void setMaximoIteracoes(int maximo) {
		this.maximoIteracoes = maximo;
	}

	@Override
	public int getMaximoIteracoes() {
		return this.maximoIteracoes;
	}

	public void addFormiga(Formiga formiga) {
		try {
			formigas.add(formiga);
			queueFormigaExecution.put(formiga);
		} catch (InterruptedException e) {
			logger.error("Nao foi possivel incluir a formiga na fila de execucao.", e);
		}
	}

	public class MultiThreadDispatched implements Runnable {

		private Formiga	formiga;

		public MultiThreadDispatched(Formiga formiga) {

			this.formiga = formiga;
		}

		@Override
		public void run() {
			FormigaMultiThreadController controller = new FormigaMultiThreadController(this.formiga, percurso, algoritmo);
			Future<Formiga> threadFormiga = executor.submit(controller);

			try {
				// Recuperando o resultado da execucao da thread.
				Formiga formigaFinished = threadFormiga.get();

				queueAuxServicesUpdate.put(formigaFinished);
			} catch (Exception e) {
				logger.error("Houve um erro no trajeto da formiga.", e);
			}

		}
	}

	public class AuxServicesUpdateClass implements Runnable {
		private FeromonioController	feromonioController;
		PercursoController			percurso;

		public AuxServicesUpdateClass(ASAlgoritmo algoritmo, PercursoController percursoController) {
			feromonioController = new FeromonioController(algoritmo, percursoController);
			this.percurso = percursoController;
		}

		@Override
		public void run() {
			try {
				while (!isStop()) {
					Formiga formiga = queueAuxServicesUpdate.take();

					// Atualizando a quantidade de feromonio do trajeto da formiga.
					feromonioController.adicionarFeromonioTrajeto(formiga);

					// Coletando informacoes para estatisticas.
					EstatisticasControler.getInstance().coletarEstatisticas(formiga);

					// Limpando os dados da formiga.
					Cidade localizacaoAtual = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percurso.getCidadesPercurso().size() - 1));
					formiga.clear(localizacaoAtual);

					int qntIteracoesExecutadas = formiga.getQntIteracaoExecutadas();
					formiga.setQntIteracaoExecutadas(qntIteracoesExecutadas + 1);

					// Adicionando a formiga novamente para execucao
					queueFormigaExecution.put(formiga);
				}
			} catch (InterruptedException e) {
				logger.error("Thread de atualizacao de feromonio interropida.", e);
			}

		}
	}

	public class FormigaExection implements Runnable {
		@Override
		public void run() {
			while (!isStop()) {
				try {
					Formiga formiga = queueFormigaExecution.take();

					// executando a thread
					if (formiga.getQntIteracaoExecutadas() < maximoIteracoes) {
						executor.execute(new MultiThreadDispatched(formiga));
					}
				} catch (InterruptedException e) {
					logger.info("Thread de execucao de formigas foi interrompida.", e);
				}
			}
		}
	}

	public class ControladorGeral implements Runnable {

		@SuppressWarnings("rawtypes")
		private Future	auxServicesFuture;
		@SuppressWarnings("rawtypes")
		private Future	formigaExecutionFuture;

		@Override
		public void run() {
			// Aciona o processamento das formigas
			formigaExecutionFuture = executor.submit(new FormigaExection());

			// Acionando a thread de atualizacao de feromonio e coleta de dados estatisticos
			auxServicesFuture = executor.submit(new AuxServicesUpdateClass(algoritmo, percurso));

			while (!isAllFinished()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					logger.error("Error ", e);
				}
			}

			this.stop();

			EstatisticasControler.getInstance().loggerEstatisticas();

			executor.shutdownNow();
		}

		public void stop() {
			auxServicesFuture.cancel(true);
			formigaExecutionFuture.cancel(true);

			while (!formigaExecutionFuture.isDone() && !auxServicesFuture.isDone()) {
			}
		}
	}

	public void setStop(boolean stop) {
		this.stop.set(stop);
	}

	public boolean isStop() {
		return this.stop.get();
	}

	public boolean isAllFinished() {
		boolean finished = true;
		for (Iterator<Formiga> it = formigas.iterator(); it.hasNext();) {
			Formiga formiga = (Formiga) it.next();

			if (formiga.getQntIteracaoExecutadas() < maximoIteracoes) {
				finished = false;
				break;
			}
		}

		return finished;

	}
}
