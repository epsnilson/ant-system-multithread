package br.com.ant.system.multithread.controller;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class MultiThreadDispatched implements Callable<Formiga> {

	private Formiga				formiga;

	private PercursoController	percurso;
	private ASAlgoritmo			algoritmo;
	private BufferBlockingClass	buffer;
	private Logger				logger	= Logger.getLogger(this.getClass());

	public MultiThreadDispatched(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo, BufferBlockingClass buffer) {
		this.formiga = formiga;
		this.percurso = percurso;
		this.algoritmo = algoritmo;
		this.buffer = buffer;
	}

	@Override
	public Formiga call() throws Exception {
		FormigaMultiThreadController controller = new FormigaMultiThreadController(this.formiga, percurso, algoritmo);
		Formiga formiga = controller.call();

		try {
			buffer.addFomigaAuxUpdate(formiga);
		} catch (InterruptedException e) {
			logger.error("Houve um erro no trajeto da formiga.", e);
		}
		
		return formiga;
	}
}
