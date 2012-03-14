package br.com.ant.system.app;

import java.util.ArrayList;
import java.util.List;

import br.com.ant.system.controller.AntController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;

public class AntSystemApp {

		  public static void main(String[] args) {
					System.out.println("Iniciando aplicacao...");
					PercursoController percurso = new PercursoController();

					Cidade bh = new Cidade(1, "bh");
					Cidade rj = new Cidade(2, "rj");
					Cidade sp = new Cidade(3, "sp");
					Cidade es = new Cidade(4, "es");
					Cidade co = new Cidade(5, "co");
					Cidade ba = new Cidade(6, "ba");
					System.out.println("Cidades.......");
					System.out.println(bh.toString());
					System.out.println(rj.toString());
					System.out.println(sp.toString());
					System.out.println(es.toString());
					System.out.println(co.toString());
					System.out.println(ba.toString());
					System.out.println("--------------------------------------");

					percurso.addPercurso(new Caminho(bh, rj, 9.3F));
					percurso.addPercurso(new Caminho(bh, co, 10.6F));

					percurso.addPercurso(new Caminho(rj, bh, 9.3F));
					percurso.addPercurso(new Caminho(rj, sp, 15.3F));
					percurso.addPercurso(new Caminho(rj, es, 5.3F));

					percurso.addPercurso(new Caminho(sp, rj, 15.3F));
					percurso.addPercurso(new Caminho(sp, es, 13.4F));
					percurso.addPercurso(new Caminho(sp, co, 25.3F));

					percurso.addPercurso(new Caminho(es, sp, 13.4F));
					percurso.addPercurso(new Caminho(es, rj, 15.3F));
					percurso.addPercurso(new Caminho(es, ba, 30.4F));

					percurso.addPercurso(new Caminho(co, bh, 10.6F));
					percurso.addPercurso(new Caminho(co, sp, 25.3F));
					percurso.addPercurso(new Caminho(co, ba, 40.6F));

					percurso.addPercurso(new Caminho(ba, co, 40.6F));
					percurso.addPercurso(new Caminho(ba, es, 30.4F));

					List<Formiga> formigas = new ArrayList<Formiga>();

					for (int i = 0; i < 10; i++) {
							  Cidade atuaCidade = percurso.getCidades().get(AntSystemUtil.getIntance().getAleatorio(1, 6));
							  Formiga formiga = new Formiga(i, atuaCidade);
							  System.out.println("--------------------------------");
							  System.out.println(formiga.toString());

							  formigas.add(formiga);
					}

					AntController controller = new AntController(formigas, percurso);

					controller.inicializar();

					System.out.println("Finalizando aplicação...");
		  }
}
