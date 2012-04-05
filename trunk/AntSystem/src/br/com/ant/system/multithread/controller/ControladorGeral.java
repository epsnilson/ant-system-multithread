package br.com.ant.system.multithread.controller;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class ControladorGeral implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future								formigaExecutionFuture;

	private ConcurrentHashMap<Integer, Formiga>	formigasDisponiveis	= new ConcurrentHashMap<Integer, Formiga>();
	private ConcurrentHashMap<Integer, Formiga>	formigasFinalizadas	= new ConcurrentHashMap<Integer, Formiga>();

	private ExecutorService						executor			= Executors.newCachedThreadPool(new FormigaExecutionThreadFactory());
	private ExecutorService						executorWait		= Executors.newCachedThreadPool(new FormigaWaitThreadFactory());

	private Logger								logger				= Logger.getLogger(this.getClass());

	private ASAlgoritmo							algoritmo;
	private PercursoController					percurso;
	private AtomicInteger						maximoIteracoes		= new AtomicInteger();

	private Lock								lock				= new ReentrantLock();
	private Condition							canContinue			= lock.newCondition();

	public ControladorGeral(ASAlgoritmo algoritmo, PercursoController percurso) {
		this.algoritmo = algoritmo;
		this.percurso = percurso;
	}

	public void setPercurso(PercursoController percurso) {
		this.percurso = percurso;
	}

	public void setFormigasDisponiveis(Collection<Formiga> formigasDisponiveis) {
		for (Formiga formiga : formigasDisponiveis) {
			this.formigasDisponiveis.putIfAbsent(formiga.getId(), formiga);

		}
	}

	@Override
	public void run() {
		// Aciona o processamento das formigas
		formigaExecutionFuture = executor.submit(new FormigaExecution(formigasDisponiveis));

		this.waitForAllFinished();
		this.stop();

		executor.shutdownNow();
		executorWait.shutdownNow();
	}

	public void stop() {
		formigaExecutionFuture.cancel(true);
	}

	public void waitForAllFinished() {
		try {
			lock.lock();
			canContinue.await();
		} catch (InterruptedException e) {
		} finally {
			lock.unlock();
		}

	}

	public class FormigaExecution implements Runnable {

		ConcurrentHashMap<Integer, Formiga>	formigas;

		public FormigaExecution(ConcurrentHashMap<Integer, Formiga> formigas) {
			this.formigas = formigas;
		}

		@Override
		public void run() {
			for (Formiga formiga : formigas.values()) {
				try {
					// executando a thread
					Future<Formiga> formigaFuture = executor.submit(new MultiThreadDispatched(formiga, percurso, algoritmo, maximoIteracoes.get()));

					FormigaWait formigaWait = new FormigaWait(formigaFuture);
					executorWait.execute(formigaWait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class FormigaWait implements Runnable {

		Future<Formiga>	formigaFuture;

		public FormigaWait(Future<Formiga> formigaFuture) {
			this.formigaFuture = formigaFuture;
		}

		@Override
		public void run() {
			try {

				Formiga formiga = formigaFuture.get();
				formigasFinalizadas.putIfAbsent(formiga.getId(), formiga);

				logger.info("Disponiveis: " + formigasDisponiveis.size());
				logger.info("Finalizadas: " + formigasFinalizadas.size());
				if (formigasDisponiveis.size() == formigasFinalizadas.size()) {
					try {
						lock.lock();
						logger.info("Todas as formigas finalizaram o percurso.");
						canContinue.signal();
					} finally {
						lock.unlock();
					}
				}
			} catch (Exception e) {
				logger.error("Ocorreu um erro na execucao do algoritmo.", e);
			}
		}
	}

	public void setMaximoIteracoes(int maximoIteracoes) {
		this.maximoIteracoes.set(maximoIteracoes);
	}

	public int getMaximoIteracoes() {
		return maximoIteracoes.get();
	}
}
