import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Classe contendo os m�todos de manipula��o do socket dos
 * datagrams no lado do CLIENTE
 */
public abstract class Data {

	private static DatagramSocket clientSocket;
	private final static String endereco = "localhost";
	private final static Integer porta = 50000;
	private static Integer timeout = 1500;

	/**
	 * Cria a conex�o do socket com base no endereco e porta configurados.
	 */
	protected static void conectarServidor() {
		try {
			iniciarSocket();
			System.out.println("Conectando ao jogo para buscar as perguntas...");
			clientSocket.connect(InetAddress.getByName(endereco), porta);
			System.out.println("Conectado com sucesso!");
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar no servidor!");
			System.out.println("Tentando conectar novamente...");
			conectarServidor();
		}
	}

	/**
	 * Fecha a conex�o e o socket.
	 */
	protected static void desconectarServidor() {
		System.out.println("Desconectando da partida...");
		clientSocket.close();
		clientSocket.disconnect();
		System.out.println("Desconectando com sucesso!");
	}

	/**
	 * Inicia o socket atribuindo um limite de tempo (timeout
	 * para receber dados.
	 */
	private static void iniciarSocket() {
		try {
			clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			System.out.println("Erro ao iniciar socket");
			e.printStackTrace();
		}
	}

	/**
	 * Recebe os dados a ser enviados e envia pelo socket j�
	 * pr� configurado. Caso ocorra algum erro no envio a exce��o
	 * � capturada e fica tenta enviar novamente at� obter sucesso.
	 * @param dados
	 */
	protected static void enviarDados(String dados) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(dados.getBytes(), dados.getBytes().length);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("Houve um problema na comunica��o com o servidor...");
			System.out.println("Tentando restabelecer a conex�o...");
			enviarDados(dados);
		}
	}

	/**
	 * Aguarda o servidor retornar os dados solicitados e retorna
	 * os dados em caso de sucesso. Caso o tempo de espera dos dados demore
	 * mais que o timeout configurado ele avisa em tela para o usu�rio que est�
	 * com problema para receber os dados e fica em loop at� conseguir receber os dados.
	 * @return dado recebido
	 */
	protected static String receberDados() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receiveDatagram);
			return new String(receiveDatagram.getData());
		} catch (IOException e) {
			System.out.println("Houve um problema na comunica��o com o servidor...");
			System.out.println("Tentando restabelecer comunica��o...");
			return receberDados();
		}
	}

}
