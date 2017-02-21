import java.util.Stack;
import java.util.ArrayList;
/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @author  Voicu Chirtes - modified the original
 * @version 6/8/2014
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> route;
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
        route = new Stack<Room>();
    }

    /**
     * Create all the rooms and link their exits together.
     * 
     */
    private void createRooms()
    {
        Room outside, theatre, pub, lab, office, cellar, basement;
      
        // create the rooms with no items
        outside = new Room("outside the main entrance of the university");
        theatre = new Room("in a lecture theatre");
        lab = new Room("in a computing lab");
        cellar = new Room("in the cellar");       
        basement = new Room("in the basement");
        
        //create items (description,weight in kilos,can be picked up)
        Item counter, stool, computer, desk, chair;       
        counter = new Item("counter",100,false);
        stool = new Item("stool",7,true);
        computer = new Item("computer",9.5,true);
        desk = new Item("desk",65,false);
        chair = new Item("chair",8.5,true);
        
        //create SaleItems (description, weight in kilos, cost in USD)        
        SaleItem beer, chips;
        beer = new SaleItem("beer",0.5,4.5);
        chips = new SaleItem("chips",0.1,2);        
        
        //create rooms with items
        ArrayList<Item> pubItems = new ArrayList<Item>();
        pubItems.add(counter);
        pubItems.add(beer);
        pubItems.add(stool);
        pubItems.add(chips);
        pub = new Room("in the campus pub",pubItems);
        
        ArrayList<Item> officeItems = new ArrayList<Item>(); 
        officeItems.add(desk);
        officeItems.add(chair);
        office = new Room("in the computing admin office",officeItems);        
        
        // initialise room exits
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        theatre.setExit("west", outside);
        pub.setExit("east", outside);
        lab.setExit("north", outside);
        lab.setExit("east", office);
        lab.setExit("down", basement);
        basement.setExit("up",lab);
        office.setExit("west", lab);
        office.setExit("down", cellar);
        cellar.setExit("up", office);
        
        currentRoom = outside;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help"))
            printHelp();
        else if (commandWord.equals("go"))
            goRoom(command);
        else if (commandWord.equals("back"))
            goBack();
        else if (commandWord.equals("quit"))
            wantToQuit = quit(command);

        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println("   go back quit help");
    }

    /** 
     * Try to go to one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            route.push(currentRoom);
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    
    /**
     * This method returns the display information on the current location available exits 
     * 
     * @return  the current location available exits in one string + \n
     */
    private String printLocationInfo(Room currentRoom)
    {
        return currentRoom.getExitString();
    }
    
    /** Takes the player back to the previous room, allowing the
     * player to retrace the route through the game 
     */
    private void goBack()
    {
        if(!route.empty()) {
            currentRoom = route.pop();
            System.out.println(currentRoom.getLongDescription());
        }
        else
        System.out.println("Cannot go back, you are in the first room of the game\n"+ currentRoom.getLongDescription());
    }
}
