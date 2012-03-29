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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

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

	PercursoController				percurso;
	ColoniaFormigasActionInterface	coloniaFormigaAction;

	Logger							logger					= Logger.getLogger(this.getClass());
	NotificationImp					notificationImp;

	public ColoniaFormigasView() {
		notificationImp = new NotificationImp();

		percurso = new PercursoController();

		ImportarArquivoCidades imp = new ImportarArquivoCidades();
		final Set<Caminho> caminhos = imp.importarAquivo("c:/distancias.csv");

		for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
			Caminho c = (Caminho) it.next();
			percurso.addCaminho(c);
		}

		ASAlgoritmo algoritmo = new ASAlgoritmo();

		List<Formiga> formigas = new ArrayList<Formiga>();
		for (int i = 0; i < percurso.getCidadesPercurso().size(); i++) {
			Cidade atuaCidade = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(1, 6));
			Formiga formiga = new Formiga(i, atuaCidade);

			formigas.add(formiga);
		}

		this.formigas = formigas;

		// coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);
		coloniaFormigaAction = new ColoniaFormigaMultithread(percurso, algoritmo);

		// Montando o grafo das cidades.
		this.montarGrafo(caminhos, formigas);

		this.execute();
	}

	private void execute() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				coloniaFormigaAction.setMaximoIteracoes(10);

				if (coloniaFormigaAction instanceof ColoniaFormigaMultithread) {
					ColoniaFormigaMultithread multiThread = (ColoniaFormigaMultithread) coloniaFormigaAction;
					for (Formiga formiga : formigas) {
						multiThread.addFormiga(formiga);
					}
				}

				coloniaFormigaAction.action();
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
					JOptionPane.showMessageDialog(null, "Finalizado com sucesso.");
				} catch (Exception e) {
					logger.error("Houve um erro na execucao do algoritmo", e);
					JOptionPane.showMessageDialog(null, "Houve um erro na execucao do algoritmo.");
				}
			}
		};
		worker.execute();
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
		getContentPane().add(graphComponent);
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

}