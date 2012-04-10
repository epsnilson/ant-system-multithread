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

public class Notificacao {
	private Object			obj;
	private NotificacaoEnum	tipoNotificacao;

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public NotificacaoEnum getTipoNotificacao() {
		return tipoNotificacao;
	}

	public void setTipoNotificacao(NotificacaoEnum tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}

	public static enum NotificacaoEnum {
		CAMINHO, FORMIGA, FEROMONIO, MELHOR_CAMINHO;
	}
}
