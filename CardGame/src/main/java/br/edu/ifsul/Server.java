package br.edu.ifsul;

import br.edu.ifsul.model.Card;
import br.edu.ifsul.model.Deck;
import br.edu.ifsul.model.Player;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Roniê Julian de Assis
 * @author Eliel Alves da Silva
 * Instituto Federal Sul-rio-grandense
 * Sistemas Distribuídos 1
 * Trabalho 2ª Etapa
 *
 */

public class Server extends Thread{
    // private static volatile List<Player> players;
    private static List<Player> players;
    private Player player;
    private Socket connection;
    private static Deck deck;
    private String userToken;
    private static String connectToken;
    private static volatile boolean end;
    private static String winner;
    private static boolean start;
    private static Card trashCard;
    private static int position;

    // Construtor
    public Server(Player player) {
        this.player = player;
    }
    
    // Função responsável por printar todos os jogadores conectados
    public String listPlayers(){

        int i =0;
        String out = "";

        for(Player p : this.getPlayers()){
            if(p.isAdm()) { // Printa tag ADM caso o jogador seja o administrador
                out = out + "[ADM]";
            }

            out = out + p.getName() +"\n";
        }
        return out;

    }

    // Verifica se todos os jogadores estão prontos 
    public boolean checkStart(){

        for(Player p : players){
            if(!p.isTurn()){
                return false;
            }
        }
        return true;

    }
    
    // Instancia Thread para cada cliente
    public void run() {
        try {
            BufferedReader startingPoint = new BufferedReader(new InputStreamReader(player.getSocket().getInputStream()));
            PrintStream endPoint = new PrintStream(player.getSocket().getOutputStream());
            player.setEndPoint(endPoint);
            this.userToken = startingPoint.readLine();
            
            // Se o token do usuário for nulo, remove o jogador
            if (this.userToken == null) {
                players.remove(player);
                return;
            }
            
            // Caso seja o primeiro jogador (administrador), define o token de conexão e exibe mensagem
            if (player.isAdm()) {
                connectToken = userToken;
                System.out.println("Sucesso: Token definido.");
                endPoint.println("Obs: Você é o administrador!");
            } else { 

                if (!userToken.equals(connectToken)) { // Tenta fazer conexão, se o token é inválido, remove jogador
                    players.remove(player);
                    endPoint.println("Erro: Token inválido!");
                    return;
                } else { // Conexão bem sucedida
                    endPoint.println("Sucesso: Conexão realizada!");
                }

            }

            String name = startingPoint.readLine();
            this.player.setName(name);
            this.player.setPosition(position);
            position +=1; 
            String line = "n";

            while((line != null && !(line.trim().equals(""))) && !this.isStart()){
                if(!(this.player.isTurn())||this.player.isAdm()){
                    endPoint.println("1. Lista Jogadores\n2. Mudar o status para pronto\n3. Iniciar Jogo(somente ADM): ");
                    line = startingPoint.readLine();

                    switch (line) {

                        case "1": // Listar jogadores
                            endPoint.println(this.listPlayers());
                            break;
                        case "2": // Mudar o status do jogador
                            this.player.setTurn(true);
                            this.player.reciveCards(deck.giveCards()); 
                            this.player.sortHand(); 
                            endPoint.println("Aguardando os outros jogadores");
                            break;
                         case "3": // Inicia o jogo
                            if(this.player.isAdm()) {
                                if(checkStart()) {
                                    this.setStart(true);
                                } else {
                                    endPoint.println("Erro: Só é possivel iniciar a partida se todos os jogadores estiverem prontos!");
                                }
                            } else {
                                endPoint.println("Erro: Somente administradores podem acessar esta função!");
                            }
                            break;
                        default: // Opção inválida
                            endPoint.println("Erro: Opção inválida!");
                            break;

                    }
                }
                
            }

            // Altera o status do turno para falso
            this.player.setTurn(false);
            line = "n";
            endPoint.println("Atenção: O jogo irá começar!");

            // Ativa o turno do primeiro jogador
            players.get(0).setTurn(true);

            while ((line != null && !(line.trim().equals(""))) && !end) {

                if(this.player.isTurn()) {

                    endPoint.println("É seu turno e a carta disponivel é " + trashCard.getValue() + " de "+trashCard.getType());
                    endPoint.println("Suas cartas são: " + this.player.showCards());
                    endPoint.println("1. Pega uma nova carta, 2. Pega a carta disponivel");
                    
                    line = startingPoint.readLine();
                    
                    switch (line) {
                        
                        // Pega uma nova carta
                        case "1":
                            trashCard = deck.giveFirstCard();
                            this.player.takeNewCard(trashCard);
                            endPoint.println(this.player.showCards());
                            endPoint.println("qual carta deseja retirar(1,2,3,4)");
                            line = startingPoint.readLine();
                            trashCard = this.player.discart(Integer.parseInt(line)-1);
                            deck.pushBack(trashCard);
                            this.player.sortHand();
                            break;
                        
                        // Pega a carta disponível (trashCard)    
                        case "2":
                            trashCard = deck.giveLastCard();
                            this.player.takeNewCard(trashCard);
                            endPoint.println(this.player.showCards());
                            endPoint.println("qual carta deseja retirar(1,2,3,4)");
                            line = startingPoint.readLine();
                            trashCard = this.player.discart(Integer.parseInt(line)-1);
                            deck.pushBack(trashCard);
                            this.player.sortHand();
                            break;
                        default:
                            endPoint.println("opção invalida");
                            break;
                    }
                    
                    // Checa se o player ganhou com sua jogada
                    if(this.player.checkEqualsWin() || this.player.checkSequenceWin()){
                        this.setWinner(this.player.getName());
                       this.setEnd(true);
                    }
                    
                    // Ativa o turno do proximo jogador
                    if (this.player.getPosition() == position - 1) {

                        this.player.setTurn(false);
                        this.getPlayers().get(0).setTurn(true);
                        PrintStream next = (PrintStream) this.getPlayers().get(0).getEndPoint();

                    } else {
                        this.player.setTurn(false);
                        this.getPlayers().get(this.player.getPosition() + 1).setTurn(true);
                        PrintStream next = (PrintStream) this.getPlayers().get(this.player.getPosition() + 1).getEndPoint();
                    }

                    this.setPlayers(this.getPlayers());

                }
            }

            endPoint.println("[FIM] A partida acabou, o ganhador foi " + winner);
            endPoint.println("Adeus!");
            players.remove(endPoint);
            players.remove(this.player);
            connection.close();
            
        } catch (Exception e) {
            System.out.println("IOException: " + e);
        }
    }

    // Função Main
    public static void main(String[] args) {
        
        // Inicializa variaveis statics
        // players = new ArrayList<>();
        players = Collections.synchronizedList(new ArrayList<>());
        deck = new Deck();
        deck.initializeTwoDeck();
        deck.shuffle();
        end = false;
        start = false;
        
        // Disponibiliza a primeira trashCard
        trashCard = deck.giveFirstCard();
        deck.pushBack(trashCard);
        position = 0;

        try {
            // Inicialzia o socket na porta desejada
            ServerSocket s = new ServerSocket(2222);
            while (true) {

                // Mostra se o token esta definido com um operador ternario
                System.out.println("Esperando conexão...\nIP: 127.0.0.1\nPorta: 2222\nToken: "
                        + ((connectToken != null) ? connectToken : "token não definido ") + " ...");
                Socket connection = s.accept();

                // Novo player do client
                Player player = new Player();
                player.setSocket(connection);

                // Se for o primeiro da lista o torna adm
                if (players.size() == 0) {
                    player.setAdm(true);
                }
                players.add(player);

                System.out.println("Conectou!: " + connection.getRemoteSocketAddress());

                // Cria a thread do client
                Thread t = new Server(player);
                t.start();

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    
    }


    // Getters e Setter normais 
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    // Getters e Setters com funções de sincronização entre todos os clientes
    public synchronized static List<Player> getPlayers() {
        return players;
    }

    public synchronized static void setPlayers(List<Player> players) {
        Server.players = players;
    }

    public synchronized static Deck getDeck() {
        return deck;
    }

    public synchronized static void setDeck(Deck deck) {
        Server.deck = deck;
    }

    public synchronized static String getConnectToken() {
        return connectToken;
    }

    public synchronized static void setConnectToken(String connectToken) {
        Server.connectToken = connectToken;
    }

    public synchronized static boolean isEnd() {
        return end;
    }

    public synchronized static void setEnd(boolean end) {
        Server.end = end;
    }

    public synchronized static String getWinner() {
        return winner;
    }

    public synchronized static void setWinner(String winner) {
        Server.winner = winner;
    }

    public synchronized static boolean isStart() {
        return start;
    }

    public synchronized static void setStart(boolean start) {
        Server.start = start;
    }

    public synchronized static Card getTrashCard() {
        return trashCard;
    }

    public synchronized static void setTrashCard(Card trashCard) {
        Server.trashCard = trashCard;
    }

    public synchronized static int getPosition() {
        return position;
    }

    public synchronized static void setPosition(int position) {
        Server.position = position;
    }

}
