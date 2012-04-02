package br.com.ant.system.multithread.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class ControladorGeral implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future				auxServicesFuture;

	@SuppressWarnings("rawtypes")
	private Future				formigaExecutionFuture;

	private Set<Formiga>		formigasDisponiveis	= new HashSet<Formiga>();
	private Set<Formiga>		formigasFinalizadas	= new HashSet<Formiga>();

	private ExecutorService		executor			= Executors.newFixedThreadPool(3, new SimpleThreadFactory());

	private Logger				logger				= Logger.getLogger(this.getClass());

	private ASAlgoritmo			algoritmo;
	private PercursoController	percurso;
	private BufferBlockingClass	buffer;
	private AtomicInteger		maximoIteracoes		= new AtomicInteger();

	private Lock				lock				= new ReentrantLock();
	private Condition			canContinue			= lock.newCondition();

	public ControladorGeral(ASAlgoritmo algoritmo, PercursoController percurso) {
		this.algoritmo = algoritmo;
		this.percurso = percurso;

		this.buffer = new BufferBlockingClass();

	}

	public void setPercurso(PercursoController percurso) {
		this.percurso = percurso;
	}

	@Override
	public void run() {
		// Aciona o processamento das formigas
		formigaExecutionFuture = executor.submit(new FormigaExecution());

		// Acionando a thread de atualizacao de feromonio e coleta de dados estatisticos
		auxServicesFuture = executor.submit(new AuxServicesUpdateClass(algoritmo, percurso, buffer));

		this.waitForAllFinished();

		this.stop();

		EstatisticasControler.getInstance().loggerEstatisticas();

		executor.shutdownNow();
	}

	public void stop() {
		auxServicesFuture.cancel(true);
		formigaExecutionFuture.cancel(true);

	}

	public void addFormiga(Formiga formiga) {
		try {
			buffer.addFomigaExecution(formiga);
		} catch (InterruptedException e) {
			logger.info("Nao foi possivel incluir a formiga na fila de execucao.");
		}
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

		@Override
		public void run() {
			while (true) {
				try {
					Formiga formiga = buffer.takeFormigaExecution();

					try {
						lock.lock();
						formigasDisponiveis.add(formiga);

						// executando a thread
						if (formiga.getQntIteracaoExecutadas() < maximoIteracoes.get()) {
							executor.submit(new MultiThreadDispatched(formiga, percurso, algoritmo, buffer));
						} else {
							formigasFinalizadas.add(formiga);

							if (formigasDisponiveis.size() == formigasFinalizadas.size()) {
								logger.info("Todas as formigas finalizaram o percurso.");
								canContinue.signal();
							}
						}
					} finally {
						lock.unlock();
					}

				} catch (InterruptedException e) {
					logger.info("Thread de execucao de formigas foi interrompida.");
				}
			}
		}
	}

	public void setMaximoIteracoes(int maximoIteracoes) {
		this.maximoIteracoes.set(maximoIteracoes);
	}

	public int getMaximoIteracoes() {
		return maximoIteracoes.get();
	}

	public void clear() {
		formigasDisponiveis.clear();
		formigasFinalizadas.clear();
	}
}
