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
package br.com.ant.system.action;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;
import br.com.ant.system.multithread.controller.ControladorGeral;

/**
 * 
 * @author j.duarte
 * 
 */
public class ColoniaFormigaMultithread implements ColoniaFormigasActionInterface {

	private PercursoController	percurso;
	private ASAlgoritmo			algoritmo;

	private ControladorGeral	control;
	private int					maximoIteracoes;

	@SuppressWarnings("rawtypes")
	Future						controlFuture;

	public ColoniaFormigaMultithread(PercursoController percursoController, ASAlgoritmo algoritmo) {
		this.percurso = percursoController;
		this.algoritmo = algoritmo;

		algoritmo.inicializarFeromonio(percurso.getCaminhosDisponiveis(), percurso.getCidadesPercurso().size());
	}

	@Override
	public void action() {
		control = new ControladorGeral(algoritmo, percurso);
		control.setMaximoIteracoes(maximoIteracoes);

		controlFuture = Executors.newFixedThreadPool(1).submit(control);
	}

	public void setMaximoIteracoes(int maximo) {
		this.maximoIteracoes = maximo;
	}

	@Override
	public int getMaximoIteracoes() {
		return maximoIteracoes;
	}

	public void addFormiga(Formiga formiga) {
		control.addFormiga(formiga);
	}

	public void waitForEnd() {
		try {
			controlFuture.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
