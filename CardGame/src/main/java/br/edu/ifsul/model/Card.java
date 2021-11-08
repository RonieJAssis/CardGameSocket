package br.edu.ifsul.model;

import br.edu.ifsul.enums.CardValue;

/**
 *
 * @author Roniê Julian de Assis
 * @author Eliel Alves da Silva
 * Instituto Federal Sul-rio-grandense
 * Sistemas Distribuídos 1
 * Trabalho 2ª Etapa
 *
 */
public class Card implements Comparable<Card>{
    private CardValue value;
    private String type;

    // Getters e Setters
    public CardValue getValue() {
        return value;
    }

    public void setValue(CardValue value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    // Compara as cartas para a realização dos sorts
    @Override
    public int compareTo(Card c) {
        if (this.value.getCardValue() < c.value.getCardValue()) {
            return -1;
        }
        
        if (this.value.getCardValue() > c.value.getCardValue()) {
            return 1;
        }

        return 0;
    }
}
