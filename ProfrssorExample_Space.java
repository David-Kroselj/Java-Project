import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Represents a GameBoard space and its associated attributes (i.e., is it a safe space or not, is it occupied 
 * by a given player's piece or not, etc.)
 * 
 * DISCLAIMER TO STUDENTS: You may adapt this code for your own scenario, but you MUST give Dr. Canada 
 * credit in your "Acknowledgements" document that you'll use for citing any and all help given or received
 * while working on this project. Failure to do so WILL result in a failing grade for the project!
 * 
 * @author bcanada@uscb.edu
 * @version 1.0
 */
public class Space extends Actor
{
    /* FIELDS */
    private boolean[] occupiedByPieceForPlayerIndex;
    private boolean safeSpace;
    
    /* CONSTRUCTORS */
    /**
     * Initializes the attributes of this Space object
     * 
     * @param safeSpace     true if this is a safe space, false otherwise
     */
    public Space( boolean safeSpace ) 
    {
        occupiedByPieceForPlayerIndex = new boolean[]{false, false};
        this.safeSpace = safeSpace;
        if ( this.safeSpace ) {
            setImage( new GreenfootImage("grass_top.png") );
        } else {
            setImage( new GreenfootImage("stone.png") );
        }
    } // end 1-arg Space constructor
    
    /* METHODS */
    /**
     * Act - do whatever the Stone wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }  // end method act
    
    /**
     * This "getter" (accessor) method returns `true` if this space is occupied by a piece
     * belonging to the player corresponding to playerIndex;
     * 
     * @param playerIndex   the index of the player whose piece may be occupying this Space
     */
    public boolean isOccupiedByPieceForPlayerIndex(int playerIndex) {
        return occupiedByPieceForPlayerIndex[playerIndex];
    } // end method isOccupiedByCrab
    
    /**
     * This "setter" (mutator) method updates the value of the current ("this") object's 
     * INSTANCE VARIABLE value of `occupiedByCrab` using the boolean value assigned to the 
     * LOCAL variable of the same name
     * 
     * @param playerIndex   the index of the player whose piece may be occupying this Space
     * @param occupied      true if this Space is now occupied by a piece belonging to the given player
     */
    public void setOccupiedByPieceForPlayerIndex( int playerIndex, boolean occupied ) {
        this.occupiedByPieceForPlayerIndex[playerIndex] = occupied;
    } // end method setOccupiedByCrab
 
    /**
     * This "getter" method returns `true` if this space is a safe space
     */
    public boolean isSafeSpace() {
        return safeSpace;
    } // end method isSafeSpace
    
} // end class Space
