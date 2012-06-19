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
package br.com.ant.system.app;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import br.com.ant.system.action.ColoniaFormigaMonothread;
import br.com.ant.system.action.ColoniaFormigaMultithread;
import br.com.ant.system.action.ColoniaFormigasActionInterface;
import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasColetorController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.notificacao.NotificationController;
import br.com.ant.system.util.AntSystemUtil;
import br.com.ant.system.util.ImportarArquivoCidades;
import br.com.ant.system.view.ColoniaFormigasView;

import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;

public class AntSystemApp {

	private boolean	multithread;
	private int		iteracoes;
	private int		execucoes;
	private String	pathArquivo;

	public static void main(String[] args) {
		String exec = args.length > 0 ? args[0] : null;

		if (StringUtils.isNotEmpty(exec) && exec.equals("-nogui")) {
			AntSystemApp app = new AntSystemApp();
			app.executeNoView(args);
		} else {
			executeView();
		}

	}

	private void executeNoView(String[] args) {
		int i = 1;
		while (i < args.length) {
			String arg = args[i];
			if (arg.equals("-d")) {
				pathArquivo = args[++i];
			} else if (arg.equals("-i")) {
				iteracoes = Integer.parseInt(args[++i]);
			} else if (arg.equals("-m")) {
				multithread = args[++i].equals("true");
			} else if (arg.equals("-e")) {
				execucoes = Integer.parseInt(args[++i]);
			} else {
				System.out.println("Parametro de execucao invalido.");
				System.exit(1);
			}

			i++;
		}

		if (iteracoes == 0 || execucoes == 0 || StringUtils.isEmpty(pathArquivo)) {
			errorMessage();
		}

		NotificationController.getInstance().disable();
		this.executeAlgoritmo(execucoes, iteracoes, multithread, pathArquivo);

		System.out.println("");
		System.out.println("Tempo Total Gasto na execucao: " + AntSystemUtil.getIntance().horaFormatada(EstatisticasColetorController.getTempoTotal()));
		System.out.println("Tempo Medio: " + AntSystemUtil.getIntance().horaFormatada(EstatisticasColetorController.getTempoMedio()));
		System.out.println("");

		// Fechando a aplicação
		System.exit(0);
	}

	private static void errorMessage() {
		System.out.println("Favor informar o numero de iteracoes, diretorio do arquivo de cidades e forma de execução");
		System.out.println("ex.: java -jar antsystem.jar -nogui -i 10 -e 10 -d C:\\Documents and Settings\\j.duarte\\Desktop\\distancias.csv -m true");
		System.out.println("ou java -jar antsystem.jar -gui");
		System.out.println("     -d --> diretorio do arquivo de cidades");
		System.out.println("     -i --> iteracoes");
		System.out.println("     -m --> execucao multithread");
		System.out.println("     -e --> numero de execucoes");
		System.exit(1);
	}

	private static void executeView() {
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				UIManager.setLookAndFeel(WindowsClassicLookAndFeel.class.getName());
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}

			ColoniaFormigasView frame = new ColoniaFormigasView();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = env.getScreenDevices();

			DisplayMode mode = devices[0].getDisplayMode();
			int height = mode.getHeight();
			int width = mode.getWidth();

			frame.setTitle("AntSystemMultithread");
			frame.setSize(width, height - 30);
			frame.setVisible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void executeAlgoritmo(int execucoes, int iteracoes, boolean multithread, String pathArquivo) {
		for (int i = 0; i < execucoes; i++) {
			System.out.println(String.format("----------------- Nro. Execucao %s ------------------", i + 1));
			EstatisticasColetorController.newEstatisticaColetorInstance(i + 1);

			EstatisticasColetorController.getEstatisticaColetor().setNumeroIteracoes(iteracoes);
			PercursoController percurso = new PercursoController();

			System.out.println("Importando o arquivos de cidades...");
			ImportarArquivoCidades imp = new ImportarArquivoCidades();
			final Set<Caminho> caminhos = imp.importarAquivo(pathArquivo);

			System.out.println("Arquivo importado com sucesso...\n");

			for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
				Caminho c = (Caminho) it.next();
				percurso.addCaminho(c);
			}

			List<Formiga> formigas = new ArrayList<Formiga>();
			for (int j = 0; j < percurso.getCidadesPercurso().size(); j++) {
				Cidade atuaCidade = percurso.getCidadesPercurso().get(j);
				Formiga formiga = new Formiga(j, atuaCidade);

				formigas.add(formiga);
			}

			long inicial = System.currentTimeMillis();

			ASAlgoritmo algoritmo = new ASAlgoritmo();

			EstatisticasColetorController.getEstatisticaColetor().setHorarioInicial(inicial);
			if (multithread) {
				System.out.println("Iniciando a execução do algoritmo em multiplas threads...");
				System.out.println("");

				executeMultiThread(formigas, percurso, algoritmo, iteracoes);
			} else {
				System.out.println("Iniciando a execução do algoritmo em monothread...");
				System.out.println("");

				executeMonoThread(formigas, algoritmo, percurso, iteracoes);
			}

			long fim = System.currentTimeMillis();

			EstatisticasColetorController.getEstatisticaColetor().setHorarioFinal(fim);

			System.out.println("Tempo Gasto na execucao: " + AntSystemUtil.getIntance().horaFormatada(EstatisticasColetorController.getEstatisticaColetor().getTempoExecucao()));

			System.out.println(String.format("Quantidade de Iteracoes: %s", EstatisticasColetorController.getEstatisticaColetor().getNumeroIteracoes()));
			System.out.println(String.format("Menor caminho: %s", EstatisticasColetorController.getEstatisticaColetor().getMenorCaminhoPercorrido()));
			System.out.println("");
			System.out.println("Melhor trajeto: ");
			System.out.println("");
			for (Caminho c : EstatisticasColetorController.getEstatisticaColetor().getMelhorCaminho()) {
				System.out.println(String.format("%s ====== %s =====> %s", c.getCidadeOrigem(), c.getDistancia(), c.getCidadeDestino()));
			}
			System.out.println("");
			System.out.println(String.format("Tempo Gasto no melhor caminho: %s ms", EstatisticasColetorController.getEstatisticaColetor().getTempoGastoMelhorCaminho()));
			System.out.println(String.format("Quantidade de solucoes encontradas: %s", EstatisticasColetorController.getEstatisticaColetor().getQntSolucoesEncotradas()));
			System.out.println("");
			System.out.println("Algoritmo finalizado...");
			System.out.println("");

			EstatisticasColetorController.getEstatisticaColetor().loggerEstatisticas(multithread);
		}

	}

	private void executeMultiThread(List<Formiga> formigas, PercursoController percurso, ASAlgoritmo algoritmo, int iteracoes) {

		ColoniaFormigasActionInterface coloniaFormigaAction = new ColoniaFormigaMultithread(percurso, algoritmo);
		coloniaFormigaAction.setMaximoIteracoes(iteracoes);

		if (coloniaFormigaAction instanceof ColoniaFormigaMultithread) {
			ColoniaFormigaMultithread multiThread = (ColoniaFormigaMultithread) coloniaFormigaAction;

			multiThread.addFormigas(formigas);
			multiThread.action();
		}

	}

	/**
	 * Executa o algoritmo em uma unica thread.
	 */
	private void executeMonoThread(List<Formiga> formigas, ASAlgoritmo algoritmo, PercursoController percurso, int iteracoes) {
		ColoniaFormigasActionInterface coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);
		coloniaFormigaAction.setMaximoIteracoes(iteracoes);

		coloniaFormigaAction.action();
	}
}
