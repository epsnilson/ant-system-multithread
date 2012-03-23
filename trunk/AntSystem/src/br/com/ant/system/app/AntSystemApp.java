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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.ColoniaFormigaMonothread;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.interfaces.ColoniaFormigasActionInterface;
import br.com.ant.system.model.Caminho;
import br.com.ant.system.model.Cidade;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.util.AntSystemUtil;
import br.com.ant.system.util.ImportarArquivoCidades;

public class AntSystemApp {

	public static void main(String[] args) {
		PercursoController percurso = new PercursoController();

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

		ColoniaFormigasActionInterface coloniaFormigaAction = new ColoniaFormigaMonothread(formigas, algoritmo, percurso);

		coloniaFormigaAction.action();

	}
}
