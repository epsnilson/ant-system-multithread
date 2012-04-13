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

import java.util.concurrent.ArrayBlockingQueue;

import br.com.ant.system.model.Formiga;

public class BufferBlockingClass {

	private static final int			CAPACIDADE_MAXIMA_FORMIGAS	= 1000;

	private ArrayBlockingQueue<Formiga>	queueFormigaExecution		= new ArrayBlockingQueue<Formiga>(CAPACIDADE_MAXIMA_FORMIGAS);

	public void addFomigaExecution(Formiga formiga) throws InterruptedException {
		queueFormigaExecution.put(formiga);
	}

	public Formiga takeFormigaExecution() throws InterruptedException {
		return queueFormigaExecution.take();
	}

}
