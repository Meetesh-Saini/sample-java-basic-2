import java.util.*;

public class Main {
    static Scanner sysScanner = new Scanner(System.in);
    static List<Room> worldMap = new ArrayList<>();
    static List<Enemy> enemies = new ArrayList<>();
    static boolean hasKey = false;
    static boolean hasPotion = false;
    static boolean treasureRoomUnlocked = false;
    static int playerHealth = 100;
    static int playerDamage = 10;

    static void initializeWorldMap() {

        worldMap.add(new Room(ROOM_TYPE.ENTRANCE, "The entrance of the world, a starting point."));
        worldMap.add(new Room(ROOM_TYPE.FOREST, "A dense forest. You can find items and enemies here."));
        worldMap.add(new Room(ROOM_TYPE.DUNGEON, "A dark dungeon full of dangers."));
        worldMap.add(new Room(ROOM_TYPE.NPC_ROOM, "A room where a helpful NPC resides."));
        worldMap.add(new Room(ROOM_TYPE.FOREST, "A forest with ancient trees."));
        worldMap.add(new Room(ROOM_TYPE.DUNGEON, "A dungeon filled with traps."));
        worldMap.add(new Room(ROOM_TYPE.TREASURE_ROOM, "The treasure room. The final goal!"));


        worldMap.get(0).addExit("north", 1);
        worldMap.get(1).addExit("south", 0);
        worldMap.get(1).addExit("east", 2);
        worldMap.get(2).addExit("west", 1);
        worldMap.get(2).addExit("north", 3);
        worldMap.get(3).addExit("south", 2);
        worldMap.get(3).addExit("north", 4);
        worldMap.get(4).addExit("south", 3);
        worldMap.get(4).addExit("east", 5);
        worldMap.get(5).addExit("west", 4);
        worldMap.get(5).addExit("north", 6);


        enemies.add(new Enemy("Undefeatable Demon", 100, 20, false));
        enemies.add(new Enemy("Easy Goblin", 30, 5, true));
        enemies.add(new Enemy("Potion Required Ogre", 50, 10, true));


        worldMap.get(1).addEnemy(enemies.get(1));
        worldMap.get(2).addEnemy(enemies.get(2));
        worldMap.get(5).addEnemy(enemies.get(0));
    }

    static COMMANDS getCommand() {
        System.out.print("> ");
        String rawCommand = sysScanner.nextLine().trim().toLowerCase();
        String[] commandSegments = rawCommand.split(" +");

        if (commandSegments.length == 1) {
            return handleSingleCommand(commandSegments[0]);
        } else if (commandSegments.length == 2) {
            if (commandSegments[0].equals("go")) {
                return handleDirectionalCommand(commandSegments[1]);
            } else if (commandSegments[0].equals("use")) {
                return handleUseCommand(commandSegments[1]);
            } else if (commandSegments[0].equals("check") && commandSegments[1].equals("inventory")) {
                return COMMANDS.CHECK_INVENTORY;
            }
        }

        printIncorrectCommandMessage();
        return null;
    }

    private static COMMANDS handleSingleCommand(String command) {
        return switch (command) {
            case "talk" -> COMMANDS.TALK;
            case "attack" -> COMMANDS.ATTACK;
            case "run" -> COMMANDS.RUN;
            case "help" -> COMMANDS.HELP;
            default -> {
                printIncorrectCommandMessage();
                yield null;
            }
        };
    }

    private static COMMANDS handleDirectionalCommand(String direction) {
        return switch (direction) {
            case "north" -> COMMANDS.GO_NORTH;
            case "south" -> COMMANDS.GO_SOUTH;
            case "east" -> COMMANDS.GO_EAST;
            case "west" -> COMMANDS.GO_WEST;
            default -> {
                printIncorrectCommandMessage();
                yield null;
            }
        };
    }

    private static COMMANDS handleUseCommand(String item) {
        return switch (item) {
            case "key" -> COMMANDS.USE_KEY;
            case "potion" -> COMMANDS.USE_POTION;
            default -> {
                printIncorrectCommandMessage();
                yield null;
            }
        };
    }

    private static void printIncorrectCommandMessage() {
        System.out.println("Invalid command. Use `help` command to view all commands.");
    }

    private static void showRoomBanner(int currentRoomIndex) {
        Room currentRoom = worldMap.get(currentRoomIndex);
        System.out.println("=".repeat(20));
        System.out.println("Room: " + currentRoom.type);
        System.out.println("\t" + currentRoom.description);
        System.out.println("Available exits: " + currentRoom.exits.keySet());


        if (!currentRoom.enemies.isEmpty()) {
            System.out.print("Enemies here: ");
            for (Enemy enemy : currentRoom.enemies) {
                System.out.print(enemy.name + " [" + (enemy.health == 0 ? "dead" : enemy.health) + "] ");
            }
            System.out.println();
        }

        if (currentRoom.type == ROOM_TYPE.NPC_ROOM) {
            System.out.println("A helpful NPC is here.");
        }

        System.out.println("=".repeat(20));
    }

    private static int move(int currentRoomIndex, String direction) {
        Room currentRoom = worldMap.get(currentRoomIndex);
        if (currentRoom.exits.containsKey(direction)) {
            int nextRoomIndex = currentRoom.exits.get(direction);
            System.out.println("You move " + direction + " to the " + worldMap.get(nextRoomIndex).type);
            return nextRoomIndex;
        } else {
            System.out.println("You can't go that way.");
            return currentRoomIndex;
        }
    }

    private static void checkInventory() {
        System.out.println("Inventory:");
        if (hasKey) {
            System.out.println("- Key");
        }
        if (hasPotion) {
            System.out.println("- Potion");
        }
        if (!hasKey && !hasPotion) {
            System.out.println("Your inventory is empty.");
        }
    }

    private static void talkToNpc() {
        System.out.println("You talk to the NPC. They give you a potion and a key!");
        hasPotion = true;
        hasKey = true;
    }

    private static void attackEnemy(Enemy enemy) {
        System.out.println("You attack " + enemy.name + "!");
        enemy.takeDamage(playerDamage);
        if (!enemy.isAlive()) {
            System.out.println(enemy.name + " is defeated!");
        } else {
            enemy.attackPlayer(1);
        }
    }

    private static void useKey() {
        if (treasureRoomUnlocked) {
            System.out.println("You've unlocked the treasure room!");
        } else {
            System.out.println("The key doesn't fit here.");
        }
    }

    private static void usePotion() {
        if (hasPotion) {
            System.out.println("You use a potion. You heal 50 health.");
            playerHealth += 50;
            hasPotion = false;
        } else {
            System.out.println("You don't have a potion!");
        }
    }

    private static void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  go <direction>   - Move in the specified direction (north, south, east, west)");
        System.out.println("  check inventory  - Check your inventory for items");
        System.out.println("  talk             - Talk to NPC (if in the NPC room)");
        System.out.println("  attack           - Attack enemies in the current room");
        System.out.println("  run              - Run away from the enemy");
        System.out.println("  use key          - Use the key if applicable in the current room");
        System.out.println("  use potion       - Use a potion to heal yourself");
        System.out.println("  help             - Show this help message");
    }

    public static void main(String[] args) {

        initializeWorldMap();
        int currentRoomIndex = 0;

        while (true) {
            if (playerHealth <= 0) {
                System.out.println("YOU LOST! PLAYER DIED!");
                break;
            } else if (currentRoomIndex == 6) {
                System.out.println("YOU WON!");
                break;
            }

            showRoomBanner(currentRoomIndex);


            System.out.println("Player Health: " + playerHealth);


            COMMANDS command = getCommand();

            if (command != null) {
                switch (command) {
                    case GO_NORTH:
                    case GO_SOUTH:
                    case GO_EAST:
                    case GO_WEST:
                        currentRoomIndex = move(currentRoomIndex, command.name().substring(3).toLowerCase());
                        break;
                    case CHECK_INVENTORY:
                        checkInventory();
                        break;
                    case TALK:
                        if (worldMap.get(currentRoomIndex).type == ROOM_TYPE.NPC_ROOM) {
                            talkToNpc();
                        } else {
                            System.out.println("No one to talk to here.");
                        }
                        break;
                    case ATTACK:

                        for (Enemy enemy : worldMap.get(currentRoomIndex).enemies) {
                            attackEnemy(enemy);
                        }
                        break;
                    case RUN:
                        System.out.println("You run away from the enemy.");
                        break;
                    case USE_KEY:
                        useKey();
                        break;
                    case USE_POTION:
                        usePotion();
                        break;
                    case HELP:
                        showHelp();
                        break;
                }
            }
        }
    }


    enum COMMANDS {
        GO_NORTH, GO_SOUTH, GO_EAST, GO_WEST, CHECK_INVENTORY, TALK, ATTACK, RUN, USE_KEY, USE_POTION, HELP
    }


    enum ROOM_TYPE {
        ENTRANCE, FOREST, DUNGEON, TREASURE_ROOM, NPC_ROOM
    }

    static class Room {
        ROOM_TYPE type;
        String description;
        Map<String, Integer> exits;
        List<Enemy> enemies;

        Room(ROOM_TYPE type, String description) {
            this.type = type;
            this.description = description;
            this.exits = new HashMap<>();
            this.enemies = new ArrayList<>();
        }

        void addExit(String direction, int roomIndex) {
            exits.put(direction, roomIndex);
        }

        void addEnemy(Enemy enemy) {
            enemies.add(enemy);
        }
    }

    static class Enemy {
        String name;
        int health;
        int damage;
        boolean isDefeatable;

        Enemy(String name, int health, int damage, boolean isDefeatable) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.isDefeatable = isDefeatable;
        }

        void attackPlayer(int difficulty) {

            int actualDamage = difficulty * this.damage;
            System.out.println(name + " attacks! You take " + actualDamage + " damage.");

            playerHealth -= actualDamage;
        }

        void takeDamage(int damage) {
            if (this.health == 0) return;
            this.health -= damage;
            System.out.println(name + " takes " + damage + " damage!");
        }

        boolean isAlive() {
            return this.health > 0;
        }
    }
}
