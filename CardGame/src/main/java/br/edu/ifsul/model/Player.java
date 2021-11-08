package br.edu.ifsul.model;

import java.io.PrintStream;
import java.net.Socket;
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
public class Player {
    
    private List<Card> hand;
    private boolean adm;
    private PrintStream endPoint;
    private Socket socket;
    private String name;
    private boolean turn;
    private int position;

    // Construtor
    public Player() {
        this.adm = false;
    }
    
    // Adiciona uma lista de cartas a mão
    public void reciveCards(List<Card> cards) {
       hand = cards;
    }
    
    // Discarta uma carta com o indice informado
    public Card discart(int i) {
        return hand.remove(i);
    }

    // Recebe uma nova carta
    public void takeNewCard(Card c) {
        hand.add(c);
    }

    // Ordena a mão do menor ao maior valor
    public void sortHand() {
        Collections.sort(hand);
    }
    
    // Checa se o player ganhou com uma sequencia de mesmo naipe(1,2,3 onde 1 tambem equivale a 14)
    public boolean checkSequenceWin() {
        if(hand.get(1).getValue().getCardValue() == hand.get(0).getValue().getCardValue()+1) {
            if((hand.get(2).getValue().getCardValue() == hand.get(1).getValue().getCardValue()+1)||((hand.get(2).getValue().getCardValue() == 14) && hand.get(0).getValue().getCardValue() == 2)) {
                if((hand.get(0).getType().equals(hand.get(1).getType())) && (hand.get(0).getType().equals(hand.get(2).getType())) && (hand.get(2).getType().equals(hand.get(1).getType()))) {
                    return true;
                }
            }   
        }

        return false;
    }

    // Checa se o player ganhou com uma trinca de númers iguais de naipe diferente
    public boolean checkEqualsWin() {
        if(hand.get(1).getValue().getCardValue() == hand.get(0).getValue().getCardValue()) {
            if(hand.get(2).getValue().getCardValue() == hand.get(1).getValue().getCardValue()) {
                if(!(hand.get(0).getType().equals(hand.get(1).getType())) && !(hand.get(0).getType().equals(hand.get(2).getType())) && !(hand.get(2).getType().equals(hand.get(1).getType()))) {
                    return true;
                }
            }   
        }

        return false;
    }

    // Retorna as cartas da mão do jogador
    public String showCards() {
        String s = "";
        
        for(Card c : hand){
            s = s + c.getValue() + " de " + c.getType() + "    ";
        }

        return s;
    }
    
    // Getters e Setters
    public List<Card> getHand() {
        return hand;
    }
    
    public PrintStream getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PrintStream endPoint) {
        this.endPoint = endPoint;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isAdm() {
        return adm;
    }

    public void setAdm(boolean adm) {
        this.adm = adm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
}
