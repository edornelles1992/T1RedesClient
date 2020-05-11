import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Classe contendo os m�todos de manipula��o do socket e dos
 * datagrams no lado do CLIENTE
 */
public abstract class Data {

	private static DatagramSocket clientSocket;
	private final static String endereco = "localhost";
	protected final static Integer porta1 = 40000;
	protected final static Integer porta2 = 50000;
	private static Integer timeout = 1500;

	/**
	 * Cria a conex�o do socket com base no endereco e porta configurados.
	 */
	protected static void conectarServidor(Integer porta) {
		try {
			iniciarSocket();
			clientSocket.connect(InetAddress.getByName(endereco), porta);
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar no servidor!");
			System.out.println("Tentando conectar novamente...");
			conectarServidor(porta);
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
	 * Retorna os dados solicitados OU um erro configurado de servidor cheio.
	 * @return dado recebido
	 */
	protected static String receberDados() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receiveDatagram);
			return tratarResposta(new String(receiveDatagram.getData()).trim());
		} catch (IOException e) {
			System.out.println("Houve um problema na comunica��o com o servidor...");
			System.out.println("Tentando restabelecer comunica��o...");
			return receberDados();
		}
	}

	/**
	 * Recebe os dados vindo do servidor e valida se foi recebido o erro de slot ocupado.
	 * Caso ocorra erro ele valida em qual porta foi a tentiva e tenta conectar no outro slot.
	 * Em caso de sucesso retorna os dados recebidos.
	 * @param dados
	 * @return dados recebidos
	 */
	private static String tratarResposta(String dados) {
		if (dados.equals("ERRO: Slot Ocupado")) {
			conectarServidor(clientSocket.getPort() == porta1 ? porta2 : porta1); //tenta conectar no outro slot
			System.out.println("Procurando vaga para continuar...");
			return dados; //retorna o erro para poder enviar a solicita��o novamente.
		} else {
			return dados; //retorna os dados recebidos
		}
	}

}
