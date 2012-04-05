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
package br.com.ant.system.notificacao;

import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

public class NotificationController {

	LinkedBlockingDeque<Notificacao>		notificacoes	= new LinkedBlockingDeque<Notificacao>();
	Logger									logger			= Logger.getLogger(this.getClass());

	private static NotificationController	instance;

	public static NotificationController getInstance() {
		if (instance == null) {
			instance = new NotificationController();
		}

		return instance;
	}

	private NotificationController() {
	}

	public void addNotificacao(Notificacao notificao) {
		try {
			notificacoes.put(notificao);
		} catch (InterruptedException e) {
			logger.error("Nao foi possivel incluir a notificacao da fila", e);
		}
	}

	public Notificacao takeNotificacao() {
		try {
			return notificacoes.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void clearNotification() {
		notificacoes.clear();
	}
}
