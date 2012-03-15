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

import java.util.ArrayList;
import java.util.List;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;

public class AntSystemApp {

	public static void main(String[] args) {
		PercursoController percurso = new PercursoController();

		Cidade bh = new Cidade(1, "bh");
		Cidade rj = new Cidade(2, "rj");
		Cidade sp = new Cidade(3, "sp");
		Cidade es = new Cidade(4, "es");
		Cidade co = new Cidade(5, "co");
		Cidade ba = new Cidade(6, "ba");

		percurso.addPercurso(bh, rj, 9.3F);
		percurso.addPercurso(bh, co, 10.6F);

		percurso.addPercurso(rj, sp, 15.3F);
		percurso.addPercurso(rj, es, 5.3F);

		percurso.addPercurso(sp, es, 13.4F);
		percurso.addPercurso(sp, co, 25.3F);

		percurso.addPercurso(es, ba, 30.4F);

		percurso.addPercurso(co, ba, 40.6F);

		List<Formiga> formigas = new ArrayList<Formiga>();

		for (int i = 0; i < 10; i++) {
			Cidade atuaCidade = percurso.getCidadesPercurso().get(AntSystemUtil.getIntance().getAleatorio(1, 6));
			Formiga formiga = new Formiga(i, atuaCidade);

			formigas.add(formiga);
		}

		ASAlgoritmo algoritmo = new ASAlgoritmo();

		FormigaController controller = new FormigaController(formigas, percurso, algoritmo);

		controller.executarAlgoritmo();

	}
}
