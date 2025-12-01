package com.basketbandit.player;

import com.basketbandit.component.Action;
import com.basketbandit.component.Card;
import com.basketbandit.component.Hand;

public class AdvantagePlayer extends Player {
    public AdvantagePlayer(String name) {
        super(name);
        this.addCurrency(9750);
    }

    // basic strategy
    public Action decideAction(Hand hand, Card dealerCard) {
        int playerHandValue = hand.value();
        int playerFirstCardValue = hand.cards().getFirst().value(); // for splits
        int dealerHandValue = dealerCard.value() == 1 ? 11 : dealerCard.value(); // deal with ace

        // split
        if(hand.cards().size() == 2 && mainHand().isPair()) {
            switch(dealerHandValue) {
                case 2, 3, 4 -> {
                    switch(playerFirstCardValue) {
                        case 2, 3, 6, 7, 8, 9, 1 -> {
                            return Action.SPLIT;
                        }
                        case 4 -> {
                            return Action.HIT;
                        }
                    }
                }
                case 5, 6 -> {
                    switch(playerFirstCardValue) {
                        case 2, 3, 4, 6, 7, 8, 9, 1 -> {
                            return Action.SPLIT;
                        }
                    }
                }
                case 7 -> {
                    switch(playerFirstCardValue) {
                        case 2, 3, 7, 8, 1 -> {
                            return Action.SPLIT;
                        }
                        case 4, 6 -> {
                            return Action.HIT;
                        }
                        case 9 -> {
                            return Action.STAND;
                        }
                    }
                }
                case 8, 9 -> {
                    switch(playerFirstCardValue) {
                        case 8, 9, 1 -> {
                            return Action.SPLIT;
                        }
                        case 2, 3, 4, 6, 7 -> {
                            return Action.HIT;
                        }
                    }
                }
                case 10, 1 -> {
                    switch(playerFirstCardValue) {
                        case 8, 1 -> {
                            return Action.SPLIT;
                        }
                        case 2, 3, 4, 6, 7 -> {
                            return Action.HIT;
                        }
                        case 9 -> {
                            return Action.STAND;
                        }
                    }
                }
            }
        }

        // soft, non-split
        if(hand.isSoft()) {
            switch(dealerHandValue) {
                case 2, 7, 8 -> {
                    switch(playerHandValue) {
                        case 13, 14, 15, 16, 17 -> {
                            return Action.HIT;
                        }
                        default -> {
                            return Action.STAND;
                        }
                    }
                }
                case 3 -> {
                    switch(playerHandValue) {
                        case 13, 14, 15, 16 -> {
                            return Action.HIT;
                        }
                        case 17, 18 -> {
                            return Action.DOUBLE;
                        }
                        default -> {
                            return Action.STAND;
                        }
                    }
                }
                case 4 -> {
                    switch(playerHandValue) {
                        case 13, 14 -> {
                            return Action.HIT;
                        }
                        case 15, 16, 17, 18 -> {
                            return Action.DOUBLE;
                        }
                        default -> {
                            return Action.STAND;
                        }
                    }
                }
                case 5, 6 -> {
                    switch(playerHandValue) {
                        case 13, 14, 15, 16, 17, 18 -> {
                            return Action.DOUBLE;
                        }
                        default -> {
                            return Action.STAND;
                        }
                    }
                }
                case 9, 10, 1 -> {
                    switch(playerHandValue) {
                        case 13, 14, 15, 16, 17, 18 -> {
                            return Action.HIT;
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

        // hard, non-split
        switch(dealerHandValue) {
            case 2 -> {
                switch(playerHandValue) {
                    case 4, 5, 6, 7, 8, 9, 12 -> {
                        return Action.HIT;
                    }
                    case 10, 11 -> {
                        return !hand.doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 3 -> {
                switch(playerHandValue) {
                    case 4, 5, 6, 7, 8, 12 -> {
                        return Action.HIT;
                    }
                    case 9, 10, 11 -> {
                        return !hand.doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 4, 5, 6 -> {
                switch(playerHandValue) {
                    case 4, 5, 6, 7, 8 -> {
                        return Action.HIT;
                    }
                    case 9, 10, 11 -> {
                        return !hand.doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 7, 8, 9, 1 -> {
                switch(playerHandValue) {
                    case 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16 -> {
                        return Action.HIT;
                    }
                    case 10, 11 -> {
                        return !hand.doubled() ? Action.DOUBLE : Action.STAND;
                    }
                    default -> {
                        return Action.STAND;
                    }
                }
            }
            case 10 -> {
                switch(playerHandValue) {
                    case 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16 -> {
                        return Action.HIT;
                    }
                    case 11 -> {
                        return !hand.doubled() ? Action.DOUBLE : Action.STAND;
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
