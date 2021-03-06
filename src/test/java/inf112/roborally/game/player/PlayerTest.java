package inf112.roborally.game.player;

import inf112.roborally.game.enums.PlayerState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;
    private Stack<ProgramCard> stack;

    @Before
    public void setup() {

        player = new Player(0, 0, 1);
        stack = ProgramCard.makeProgramCardDeck();
    }

    @Test
    public void takeDamageTest() {
        player.takeDamage();
        assertEquals(1, player.getDamage());
    }

    @Test
    public void takeDamageTest2() {
        int expected = 1;
        for (int i = 0; i < 9; i++) {
            player.takeDamage();
            assertEquals(expected++, player.getDamage());
        }
    }

    @Test
    public void oneDamageOneLessCard() {
        for (int i = 0; i < 10; i++) {
            assertEquals(9 - i, player.getCardLimit());
            player.takeDamage();
        }
    }

    @Test
    public void zeroDmgDoesNotDestroy() {
        assert (!player.isDestroyed());
    }

    @Test
    public void nineDmgDoesNotDestroy() {
        for (int i = 0; i < 9; i++) {
            player.takeDamage();
        }
        assert (!player.isDestroyed());
    }

    @Test
    public void tenDmgDestroys() {
        for (int i = 0; i < 10; i++) {
            player.takeDamage();
        }
        assert (player.isDestroyed());
        assertEquals(PlayerState.DESTROYED, player.getPlayerState());
    }

    @Test
    public void fiveDmgLocksOneRegister() {
        for (int i = 0; i < 5; i++)
            player.takeDamage();
        assertEquals(true, player.getRegisters().isLocked(4));

        //the other registers should not be locked:
        for (int i = 0; i < 4; i++)
            assertEquals(false, player.getRegisters().isLocked(i));
    }

    @Test
    public void fourDmgDoesNotLocksOneRegister() {
        for (int i = 0; i < 4; i++)
            player.takeDamage();

        //the other registers should not be locked:
        for (int i = 0; i < 5; i++)
            assert (!player.getRegisters().isLocked(i));
    }

    @Test
    public void nineDmgLocksAllRegisters() {
        for (int i = 0; i < 9; i++)
            player.takeDamage();
        //all registers should be locked:
        for (int i = 0; i < 5; i++)
            assert (player.getRegisters().isLocked(i));
    }

    @Test
    public void repairResultsInZeroDmg() {
        for (int i = 0; i < 9; i++)
            player.takeDamage();
        player.repairAllDamage();
        assertEquals(0, player.getDamage());
        for (int i = 0; i < 5; i++)
            assert (!player.getRegisters().isLocked(i));
    }

    @Test
    public void noLockedRegistersReturnsAllCards() {
        for (int i = 0; i < 9; i++) {
            // Player is given nine cards:
            player.getHand().receiveCard(stack.pop());
            if (i < 5)
                // Player puts the five first in registers:
                player.getRegisters().placeCard(0);
        }

        ArrayList<ProgramCard> cardsReturned = player.returnCards();
        assertEquals(9, cardsReturned.size());

        // All registers are empty:
        for (ProgramCard register : player.getRegisters().getAllCards())
            assert (register == null);
    }

    @Test
    public void lockedCardsAreNotReturned() {
        for (int i = 0; i < 9; i++) {
            // Player is given nine cards:
            player.getHand().receiveCard(stack.pop());
            if (i < 5)
                // Player puts the five first in registers:
                player.getRegisters().placeCard(0);
        }
        // Player takes nine damage, locking all registers:
        for (int i = 0; i < 9; i++)
            player.takeDamage();

        // Only cards in hand are returned:

        ArrayList<ProgramCard> cardsReturned = player.returnCards();
        assertEquals(4, cardsReturned.size());

        // All registers contains program cards:
        for (ProgramCard register : player.getRegisters().getAllCards())
            assert (register != null);
    }

    @Test
    public void lockedCardsAreNotReturned2() {
        for (int i = 0; i < 9; i++) {
            // Player is given nine cards:
            player.getHand().receiveCard(stack.pop());
            if (i < 5)
                // Player puts the five first in registers:
                player.getRegisters().placeCard(0);
        }
        // Player takes 5 damage, locking one register:
        for (int i = 0; i < 5; i++)
            player.takeDamage();

        // Cards in hand are returned + 4 from registers:
        ArrayList<ProgramCard> cardsReturned = player.returnCards();
        assertEquals(8, cardsReturned.size());

        // The first 4 registers do not contain program cards:
        for (int i = 0; i < 4; i++)
            assert (player.getRegisters().getAllCards().get(i) == null);
        // The last register does:
        assert (player.getRegisters().getAllCards().get(4) != null);
    }

    @Test
    public void priorityTest() {
        PriorityQueue<ProgramCard> q = new PriorityQueue<>();
        // create 3 players:
        Player p1 = new Player(0, 0, 1);
        Player p2 = new Player(0, 0, 1);
        Player p3 = new Player(0, 0, 1);
        // give them five cards each:
        for (int i = 0; i < 5; i++) {
            p1.getHand().receiveCard(stack.pop());
            p2.getHand().receiveCard(stack.pop());
            p3.getHand().receiveCard(stack.pop());
        }
        // have them all pick the first card and place it in a register:
        p1.getRegisters().placeCard(0);
        p2.getRegisters().placeCard(0);
        p3.getRegisters().placeCard(0);
        // retrieve cards from the first register:
        q.add(p1.getRegisters().getCard(0));
        q.add(p2.getRegisters().getCard(0));
        q.add(p3.getRegisters().getCard(0));
        // print in order of highest priority
        while (!q.isEmpty()) {
            System.out.println(q.poll());
        }
    }

    @Test
    public void repairUnlocksAllRegister() {
        for (int i = 0; i < 9; i++) {
            player.takeDamage();
        }
        player.repairAllDamage();
        assertEquals(5, player.getRegisters().getNumUnlockedRegisters());
    }

    @Test
    public void canNotTakeMoreThanMaxDamage() {
        for (int i = 0; i < 34; i++) {
            player.takeDamage();
        }
        assertEquals(Player.MAX_DAMAGE, player.getDamage());
    }

    @Test
    public void takingTenDamageCausesOneLifeLost() {
        for (int i = 0; i < 10; i++) {
            player.takeDamage();
        }
        assertEquals(true, player.respawn());
        assertEquals(0, player.getDamage());
        assertEquals(2, player.getLives());
    }

    @Test
    public void takingElevenDamageCausesOneLifeLost() {
        for (int i = 0; i < 11; i++) {
            player.takeDamage();
        }
        assertEquals(true, player.respawn());
        assertEquals(0, player.getDamage());
        assertEquals(2, player.getLives());
    }

    @Test
    public void playerNeedsToRespawnBeforeTakingDamageAgain() {
        for (int i = 0; i < 16; i++)
            player.takeDamage();
        assertEquals(Player.MAX_DAMAGE, player.getDamage());
        assertEquals(2, player.getLives());
        assertEquals(true, player.respawn());
    }

    @Test
    public void playerTakesDamageAfterRepsawning() {
        for (int i = 0; i < 16; i++) {
            player.takeDamage();
            player.respawn();
        }
        assertEquals(16 - Player.MAX_DAMAGE, player.getDamage());
        assertEquals(2, player.getLives());
    }


    @Test
    public void take6Damage() {
        for (int i = 0; i < 6; i++)
            player.takeDamage();
        assertEquals(6, player.getDamage());
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeIndexShouldThrowIndexOutOfBounds() {
        player.getHand().receiveCard(stack.pop());
        player.getHand().removeCard(-1);
    }

    @Test
    public void gettingDestroyedLeadsToLosingALife() {
        player.destroy();
        assertEquals(2, player.getLives());
    }

    @Test
    public void checkOutOfLives() {
        assertFalse(player.outOfLives());
    }

    @Test
    public void notOutOfLivesAfterGettingDestroyedOnce() {
        player.destroy();
        assertFalse(player.outOfLives());
    }

    @Test
    public void notOutOfLivesAfterGettingDestroyedTwice() {
        for (int i = 0; i < 2; i++) {
            player.destroy();
            player.respawn();
        }
        assertFalse(player.outOfLives());
    }

    @Test
    public void isOutOfLivesAfterGettingDestroyedThrice() {
        for (int i = 0; i < 3; i++) {
            player.destroy();
            player.respawn();
        }
        assertTrue(player.outOfLives());
    }

    @Test
    public void checkIfHandIsFull() {
        for (int i = 0; i < player.getCardLimit(); i++)
            player.getHand().receiveCard(stack.pop());
        assertTrue(player.getHand().isFull());
    }

    @Test
    public void handIsNotFull() {
        for (int i = 0; i < player.getCardLimit() - 1; i++) {
            player.getHand().receiveCard(stack.pop());
            assertFalse(player.getHand().isFull());
        }
    }

    @Test
    public void getCardInHandReturnsCorrectCards() {
        for (int i = 0; i < player.getCardLimit(); i++) {
            ProgramCard card = stack.pop();
            player.getHand().receiveCard(card);
            assertEquals(card, player.getHand().getCard(i));
        }
    }

    @Test
    public void destroyDestroysPlayer() {
        player.destroy();
        assertEquals(PlayerState.DESTROYED, player.getPlayerState());
        assertEquals(2, player.getLives());
    }

    @Test
    public void tenDamageDestroysRobot() {
        for (int i = 0; i < 10; i++) {
            player.takeDamage();
        }
        assertEquals(PlayerState.DESTROYED, player.getPlayerState());
        assertEquals(2, player.getLives());
    }

    @Test
    public void respawnSetsStateToOperational() {
        player.destroy();
        assertEquals(true, player.respawn());
        assertEquals(PlayerState.OPERATIONAL, player.getPlayerState());
    }

}

