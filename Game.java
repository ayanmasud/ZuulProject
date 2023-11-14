/*
 * Most of this code is not written by me as you can see below
 * I have edited this code to fit my desires
 *
 * Date: 11/13/23
 * Author: Ayan Masud
 * Teacher: Jason Galbraith
 */

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
 * @version 1.0 (February 2002)
 */

class Game 
{
    private Parser parser;
    private Room currentRoom;
    Room outside1, livingroom1, kitchen, bathroom1, diningroom, recreationalroom, corridor, ayansroom, bathroom2, ehansroom, livingroom2, hallway, gymroom, garage, outside2;
    ArrayList<Item> inventory = new ArrayList<Item>();

    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    public static void main(String[] args) {
        Game mygame = new Game();
        mygame.play();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        outside1 = new Room("outside the main entrance of the house");
        livingroom1 = new Room("in the first living room of the house");
        kitchen = new Room("in the kitchen");
        bathroom1 = new Room("in the first bathroom of the house");
        diningroom = new Room("in the dining room");
        recreationalroom = new Room("in the recreational room");
        corridor = new Room("in the corridor");
        ayansroom = new Room("in the Ayan's bedroom");
        bathroom2 = new Room("in the second bathroom of the house");
        ehansroom = new Room("in the Ehan's bedroom");
        livingroom2 = new Room("in the second living room of the house");
        hallway = new Room("in the hallway");
        gymroom = new Room("in the gym room");
        garage = new Room("in the garage");
        outside2 = new Room("outside the back entrance of the house");

        
        // initialise room exits
        outside1.setExit("north", livingroom1);

        livingroom1.setExit("south", outside1);
        livingroom1.setExit("north", kitchen);
        livingroom1.setExit("east", diningroom);

        kitchen.setExit("south", livingroom1);
        kitchen.setExit("east", bathroom1);

        bathroom1.setExit("west", kitchen);

        diningroom.setExit("west", livingroom1);
        diningroom.setExit("east", recreationalroom);

        recreationalroom.setExit("west", diningroom);
        recreationalroom.setExit("east", corridor);

        corridor.setExit("west", recreationalroom);
        corridor.setExit("south", livingroom2);
        corridor.setExit("north", ayansroom);
        corridor.setExit("east", ehansroom);

        ayansroom.setExit("east", bathroom2);
        ayansroom.setExit("south", corridor);

        bathroom2.setExit("west", ayansroom);

        ehansroom.setExit("west", corridor);

        livingroom2.setExit("north", corridor);
        livingroom2.setExit("east", hallway);

        hallway.setExit("west", livingroom2);
        hallway.setExit("east", gymroom);

        gymroom.setExit("west", hallway);
        gymroom.setExit("north", garage);

        garage.setExit("south", gymroom);
        garage.setExit("north", outside2);

        outside2.setExit("south", garage);

        currentRoom = outside1;  // start game outside

        kitchen.setItem(new Item("Spoon")); // items and room location
        bathroom2.setItem(new Item("Soap"));
        ehansroom.setItem(new Item("Photo"));
        livingroom2.setItem(new Item("Phone"));
        garage.setItem(new Item("Keys"));
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
        System.out.println("Welcome to Adventure!");
        System.out.println("Adventure is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            wantToQuit = goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("inventory")) {
            printInventory();
        }
        else if (commandWord.equals("get")) {
            getItem(command);
        }
        else if (commandWord.equals("drop")) {
            dropItem(command);
        }
        return wantToQuit;
    }

    private void printInventory() {
        String output = "";
        for (int i = 0; i < inventory.size(); i++) {
            output += inventory.get(i).getdescription() + " ";
        }
        System.out.println("You are carrying:");
        System.out.println(output);
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
        parser.showCommands();
    }
    private void dropItem(Command command) // drop item
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = null;
        int index = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i).getdescription().equals(item)){
                newItem = inventory.get(i);
                index = i;
            }
        }

        if (newItem == null)
            System.out.println("That item is not in your inventory!");
        else {
            inventory.remove(index);
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped: " + item);
        }
    }
    private void getItem(Command command) // pick up item
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to get...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = currentRoom.getItem(item);

        if (newItem == null)
            System.out.println("That item is not here!");
        else {
            inventory.add(newItem);
            currentRoom.removeItem(item);
            System.out.println("Picked up: " + item);
        }
    }
    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private boolean goRoom(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return false;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null)
            System.out.println("There is no door!");
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            if(currentRoom == outside2) { // win condition
                System.out.println("\nYou win!");
                return true;
            }
        }
        return false;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game. Return true, if this command
     * quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else
            return true;  // signal that we want to quit
    }
}
