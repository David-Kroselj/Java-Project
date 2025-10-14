import greenfoot.*;  // imports Actor, World, Greenfoot, GreenfootImage

/**
 * The physical game board for DrTargetGameBoardLocationIndex. Canada's "Crabs vs. Lobsters" game
 * (NOTE: this class is also used as a "level manager" for managing the game logic, game state, etc.)
 * 
 * DISCLAIMER TO STUDENTS: You may adapt this code for your own scenario, but you MUST give Dr. Canada 
 * credit in your "Acknowledgements" document that you'll use for citing any and all help given or received
 * while working on this project. Failure to do so WILL result in a failing grade for the project!
 * 
 * @author bcanada@uscb.edu
 * @version 1.0
 */
public class GameBoard extends World
{
    /* FIELDS (instance variables and constants) */
    
    // NOTE: In general, ALL fields should be declared `private`; if a field 
    //       variable needs to be accessed outside the class, you must ENCAPSULATE 
    //       your fields by defining `public` methods (getters/setters) for 
    //       retrieving and/or updating those variable values. We will demonstrate
    //       this in class with some (simpler) examples so that you can develop a 
    //       "muscle memory" for following this programming pattern.
    // 
    // ALSO: For the purpose of this project, DO NOT DECLARE ANY FIELDS as 
    //       `static` UNLESS they are CONSTANTS that do not need modification
    //       If your code attempts to declare any VARIABLE fields as either 
    //       `public` or `static` then your grade on this project will be SEVERELY PENALIZED
    //       (However, you are welcome to declare CONSTANTS as both `static` 
    //       and `public` if they need to be accessed outside the class)
    
    private boolean[] playerIsHumanForPlayerIndex;      // 1-D array that stores `true` (human) or `false` (CPU) for the player at the given array index
    
    private Space[] spaces;                             // GameBoard "has-a" set of Spaces
    public static final int HEIGHT = 720;               // set GameBoard height as a CONSTANT; is public, so access using `Gameboard.HEIGHT` from outside this class
    public static final int WIDTH = 360;                // set GameBoard width as a CONSTANT; is public, so access using`Gameboard.WIDTH` from outside this class

    private PlayerPiece[][] playerPieces;               // 2-D array of PlayerPiece object references; row index 0 (crabs) or 1 (lobsters)
                                                        // while column index refers to the index of the piece belonging to the player
                                                
    private final int NUMBER_OF_PIECES_PER_PLAYER = 3;  // set this value here, once, then use the constant to get this value elsewhere in the class
    private final int DELAY_LENGTH = 60;                // Adjust this value to set the duration used when Greenfoot.delay is called 
                                                        // (Asumming your slider is at 50% -> 60 fps, then a DELAY_LENGTH of 60 will be 1 second)
    
    private int[] crabStartYCoords;                     // 1-D array of ints for storing the starting Y coordinates for the crabs (initialized in constructor)
    private int[] lobsterStartYCoords;                  // 1-D array of ints for storing the starting Y coordinates for the lobsters (initialized in constructor)
    
    private Space[][] movementPathForPlayerIndex;       // 2-D array used to "map" each player's movement path to corresponding Spaces on the game board
    
    private Die die;                                    // GameBoard "has-a" reference to a Die object
    private int dieRollValue;                           // GameBoard "has-a" value of the most recent die roll
    private final int DIE_TEXT_VERTICAL_OFFSET = 50;    // a CONSTANT used by the `showText` method to determine where to display text beneath the Die's y-position
   
    private int state;                                  // GameBoard "has-a" state (an int value that can be referred to by the constants below)
    
    /* `public` CONSTANTS for keeping track of the game's overall state */
    public static final int BOARD_SETUP = 0;            // accessed by BOARD_SETUP within GameBoard; is public, so access using `Gameboard.BOARD_SETUP` elsewhere
    public static final int PLAYER1_ROLL_DIE = 1;       // accessed by PLAYER1_ROLL_DIE within GameBoard; is public, so access using `Gameboard.PLAYER1_ROLL_DIE` elsewhere
    public static final int PLAYER1_MOVE_CRAB = 2;      // accessed by PLAYER1_MOVE_CRAB within GameBoard; is public, so access using `Gameboard.PLAYER1_MOVE_CRAB` elsewhere
    public static final int PLAYER2_ROLL_DIE = 3;       // accessed by PLAYER2_ROLL_DIE within GameBoard; is public, so access using `Gameboard.PLAYER2_ROLL_DIE` elsewhere
    public static final int PLAYER2_MOVE_LOBSTER = 4;   // accessed by PLAYER2_MOVE_LOBSTER within GameBoard; is public, so access using `Gameboard.PLAYER2_MOVE_LOBSTER` elsewhere
    public static final int PLAYER1_WIN = 5;            // accessed by PLAYER1_WIN within GameBoard; is public, so access using `Gameboard.PLAYER1_WIN` elsewhere
    public static final int PLAYER2_WIN = 6;            // accessed by PLAYER2_WIN within GameBoard; is public, so access using `Gameboard.PLAYER2_WIN` elsewhere

    private boolean readyToExitState;                   // GameBoard "has-a" value for tracking whether or not we are ready to update the game's overall state
    
    private boolean onSafeSpaceSoRollAgain;             // GameBoard "has-a" value for tracking whether or not the current player just landed on a safe space
                                                        // (and therefore that player gets to roll again)
    
    private int[] goalCountForPlayerIndex;              // GameBoard "has-a" value for tracking the number of crabs (for player index 0) or lobsters (for player index 1) 
                                                        // that have moved into the corresponding player's goal (i.e., movement path index 7)

    private GameManager referenceToGameManager;         // Not used in this version, but if you wanted to use a GameManager object for maintaining your game's state
                                                        // between worlds or scenes, you'll need an instance variable to refer to it (see Dr. Canada's version of 
                                                        // the "Pengu" scenario for a very simple example of how to use a GameManager object to allow data to "persist"
                                                        // as you switch from one world to another)
                      
                                                        
    /* CONSTRUCTOR(S) */
    /**
     * Initialize the GameBoard object and its variable fields
     */
    public GameBoard() 
    {
        // if calling the superclass constructor, then `super` must be called BEFORE any other statements in the constructor
        super(WIDTH, HEIGHT, 1, false); // portrait orientation for mobile but fits on desktop, 1 pixel per cell, world is UN-bounded        

        /* initialize instance variables */
        playerIsHumanForPlayerIndex = new boolean[]{ false, false }; // `true, false`  -> crab (player index 0) is human, lobster (index 1) is CPU
                                                                    // `true, true`   -> crab is human, lobster is also human
                                                                    // `false, false` -> crab is CPU, lobster is also CPU

        spaces = new Space[7];     // "empty" array of 7 `null` references; Space assignments are handled by the `prepare` method below
        
        playerPieces = new PlayerPiece[2][NUMBER_OF_PIECES_PER_PLAYER]; // Always 2 players, with (normally) 3 pieces per player
         
        state = BOARD_SETUP;

        readyToExitState = false;
        onSafeSpaceSoRollAgain = false;
        
        goalCountForPlayerIndex = new int[2]; // set size of array to be 2 elements long 
        goalCountForPlayerIndex[0] = 0; // at start of game, no crabs in the goal
        goalCountForPlayerIndex[1] = 0; // at start of game, no lobsters in the goal

        // NOTE: if you use an array initializer to populate a 1-D array 
        //       that was previously declared in an earlier statement,
        //       you will need to use `new array-data-type[]` before
        //       the usual array initializer syntax
        crabStartYCoords = new int[]{ HEIGHT - 180, HEIGHT - 120, HEIGHT - 60 };   // NOTE: if using more or less than 3 pieces per player, you'll 
        lobsterStartYCoords = new int[]{ 60, 120, 180 };                           //       need update the number of array elements to match

        /* set up the physical board layout (Start vs. Awake??) */
        prepare();
    } // end GameBoard no-arg constructor

    /* METHODS */
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        // 1. set background image
        setBackground("sand.jpg");

        // 2. place game space sprites (Actor objects) on screen
        for ( int spaceIndex = 0; spaceIndex < 7; spaceIndex++ )
        {
            if ( spaceIndex != 3 ) // if NOT the middle space (i.e., if NOT the safe space)...
            {
                spaces[spaceIndex] = new Space( false ); // is a stone space
            } 
            else // otherwise, if the middle space (i.e., if the safe space),,,
            {
                spaces[spaceIndex] = new Space( true ); // is a green space
            }
            addObject( spaces[spaceIndex], (int)(0.5 * WIDTH), 100 * spaceIndex + 60 ); // y = mx + b

        } // end for

        // You could populate arrays one element at a time...
        // (Here, the movement path array elements are simply REFERRING 
        //  or POINTING to different spaces that make up the game board)
        
        // Set up the movement path for the crabs (controlled by player 1, or player Index 0)
        // Note that you could assign space references one statement at a time...
        movementPathForPlayerIndex = new Space[2][7]; // movementPathForPlayerIndex array has 2 rows (one per player);
                                                      // each player has a path of 7 board spaces to move through
        
        // Now "map" the movement path for player 1 (the crabs) to 
        // the corresponding spaces on the game board itself (spaces have index 
        // values 0 through 6, or 0 through `spaces.length - 1`)
        movementPathForPlayerIndex[0][0] = spaces[6]; // REMEMBER: Java is a ZERO-INDEX language -- check for "off-by-one" LOGIC ERRORS!
        movementPathForPlayerIndex[0][1] = spaces[5]; // (also note the indices used -- this will help you figure out how to "map" 
        movementPathForPlayerIndex[0][2] = spaces[4]; //  the player's movement path to the spaces on YOUR game board)
        movementPathForPlayerIndex[0][3] = spaces[3]; 
        movementPathForPlayerIndex[0][4] = spaces[2]; 
        movementPathForPlayerIndex[0][5] = spaces[1]; 
        movementPathForPlayerIndex[0][6] = spaces[0]; 

        // Set up (and map) the movement path for lobsters (controlled by player 2, or player Index 1)
        // NOTE: Rather than assign space references one statement at a time, 
        // you COULD use a loop, to be a bit more efficient and flexible...
        // HOWEVER, you may find the code more readable if you explicitly write individual statements 
        // that "map" the movement path to the game spaces, like what was done for the crabs above)
        for ( int movementPathIndex = 0; movementPathIndex < movementPathForPlayerIndex[1].length; movementPathIndex++ )
        {
            // note the indices for the lobster path match those of the spaces themselves...
            movementPathForPlayerIndex[1][movementPathIndex] = spaces[movementPathIndex]; 
            
            // ...but note that if we were using a loop to map the CRAB's movement path to the board spaces, we could use:
            // movementPathForPlayerIndex[0][movementPathIndex] = spaces[6 - movementPathIndex];
        } // end for

        // 3. Instantiate game piece objects and place sprites on the board
        for ( int crabIndex = 0; crabIndex < playerPieces[0].length; crabIndex++ )
        {
            // call 3-arg constructor to set the crab's initial position
            int crabXcoord = (int)(0.8 * WIDTH);
            int crabYcoord = crabStartYCoords[crabIndex];
            playerPieces[0][crabIndex] = new PlayerPiece(0, crabXcoord, crabYcoord); // calls 3-arg PlayerPiece constructor
            addObject( playerPieces[0][crabIndex], crabXcoord, crabYcoord );
        } // end for

        for ( int lobsterIndex = 0; lobsterIndex < playerPieces[1].length; lobsterIndex++ )
        {
            // call 3-arg constructor to set the lobster's initial position
            int lobsterXcoord = (int)(0.2 * WIDTH);
            int lobsterYcoord = lobsterStartYCoords[lobsterIndex];
            playerPieces[1][lobsterIndex] = new PlayerPiece(1, lobsterXcoord, lobsterYcoord ); // calls 3-arg PlayerPiece constructor
            addObject( playerPieces[1][lobsterIndex], lobsterXcoord, lobsterYcoord );
        } // end for

        // 4. Instantiate the die and place it on the board so that its horizontal coordinate is 
        //    offset from the left edge of the world by 80% of the width of the game board, 
        //    and so that its vertical coordinate is
        //    offset from the top edge of the world by 50% of the height of the board
        die = new Die();
        addObject( die, (int)(0.8 * WIDTH), (int)(0.5 * HEIGHT ) ); 

        // 5. When setup is all done, update GameBoard state to begin actual gameplay
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

            case PLAYER1_MOVE_CRAB:
                determineWhichPiecesAreMoveableForPlayerIndex( 0 ); // remember, player "number" 1 (crabs) is player INDEX 0
                if ( !readyToExitState ) {
                    determineMoveForPlayerIndex(0);
                    return; // exits `act` method (and skips additional method calls for the current `act` method call)
                            // because player 1 (crabs, for player index 0) hasn't selected a piece to move yet
                } // end if
                
                // Note that the only way we end up here is if `readyToExitState` is `true` (for the current value of `state`)
                makeAllPiecesMoveableAgainForPlayerIndex(0);  
                updateGameStateAfterTurnForPlayerIndex(0);  
            
                // OPTIONAL method call, uncomment for debugging/diagnostic purposes 
                // logCurrentStateOfGameBoard( "At end of PLAYER1_MOVE_CRAB state" );
                
                break; // break out of **switch**

            case PLAYER2_ROLL_DIE:
                determineDieRollValueForPlayerIndex(1); 
                break;

            case PLAYER2_MOVE_LOBSTER:
                determineWhichPiecesAreMoveableForPlayerIndex( 1 ); // remember, player "number" 2 (lobsters) is player INDEX 1
                if ( !readyToExitState ) {
                    determineMoveForPlayerIndex(1);
                    return; // exits `act` method (and skips additional method calls for the current `act` method call)
                            // because player 2 (lobsters, for player index 1) hasn't selected a piece to move yet

                } // end if ( !readyToExitState )
                
                // Note that the only way we end up here is if `readyToExitState` is `true` (for the current value of `state`)
                makeAllPiecesMoveableAgainForPlayerIndex(1);        
                updateGameStateAfterTurnForPlayerIndex(1);  
                
                // OPTIONAL method call, uncomment for debugging/diagnostic purposes 
                // logCurrentStateOfGameBoard( "At end of PLAYER2_MOVE_LOBSTER state" );
                
                break; // break out of **switch**

            case PLAYER1_WIN:
                showText( "\nPlayer 1\nWINS!!", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                Greenfoot.stop();
                break;

            case PLAYER2_WIN:
                showText( "\nPlayer 2\nWINS!!", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
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
        // Determine player number (not index), for use in string expressions later in this method.
        // The expression on the right uses Java's ternary conditional operator, ?:, 
        // to produce a construct that is a more concise version of an if/else statement). 
        // Try asking a generative AI to rewrite this statement as an if/else construct.
        // Which is more readable to you? Can you see the correspondence between the more 
        // compact conditional expression and a logically equivalent if/else construct?
        String playerNumberString = ( playerIndex == 0 ? "1" : "2" );
        
        // NOTE: Using a "guard condition" to avoid the need for nesting if-statements
        // (In plain English, we are asking: "If the player is human AND we have NOT clicked on the die....")
        if ( playerIsHumanForPlayerIndex[playerIndex] && !Greenfoot.mouseClicked(die) )
        {
            showText( "\nPlayer " + playerNumberString + "\nclick to roll", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
            return; // skip remaining statements and return to this method's caller
        } // end if
        
        dieRollValue = Greenfoot.getRandomNumber(6) + 1; // rolls a 1 to 6, inclusively
        
        showText( "\nPlayer " + playerNumberString + "\nrolls a " + dieRollValue, die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );

        Greenfoot.delay(DELAY_LENGTH); // allow time to view on-screen message
        
        if ( dieRollValue == 0 ) 
        {
            showText( "\nSkipping\nturn...", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
            Greenfoot.delay(DELAY_LENGTH); // allow time to view on-screen message
            state = (playerIndex == 0 ? PLAYER2_ROLL_DIE : PLAYER1_ROLL_DIE ); // ternary operator
        } 
        else {
            state = ( playerIndex == 0 ? PLAYER1_MOVE_CRAB : PLAYER2_MOVE_LOBSTER ); // ternary operator
        } // end INNER if/else

    } // end method determineDieRollValueForPlayerIndex
    
    /**
     * Routine for determining which crabs are moveable
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

            // call a "getter" method to retrieve the player piece's current position on the game board 
            int currentPlayerPieceGameBoardLocationIndex = 
                playerPieces[playerIndex][playerPieceIndex].getGameBoardLocationIndex();
                
            // Here, we "look ahead" by die roll value to determine "target" array index for this piece
            // (TODO: consider changing the variable name to reflect the word "target")
            int playerPieceTargetGameBoardLocationIndex = currentPlayerPieceGameBoardLocationIndex + dieRollValue;
            
            // if the player piece is ALREADY currently in the goal zone, then it shouldn't be moveable
            if ( currentPlayerPieceGameBoardLocationIndex == 7 )
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
            if ( playerPieceTargetGameBoardLocationIndex == 7 ) 
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
                playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(playerPieceTargetGameBoardLocationIndex);
                continue; // we know this piece IS moveable AND we know it's NOT in the start zone,
                          // so we skip remaining statements in `for` loop 
                          // BUT we will continue w/next iteration so we can determine if any other crabs are moveable
            } // end if

            // if the target space for this piece is BEYOND the goal (i.e., if die roll is too high to exactly "land on" the goal)
            // then this piece is NOT moveable
            if (playerPieceTargetGameBoardLocationIndex > 7 ) 
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this crab is NOT moveable, so we 
                          // skip remaining statements in `for` loop but continue w/next iteration
            } // end if
            
            // Check to see if the target space is occupied by one of the current player's pieces
            // ...if so, then we CANNOT move to that space
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(playerIndex) ) {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this crab is NOT moveable, so we 
                          // skip remaining statements in `for` loop but continue w/next iteration
            } // end if

            // Check to see if the target space meets both of these conditions:
            // 1) is occupied by one of the OPPOSING player's pieces
            // AND
            // 2) is a safe space
            // ...if BOTH conditions are true, then this piece CANNOT be moved to that space
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(opposingPlayerIndex)  
                 && movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isSafeSpace() )
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this crab is NOT moveable, so we 
                          // skip remaining statements in `for` loop but continue w/next iteration
            } // end if
            
            // Check to see if the target space meets both of these conditions:
            // 1) is occupied by one of the CURRENT player's pieces
            // AND
            // 2) is a safe space
            // ...if BOTH conditions are true, then this piece CANNOT be moved to that space
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(playerIndex)  
                 && movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isSafeSpace() )
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( false );
                continue; // we know this crab is NOT moveable, so we 
                          // skip remaining statements in `for` loop but continue w/next iteration
            } // end if
            
            // Check to see if the target space meets both of these conditions:
            // 1) is occupied by one of the OPPOSING player's pieces
            // AND
            // 2) is NOT a safe space
            // ...if BOTH conditions are true, then this piece CAN be moved to that space
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isOccupiedByPieceForPlayerIndex(opposingPlayerIndex)  
                 && !movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isSafeSpace() )
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
                playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(playerPieceTargetGameBoardLocationIndex);
            } // end OUTER if
            
            // At this point in the code, we have used the earlier if-statements to determine that the target space
            // is NOT already occupied (by either player), so if the target space for this piece is a safe space, 
            // then this piece IS moveable
            if ( movementPathForPlayerIndex[playerIndex][ playerPieceTargetGameBoardLocationIndex ].isSafeSpace() ) 
            {
                playerPieces[playerIndex][playerPieceIndex].setMoveable( true );
                playerPieces[playerIndex][playerPieceIndex].setTargetGameBoardLocationIndex(playerPieceTargetGameBoardLocationIndex);
            } // end OUTER if

            // otherwise, if we made it this far, we assume that NOTHING is preventing this piece from being moveable
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
                String playerPieceName = playerIndex == 0 ? "crab" : "lobster";
                
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
             selectedPlayerPiece.getTargetGameBoardLocationIndex() < 7 ) 
        {
            moveSelectedPieceOntoTargetSpaceForPlayerIndex( playerIndex, selectedPlayerPiece );
        } 
        else if ( selectedPlayerPiece.getTargetGameBoardLocationIndex() == 7 ) { 
            moveSelectedPieceIntoGoalZoneForPlayerIndex( playerIndex, selectedPlayerPiece );
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

        // if the target space is a safe space, mark for rolling again when the turn is over
        if ( movementPathForPlayerIndex[ playerIndex ][ selectedPlayerPiece.getTargetGameBoardLocationIndex() ].isSafeSpace() )
        {
            onSafeSpaceSoRollAgain = true;
        } // end if

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
            // (index 0 to 6) then move it back to location "index" -1 (starting zone)
            // AND also move its SPRITE back to its original X- and Y-coordinate locations in the world
            if( currentOpponentPieceToCheck.getGameBoardLocationIndex() == -1 
                ||
                currentOpponentPieceToCheck.getGameBoardLocationIndex() == 7 ) // replace 7 with whatever index corresponds to your GOAL zone
            {
                continue; // skip over remaining statements in this current loop iteration
                          // so that the values -1 and 7 are not used as array idices in the if-statement below
            } // end if
            
            if ( movementPathForPlayerIndex[playerIndex][selectedPlayerPiece.getTargetGameBoardLocationIndex()] 
                  == movementPathForPlayerIndex[opposingPlayerIndex][currentOpponentPieceToCheck.getGameBoardLocationIndex()] ) 
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
    public void moveSelectedPieceIntoGoalZoneForPlayerIndex( int playerIndex, PlayerPiece selectedPlayerPiece )
    {
        if ( playerIndex == 0 ) // if it's a crab...
        {
            selectedPlayerPiece.setLocation( (int)((0.75 + 0.05*goalCountForPlayerIndex[0]) * WIDTH), 20*goalCountForPlayerIndex[0] + 60 );
        } 
        else { // otherwise, if it's a lobster...
            selectedPlayerPiece.setLocation( (int)((0.125 + 0.05*goalCountForPlayerIndex[1]) * WIDTH), HEIGHT - 100 + 20 * goalCountForPlayerIndex[1] );
        } // end INNER if/else        
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
                else if (onSafeSpaceSoRollAgain) {
                    showText( "\nPlayer 1\nrolls again!", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    onSafeSpaceSoRollAgain = false; // reset for next turn
                    Greenfoot.delay(DELAY_LENGTH);
                    readyToExitState = false; // reset for next turn
                    state = PLAYER1_ROLL_DIE; // update state for next `act` method call
        
                } 
                else { 
                    // player 1 (index 0)'s turn is finished, so update game state for player 2's turn
                    showText( "\nPlayer 2\nup next", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    Greenfoot.delay(DELAY_LENGTH);
                    readyToExitState = false; // reset for next turn
                    state = PLAYER2_ROLL_DIE; // update state for next `act` method call
        
                } // end multi-way if/else
                break;
            
            case 1: // remember, playerIndex 1 means player NUMBER 2...
                updateGoalCountForPlayerIndex(1);
                        
                if ( goalCountForPlayerIndex[1] == NUMBER_OF_PIECES_PER_PLAYER ) 
                {
                    state = PLAYER2_WIN; // update state for next `act` method call
                } 
                else if (onSafeSpaceSoRollAgain) {
                    showText( "\nPlayer 2\nrolls again!", die.getX(), die.getY() + DIE_TEXT_VERTICAL_OFFSET );
                    onSafeSpaceSoRollAgain = false; // reset for next turn
                    Greenfoot.delay(DELAY_LENGTH);
                    readyToExitState = false; // reset for next turn
                    state = PLAYER2_ROLL_DIE; // update state for next `act` method call
        
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
        
        /* 
         * NOTE: An "enhanced for" loop works similarly to a counter-controlled
         *       for loop, but no control variable is used to iterate over the
         *       specified elements. 
         *       
         *       One might read the loop header below in plain English as:
         *       "For each playerPiece in playerPieces[playerIndex]..."
         */        
        for ( PlayerPiece currentPlayerPieceToCheck : playerPieces[playerIndex] )
        {
            if ( currentPlayerPieceToCheck.getGameBoardLocationIndex() == 7 ) {
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
        System.out.println( "Current states of all player 1 pieces (crabs):");
        for ( int crabIndex = 0; crabIndex < NUMBER_OF_PIECES_PER_PLAYER; crabIndex++ )
        {
            System.out.println( "playerPieces[0]["+crabIndex+"].getGameBoardLocationIndex() = " 
                                    + playerPieces[0][crabIndex].getGameBoardLocationIndex() );
        } // end for
        
        System.out.println(); // blank line for readability
        
        System.out.println( "Current states of all player 2 pieces (lobsters):");
        for ( int lobsterIndex = 0; lobsterIndex < NUMBER_OF_PIECES_PER_PLAYER; lobsterIndex++ )
        {
            System.out.println( "playerPieces[1]["+lobsterIndex+"].getGameBoardLocationIndex() = " 
                                    + playerPieces[1][lobsterIndex].getGameBoardLocationIndex() );
        } // end for
        
        System.out.println(); // blank line for readability
        
        System.out.println( "Current states of all spaces on GameBoard:");
        for ( int spaceIndex = 0; spaceIndex < 7; spaceIndex++ )
        {
            System.out.println( "spaces["+spaceIndex+"].isOccupiedByPieceForPlayerIndex(0) = " 
                                    + spaces[spaceIndex].isOccupiedByPieceForPlayerIndex(0) );
            System.out.println( "spaces["+spaceIndex+"].isOccupiedByPieceForPlayerIndex(1) = " 
                                    + spaces[spaceIndex].isOccupiedByPieceForPlayerIndex(1) ); 
        } // end for
        
        System.out.println(); // blank line for readability
        
    } // end method logCurrentStateOfGameBoard
    
} // end class GameBoard
 