package br.com.ant.system.multithread.controller;

import java.util.concurrent.Callable;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.FeromonioController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

public class MultiThreadDispatched implements Callable<Formiga> {

	private Formiga				formiga;

	private PercursoController	percurso;
	private ASAlgoritmo			algoritmo;

	private int					maxIteracoes;

	FeromonioController			feromonioController;

	public MultiThreadDispatched(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo, int maxIteracoes) {
		this.formiga = formiga;
		this.percurso = percurso;
		this.algoritmo = algoritmo;
		this.maxIteracoes = maxIteracoes;

		feromonioController = new FeromonioController(algoritmo, percurso);
	}

	@Override
	public Formiga call() throws Exception {
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

		return formiga;
	}
}
