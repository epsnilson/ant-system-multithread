package br.com.ant.system.action;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.multithread.controller.FormigaMultiThreadController;
import br.com.ant.system.util.AntSystemUtil;

public class ColoniaFormigaMultithread implements ColoniaFormigasActionInterface {

	private static final int			CAPACIDADE_MAXIMA_FORMIGAS	= 1000;
	private int							maximoIteracoes;
	private PercursoController			percurso;

	private ASAlgoritmo					algoritmo;

	private ArrayBlockingQueue<Formiga>	queueExecucaoFormiga		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);
	private ArrayBlockingQueue<Formiga>	queueFeromonioUpdate		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);

	private ExecutorService				executor					= Executors.newCachedThreadPool();

	private Logger						logger						= Logger.getLogger(this.getClass());

	public ColoniaFormigaMultithread(PercursoController percursoController, ASAlgoritmo algoritmo) {
		this.percurso = percursoController;
		this.algoritmo = algoritmo;

		// Acionando a thread de atualizacao de feromonio
		executor.execute(new FeromonioUpdateClass(algoritmo, percursoController));
	}

	@Override
	public void action() {
		while (true) {
			try {
				Formiga formiga = queueExecucaoFormiga.take();

				// executando a thread
				executor.execute(new MultiThreadDispatched(formiga));
			} catch (InterruptedException e) {
				logger.info("Nao foi possivel retirar a formiga da fila de execucao.", e);
			}
		}
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
			queueExecucaoFormiga.put(formiga);
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
				Formiga formigaFinished = threadFormiga.get();
				queueFeromonioUpdate.put(formigaFinished);
			} catch (Exception e) {
				logger.error("Houve um erro no trajeto da formiga.", e);
			}

		}
	}

	public class FeromonioUpdateClass implements Runnable {
		private FeromonioController	feromonioController;
		PercursoController			percurso;

		public FeromonioUpdateClass(ASAlgoritmo algoritmo, PercursoController percursoController) {
			feromonioController = new FeromonioController(algoritmo, percursoController);
			this.percurso = percursoController;
		}

		@Override
		public void run() {
			try {
				Formiga formiga = queueFeromonioUpdate.take();

				// Atualizando a quantidade de feromonio do trajeto da formiga.
				feromonioController.adicionarFeromonioTrajeto(formiga);

				Cidade localizacaoAtual = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(0, percurso.getCidadesPercurso().size() - 1));

				// Limpando os dados da formiga.
				formiga.clear(localizacaoAtual);

				// Adicionando a formiga novamente para execucao
				queueExecucaoFormiga.put(formiga);
			} catch (Exception e) {
				logger.error("Erro ao atualizar o feromonio do caminho percorrido", e);
			}

		}
	}

}
