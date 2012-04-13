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
package br.com.ant.system.model;

import java.util.LinkedList;
import java.util.List;

public class Estatistica {
	private int				formigaId;
	private long			tempoGasto;
	private double			distanciaPercorrida;
	private Cidade			cidadeInicial;

	private List<Caminho>	caminhoPercorrido	= new LinkedList<Caminho>();

	public int getFormigaId() {
		return formigaId;
	}

	public void setFormigaId(int formigaId) {
		this.formigaId = formigaId;
	}

	public long getTempoGasto() {
		return tempoGasto;
	}

	public void setTempoGasto(long tempoGasto) {
		this.tempoGasto = tempoGasto;
	}

	public double getDistanciaPercorrida() {
		return distanciaPercorrida;
	}

	public void setDistanciaPercorrida(double distanciaPercorrida) {
		this.distanciaPercorrida = distanciaPercorrida;
	}

	public List<Caminho> getCaminhoPercorrido() {
		return caminhoPercorrido;
	}

	public void setCaminhoPercorrido(List<Caminho> caminhoPercorrido) {
		this.caminhoPercorrido = caminhoPercorrido;
	}

	public Cidade getCidadeInicial() {
		return cidadeInicial;
	}

	public void setCidadeInicial(Cidade cidadeInicial) {
		this.cidadeInicial = cidadeInicial;
	}

}