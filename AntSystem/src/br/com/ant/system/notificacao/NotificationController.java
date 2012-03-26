package br.com.ant.system.notificacao;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import br.com.ant.system.model.Caminho;
import br.com.ant.system.view.ColoniaFormigasView;

public class NotificationController implements Runnable {

	private static final int				MAX_CAPACITY	= 1000;
	ArrayBlockingQueue<Notificacao>			notificacoes	= new ArrayBlockingQueue<Notificacao>(MAX_CAPACITY);
	Logger									logger			= Logger.getLogger(this.getClass());

	ColoniaFormigasView						view;
	private static NotificationController	instance;

	public static NotificationController getInstance() {
		if (instance == null) {
			instance = new NotificationController();
		}

		return instance;
	}

	private NotificationController() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public ColoniaFormigasView getView() {
		return view;
	}

	public void setView(ColoniaFormigasView view) {
		this.view = view;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Notificacao notificacao = notificacoes.take();
				Object obj = notificacao.getObj();

				if (obj instanceof Caminho) {
					Caminho c = (Caminho) obj;

					if (view != null) {
						view.updateEdge(c);
					}
				}
				Thread.sleep(50);
			} catch (InterruptedException e) {
				logger.error("Ocorreu um erro ao enviar a notificacao: ", e);
			}
		}
	}

	public void addNotificacao(Notificacao notificao) {
		notificacoes.offer(notificao);
	}
}
