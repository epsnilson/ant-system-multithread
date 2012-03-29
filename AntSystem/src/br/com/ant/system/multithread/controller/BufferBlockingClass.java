package br.com.ant.system.multithread.controller;

import java.util.concurrent.ArrayBlockingQueue;

import br.com.ant.system.model.Formiga;

public class BufferBlockingClass {

	private static final int			CAPACIDADE_MAXIMA_FORMIGAS	= 1000;

	private ArrayBlockingQueue<Formiga>	queueFormigaExecution		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);
	private ArrayBlockingQueue<Formiga>	queueAuxServicesUpdate		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);

	public void addFomigaExecution(Formiga formiga) throws InterruptedException {
		queueFormigaExecution.put(formiga);
	}

	public void addFomigaAuxUpdate(Formiga formiga) throws InterruptedException {
		queueAuxServicesUpdate.put(formiga);
	}

	public Formiga takeFormigaExecution() throws InterruptedException {
		return queueFormigaExecution.take();
	}

	public Formiga takeFormigaAuxUpdate() throws InterruptedException {
		return queueAuxServicesUpdate.take();
	}

}
