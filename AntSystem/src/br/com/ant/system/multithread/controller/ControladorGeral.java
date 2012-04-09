package br.com.ant.system.multithread.controller;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class ControladorGeral implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future								formigaExecutionFuture;

	private ConcurrentHashMap<Integer, Formiga>	formigasDisponiveis	= new ConcurrentHashMap<Integer, Formiga>();

	private ExecutorService						executor			= Executors.newCachedThreadPool(new FormigaExecutionThreadFactory());

	private Logger								logger				= Logger.getLogger(this.getClass());

	private ASAlgoritmo							algoritmo;
	private PercursoController					percurso;
	private AtomicInteger						maximoIteracoes		= new AtomicInteger();

	private CountDownLatch						downLatch;

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

		try {
			downLatch = new CountDownLatch(formigasDisponiveis.size());

			// Aciona o processamento das formigas
			formigaExecutionFuture = executor.submit(new FormigaExecution(formigasDisponiveis));

			downLatch.await();
			this.stop();

			executor.shutdownNow();
		} catch (InterruptedException e) {
			throw new RuntimeException("Houve um erro na execucao do programa.", e);
		}
	}

	public void stop() {
		formigaExecutionFuture.cancel(true);
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
					executor.submit(new MultiThreadDispatched(formiga, percurso, algoritmo, maximoIteracoes.get(), downLatch));
				} catch (Exception e) {
					e.printStackTrace();
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
}
