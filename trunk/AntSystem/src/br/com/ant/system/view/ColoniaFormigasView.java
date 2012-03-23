package br.com.ant.system.view;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import br.com.ant.system.action.ColoniaFormigaMonothread;
import br.com.ant.system.action.ColoniaFormigasActionInterface;
import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;
import br.com.ant.system.util.ImportarArquivoCidades;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

public class ColoniaFormigasView extends JFrame {
	private static final long		serialVersionUID	= 1L;
	private static final int		LENGHT_VERTEX		= 10;

	private int						x					= 5;
	private int						y					= 5;

	Set<Caminho>					caminhos;
	Map<Cidade, Object>				mapVertex			= new HashMap<Cidade, Object>();
	Map<Caminho, Object>			mapEdge				= new HashMap<Caminho, Object>();
	mxGraph							graph;

	PercursoController				percurso;
	ColoniaFormigasActionInterface	coloniaFormigaAction;

	public ColoniaFormigasView() {
		percurso = new PercursoController();

		ImportarArquivoCidades imp = new ImportarArquivoCidades();
		Set<Caminho> caminhos = imp.importarAquivo("c:/distancias.csv");

		for (Iterator<Caminho> it = caminhos.iterator(); it.hasNext();) {
			Caminho c = (Caminho) it.next();
			percurso.addCaminho(c);
		}

		ASAlgoritmo algoritmo = new ASAlgoritmo();

		List<FormigaController> formigas = new ArrayList<FormigaController>();
		for (int i = 0; i < percurso.getCidadesPercurso().size(); i++) {
			Cidade atuaCidade = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(1, 6));
			Formiga formiga = new Formiga(i, atuaCidade);

			formigas.add(new FormigaController(formiga, percurso, algoritmo));
		}

		coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);

		graph = new mxGraph();
		graph.setKeepEdgesInBackground(true);
		graph.setCellsLocked(true);

		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();

		try {
			for (Caminho c : percurso.getCaminhosDisponiveis()) {
				this.addVertex(parent, c.getCidadeOrigem());
				this.addVertex(parent, c.getCidadeDestino());

				addEdge(parent, c);
			}

		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				coloniaFormigaAction.setMaximoIteracoes(1);
				coloniaFormigaAction.action();
			}
		});
	}

	private void addEdge(Object parent, Caminho c) {
		if (!mapEdge.containsKey(c)) {
			Object origem = mapVertex.get(c.getCidadeOrigem());
			Object destino = mapVertex.get(c.getCidadeDestino());
			Object obj = graph.insertEdge(parent, null, null, origem, destino);

			mapEdge.put(c, obj);
		}
	}

	private void addVertex(Object parent, Cidade c) {
		if (!mapVertex.containsKey(c)) {
			x = AntSystemUtil.getIntance().getAleatorio(10, 1500);
			y = AntSystemUtil.getIntance().getAleatorio(10, 1000);

			Object obj = graph.insertVertex(parent, c.getNome(), c.getNome(), x, y, LENGHT_VERTEX, LENGHT_VERTEX);
			mapVertex.put(c, obj);
		}
	}

	public void plote() {

	}

	public static void main(String[] args) {
		ColoniaFormigasView frame = new ColoniaFormigasView();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();

		DisplayMode mode = devices[0].getDisplayMode();
		int height = mode.getHeight();
		int width = mode.getWidth();

		frame.setSize(width, height - 30);
		frame.setVisible(true);
	}
}