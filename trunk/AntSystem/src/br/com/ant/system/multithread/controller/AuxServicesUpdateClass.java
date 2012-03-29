package br.com.ant.system.multithread.controller;

import org.apache.log4j.Logger;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

public class AuxServicesUpdateClass implements Runnable {
	private FeromonioController	feromonioController;
	PercursoController			percurso;
	BufferBlockingClass			buffer;

	Logger						logger	= Logger.getLogger(this.getClass());

	public AuxServicesUpdateClass(ASAlgoritmo algoritmo, PercursoController percursoController, BufferBlockingClass buffer) {
		feromonioController = new FeromonioController(algoritmo, percursoController);
		this.percurso = percursoController;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Formiga formiga = buffer.takeFormigaAuxUpdate();

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
				buffer.addFomigaExecution(formiga);
			}
		} catch (InterruptedException e) {
			logger.error("Thread de atualizacao de feromonio interropida.", e);
		}

	}
}
