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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import javax.swing.plaf.FileChooserUI;

import org.apache.log4j.Logger;

import br.com.ant.system.action.ColoniaFormigaMonothread;
import br.com.ant.system.action.ColoniaFormigaMultithread;
import br.com.ant.system.action.ColoniaFormigasActionInterface;
import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
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
	private static final int		LENGHT_VERTEX_FORMIGA	= 50;

	private int						x						= 5;
	private int						y						= 5;

	Set<Caminho>					caminhos;
	Map<Cidade, Object>				mapVertexCidade			= new HashMap<Cidade, Object>();
	Map<Integer, Object>			mapVertexFormiga		= new HashMap<Integer, Object>();
	Map<Caminho, Object>			mapEdge					= new HashMap<Caminho, Object>();

	List<Formiga>					formigas;

	mxGraph							graph;

	JPanel							applicationPanel;
	JPanel							leftPanel;
	JPanel							rightPanel;

	JRadioButton					monothreadButton;
	JRadioButton					multiThreadButton;
	FileChooserUI					arquivoImportacao;

	JLabel							iteracoesLabel;
	NumberField						iteracoesField;

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

		Set<Caminho> caminhos = ImportarArquivoCidades();
		this.formigas = adicionarFormigas();

		// Montar paines.
		this.montarPaineis();

		// Montando o grafo das cidades.
		this.montarGrafo(caminhos, this.formigas);
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

	private Set<Caminho> ImportarArquivoCidades() {
		ImportarArquivoCidades imp = new ImportarArquivoCidades();
		final Set<Caminho> caminhos = imp.importarAquivo("c:/distancias.csv");

		for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
			Caminho c = (Caminho) it.next();
			percurso.addCaminho(c);
		}
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
		gbc.weightx = 1;
		gbc.gridheight = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;

		leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("Grafico"));
		applicationPanel.add(leftPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 0.7;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_END;

		FormLayout layout = new FormLayout("$lcgap, left:p, $lcgap, p:grow, $lcgap", "$lg, p,$lg, p,$lg, p,$lg, p,$lg, p,$lg, p,$lg, p, $lg, p, $lg, p, B:p:grow");
		CellConstraints cc = new CellConstraints();
		rightPanel = new JPanel(layout);
		rightPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.blue));

		iteracoesLabel = new JLabel("Num. Iteracoes: ");
		iteracoesField = new NumberField();

		monothreadButton = new JRadioButton("MonoThread", true);
		multiThreadButton = new JRadioButton("MultiThread");

		ButtonGroup group = new ButtonGroup();
		group.add(monothreadButton);
		group.add(multiThreadButton);

		executeButton = new JButton(new ExecutarAction());
		rightPanel.add(monothreadButton, cc.xy(2, 2));
		rightPanel.add(multiThreadButton, cc.xy(4, 2));
		rightPanel.add(iteracoesLabel, cc.xy(2, 6));
		rightPanel.add(iteracoesField, cc.xy(4, 6));
		// rightPanel.add(arquivoImportacao, cc.xyw(2, 8, 2));
		rightPanel.add(executeButton, cc.xy(4, 19));

		applicationPanel.add(rightPanel, gbc);
	}

	private void montarGrafo(Collection<Caminho> caminhos, List<Formiga> formigas) {
		graph = new mxGraph();

		graph.setKeepEdgesInBackground(true);
		graph.setCellsLocked(true);

		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();

		try {
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

		mxGraphComponent graphComponent = new mxGraphComponent(graph);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 0.5;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_END;

		leftPanel.add(graphComponent, gbc);
	}

	private void addEdge(Object parent, Caminho c) {
		if (!mapEdge.containsKey(c)) {
			Object origem = mapVertexCidade.get(c.getCidadeOrigem());
			Object destino = mapVertexCidade.get(c.getCidadeDestino());

			String style = EDGE_STYLE + ";strokeColor=#FFFFFF";

			mxCell obj = (mxCell) graph.insertEdge(parent, null, null, origem, destino, style);

			// obj.setVisible(false);
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

			x = cell.getGeometry().getPoint().x;
			y = cell.getGeometry().getPoint().y;

			String pathJar = System.getProperty("user.dir");
			ImageIcon imagemPath = new ImageIcon(pathJar + "\\resources\\imagens\\images.png");

			String style = "fillColor=#66FF00;strokecolor=#66FF00;perimeter=rectanglePerimeter;imageWidth=1000;imageHeight=1000;shape=image;image=file:" + imagemPath;
			Object obj = graph.insertVertex(parent, String.valueOf(f.getId()), String.valueOf(f.getId()), x, y, LENGHT_VERTEX_FORMIGA, LENGHT_VERTEX_FORMIGA, style);
			mapVertexFormiga.put(f.getId(), obj);
		}
	}

	public void updateVertexFormiga(final Formiga f) {
		mxCell cell = (mxCell) mapVertexFormiga.get(f.getId());
		try {
			graph.getModel().beginUpdate();

			mxCell cidade = (mxCell) mapVertexCidade.get(f.getLocalizacaoCidadeAtual());

			mxGeometry geometry = cidade.getGeometry();
			geometry.setHeight(LENGHT_VERTEX_FORMIGA);
			geometry.setWidth(LENGHT_VERTEX_FORMIGA);

			graph.getModel().setGeometry(cell, geometry);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void updateEdge(final Caminho c) {
		// mxCell cell = (mxCell) mapEdge.get(c);
		// try {
		// graph.getModel().beginUpdate();
		// } finally {
		// try {
		// graph.getModel().endUpdate();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	public void updateEdgeFeromonio(final Caminho c) {
		mxCell cell = (mxCell) mapEdge.get(c);
		try {
			graph.getModel().beginUpdate();
			String color = null;
			if (c.getFeromonio().getQntFeromonio() <= 0.000070) {
				color = ";strokeColor=#C3C3C3";
			} else if (c.getFeromonio().getQntFeromonio() <= 0.000093) {
				color = ";strokeColor=#6F6D6D";
			} else {
				color = ";strokeColor=#000000";
			}

			String style = EDGE_STYLE + color;

			graph.getModel().remove(cell);
			Object newCell = graph.insertEdge(cell.getParent(), null, null, cell.getSource(), cell.getTarget(), style);

			mapEdge.put(c, newCell);

			cell = null;

		} finally {
			try {
				graph.getModel().endUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class NotificationImp implements Runnable {
		private NotificationImp() {
			Thread thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {
			while (true) {
				Notificacao notificacao = NotificationController.getInstance().takeNotificacao();
				Object obj = notificacao.getObj();

				if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.CAMINHO)) {

					Caminho c = (Caminho) obj;
					updateEdge(c);
				} else if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.FORMIGA)) {
					Formiga formiga = (Formiga) obj;

					updateVertexFormiga(formiga);
				} else if (notificacao.getTipoNotificacao().equals(NotificacaoEnum.FEROMONIO)) {
					Caminho c = (Caminho) obj;

					updateEdgeFeromonio(c);
				}
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
			if (multiThreadButton.isSelected()) {
				this.executeMultiThread();
			} else if (monothreadButton.isSelected()) {
				this.executeMonoThread();
			}
		}

		private void executeMultiThread() {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					coloniaFormigaAction = new ColoniaFormigaMultithread(percurso, algoritmo);
					coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

					if (coloniaFormigaAction instanceof ColoniaFormigaMultithread) {
						ColoniaFormigaMultithread multiThread = (ColoniaFormigaMultithread) coloniaFormigaAction;

						multiThread.action();

						for (Formiga formiga : formigas) {
							multiThread.addFormiga(formiga);
						}
					}

					return null;
				}

				@Override
				protected void done() {
					try {
						get();
					} catch (Exception e) {
						logger.error("Houve um erro na execucao do algoritmo", e);
						JOptionPane.showMessageDialog(null, "Houve um erro na execucao do algoritmo.");
					}
				}
			};
			worker.execute();
		}

		private void executeMonoThread() {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);
					coloniaFormigaAction.setMaximoIteracoes(Integer.parseInt(iteracoesField.getText()));

					coloniaFormigaAction.action();

					return null;
				}

				@Override
				protected void done() {
					try {
						get();
					} catch (Exception e) {
						logger.error("Houve um erro na execucao do algoritmo", e);
						JOptionPane.showMessageDialog(null, "Houve um erro na execucao do algoritmo.");
					}
				}
			};
			worker.execute();
		}

	}

}