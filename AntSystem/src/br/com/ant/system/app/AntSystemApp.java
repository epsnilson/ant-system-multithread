package br.com.ant.system.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.ant.system.controller.AntController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.model.Percurso;
import br.com.ant.system.model.Percurso.Distancia;

public class AntSystemApp {

		  public static void main(String[] args) {
					Percurso percurso = new Percurso();
					Cidade bh = new Cidade(1);
					Cidade rj = new Cidade(2);
					Cidade sp = new Cidade(3);
					Cidade es = new Cidade(4);
					Cidade co = new Cidade(5);
					Cidade ba = new Cidade(6);

					percurso.addPercurso(new Distancia(bh, rj, 9.3F));
					percurso.addPercurso(new Distancia(bh, co, 10.6F));

					percurso.addPercurso(new Distancia(rj, bh, 9.3F));
					percurso.addPercurso(new Distancia(rj, sp, 15.3F));
					percurso.addPercurso(new Distancia(rj, es, 5.3F));

					percurso.addPercurso(new Distancia(sp, rj, 15.3F));
					percurso.addPercurso(new Distancia(sp, es, 13.4F));
					percurso.addPercurso(new Distancia(sp, co, 25.3F));

					percurso.addPercurso(new Distancia(es, sp, 13.4F));
					percurso.addPercurso(new Distancia(es, rj, 15.3F));
					percurso.addPercurso(new Distancia(es, ba, 30.4F));

					percurso.addPercurso(new Distancia(co, bh, 10.6F));
					percurso.addPercurso(new Distancia(co, sp, 25.3F));
					percurso.addPercurso(new Distancia(co, ba, 40.6F));

					percurso.addPercurso(new Distancia(ba, co, 40.6F));
					percurso.addPercurso(new Distancia(ba, es, 30.4F));

					List<Formiga> formigas = new ArrayList<Formiga>();

					Random random = new Random(5);
					for (int i = 0; i < 10; i++) {
							  Cidade atuaCidade = percurso.getCidades().get((int) (6 * Math.random()));
							  Formiga formiga = new Formiga(i, atuaCidade);

							  formigas.add(formiga);
					}

					AntController controller = new AntController(formigas, percurso);

					controller.inicializar();
		  }
}
