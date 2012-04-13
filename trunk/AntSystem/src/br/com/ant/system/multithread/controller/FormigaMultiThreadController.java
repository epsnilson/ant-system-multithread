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
package br.com.ant.system.multithread.controller;

import br.com.ant.system.algoritmo.ASAlgoritmo;
import br.com.ant.system.controller.FormigaController;
import br.com.ant.system.controller.PercursoController;
import br.com.ant.system.model.Formiga;

public class FormigaMultiThreadController {

	private FormigaController	formigaController;
	private PercursoController	percurso;

	public FormigaMultiThreadController(Formiga formiga, PercursoController percurso, ASAlgoritmo algoritmo) {
		this.formigaController = new FormigaController(formiga, percurso, algoritmo);
		this.percurso = percurso;
	}

	public Formiga call() {
		// Setando o tempo inicial
		formigaController.getFormiga().setTempoInicial(System.currentTimeMillis());

		do {
			formigaController.escolherPercurso();
		} while (!percurso.isFinalizouPercurso(formigaController.getFormiga()));

		// Setando o tempo Final
		formigaController.getFormiga().setTempoFinal(System.currentTimeMillis());

		return formigaController.getFormiga();
	}
}
