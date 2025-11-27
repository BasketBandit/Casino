package com.basketbandit.player;

import com.basketbandit.component.Action;
import com.basketbandit.component.Card;

public class AdvantagePlayer extends Player {
    public AdvantagePlayer(String name) {
        super(name);
        this.addCurrency(9750);
    }

    // basic strategy
    public Action decideAction(Card dealerCard) {
        // hard
        int value = dealerCard.value() == 1 ? 11 : dealerCard.value(); // deal with ace
        switch(value) {
            case 2 -> {
                switch(hand().value()) {
                    case 4, 5, 6, 7, 8, 9, 12 -> {
                        return Action.HIT;
                    }
                    case 10, 11 -> {
                        return !hand().doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 3 -> {
                switch(hand().value()) {
                    case 4, 5, 6, 7, 8, 12 -> {
                        return Action.HIT;
                    }
                    case 9, 10, 11 -> {
                        return !hand().doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 4, 5, 6 -> {
                switch(hand().value()) {
                    case 4, 5, 6, 7, 8 -> {
                        return Action.HIT;
                    }
                    case 9, 10, 11 -> {
                        return !hand().doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 7, 8, 9, 1 -> {
                switch(hand().value()) {
                    case 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16 -> {
                        return Action.HIT;
                    }
                    case 10, 11 -> {
                        return !hand().doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 10 -> {
                switch(hand().value()) {
                    case 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16 -> {
                        return Action.HIT;
                    }
                    case 11 -> {
                        return !hand().doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            default -> {
                return Action.STAND;
            }
        }
    }
}
