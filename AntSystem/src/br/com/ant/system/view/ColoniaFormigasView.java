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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.com.ant.system.action.ColoniaFormigaMonothread;
import br.com.ant.system.action.ColoniaFormigaMultithread;
import br.com.ant.system.action.ColoniaFormigasActionInterface;
import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.EstatisticasControler;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Estatistica;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.notificacao.Notificacao;
import br.com.ant.system.notificacao.Notificacao.NotificacaoEnum;
import br.com.ant.system.notificacao.NotificationController;
import br.com.ant.system.util.AntSystemUtil;
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
	private static final String		EDGE_STYLE				= "startArrow=none;endArrow=none";
	private static final int		_Y						= 700;
	private static final int		_X						= 900;
	private static final long		serialVersionUID		= 1L;
	private static final int		LENGHT_VERTEX_CIDADE	= 10;
	private static final int		LENGHT_VERTEX_FORMIGA	= 40;

	private int						x						= 5;
	private int						y						= 5;

	Set<Caminho>					caminhos;
	Map<Cidade, Object>				mapVertexCidade			= new HashMap<Cidade, Object>();
	Map<Integer, Object>			mapVertexFormiga		= new HashMap<Integer, Object>();
	Map<Caminho, Object>			mapEdge					= new HashMap<Caminho, Object>();

	List<Formiga>					formigas;

	mxGraph							graph;
	mxGraphComponent				graphComponent;

	JPanel							applicationPanel;
	JPanel							leftPanel;
	JPanel							rightTopPanel;
	JPanel							rightFooterPanel;

	JRadioButton					monothreadButton;
	JRadioButton					multiThreadButton;

	JLabel							iteracoesLabel;
	NumberField						iteracoesField;
	JTextField						caminhoArquivoField;
	JTextArea						consoleField;

	JButton							buscarArquivoButton;
	JButton							executeButton;

	PercursoController				percurso;
	ColoniaFormigasActionInterface	coloniaFormigaAction;

	ASAlgoritmo						algoritmo;

	Logger							logger					= Logger.getLogger(this.getClass());
	NotificationImp					notificationImp;

	public ColoniaFormigasView() {
		notificationImp = new NotificationImp();
		percurso = new PercursoController();
		algoritmo = new ASAlgoritmo();

		// Montar paines.
		this.montarPaineis();
	}

	private List<Formiga> adicionarFormigas() {
		List<Formiga> formigas = new ArrayList<Formiga>();
		for (int i = 0; i < percurso.getCidadesPercurso().size(); i++) {
			Cidade atuaCidade = percurso.getCidadesPercurso().get(i);
			Formiga formiga = new Formiga(i, atuaCidade);

			formigas.add(formiga);
		}

		return formigas;
	}

	private Set<Caminho> ImportarArquivoCidades(String pathArquivo) {
		ImportarArquivoCidades imp = new ImportarArquivoCidades();
		final Set<Caminho> caminhos = imp.importarAquivo(pathArquivo);

		return caminhos;
	}

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

		consoleField = new JTextArea();
		consoleField.setEditable(false);
		consoleField.setWrapStyleWord(true);

		rightFooterPanel.add(consoleField, gbc1);
		return rightFooterPanel;
	}

	private JPanel montarRightTopPainel() {
		FormLayout layout = new FormLayout("$lcgap, left:p,  $lcgap, p:grow, $lcgap", "$lg, p,$lg, p,$lg, p,$lg, p,$lg, p");
		CellConstraints cc = new CellConstraints();

		JPanel rightTopPanel = new JPanel(layout);
		rightTopPanel.setBorder(BorderFactory.createTitledBorder("Op��es:"));

		iteracoesLabel = new JLabel("Num. Iteracoes: ");
		iteracoesField = new NumberField();
		iteracoesField.setText("999");

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
		caminhoArquivoField.setText("C:\\Users\\Sildu\\Desktop\\distancias.csv");
		// caminhoArquivoField.setText("C:\\Documents and Settings\\j.duarte\\Desktop\\distancias.csv");

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
		rightTopPanel.add(iteracoesLabel, cc.xy(2, 4));
		rightTopPanel.add(iteracoesField, cc.xy(4, 4));
		rightTopPanel.add(panelArquivo, cc.xyw(2, 6, 4));
		rightTopPanel.add(executeButton, cc.xyw(2, 10, 3));

		return rightTopPanel;
	}

	private void montarGrafo(Collection<Caminho> caminhos, List<Formiga> formigas) {
		NotificationController.getInstance().clearNotification();
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

				this.addEdge(parent, c);
			}

			for (Formiga formiga : formigas) {
				this.addVertexFormiga(parent, formiga);
			}
		} finally {
			graph.getModel().endUpdate();
		}

		graphComponent.repaint();
		leftPanel.repaint();
	}

	private void addEdge(Object parent, Caminho c) {
		if (!mapEdge.containsKey(c)) {
			Object origem = mapVertexCidade.get(c.getCidadeOrigem());
			Object destino = mapVertexCidade.get(c.getCidadeDestino());

			String style = EDGE_STYLE + ";strokeColor=#FFFFFF";

			mxCell obj = (mxCell) graph.insertEdge(parent, String.valueOf(c.getDistancia()), null, origem, destino, style);

			mapEdge.put(c, obj);
		}
	}

	private void addVertexCidade(Object parent, Cidade c) {
		if (!mapVertexCidade.containsKey(c)) {
			x = AntSystemUtil.getIntance().getAleatorio(10, _X);
			y = AntSystemUtil.getIntance().getAleatorio(10, _Y);

			Object obj = graph.insertVertex(parent, c.getNome(), c.getNome(), x, y, LENGHT_VERTEX_CIDADE, LENGHT_VERTEX_CIDADE);
			mapVertexCidade.put(c, obj);
		}
	}

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
			graph.getModel().endUpdate();
		}
	}

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
			graph.getModel().endUpdate();
		}

	}

	public void addConsoleText(String text) {
		consoleField.setText(consoleField.getText() + text + "\n");
	}

	public class NotificationImp implements Runnable {
		private NotificationImp() {
			Thread thread = new Thread(this);
			thread.setName("NotificationGrafo");
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();
		}

		@Override
		public void run() {
			while (true) {
				final Notificacao notificacao = NotificationController.getInstance().takeNotificacao();

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
			}
		}
	}

	public class ExecutarAction extends AbstractAction {
		private static final long	serialVersionUID	= 182237609101003562L;

		public ExecutarAction() {
			super("Executar");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				volatile long	inicial;
				volatile long	fim;

				@Override
				protected Void doInBackground() throws Exception {
					executeButton.setEnabled(false);

					EstatisticasControler.getInstance().clear();

					addConsoleText("Importando o arquivos de cidades...");
					caminhos = ImportarArquivoCidades(caminhoArquivoField.getText());

					for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
						Caminho c = (Caminho) it.next();
						percurso.addCaminho(c);
					}

					formigas = adicionarFormigas();

					// Montando o grafo das cidades.
					montarGrafo(caminhos, formigas);

					inicial = System.currentTimeMillis();

					if (multiThreadButton.isSelected()) {
						executeMultiThread();
					} else if (monothreadButton.isSelected()) {
						executeMonoThread();
					}

					fim = System.currentTimeMillis();

					// createGrafico();

					addConsoleText("Algoritmo finalizado...");
					addConsoleText("Tempo Gasto: " + (fim - inicial));
					return null;
				}

				@Override
				protected void done() {
					try {
						get();
						executeButton.setEnabled(true);

//						EstatisticasControler.getInstance().loggerEstatisticas();

						System.gc();
						cancel(true);
					} catch (Exception e) {
						executeButton.setEnabled(true);

						logger.error("Houve um erro na execucao do algoritmo", e);
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				}
			};
			worker.execute();
		}

		private void executeMultiThread() {
			addConsoleText("Iniciando a execu��o do algoritmo em multiplas threads...");
			coloniaFormigaAction = new ColoniaFormigaMultithread(percurso, algoritmo);
			coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

			if (coloniaFormigaAction instanceof ColoniaFormigaMultithread) {
				ColoniaFormigaMultithread multiThread = (ColoniaFormigaMultithread) coloniaFormigaAction;

				multiThread.addFormigas(formigas);
				multiThread.action();
			}

		}

		private void executeMonoThread() {
			addConsoleText("Iniciando a execu��o do algoritmo em monothread...");
			coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);
			coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

			coloniaFormigaAction.action();
		}
	}

	public class BuscarArquivoAction extends AbstractAction {

		private static final long	serialVersionUID	= 1L;

		public BuscarArquivoAction() {
			super("Escolher");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser(System.getenv("user.dir"));
			chooser.showOpenDialog(null);

			caminhoArquivoField.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void createGrafico() {

		// Create a simple XY chart
		XYSeries series = new XYSeries("XYGraph");
		XYSeries series1 = new XYSeries("XYGraph1");

		JFrame frame = new JFrame();

		for (Estatistica e : EstatisticasControler.getInstance().getEstatisticas()) {
			series.add(e.getFormigaId(), e.getTempoGasto());
			series1.add(e.getFormigaId(), e.getTempoGasto());
		}

		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		dataset.addSeries(series1);

		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart("XY Chart", "x-axis", "y-axis", dataset, PlotOrientation.VERTICAL, true, true, false);
		frame.getContentPane().add(new ChartPanel(chart));

		frame.setPreferredSize(new Dimension(600, 600));
		frame.setMinimumSize(new Dimension(600, 600));
		frame.setMaximumSize(new Dimension(600, 600));
		frame.setVisible(true);

		try {
			ChartUtilities.saveChartAsJPEG(new File("C:\\chart.jpg"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}

}