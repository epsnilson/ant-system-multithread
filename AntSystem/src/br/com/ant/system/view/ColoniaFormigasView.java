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
package br.com.ant.system.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import br.com.ant.system.action.ColoniaFormigaMonothread;
import br.com.ant.system.action.ColoniaFormigaMultithread;
import br.com.ant.system.action.ColoniaFormigasActionInterface;
import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasColetorController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.notificacao.Notificacao;
import br.com.ant.system.notificacao.Notificacao.NotificacaoEnum;
import br.com.ant.system.notificacao.NotificationController;
import br.com.ant.system.util.AntSystemUtil;
import br.com.ant.system.util.ChartUtil;
import br.com.ant.system.util.ImportarArquivoCidades;
import br.com.ant.system.view.util.NumberField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/**
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigasView extends JFrame {
	private static final String				EDGE_STYLE				= "startArrow=none;endArrow=none";
	private static final int				_Y						= 700;
	private static final int				_X						= 900;
	private static final long				serialVersionUID		= 1L;
	private static final int				LENGHT_VERTEX_CIDADE	= 10;
	private static final int				LENGHT_VERTEX_FORMIGA	= 40;

	private int								x						= 5;
	private int								y						= 5;

	private Set<Caminho>					caminhos;
	private Map<Cidade, Object>				mapVertexCidade			= new HashMap<Cidade, Object>();
	private Map<Integer, Object>			mapVertexFormiga		= new HashMap<Integer, Object>();
	private Map<Caminho, Object>			mapEdge					= new HashMap<Caminho, Object>();

	private List<Formiga>					formigas;

	private mxGraph							graph;
	private mxGraphComponent				graphComponent;

	private JPanel							applicationPanel;
	private JPanel							leftPanel;
	private JPanel							rightTopPanel;
	private JPanel							rightFooterPanel;

	private JRadioButton					monothreadButton;
	private JRadioButton					multiThreadButton;

	private JLabel							iteracoesLabel;
	private NumberField						iteracoesField;

	private JLabel							execucoesLabel;
	private NumberField						execucoesField;
	private JTextField						caminhoArquivoField;
	private JScrollPane						scrollPane;
	private JTextArea						consoleField;

	private JButton							buscarArquivoButton;
	private JButton							executeButton;

	private PercursoController				percurso;
	private ColoniaFormigasActionInterface	coloniaFormigaAction;

	private ASAlgoritmo						algoritmo;
	private Logger							logger					= Logger.getLogger(this.getClass());

	public ColoniaFormigasView() {
		new NotificationImp();
		algoritmo = new ASAlgoritmo();

		// Montar paines.
		this.montarPaineis();
	}

	/**
	 * Adiciona as formigas no percurso.
	 * 
	 * @return
	 */
	private List<Formiga> adicionarFormigas() {
		List<Formiga> formigas = new ArrayList<Formiga>();
		for (int i = 0; i < percurso.getCidadesPercurso().size(); i++) {
			Cidade atuaCidade = percurso.getCidadesPercurso().get(i);
			Formiga formiga = new Formiga(i, atuaCidade);

			formigas.add(formiga);
		}

		return formigas;
	}

	/**
	 * Importa o arquivo de cidades.
	 * 
	 * @param pathArquivo
	 *            localização do arquivo de cidades.
	 * @return
	 */
	private Set<Caminho> ImportarArquivoCidades(String pathArquivo) {
		ImportarArquivoCidades imp = new ImportarArquivoCidades();
		final Set<Caminho> caminhos = imp.importarAquivo(pathArquivo);

		return caminhos;
	}

	/**
	 * Monta todos os paineis da aplicacao.
	 */
	private void montarPaineis() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		applicationPanel = new JPanel(gridBagLayout);

		this.getContentPane().add(applicationPanel);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.weightx = 0.9;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;

		leftPanel = this.montarLeftPanel();

		applicationPanel.add(leftPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 0.3;
		gbc.weightx = 0.1;
		gbc.gridheight = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.LINE_END;

		rightTopPanel = this.montarRightTopPainel();
		applicationPanel.add(rightTopPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weighty = 0.7;
		gbc.weightx = 0.1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.LINE_END;

		rightFooterPanel = this.montarRightFooterPanel();
		applicationPanel.add(rightFooterPanel, gbc);

	}

	/**
	 * Monta o painel esquerdo.
	 * 
	 * @return
	 */
	private JPanel montarLeftPanel() {
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("Grafico"));

		graph = new mxGraph();
		graphComponent = new mxGraphComponent(graph);
		graph.setKeepEdgesInBackground(true);
		graph.setCellsLocked(true);

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weighty = 1.0;
		gbc1.weightx = 1.0;
		gbc1.gridheight = GridBagConstraints.REMAINDER;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.LINE_START;
		leftPanel.add(graphComponent, gbc1);

		return leftPanel;
	}

	/**
	 * Monta o painel direito inferior.
	 * 
	 * @return
	 */
	private JPanel montarRightFooterPanel() {
		GridBagConstraints gbc1 = new GridBagConstraints();
		JPanel rightFooterPanel = new JPanel(new GridBagLayout());
		rightFooterPanel.setBorder(BorderFactory.createTitledBorder("Console"));

		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weighty = 1;
		gbc1.weightx = 1;
		gbc1.gridheight = GridBagConstraints.REMAINDER;
		gbc1.gridwidth = GridBagConstraints.REMAINDER;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.LINE_START;

		scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);

		consoleField = new JTextArea();
		consoleField.setEditable(false);
		consoleField.setWrapStyleWord(true);

		scrollPane.getViewport().add(consoleField);

		rightFooterPanel.add(scrollPane, gbc1);

		return rightFooterPanel;
	}

	/**
	 * Monta o painel direito superior.
	 * 
	 * @return
	 */
	private JPanel montarRightTopPainel() {
		FormLayout layout = new FormLayout("$lcgap, left:p,  $lcgap, p:grow, $lcgap", "$lg, p,$lg, p,$lg, p,$lg, p,$lg, p ,$lg, B:p:grow");
		CellConstraints cc = new CellConstraints();

		JPanel rightTopPanel = new JPanel(layout);
		rightTopPanel.setBorder(BorderFactory.createTitledBorder("Opções:"));

		iteracoesLabel = new JLabel("Num. Iteracoes: ");
		iteracoesField = new NumberField();
		iteracoesField.setText("99");

		execucoesLabel = new JLabel("Num. Execucoes: ");
		execucoesField = new NumberField();
		execucoesField.setText("5");

		monothreadButton = new JRadioButton("MonoThread", true);
		multiThreadButton = new JRadioButton("MultiThread");

		JPanel panelGroup = new JPanel();
		ButtonGroup group = new ButtonGroup();
		group.add(monothreadButton);
		group.add(multiThreadButton);

		panelGroup.add(monothreadButton);
		panelGroup.add(multiThreadButton);

		JPanel panelArquivo = new JPanel(new GridBagLayout());
		panelArquivo.setBorder(BorderFactory.createTitledBorder("Arquivo de cidades"));
		GridBagConstraints gbc = new GridBagConstraints();

		caminhoArquivoField = new JTextField();
		caminhoArquivoField.setEnabled(false);
		// caminhoArquivoField.setText("C:\\Users\\Sildu\\Desktop\\distancias.csv");
		caminhoArquivoField.setText("C:\\Documents and Settings\\j.duarte\\Desktop\\distancias.csv");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.weightx = 0.9;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;

		panelArquivo.add(caminhoArquivoField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.weightx = 0.1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_END;
		buscarArquivoButton = new JButton(new BuscarArquivoAction());

		panelArquivo.add(buscarArquivoButton, gbc);

		executeButton = new JButton(new ExecutarAction());

		rightTopPanel.add(panelGroup, cc.xyw(2, 2, 4));
		rightTopPanel.add(execucoesLabel, cc.xy(2, 4));
		rightTopPanel.add(execucoesField, cc.xy(4, 4));
		rightTopPanel.add(iteracoesLabel, cc.xy(2, 6));
		rightTopPanel.add(iteracoesField, cc.xy(4, 6));
		rightTopPanel.add(panelArquivo, cc.xyw(2, 8, 4));
		rightTopPanel.add(executeButton, cc.xyw(2, 10, 3));

		return rightTopPanel;
	}

	/**
	 * Monta o grafico.
	 * 
	 * @param caminhos
	 * @param formigas
	 */
	private void montarGrafo(Collection<Caminho> caminhos, List<Formiga> formigas) {
		Object parent = graph.getDefaultParent();

		mapEdge.clear();
		mapVertexCidade.clear();
		mapVertexFormiga.clear();

		graph.getModel().beginUpdate();

		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

			for (Caminho c : percurso.getCaminhosDisponiveis()) {
				this.addVertexCidade(parent, c.getCidadeOrigem());
				this.addVertexCidade(parent, c.getCidadeDestino());

				Caminho inverso = null;
				for (Caminho ca : percurso.getCaminhosDisponiveis()) {
					if (c.getCidadeOrigem().equals(ca.getCidadeDestino()) && c.getCidadeDestino().equals(ca.getCidadeOrigem())) {
						inverso = ca;
						break;
					}
				}

				this.addEdge(parent, c, inverso);
			}

			for (Formiga formiga : formigas) {
				this.addVertexFormiga(parent, formiga);
			}
		} finally {
			try {
				graph.getModel().endUpdate();
			} catch (Exception e2) {
			}
		}

		graphComponent.repaint();
		leftPanel.repaint();
	}

	/**
	 * Adiciona um novo caminho no grafico.
	 * 
	 * @param parent
	 * @param c
	 */
	private void addEdge(Object parent, Caminho c, Caminho inverso) {
		if (!mapEdge.containsKey(c)) {
			Object origem = mapVertexCidade.get(c.getCidadeOrigem());
			Object destino = mapVertexCidade.get(c.getCidadeDestino());

			String style = EDGE_STYLE + ";strokeColor=#FFFFFF";

			mxCell obj = (mxCell) graph.insertEdge(parent, String.valueOf(c.getDistancia()), null, origem, destino, style);

			mapEdge.put(c, obj);
			mapEdge.put(inverso, obj);
		}
	}

	/**
	 * Adiciona uma cidade no grafico.
	 * 
	 * @param parent
	 * @param c
	 */
	private void addVertexCidade(Object parent, Cidade c) {
		if (!mapVertexCidade.containsKey(c)) {
			x = AntSystemUtil.getIntance().getAleatorio(10, _X);
			y = AntSystemUtil.getIntance().getAleatorio(10, _Y);

			Object obj = graph.insertVertex(parent, c.getNome(), c.getNome(), x, y, LENGHT_VERTEX_CIDADE, LENGHT_VERTEX_CIDADE);
			mapVertexCidade.put(c, obj);
		}
	}

	/**
	 * Adiciona uma formiga no grafico.
	 * 
	 * @param parent
	 * @param f
	 */
	private void addVertexFormiga(Object parent, Formiga f) {
		if (!mapVertexFormiga.containsKey(f.getId())) {
			mxCell cell = (mxCell) mapVertexCidade.get(f.getLocalizacaoCidadeInicial());

			int x = cell.getGeometry().getPoint().x;
			int y = cell.getGeometry().getPoint().y;

			String pathJar = System.getProperty("user.dir");
			ImageIcon imagemPath = new ImageIcon(pathJar + "\\resources\\imagens\\images.png");

			String style = "fillColor=#66FF00;strokecolor=#66FF00;perimeter=rectanglePerimeter;imageWidth=1000;imageHeight=1000;shape=image;image=file:" + imagemPath;

			// String style =
			// "fillColor=#66FF00;strokecolor=#66FF00;perimeter=rectanglePerimeter";
			Object obj = graph.insertVertex(parent, String.valueOf(f.getId()), String.valueOf(f.getId()), x, y, LENGHT_VERTEX_FORMIGA, LENGHT_VERTEX_FORMIGA, style);
			mapVertexFormiga.put(f.getId(), obj);
		}
	}

	/**
	 * Atualiza a cidade atual da formiga.
	 * 
	 * @param f
	 *            Formiga a ser atualizada.
	 */
	public void updateVertexFormiga(Formiga f) {
		mxCell cell = (mxCell) mapVertexFormiga.get(f.getId());
		try {
			graph.getModel().beginUpdate();

			mxCell cidade = (mxCell) mapVertexCidade.get(f.getLocalizacaoCidadeAtual());

			mxGeometry geometry = cidade.getGeometry();
			geometry.setHeight(LENGHT_VERTEX_FORMIGA);
			geometry.setWidth(LENGHT_VERTEX_FORMIGA);

			graph.getModel().setGeometry(cell, geometry);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				graph.getModel().endUpdate();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * Atualiza a aresta de feromonio no grafico.
	 * 
	 * @param c
	 *            Caminho a ser atualizado.
	 */
	public void updateEdgeFeromonio(Caminho c) {
		mxCell cell = (mxCell) mapEdge.get(c);
		try {
			graph.getModel().beginUpdate();
			String color = null;
			if (c.getFeromonio().getQntFeromonio() <= 0.000070) {
				color = ";strokeColor=#63B8FF";
			} else if (c.getFeromonio().getQntFeromonio() <= 0.000093) {
				color = ";strokeColor=#1C86EE";
			} else {
				color = ";strokeColor=#0000CD";
			}

			String style = EDGE_STYLE + color;

			graph.getModel().setStyle(cell, style);

			cell = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				graph.getModel().endUpdate();
			} catch (Exception e2) {
			}
		}

	}

	/**
	 * Atualiza a aresta de feromonio no grafico.
	 * 
	 * @param c
	 *            Caminho a ser atualizado.
	 */
	public void updateEdgeMelhorCaminho(Collection<Caminho> caminhos) {
		for (Caminho c : caminhos) {
			mxCell cell = (mxCell) mapEdge.get(c);
			try {
				graph.getModel().beginUpdate();
				String color = null;
				color = ";strokeColor=#00FF00";
				String style = EDGE_STYLE + color;
				graph.getModel().setStyle(cell, style);
				cell = null;

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					graph.getModel().endUpdate();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * Adiciona o texto no console.
	 * 
	 * @param text
	 *            Texto a ser inserido
	 */
	public void addConsoleText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				consoleField.setText(consoleField.getText() + text + "\n");
				consoleField.moveCaretPosition(consoleField.getText().length());
			}
		});
	}

	/**
	 * Classe responsavel pela atualizacao do grafico na tela.
	 */
	public class NotificationImp implements Runnable {
		private NotificationImp() {
			Thread thread = new Thread(this);
			thread.setName("NotificationGrafo");
			thread.start();
		}

		@Override
		public void run() {
			while (true) {
				Notificacao notificacao = NotificationController.getInstance().takeNotificacao();

				Object obj = notificacao.getObj();
				execute(notificacao, obj);
			}
		}

		private void execute(Notificacao notificacao, Object obj) {
			if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.FORMIGA)) {
				Formiga formiga = (Formiga) obj;

				updateVertexFormiga(formiga);
			} else if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.FEROMONIO)) {
				Caminho c = (Caminho) obj;

				updateEdgeFeromonio(c);
			} else if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.MELHOR_CAMINHO)) {
				@SuppressWarnings("unchecked")
				List<Caminho> caminhos = (List<Caminho>) obj;
				updateEdgeMelhorCaminho(caminhos);
			}
		}
	}

	/**
	 * Action responsavel pela a acao do botao.
	 */
	public class ExecutarAction extends AbstractAction {
		private static final long	serialVersionUID	= 182237609101003562L;

		public ExecutarAction() {
			super("Executar");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingWorker<Void, Void> worker = createWorker();
			worker.execute();
		}

		private SwingWorker<Void, Void> createWorker() {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					executeButton.setEnabled(false);
					addConsoleText("****************************************************");
					addConsoleText("***************Iniciado a execucao******************");
					addConsoleText("****************************************************");

					int numExec = Integer.parseInt(execucoesField.getText());

					for (int i = 0; i < numExec; i++) {
						EstatisticasColetorController.newEstatisticaColetorInstance(i + 1);
						NotificationController.getInstance().clearNotification();

						addConsoleText("");
						addConsoleText(String.format("----------------- Nro. Execucao %s ------------------", i + 1));

						this.executeAlgoritmo();

					}

					addConsoleText("");
					addConsoleText("Tempo Total Gasto na execucao: " + new SimpleDateFormat("mm:ss:SSS").format(new Date(EstatisticasColetorController.getTempoTotal())));
					addConsoleText("Tempo Medio: " + new SimpleDateFormat("mm:ss:SSS").format(new Date(EstatisticasColetorController.getTempoMedio())));
					addConsoleText("");

					return null;
				}

				@Override
				protected void done() {
					try {
						get();

						// ChartUtil.getInstance().createTempoTotalExecucao(EstatisticasColetorController.getMapEstatisticas());
						ChartUtil.getInstance().createCaminhoPercorrido(EstatisticasColetorController.getEstatisticaColetor());

						executeButton.setEnabled(true);

						this.clear();
						cancel(true);
					} catch (Exception e) {
						executeButton.setEnabled(true);

						logger.error("Houve um erro na execucao do algoritmo", e);
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				}

				private void executeAlgoritmo() {

					EstatisticasColetorController.getEstatisticaColetor().setNumeroIteracoes(Integer.parseInt(iteracoesField.getText()));
					percurso = new PercursoController();

					addConsoleText("Importando o arquivos de cidades...");
					caminhos = ImportarArquivoCidades(caminhoArquivoField.getText());
					addConsoleText("Arquivo importado com sucesso...\n");

					for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
						Caminho c = (Caminho) it.next();
						percurso.addCaminho(c);
					}

					formigas = adicionarFormigas();

					// Montando o grafo das cidades.
					montarGrafo(caminhos, formigas);

					long inicial = System.currentTimeMillis();

					EstatisticasColetorController.getEstatisticaColetor().setHorarioInicial(inicial);
					if (multiThreadButton.isSelected()) {
						addConsoleText("Iniciando a execução do algoritmo em multiplas threads...");
						addConsoleText("");

						executeMultiThread();
					} else if (monothreadButton.isSelected()) {
						addConsoleText("Iniciando a execução do algoritmo em monothread...");
						addConsoleText("");

						executeMonoThread();
					}

					long fim = System.currentTimeMillis();

					EstatisticasColetorController.getEstatisticaColetor().setHorarioFinal(fim);

					// Notifica o melhor caminho seguido.
					notificarMelhorCaminho();

					addConsoleText("Tempo Gasto na execucao: " + new SimpleDateFormat("mm:ss:SSS").format(new Date(EstatisticasColetorController.getEstatisticaColetor().getTempoExecucao())));

					addConsoleText(String.format("Quantidade de Iteracoes: %s", EstatisticasColetorController.getEstatisticaColetor().getNumeroIteracoes()));
					addConsoleText(String.format("Menor caminho: %s", EstatisticasColetorController.getEstatisticaColetor().getMenorCaminhoPercorrido()));
					addConsoleText("");
					addConsoleText("Melhor trajeto: ");
					addConsoleText("");
					for (Caminho c : EstatisticasColetorController.getEstatisticaColetor().getMelhorCaminho()) {
						addConsoleText(String.format("%s ====== %s =====> %s", c.getCidadeOrigem(), c.getDistancia(), c.getCidadeDestino()));
					}
					addConsoleText("");
					addConsoleText(String.format("Tempo Gasto no melhor caminho: %s ms", EstatisticasColetorController.getEstatisticaColetor().getTempoGastoMelhorCaminho()));
					addConsoleText(String.format("Quantidade de solucoes encontradas: %s", EstatisticasColetorController.getEstatisticaColetor().getEstatisticas().size()));
					addConsoleText("");
					addConsoleText("Algoritmo finalizado...");
					addConsoleText("");

					EstatisticasColetorController.getEstatisticaColetor().loggerEstatisticas(multiThreadButton.isSelected());
				}

				private void notificarMelhorCaminho() {
					Notificacao notificacao = new Notificacao();
					notificacao.setTipoNotificacao(NotificacaoEnum.MELHOR_CAMINHO);
					notificacao.setObj(EstatisticasColetorController.getEstatisticaColetor().getMelhorCaminho());

					NotificationController.getInstance().addNotificacao(notificacao);
				}

				private void clear() {
					percurso.clear();
					formigas.clear();
					percurso = null;

					EstatisticasColetorController.clear();

					System.gc();
				}
			};
			return worker;
		}

		/**
		 * Executa o algoritmo em multiplas threads.
		 */
		private void executeMultiThread() {

			coloniaFormigaAction = new ColoniaFormigaMultithread(percurso, algoritmo);
			coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

			if (coloniaFormigaAction instanceof ColoniaFormigaMultithread) {
				ColoniaFormigaMultithread multiThread = (ColoniaFormigaMultithread) coloniaFormigaAction;

				multiThread.addFormigas(formigas);
				multiThread.action();
			}

		}

		/**
		 * Executa o algoritmo em uma unica thread.
		 */
		private void executeMonoThread() {
			coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);
			coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

			coloniaFormigaAction.action();
		}
	}

	/**
	 * Action responsavel por efetuar a busca do arquivo de cidades.
	 */
	public class BuscarArquivoAction extends AbstractAction {

		private static final long	serialVersionUID	= 1L;

		public BuscarArquivoAction() {
			super("Arquivo");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser(System.getenv("user.dir"));
			chooser.showOpenDialog(null);

			caminhoArquivoField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}
}