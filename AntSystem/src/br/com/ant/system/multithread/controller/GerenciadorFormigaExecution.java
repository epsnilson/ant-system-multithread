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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class GerenciadorFormigaExecution implements Runnable {

	@SuppressWarnings("rawtypes")
	private Future								formigaExecutionFuture;

	private ConcurrentHashMap<Integer, Formiga>	formigasDisponiveis	= new ConcurrentHashMap<Integer, Formiga>();

	private ExecutorService						executor			= Executors.newCachedThreadPool(new FormigaExecutionThreadFactory());

	private ASAlgoritmo							algoritmo;
	private PercursoController					percurso;
	private AtomicInteger						maximoIteracoes		= new AtomicInteger();

	private CountDownLatch						downLatch;

	public GerenciadorFormigaExecution(ASAlgoritmo algoritmo, PercursoController percurso) {
		this.algoritmo = algoritmo;
		this.percurso = percurso;
	}

	@Override
	public void run() {

		try {
			downLatch = new CountDownLatch(formigasDisponiveis.size());

			// Aciona o processamento das formigas
			formigaExecutionFuture = executor.submit(new FormigaDispatcher(formigasDisponiveis));

			downLatch.await();
			this.stop();

			executor.shutdownNow();
		} catch (InterruptedException e) {
			throw new RuntimeException("Houve um erro na execucao do programa.", e);
		}
	}

	public void setPercurso(PercursoController percurso) {
		this.percurso = percurso;
	}

	public void setFormigasDisponiveis(Collection<Formiga> formigasDisponiveis) {
		for (Formiga formiga : formigasDisponiveis) {
			this.formigasDisponiveis.putIfAbsent(formiga.getId(), formiga);
		}
	}

	public void stop() {
		formigaExecutionFuture.cancel(true);
	}

	public class FormigaDispatcher implements Runnable {

		ConcurrentHashMap<Integer, Formiga>	formigas;

		public FormigaDispatcher(ConcurrentHashMap<Integer, Formiga> formigas) {
			this.formigas = formigas;
		}

		@Override
		public void run() {
			for (Formiga formiga : formigas.values()) {
				try {
					// executando a thread
					executor.submit(new FormigaThreadExecutor(formiga, percurso, algoritmo, maximoIteracoes.get(), downLatch));
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
