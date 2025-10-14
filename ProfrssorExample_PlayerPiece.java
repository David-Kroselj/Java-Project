import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Represents a player's game piece -- player 1's pieces (for player index 0) are crabs,
 * player 2's pieces (for player index 1) are lobsters
 * 
 * DISCLAIMER TO STUDENTS: You may adapt this code for your own scenario, but you MUST give Dr. Canada 
 * credit in your "Acknowledgements" document that you'll use for citing any and all help given or received
 * while working on this project. Failure to do so WILL result in a failing grade for the project!
 * 
 * @author bcanada@uscb.edu
 * @version 1.0
 */
public class PlayerPiece extends Actor 
{
    /* FIELDS/INSTANCE VARIABLES */
    // These should almost always be declared `private`; if any of them need to be
    // made available to other classes, you should write a `public` getter and/or
    // setter method for each applicable field or instance variable
    private boolean moveable;   
    private int originalXcoord;
    private int originalYcoord;
    private int gameBoardLocationIndex; // 0-6 dictates crabMovementPath in GameBoard                            
                                        // ???? OR -1 = start, 0-6 = on board, 7 = goal ????
                                        // ???? OR 0 = start, 1-7 = on board, 8 = goal ????
    private int targetGameBoardLocationIndex; 
    private int playerIndex; // 0 for crab (player NUMBER 1), 1 for lobster (player NUMBER 2)
    
    /* CONSTRUCTORS */
    /**
     * Initializes a newly-instantiated PlayerPiece object
     * 
     * @param playerIndex       the index of the player that "owns" this PlayerPiece (0 = crabs, 1 = lobsters)
     * @param originalXcoord    the original X-coordinate of where this playerPiece was located for this player's "starting zone"
     * @param originalYcoord    the original Y-coordinate of where this playerPiece was located for this player's "starting zone"
     */
    public PlayerPiece(int playerIndex, int originalXcoord, int originalYcoord)
    {
        /* 
         * NOTE: the formal parameters `playerIndex`, `originalXcoord`, and `originalYcoord` 
         * are all LOCAL variables with CONSTRUCTOR-WIDE scope. It is common in object-oriented programming
         * to use the SAME NAME for an INSTANCE variable and the LOCAL variable
         * that the constructor will use to initialize the INSTANCE variable -> this is called "shadowing"
         */
        
        // To "disambiguate" an instance variable that is "shadowed" by a local variable that 
        // has the same name, we pre-pend the `this` keyword to the INSTANCE variable so
        // that Java knows we are using the LOCAL variable's value to UPDATE the value
        // of the INSTANCE variable that has the same name. (Remember that assignment 
        // statements associate from RIGHT to LEFT!)
        this.playerIndex = playerIndex;
        this.originalXcoord = originalXcoord;
        this.originalYcoord = originalYcoord;
        
        // initialize other instance variables that aren't assigned using parameter values
        moveable = true;
        gameBoardLocationIndex = -1; // starting position
        targetGameBoardLocationIndex = -1;
    
        // Now, use the `setImage` method to set this PlayerPiece's appearance, using a newly-instantiated
        // GreenfootImage object that is initialized using a String parameter for the corresponding image filename
        // NOTE: Since by this point in the constructor the instance variable `playerIndex`
        //       has been intiialized with the value of the shadowing (same-named) LOCAL variable,
        //       the use of the `this` keyword is technically optional 
        //       (i.e., we could simply check to see if `playerIndex == 0`, without the `this` keyword)
        if ( this.playerIndex == 0 ) {
            setImage( new GreenfootImage("crab.png") );
        } else {
            setImage( new GreenfootImage("lobster.png") );
        } // end if/else
        
    } // end PlayerPiece 3-arg constructor
    
    /**
     * The `act` method for the PlayerPiece is fairly simple
     * since most of the game logic is handled by the GameBoard class;
     * but we will use the `act` method to set the opacity (transparency)
     * of the object depending on whether or not the piece is moveable
     */
    public void act()
    {
        if ( moveable ) {
            getImage().setTransparency( 255 ); // if moveable, piece is fully opaque (0 = transparent, 255 = opaque)
        } else {
            getImage().setTransparency( 128 ); // if moveable, piece is 50% transparent
        } // end if/else
    } // end method act
    
    /**
     * Getter (accessor) method for retrieving this player piece's `moveable` state value (true or false)
     * 
     * NOTE: While it is a common Java convention to name "getter" methods by concatenating
     *       the prefix `get` with the name of the corresponding instance variable (while obeying camelCase rules), 
     *       it is usually preferable (more readable) to use the prefix `is` when the 
     *       corresponding instance variable is of type `boolean`
     */
    public boolean isMoveable() {
        return moveable;
    } // end method isMoveable;
    
    /**
     * Updates the moveable state of this PlayerPiece object
     * 
     * @param moveable whether this object should be moveable or not
     */
    public void setMoveable( boolean moveable ) {
        this.moveable = moveable; 
    } // end method setMoveable
    
    /**
     * Getter (accessor) method for retrieving the original 
     * X-coordinate (horizontal coordinate) of this PlayerPiece
     */
    public int getOriginalXcoord()
    {
        return originalXcoord;
    } // end method getOriginalXcoord
    
    /**
     * Getter (accessor) method for retrieving the original 
     * Y-coordinate (vertical coordinate) of this PlayerPiece
     */
    public int getOriginalYcoord()
    {
        return originalYcoord;
    } // end method getOriginalYcoord
    
    /**
     * Getter (accessor) method for retrieving the current 
     * gameBoardLocationIndex for this PlayerPiece
     */
    public int getGameBoardLocationIndex() {
        return gameBoardLocationIndex;
    } // end method getGameBoardLocationIndex
    
    /**
     * Setter (mutator) method for UPDATING the gameBoardLocationIndex for this PlayerPiece
     * 
     * @param gameBoardLocationIndex    the current gameBoardLocationIndex 
     */
    public void setGameBoardLocationIndex(int gameBoardLocationIndex) 
    {
        /* 
         * NOTE: the formal parameter `gameBoardLocationIndex` is a LOCAL
         * variable with METHOD-WIDE scope. It is common in object-oriented programming
         * to use the SAME NAME for an INSTANCE variable and the LOCAL variable
         * that the method uses to UPDATE the INSTANCE variable -> this is called "shadowing"
         */
        
        // To "disambiguate" an instance variable that is "shadowed" by a local variable that 
        // has the same name, we pre-pend the `this` keyword to the INSTANCE variable so
        // that Java knows we are using the LOCAL variable's value to UPDATE the value
        // of the INSTANCE variable that has the same name. (Remember that assignment 
        // statements associate from RIGHT to LEFT!)
        this.gameBoardLocationIndex = gameBoardLocationIndex;
    } // end method getGameBoardLocationIndex
    
    /**
     * Getter (accessor) method for retrieving the possible
     * game board location index based on the current die roll
     */
    public int getTargetGameBoardLocationIndex() {
        return targetGameBoardLocationIndex;
    } // end method gettargetGameBoardLocationIndex
    
    /**
     * Setter (mutator) method for updating the possible TARGET
     * game board location index based on the current die roll
     * 
     * @param targetGameBoardLocationIndex     The possible TARGET game board location index for this piece, given the current die roll
     */
    public void setTargetGameBoardLocationIndex( int targetGameBoardLocationIndex ) 
    {
        // As above, use the `this` keyword to disambiguate the INSTANCE variable
        // from an identically-named LOCAL variable (Remember that assignment 
        // statements associate from RIGHT to LEFT!)
        this.targetGameBoardLocationIndex = targetGameBoardLocationIndex;
    } // end method settargetGameBoardLocationIndex
    
} // end class Crab
