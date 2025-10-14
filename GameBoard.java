import greenfoot.*;  // imports Actor, World, Greenfoot, GreenfootImage

/**
 * 
 * The Gameboard is where all the real work is done. The GameBoard manages, handles, and carries out every function and
 * mechanic this game has to offer. 
 * 
 * DISCLAIMER: Code adapted from a codebase by "bcanada@uscb.edu"
 * 
 * @author dkroselj@email.uscb.edu
 * @version 1.0
 */
public class GameBoard extends World
{
    /* FIELDS (instance variables and constants) */
    
    private boolean[] playerIsHumanForPlayerIndex;      // 1-D array that stores `true` (human) or `false` (CPU) for the player at the given array index
    
    private Space[] spaces;                             // GameBoard "has-a" set of Spaces
    public static final int HEIGHT = 800;               // set GameBoard height as a CONSTANT; is public, so access using `Gameboard.HEIGHT` from outside this class
    public static final int WIDTH = 700;                // set GameBoard width as a CONSTANT; is public, so access using`Gameboard.WIDTH` from outside this class

    private PlayerPiece[][] playerPieces;               // 2-D array of PlayerPiece object references; row index 0 (cups) or 1 (snakes)
                                                        // while column index refers to the index of the piece belonging to the player
                                                
    private final int NUMBER_OF_PIECES_PER_PLAYER = 4;  // set this value here, once, then use the constant to get this value elsewhere in the class
    private final int DELAY_LENGTH = 60;                // Adjust this value to set the duration used when Greenfoot.delay is called 
                                                        // (Asumming your slider is at 50% -> 60 fps, then a DELAY_LENGTH of 60 will be 1 second)
    
    private int[] cupStartYCoords;                     // 1-D array of ints for storing the starting Y coordinates for the cups (initialized in constructor)
    private int[] snakeStartYCoords;                  // 1-D array of ints for storing the starting Y coordinates for the snakes (initialized in constructor)
    
    private Space[][] movementPathForPlayerIndex;       // 2-D array used to "map" each player's movement path to corresponding Spaces on the game board
    
    private Die die;                                    // GameBoard "has-a" reference to a Die object
    private int dieRollValue;                           // GameBoard "has-a" value of the most recent die roll
    private final int DIE_TEXT_VERTICAL_OFFSET = 60;    // a CONSTANT used by the `showText` method to determine where to display text beneath the Die's y-position
   
    private int state;                                  // GameBoard "has-a" state (an int value that can be referred to by the constants below)
    
    /* `public` CONSTANTS for keeping track of the game's overall state */
    public static final int BOARD_SETUP = 0;            // accessed by BOARD_SETUP within GameBoard; is public, so access using `Gameboard.BOARD_SETUP` elsewhere
    public static final int PLAYER1_ROLL_DIE = 1;       // accessed by PLAYER1_ROLL_DIE within GameBoard; is public, so access using `Gameboard.PLAYER1_ROLL_DIE` elsewhere
    public static final int PLAYER1_MOVE_CUP = 2;      // accessed by PLAYER1_MOVE_CUP within GameBoard; is public, so access using `Gameboard.PLAYER1_MOVE_CUP` elsewhere
    public static final int PLAYER2_ROLL_DIE = 3;       // accessed by PLAYER2_ROLL_DIE within GameBoard; is public, so access using `Gameboard.PLAYER2_ROLL_DIE` elsewhere
    public static final int PLAYER2_MOVE_SNAKE = 4;   // accessed by PLAYER2_MOVE_SNAKE within GameBoard; is public, so access using `Gameboard.PLAYER2_MOVE_SNAKE` elsewhere
    public static final int PLAYER1_WIN = 5;            // accessed by PLAYER1_WIN within GameBoard; is public, so access using `Gameboard.PLAYER1_WIN` elsewhere
    public static final int PLAYER2_WIN = 6;            // accessed by PLAYER2_WIN within GameBoard; is public, so access using `Gameboard.PLAYER2_WIN` elsewhere

    private boolean readyToExitState;                   // GameBoard "has-a" value for tracking whether or not we are ready to update the game's overall state
    
    
    private int[] goalCountForPlayerIndex;              // GameBoard "has-a" value for tracking the number of cups (for player index 0) or snakes (for player index 1) 
                                                        // that have moved into the corresponding player's goal (i.e., movement path index 20)
                      
                                                        
    /* CONSTRUCTOR(S) */
    /**
     * Initialize the GameBoard object and its variable fields
     */
    public GameBoard() 
    {
        super(WIDTH, HEIGHT, 1, false); // portrait orientation for mobile but fits on desktop, 1 pixel per cell, world is UN-bounded        

        /* initialize instance variables */
        playerIsHumanForPlayerIndex = new boolean[]{ true, false }; // `true, false`  -> cup (player index 0) is human, snake (index 1) is CPU
                                                                    // `true, true`   -> cup is human, snake is also human
                                                                    // `false, false` -> cup is CPU, snake is also CPU

        spaces = new Space[20];     // "empty" array of 20 `null` references; Space assignments are handled by the `prepare` method below
        
        playerPieces = new PlayerPiece[2][NUMBER_OF_PIECES_PER_PLAYER]; // Always 2 players, with 4 pieces per player
         
        state = BOARD_SETUP;

        readyToExitState = false;
        
        goalCountForPlayerIndex = new int[2]; // set size of array to be 2 elements long 
        goalCountForPlayerIndex[0] = 0; // at start of game, no cups in the goal
        goalCountForPlayerIndex[1] = 0; // at start of game, no snakes in the goal
        
        cupStartYCoords = new int[]{ HEIGHT -230, HEIGHT - 170, HEIGHT - 110, HEIGHT - 50 };    
        snakeStartYCoords = new int[]{ HEIGHT -220, HEIGHT - 160, HEIGHT - 100, HEIGHT -40 }; 

        // sets up the physical board layout
        prepare();
    } // end GameBoard no-arg constructor

    /* METHODS */
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        // setBackground sets the games background
        setBackground("HD-wallpaper-blue-green-circuit-abstract-amoled-black-board-circuit-circuits-green-tron-thumbnail.jpg");

        // Each tile is broken up by a space and seperated into rows 
        
        // Row 1 Tiles
        spaces[0] = new Space();
        spaces[0].setImage("C++Space.png"); // each of these sets the image that is designated to that specific tile
        addObject(spaces[0], 250, 50);
        
        spaces[1] = new Space();
        spaces[1].setImage("JavaScriptSpace.png");
        addObject(spaces[1], 350, 50);
        
        spaces[2]  = new Space();
        spaces[2].setImage("C++Space.png");
        addObject(spaces[2], 450, 50);
        
        // Row 2 Tiles
        spaces[3] = new Space();
        spaces[3].setImage("StartingSpaceP1.png");
        addObject(spaces[3], 250, 150);
        
        spaces[4] = new Space();
        spaces[4].setImage("PythonSpace.png");
        addObject(spaces[4], 350, 150);
        
        spaces[5] = new Space();
        spaces[5].setImage("StartingSpaceP2.png");
        addObject(spaces[5], 450, 150);
        
        // Row 3 Tiles
        spaces[6] = new Space();
        spaces[6].setImage("C#Space.png");
        addObject(spaces[6], 350, 250);
        
        // Row 4 Tiles
        spaces[7] = new Space();
        spaces[7].setImage("JavaScriptSpace.png");
        addObject(spaces[7], 350, 350);
        
        // Row 5 Tiles
        spaces[8] = new Space();
        spaces[8].setImage("EndSpaceP1.png");
        addObject(spaces[8], 250, 450);
        
        spaces[9] = new Space();
        spaces[9].setImage("JavaSpace.png");
        addObject(spaces[9], 350, 450);
        
        spaces[10] = new Space();
        spaces[10].setImage("EndSpaceP2.png");
        addObject(spaces[10], 450, 450);
        
        //Row 6 Tiles
        spaces[11] = new Space();
        spaces[11].setImage("JavaScriptSpace.png");
        addObject(spaces[11], 250, 550);
        
        spaces[12] = new Space();
        spaces[12].setImage("C#Space.png");
        addObject(spaces[12], 350, 550);
        
        spaces[13] = new Space();
        spaces[13].setImage("JavaScriptSpace.png");
        addObject(spaces[13], 450, 550);
        
        // Row 7 Tiles
        spaces[14] = new Space();
        spaces[14].setImage("PythonSpace.png");
        addObject(spaces[14], 250, 650);
        
        spaces[15] = new Space();
        spaces[15].setImage("JavaScriptSpace.png");
        addObject(spaces[15], 350, 650);
        
        spaces[16] = new Space();
        spaces[16].setImage("PythonSpace.png");
        addObject(spaces[16], 450, 650);
        
        // Row 8 Tiles
        spaces[17] = new Space();
        spaces[17].setImage("JavaSpace.png");
        addObject(spaces[17], 250, 750);
        
        spaces[18] = new Space();
        spaces[18].setImage("CSpace.png");
        addObject(spaces[18], 350, 750);
        
        spaces[19] = new Space();
        spaces[19].setImage("JavaSpace.png");
        addObject(spaces[19], 450, 750);

        
        movementPathForPlayerIndex = new Space[2][14]; // movementPathForPlayerIndex array has 2 rows (one per player);
                                                      // each player has a path of 14 board spaces to move through
        
        // 2D array used to indicate which player this movement path is indicated for, in this case it is player 1 so the first bracket contains a 0
        // and the second bracket contains which number tile in the movement path a space correlates to
        // and example of that is listed below
        
        movementPathForPlayerIndex[0][0] = spaces[3]; // EXAMPLE: Player 1, space 2, and space 2 is equal to space 3 or the 4th space on the gameboard
        movementPathForPlayerIndex[0][1] = spaces[0]; 
        movementPathForPlayerIndex[0][2] = spaces[1]; 
        movementPathForPlayerIndex[0][3] = spaces[4]; 
        movementPathForPlayerIndex[0][4] = spaces[6]; 
        movementPathForPlayerIndex[0][5] = spaces[7]; 
        movementPathForPlayerIndex[0][6] = spaces[9];
        movementPathForPlayerIndex[0][7] = spaces[12];
        movementPathForPlayerIndex[0][8] = spaces[15];
        movementPathForPlayerIndex[0][9] = spaces[18];
        movementPathForPlayerIndex[0][10] = spaces[17];
        movementPathForPlayerIndex[0][11] = spaces[14];
        movementPathForPlayerIndex[0][12] = spaces[11];
        movementPathForPlayerIndex[0][13] = spaces[8];

        
        // This has the same logic as the block of code above but the frist set of brackets contains a 1 to indicate that this is the movement path for player 2
        movementPathForPlayerIndex[1][0] = spaces[5]; 
        movementPathForPlayerIndex[1][1] = spaces[2];
        movementPathForPlayerIndex[1][2] = spaces[1]; 
        movementPathForPlayerIndex[1][3] = spaces[4]; 
        movementPathForPlayerIndex[1][4] = spaces[6]; 
        movementPathForPlayerIndex[1][5] = spaces[7]; 
        movementPathForPlayerIndex[1][6] = spaces[9];
        movementPathForPlayerIndex[1][7] = spaces[12];
        movementPathForPlayerIndex[1][8] = spaces[15];
        movementPathForPlayerIndex[1][9] = spaces[18];
        movementPathForPlayerIndex[1][10] = spaces[19];
        movementPathForPlayerIndex[1][11] = spaces[16];
        movementPathForPlayerIndex[1][12] = spaces[13];
        movementPathForPlayerIndex[1][13] = spaces[10];

        for ( int cupIndex = 0; cupIndex < playerPieces[0].length; cupIndex++ )
        {
            // call 3-arg constructor to set the cups's initial position
            int cupXcoord = (int)(0.1 * WIDTH);
            int cupYcoord = cupStartYCoords[cupIndex];
            playerPieces[0][cupIndex] = new PlayerPiece(0, cupXcoord, cupYcoord ); // calls 3-arg PlayerPiece constructor
            addObject( playerPieces[0][cupIndex], cupXcoord, cupYcoord );
        } // end for
        
        for ( int snakeIndex = 0; snakeIndex < playerPieces[1].length; snakeIndex++ )
        {
            // call 3-arg constructor to set the snake's initial position
            int snakeXcoord = (int)(0.9 * WIDTH);
            int snakeYcoord = snakeStartYCoords[snakeIndex];
            playerPieces[1][snakeIndex] = new PlayerPiece(1, snakeXcoord, snakeYcoord ); // calls 3-arg PlayerPiece constructor
            addObject( playerPieces[1][snakeIndex], snakeXcoord, snakeYcoord );
        } // end for
        
        // Lines 256 and 257 place the die in a spot where the location of the die is 80% left of the gameboard and
        // 50% of the height of the gameboard
        die = new Die();
        addObject( die, (int)(0.9 * WIDTH), (int)(0.5 * HEIGHT ) ); 
        
        state = PLAYER1_ROLL_DIE;
    } // end method prepare

    /**
     * Depending on the game's state, determines what the GameBoard does during
     * each frame or cycle of the `act` method
     * 
     * Remember that as long as your scenario is running, the `act` method
     * continues to be invoked for each cycle through the game loop.
     */
    public void act()
    {
        switch ( state ) {
            case PLAYER1_ROLL_DIE:
                determineDieRollValueForPlayerIndex(0);
                break; // break out of **switch** but if the game state isn't updated, we'll continue checking for die roll

            case PLAYER1_MOVE_CUP:
                determineWhichPiecesAreMoveableForPlayerIndex( 0 ); // remember, player "number" 1 (cups) is player INDEX 0
                if ( !readyToExitState ) {
                    determineMoveForPlayerIndex(0);
                    return; // exits `act` method (and skips additional method calls for the current `act` method call)
                            // because player 1 (cups, for player index 0) hasn't selected a piece to move yet
                } // end if
                
                // Note that the only way we end up here is if `readyToExitState` is `true` (for the current value of `state`)
                makeAllPiecesMoveableAgainForPlayerIndex(0);  
                updateGameStateAfterTurnForPlayerIndex(0);  
                
                break; // break out of **switch**

            case PLAYER2_ROLL_DIE:
                determineDieRollValueForPlayerIndex(1); 
                break;

            case PLAYER2_MOVE_SNAKE:
                determineWhichPiecesAreMoveableForPlayerIndex( 1 ); // remember, player "number" 2 (snakes) is player INDEX 1
                if ( !readyToExitState ) {
                    determineMoveForPlayerIndex(1);
                    return; // exits `act` method (and skips additional method calls for the current `act` method call)
                            // because player 2 (snakes, for player index 1) hasn't selected a piece to move yet

                } // end if ( !readyToExitState )
                
                // Note that the only way we end up here is if `readyToExitState` is `true` (for the current value of `state`)
                makeAllPiecesMoveableAgainForPlayerIndex(1);        
                updateGameStateAfterTurnForPlayerIndex(1);  
                
                // OPTIONAL method call, uncomment for debugging/diagnostic purposes 
                // logCurrentStateOfGameBoard( "At end of PLAYER2_MOVE_SNAKE state" );
                
                break; // break out of **switch**

            case PLAYER1_WIN:
                showText(
                    "\nPlayer 1\nWINS!!" +
                    "\nTo learn more" +
                    "\nAbout Programming" +
                    "\nGo to Codeacademy.com" +
                    "\nFor free online lessons",
                    525,
                    260
                );
                Greenfoot.stop();
                break;

            case PLAYER2_WIN:
                showText(
                    "\nPlayer 2\nWINS!!" +
                    "\nTo learn more" +
                    "\nAbout programming" +
                    "\nGo to Codeacademy.com" +
                    "\nFor free online lessons",
                    525,
                    260
                );
                Greenfoot.stop();
                break;

            default:
                break;
        } // end switch
    } // end method act

   
    /**
     * Determines the die roll for the current player. If the player is human,
     * then the human player clicks on the die object to roll the die; otherwise,
     * the die is automatically rolled by the CPU.
     * 
     * @param playerIndex  the index of the player rolling the die
     */
    public void determineDieRollValueForPlayerIndex( int playerIndex )
    {
        String playerNumberString = ( playerIndex == 0 ? "1" : "2" );
        
        if ( playerIsHumanForPlayerIndex[playerIndex] && !Greenfoot.mouseClicked(die) )
        {
            showText( "\nPlayer " + playerNumberString + "\nclick to roll", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
            return; // skip remaining statements and return to this method's caller
        } // end if
        
        dieRollValue = Greenfoot.getRandomNumber(6) + 1; // rolls a 1 to 6, inclusively
        
        // Sets the image for the die depending on the players roll value
        if (dieRollValue == 1) {
            this.die.setImage("Die1.png");
        } else if (dieRollValue == 2) {
            this.die.setImage("Die2.png");
        } else if (dieRollValue == 3) {
            this.die.setImage("Die3.png");
        } else if (dieRollValue == 4) {
            this.die.setImage("Die4.png");
        } else if (dieRollValue == 5) {
            this.die.setImage("Die5.png");
        } else if (dieRollValue == 6) {
            this.die.setImage("Die6.png");
        }// end if
        
        showText( "\nPlayer " + playerNumberString + "\nrolls a " + dieRollValue, die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );

        Greenfoot.delay(DELAY_LENGTH); // allows time to view on-screen message
        
        state = ( playerIndex == 0 ? PLAYER1_MOVE_CUP : PLAYER2_MOVE_SNAKE );
    } // end method determineDieRollValueForPlayerIndex
    
    /**
     * Routine for determining which cups are moveable
     * 
     * @param playerIndex   index of the player whose turn is currently active
     */
    public void determineWhichPiecesAreMoveableForPlayerIndex( int playerIndex ) {

        // first check to see which of this player's pieces are moveable 
        // for the given die roll value 
        for ( int playerPieceIndex = 0; playerPieceIndex < playerPieces[playerIndex].length; playerPieceIndex++ ) 
        {    
            // determine index of opposing player using simple arithmetic (no if-statement needed!)
            int opposingPlayerIndex = 1 - playerIndex; // if playerIndex = 1, opposingPlayerIndex = 1 - 1 = 0
                                                       // if playerIndex = 0, opposingPlayerIndex = 1 - 0 = 1
            
            playerPieces[playerIndex][playerPieceIndex].setMoveable( false ); // "default" state; update depending on conditions to be checked below
 
            int currentPlayerPieceGameBoardLocationIndex = 
                playerPieces[playerIndex][playerPieceIndex].getGameBoardLocationIndex();
 
            int playerPieceTargetGameBoardLocationIndex = currentPlayerPieceGameBoardLocationIndex + dieRollValue;
            
            // if the player piece is ALREADY currently in the goal zone, then it shouldn't be moveable
            if ( currentPlayerPieceGameBoardLocationIndex == 14 )
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this piece is NOT moveable so we can skip the remaining
                          // statements in this iteration of the for loop and then
                          // proceed to the next iteration
            } // end if
            
            // if the target space IS the goal, then this piece IS moveable
            // and we call a "setter" method to update the target space location index 
            // (as an attribute of the PlayerPiece object itself) so that this value can be 
            // easily retrieved by later methods (such as `handleSelectedPieceForPlayerIndex`)
            if ( playerPieceTargetGameBoardLocationIndex >= 14 ) 
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
                playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(14);
                continue; // we know this piece IS moveable AND we know it's NOT in the start zone,
                          // so we skip remaining statements in `for` loop 
                          // BUT we will continue w/next iteration so we can determine if any other cups are moveable
            } // end if

            // Check to see if the target space is occupied by one of the current player's pieces
            // ...if so, then we CANNOT move to that space
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(playerIndex) ) {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this cup is NOT moveable, so we 
                          // skip remaining statements in `for` loop but continue w/next iteration
            } // end if
            
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(opposingPlayerIndex) )
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
                playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(playerPieceTargetGameBoardLocationIndex);
            } // end OUTER if
            
            playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
            playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(playerPieceTargetGameBoardLocationIndex);
        } // end for

    } // end method determineWhichPiecesAreMoveableForPlayerIndex

    /**
     * Determines which of the player's moveable pieces will actually be moved
     * 
     * @param playerIndex   the index of the player currently moving 
     */
    public void determineMoveForPlayerIndex( int playerIndex )
    {
        int countOfPlayerPiecesThatAreNotMoveable = 0; // OK as a local variable

        // First, check to see if there are actually any moves to make
        for ( int playerPieceIndex = 0; playerPieceIndex < NUMBER_OF_PIECES_PER_PLAYER; playerPieceIndex++ )
        {
            PlayerPiece currentPlayerPieceToCheck = playerPieces[playerIndex][playerPieceIndex];
            
            if ( !currentPlayerPieceToCheck.isMoveable() ) 
            {
                countOfPlayerPiecesThatAreNotMoveable++;   
                
                if ( countOfPlayerPiecesThatAreNotMoveable == NUMBER_OF_PIECES_PER_PLAYER )
                {
                    showText( "No moves!", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    Greenfoot.delay(DELAY_LENGTH); 
                    readyToExitState = true;
                    return; // terminate method (skipping all statements below) and return to method caller
                } // end INNER if
                
            } // end OUTER if 
        }
        
        // If you've gotten this far, then there is at least one moveable piece,
        // so we loop again through the moveable pieces to see which will be moved
        for ( int playerPieceIndex = 0; playerPieceIndex < NUMBER_OF_PIECES_PER_PLAYER; playerPieceIndex++ )
        {
            PlayerPiece currentPlayerPieceToCheck = playerPieces[playerIndex][playerPieceIndex];
            
            if ( currentPlayerPieceToCheck.isMoveable() )
            {
                String playerPieceName = playerIndex == 0 ? "cup" : "snake";
                
                // Note use of ternary conditional expression to determine whether to include the singular 
                // literal String "space" or the plural literal String "spaces," depending on the value of the die roll
                showText( "\n\n\nSelect\n" + playerPieceName + "\nto move\n" + dieRollValue + (dieRollValue == 1 ? " space" : " spaces"), 
                    die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                
                if ( playerIsHumanForPlayerIndex[playerIndex] && Greenfoot.mouseClicked(currentPlayerPieceToCheck) ) 
                {                                                                          
                    handleSelectedPieceForPlayerIndex( playerIndex, currentPlayerPieceToCheck );
                    readyToExitState = true; // now that a piece is selected (here, by the human player), the game will update its state 
                    return; // move has been made, so we exit the method early and return to method caller
    
                } // end if 
    
                // if we've gotten to this point in the code, then we allow the CPU to determine which piece to move
                // (Specifically, we'll use a random number generator to simulate a 30% chance of the 
                //  CPU "mouse-clicking" on THIS piece -- it's not "smart" AI, but it works well enough)
                if ( !playerIsHumanForPlayerIndex[playerIndex] && Greenfoot.getRandomNumber(100) < 30 ) 
                {
                    Greenfoot.delay(DELAY_LENGTH); // allows time to see which board pieces are moveable or not
                    
                    showText( "\nMoving\n" + playerPieceName + " " + (playerPieceIndex + 1), die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    Greenfoot.delay(DELAY_LENGTH); // allows time to view message
    
                    handleSelectedPieceForPlayerIndex( playerIndex, currentPlayerPieceToCheck );
                    
                    readyToExitState = true; // now that a piece is selected (here, by the CPU), the game will update its state 
                    return; // move has been made, so we exit the method early and return to method caller
      
                } // end INNER if 
            } // end OUTER if
        } // end for

    } // end method determineMoveForPlayerIndex

    /**
     * Updates the given player piece's location index along the movement path for the given playerIndex
     * 
     * @param playerIndex           the index of the player moving the selected piece
     * @param selectedPlayerPiece   a reference to the player's selected piece
     */
    public void handleSelectedPieceForPlayerIndex( int playerIndex, PlayerPiece selectedPlayerPiece )
    {
        if ( selectedPlayerPiece.getTargetGameBoardLocationIndex() >= 0 
              && 
             selectedPlayerPiece.getTargetGameBoardLocationIndex() < 14 ) 
        {
            moveSelectedPieceOntoTargetSpaceForPlayerIndex( playerIndex, selectedPlayerPiece );
        } 
        else if ( selectedPlayerPiece.getTargetGameBoardLocationIndex() >= 14 ) { 
            moveSelectedPieceIntoGoalZoneForPlayerIndex( selectedPlayerPiece );
        } // end if/else

        // since we have now moved the player piece's location on the screen, let's first update
        // the status of the Space it is LEAVING so that it is NO LONGER OCCUPIED by that player...
        if ( selectedPlayerPiece.getGameBoardLocationIndex() >= 0 ) 
        {
            movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getGameBoardLocationIndex() ].setOccupiedByPieceForPlayerIndex(playerIndex, false);
        } // end if
        
        // ...and finally, we UPDATE the selected player piece's  
        //    Space location to be whatever its TARGET location is 
        selectedPlayerPiece.setGameBoardLocationIndex( selectedPlayerPiece.getTargetGameBoardLocationIndex() );
        
    } // end method handleSelectedPieceForPlayerIndex
    
    /**
     * "Helper" method (called by handleSelectedPieceForPlayerIndex) for moving a
     * piece into a target space that is NOT the goal
     * 
     * @param playerIndex           the index of the player moving a piece into the piece's target space 
     * @param selectedPlayerPiece   a reference to the player's selected piece
     */
    public void moveSelectedPieceOntoTargetSpaceForPlayerIndex( int playerIndex, PlayerPiece selectedPlayerPiece )
    {
        // determine index of opposing player using simple arithmetic (no if-statement needed!)
        int opposingPlayerIndex = 1 - playerIndex; // if playerIndex = 1, opposingPlayerIndex = 1 - 1 = 0
                                                   // if playerIndex = 0, opposingPlayerIndex = 1 - 0 = 1
        
        // move the playerPiece SPRITE to its new X- and Y- locations on the screen
        selectedPlayerPiece.setLocation( 
            movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getTargetGameBoardLocationIndex() ].getX(), 
            movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getTargetGameBoardLocationIndex() ].getY() );

        // update the "occupied" state for the target space where the selected player piece is being moved
        movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getTargetGameBoardLocationIndex() ].setOccupiedByPieceForPlayerIndex(playerIndex, true);

        // if this space is occupied by a piece belonging to the OPPOSING player, 
        // move the OPPOSING playerPiece at this space back to the beginning
        if ( movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getTargetGameBoardLocationIndex() ].isOccupiedByPieceForPlayerIndex(opposingPlayerIndex) ) 
        {
            resetCapturedPieceOnBoardAndReplaceWithSelectedPieceForPlayerIndex( playerIndex, selectedPlayerPiece );
        } // end if
    } // end method moveSelectedPieceOntoTargetSpaceForPlayerIndex
    
    /**
     * "Helper" method (here called by `moveSelectedPieceOntoTargetSpaceForPlayerIndex`) 
     * for handling the capture of an opposing player's piece 
     * 
     * @param playerIndex           the index of the player moving a piece into that piece's target space 
     * @param selectedPlayerPiece   a reference to the player's selected piece
     */
    public void resetCapturedPieceOnBoardAndReplaceWithSelectedPieceForPlayerIndex( int playerIndex, PlayerPiece selectedPlayerPiece )
    {
        // determine index of opposing player using simple arithmetic (no if-statement needed!)
        int opposingPlayerIndex = 1 - playerIndex; // if playerIndex = 1, opposingPlayerIndex = 1 - 1 = 0
                                                   // if playerIndex = 0, opposingPlayerIndex = 1 - 0 = 1
                                                   
        for ( PlayerPiece currentOpponentPieceToCheck : playerPieces[opposingPlayerIndex] ) {
            
            // if the captured opponent's piece is located along its board space movement path 
            // (index 0 to 14) then move it back to location "index" -1 (starting zone)
            // AND also move its SPRITE back to its original X- and Y-coordinate locations in the world
            if ( currentOpponentPieceToCheck.getGameBoardLocationIndex() == selectedPlayerPiece.getTargetGameBoardLocationIndex() ) 
            {
                currentOpponentPieceToCheck.setLocation( currentOpponentPieceToCheck.getOriginalXcoord(), currentOpponentPieceToCheck.getOriginalYcoord() );
                
                currentOpponentPieceToCheck.setGameBoardLocationIndex(-1); // reset piece's location index back to 
                                                                           // starting zone (location "index" -1)
                
                // at the current player's TARGET location index along the movement path, update the state of that Space
                // so that it is NO LONGER OCCUPIED by the (captured) opposing player's piece
                movementPathForPlayerIndex[playerIndex][selectedPlayerPiece.getTargetGameBoardLocationIndex()].setOccupiedByPieceForPlayerIndex(opposingPlayerIndex, false);

            } // end if
        } // end for    
    } // end method resetCapturedPieceOnBoardAndReplaceWithSelectedPieceForPlayerIndex
  
    /**
     * "Helper" method (here called by handleSelectedPieceForPlayerIndex) to move 
     * the selected playerPiece's SPRITE into the goal zone.
     * Note that this only moves the player piece's SPRITE; the player piece's 
     * board location index is updated elsewhere (can you figure out where?)
     * 
     * Actual X- and Y-coordinates of each player's piece in the goal zone are each  
     * computed as a linear function of how many of that player's pieces are already 
     * in the goal zone (i.e., goalCountForPlayer[playerIndex] )
     * 
     * @param playerIndex           the index of the player moving a piece into the goal zone
     * @param selectedPlayerPiece   a reference to the player's selected piece
     */
    public void moveSelectedPieceIntoGoalZoneForPlayerIndex( PlayerPiece selectedPlayerPiece )
    {
        removeObject(selectedPlayerPiece);        
    } // end method moveSelectedPieceIntoGoalZoneForPlayerIndex
    
    /**
     * "Turns on" (makes moveable) all of pieces for the given player (specified by `playerIndex`) 
     * at the conclusion of that player's turn
     * 
     * @param playerIndex   the index of the player that is completing their turn
     */
    public void makeAllPiecesMoveableAgainForPlayerIndex( int playerIndex )
    {
        for ( PlayerPiece currentPlayerPieceToCheck : playerPieces[playerIndex] )
        {
            currentPlayerPieceToCheck.setMoveable(true);
        } // end for
    } // end method makeAllCrabsMoveableAgain
    
    /**
     * Updates the game state (and checks for a possible win condition) after the player
     * (specified by `playerIndex`) has just completed their turn
     * 
     * @param playerIndex   the index of the player that has just completed their turn
     */
    public void updateGameStateAfterTurnForPlayerIndex( int playerIndex )
    {
        switch ( playerIndex ) {
            case 0: // remember, playerIndex 0 means player NUMBER 1...
                updateGoalCountForPlayerIndex(0);
            
                if ( goalCountForPlayerIndex[0] == NUMBER_OF_PIECES_PER_PLAYER ) {
                    state = PLAYER1_WIN; // update state for next `act` method call
                } 
                else { 
                    // player 1 (index 0)'s turn is finished, so update game state for player 2's turn
                    showText( "\nPlayer 2\nup next", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    Greenfoot.delay(DELAY_LENGTH);
                    readyToExitState = false; // reset for next turn
                    state = PLAYER2_ROLL_DIE; // update state for next `act` method call
        
                } // end if/else
                break;
            
            case 1: // remember, playerIndex 1 means player NUMBER 2...
                updateGoalCountForPlayerIndex(1);
                        
                if ( goalCountForPlayerIndex[1] == NUMBER_OF_PIECES_PER_PLAYER ) 
                {
                    state = PLAYER2_WIN; // update state for next `act` method call
                } 
                else { 
                    // player 1 (index 0)'s turn is finished, so update game state for player 2's turn
                    showText( "\nPlayer 1\nup next", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    Greenfoot.delay(DELAY_LENGTH);
                    readyToExitState = false; // reset for next turn
                    state = PLAYER1_ROLL_DIE; // update state for next `act` method call
        
                }
                break;
                
            default:
                break;
                
        } // end switch

    } // end method updateGameStateAfterTurnForPlayerIndex
    
    /**
     * For the given playerIndex, checks to see how many of that player's pieces have been 
     * moved into that goal zone at the end of that player's movement path
     * 
     * @param playerIndex   the index of the player whose pieces are being checked to see if they're in the goal zone
     */
    public void updateGoalCountForPlayerIndex( int playerIndex )
    {
        goalCountForPlayerIndex[playerIndex] = 0; // resetting for purpose of using 
                                                  // loop to re-compute goal count
        
        for ( PlayerPiece currentPlayerPieceToCheck : playerPieces[playerIndex] )
        {
            if ( currentPlayerPieceToCheck.getGameBoardLocationIndex() == 14 ) {
                goalCountForPlayerIndex[playerIndex]++;
            } // end if
        } // end enhanced for loop 
    } // end method updateGoalCountForPlayerIndex
    
    /**
     * A diagnostic method for logging the current state of the GameBoard to the console
     * (Feel free to modify this however you like, and consider using System.out.println elsewhere
     *  in your code to display similar "diagnostic messages" or "debug messages")
     * 
     * @param headerMessage     a message to help the reader know when this method was actually called 
     */
    public void logCurrentStateOfGameBoard( String headerMessage )
    {
        System.out.println( "----------------------------------------------------" );
        System.out.println( headerMessage );
        System.out.println( "----------------------------------------------------" );
        System.out.println( "Current states of all player 1 pieces (cups):");
        for ( int cupIndex = 0; cupIndex < NUMBER_OF_PIECES_PER_PLAYER; cupIndex++ )
        {
            System.out.println( "playerPieces[0]["+cupIndex+"].getGameBoardLocationIndex() = " 
                                    + playerPieces[0][cupIndex].getGameBoardLocationIndex() );
        } // end for
        
        System.out.println(); // blank line for readability
        
        System.out.println( "Current states of all player 2 pieces (snakes):");
        for ( int snakeIndex = 0; snakeIndex < NUMBER_OF_PIECES_PER_PLAYER; snakeIndex++ )
        {
            System.out.println( "playerPieces[1]["+snakeIndex+"].getGameBoardLocationIndex() = " 
                                    + playerPieces[1][snakeIndex].getGameBoardLocationIndex() );
        } // end for
        
        System.out.println(); // blank line for readability
        
        System.out.println( "Current states of all spaces on GameBoard:");
        for ( int spaceIndex = 0; spaceIndex < 20; spaceIndex++ )
        {
            System.out.println( "spaces["+spaceIndex+"].isOccupiedByPieceForPlayerIndex(0) = " 
                                    + spaces[spaceIndex].isOccupiedByPieceForPlayerIndex(0) );
            System.out.println( "spaces["+spaceIndex+"].isOccupiedByPieceForPlayerIndex(1) = " 
                                    + spaces[spaceIndex].isOccupiedByPieceForPlayerIndex(1) ); 
        } // end for
        
        System.out.println(); // blank line for readability
        
    } // end method logCurrentStateOfGameBoard
    
} // end class GameBoard
 