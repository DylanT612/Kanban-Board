import java.io.*;
import java.util.*;

// card class with variables
class Card {
    String title;
    String deadline;
    String priorityLevel;
    String info;

    // card object that must have title and defaults for other settings
    public Card(String title) {
        this.title = title;
        this.deadline = "NA";
        this.priorityLevel = "NA";
        this.info = "";
    }

    // card object with everything filled
    public Card(String title, String deadline, String priorityLevel, String info) {
        this.title = title;
        this.deadline = deadline;
        this.priorityLevel = priorityLevel;
        this.info = info;
    }


    // getter methods for fields
    public String getTitle() {
        return title;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public String getInfo() {
        return info;
    }
}

// kanbanBoard class
class KanbanBoard {
    private ArrayList<Card> toDo;
    private ArrayList<Card> inProgress;
    private ArrayList<Card> finalizing;
    private ArrayList<Card> complete;


    // each as new arrayList
    public KanbanBoard() {
        toDo = new ArrayList<>();
        inProgress = new ArrayList<>();
        finalizing = new ArrayList<>();
        complete = new ArrayList<>();
    }


    // getter methods for fields
    public ArrayList<Card> getToDo() {
        return toDo;
    }

    public ArrayList<Card> getInProgress() {
        return inProgress;
    }

    public ArrayList<Card> getFinalizing() {
        return finalizing;
    }

    public ArrayList<Card> getComplete() {
        return complete;
    }

    // moveCard method moves card from one section to another
    public void moveCard(Card card, ArrayList<Card> sourceList, ArrayList<Card> destinationList, String section) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Would you like to move the task \"" + card.title + "\" to " + section + "? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            // remove the card from the source list
            boolean removedFromSource = sourceList.remove(card);
            if (removedFromSource) {
                // add the card to the destination list
                destinationList.add(card);
                System.out.println("Task moved successfully to " + section + ".");
            } else {
                System.out.println("Failed to remove task from source list.");
            }
        } else {
            System.out.println("Move cancelled.");
        }
    }


    // createCard method allows input for each card field
    public Card createCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the title (mandatory):");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Please try again.");
            return createCard();
        }

        System.out.println("Enter the deadline (optional, press Enter to skip):");
        String deadline = scanner.nextLine().trim();

        System.out.println("Enter the priority level (optional, press Enter to skip):");
        String priorityLevel = scanner.nextLine().trim();

        System.out.println("Enter the info (optional, press Enter to skip):");
        String info = scanner.nextLine().trim();

        // new card needs title other fields are handled
        return new Card(title, deadline.isEmpty() ? "NA" : deadline,
                priorityLevel.isEmpty() ? "NA" : priorityLevel,
                info.isEmpty() ? "" : info);
    }


    // saveTasksToFile writes section to output file
    public void saveTasksToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writeCardsToFile(writer, "ToDo", toDo);
            writeCardsToFile(writer, "InProgress", inProgress);
            writeCardsToFile(writer, "Finalizing", finalizing);
            writeCardsToFile(writer, "Complete", complete);
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
    }


    // writeCardsToFIle writes cards to file
    private void writeCardsToFile(PrintWriter writer, String section, ArrayList<Card> cards) {
        writer.println(section);
        // writes card fields line by line
        for (Card card : cards) {
            writer.println(card.title);
            writer.println(card.getDeadline());
            writer.println(card.getPriorityLevel());
            writer.println(card.getInfo());
        }
        // empty line
        writer.println();
    }


    // loadTasksFromFile reads from the file
    public void loadTasksFromFile(String fileName) {
        // read from file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<Card> section = getSection(line);
                if (section != null) {
                    // assign fields line by line
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        String title = line;
                        String deadline = reader.readLine();
                        String priorityLevel = reader.readLine();
                        String info = reader.readLine();
                        section.add(new Card(title, deadline, priorityLevel, info));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks from file: " + e.getMessage());
        }
    }


    // getSection returns section
    private ArrayList<Card> getSection(String sectionName) {
        switch (sectionName) {
            case "ToDo":
                return toDo;
            case "InProgress":
                return inProgress;
            case "Finalizing":
                return finalizing;
            case "Complete":
                return complete;
            default:
                return null;
        }
    }
}



// main class
public class theisKanbanBoard {
    public static void main(String[] args) {
        KanbanBoard kanbanBoard = new KanbanBoard();
        Scanner scanner = new Scanner(System.in);

        // load tasks from file
        kanbanBoard.loadTasksFromFile("tasks.txt");

        // main menu options
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Add a new card");
            System.out.println("2. View cards");
            System.out.println("3. Move a card");
            System.out.println("4. Close");

            // user input
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // depending on user selection run methods
            switch (choice) {
                case 1:
                    // allows user to crete new tasks
                    Card newCard = kanbanBoard.createCard();
                    kanbanBoard.getToDo().add(newCard);
                    break;
                case 2:
                    // brings up sub menu where user can view, edit, delete tasks
                    viewMenu(kanbanBoard, scanner);
                    break;
                case 3:
                    // lets user move a card
                    moveCardMenu(kanbanBoard, scanner);
                    break;
                case 4:
                    // save tasks to file and exit
                    kanbanBoard.saveTasksToFile("tasks.txt");
                    System.out.println("Tasks saved successfully. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // displayAllCards displays all cards in all sections
    private static void displayAllCards(KanbanBoard kanbanBoard) {
        displayCardsInSection("ToDo", kanbanBoard.getToDo());
        displayCardsInSection("InProgress", kanbanBoard.getInProgress());
        displayCardsInSection("Finalizing", kanbanBoard.getFinalizing());
        displayCardsInSection("Complete", kanbanBoard.getComplete());
    }


    // displays cards in specific section
    private static void displayCardsInSection(String sectionName, ArrayList<Card> cards) {
        System.out.println("\n" + sectionName + ":");
        if (cards.isEmpty()) {
            // none available
            System.out.println("No cards in this section.");
        } else {
            // show card info
            for (Card card : cards) {
                System.out.println("Title: " + card.title);
                System.out.println("Deadline: " + card.getDeadline());
                System.out.println("Priority Level: " + card.getPriorityLevel());
                System.out.println("Info: " + card.getInfo());
                System.out.println();
            }
        }
    }


    // moves card from one section to another
    private static void moveCardMenu(KanbanBoard kanbanBoard, Scanner scanner) {
        System.out.println("\nMove a Card:");
        System.out.println("Select a task to move:");

        // gets cards in each section
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(kanbanBoard.getToDo());
        allCards.addAll(kanbanBoard.getInProgress());
        allCards.addAll(kanbanBoard.getFinalizing());
        allCards.addAll(kanbanBoard.getComplete());

        // lists all cards with task numbers
        displayTasksWithNumbers(allCards);

        // select task number
        System.out.print("Enter the task number: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine();

        // if too big or small
        if (taskNumber < 1 || taskNumber > allCards.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        // selection
        Card selectedCard = allCards.get(taskNumber - 1);

        // select where to move
        while (true) {
            System.out.println("\nMove \"" + selectedCard.title + "\" to:");
            System.out.println("1. In Progress");
            System.out.println("2. Finalizing");
            System.out.println("3. Complete");
            System.out.println("4. Cancel");

            System.out.print("Choose an option: ");
            int moveOption = scanner.nextInt();
            scanner.nextLine();

            // move card up a section
            switch (moveOption) {
                case 1:
                    // moves task from To-Do to InProgress
                    kanbanBoard.moveCard(selectedCard, kanbanBoard.getToDo(), kanbanBoard.getInProgress(), "In Progress");
                    return;
                case 2:
                    // moves task from InProgress to Finalizing
                    kanbanBoard.moveCard(selectedCard, kanbanBoard.getInProgress(), kanbanBoard.getFinalizing(), "Finalizing");
                    return;
                case 3:
                    // moves task from Finalizing to Completed
                    kanbanBoard.moveCard(selectedCard, kanbanBoard.getFinalizing(), kanbanBoard.getComplete(), "Complete");
                    return;
                case 4:
                    System.out.println("Move cancelled.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


    // displays titles with 1,2,3,etc. in front
    private static void displayTasksWithNumbers(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i).title);
        }
    }

 // sub menu for view cards
private static void viewMenu(KanbanBoard kanbanBoard, Scanner scanner) {
        // sub menu options
        while (true) {
            System.out.println("\nView Menu:");
            System.out.println("1. View all cards");
            System.out.println("2. Edit a card");
            System.out.println("3. Delete a task from Complete");
            System.out.println("4. Back to Main Menu");

            System.out.print("Choose an option: ");
            int viewOption = scanner.nextInt();
            scanner.nextLine();

            switch (viewOption) {
                case 1:
                    // Display all cards
                    System.out.println("\nAll Cards:");
                    displayAllCards(kanbanBoard);
                    break;
                case 2:
                    // edit a card
                    editCard(kanbanBoard, scanner);
                    break;
                case 3:
                    // deletes a card fom complete
                    deleteTaskFromComplete(kanbanBoard, scanner);
                    break;
                case 4:
                    // Back to Main Menu
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


    // deletes a task if input matches number given
    private static void deleteTaskFromComplete(KanbanBoard kanbanBoard, Scanner scanner) {
        System.out.println("\nDelete a Task from Complete:");
        ArrayList<Card> completeList = kanbanBoard.getComplete();

        // displays tasks
        displayTasksWithNumbers(completeList);

        // select task to delete
        System.out.print("Enter the task number to delete: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine();

        // if number to big or small
        if (taskNumber < 1 || taskNumber > completeList.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        // confirm deletion
        Card taskToDelete = completeList.get(taskNumber - 1);
        System.out.println("Are you sure you want to delete task: " + taskToDelete.getTitle() + "? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            completeList.remove(taskToDelete);
            System.out.println("Task deleted successfully from Complete.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }


    // lets user adjust title, deadline, priority, info
    private static void editCard(KanbanBoard kanbanBoard, Scanner scanner) {
        System.out.println("\nEdit Menu:");
        System.out.println("Select a task to edit:");

        // gets all tasks
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(kanbanBoard.getToDo());
        allCards.addAll(kanbanBoard.getInProgress());
        allCards.addAll(kanbanBoard.getFinalizing());
        allCards.addAll(kanbanBoard.getComplete());

        // enumerates tasks
        displayTasksWithNumbers(allCards);

        // select task
        System.out.print("Enter the task number: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine();

        // if too big or small
        if (taskNumber < 1 || taskNumber > allCards.size()) {
            System.out.println("Invalid task number.");
            return;
        }

        // allows user to edit task fields
        Card card = allCards.get(taskNumber - 1);
        System.out.println("Editing task: " + card.title);
        System.out.println("Enter the new title (press Enter to keep the current title):");
        String newTitle = scanner.nextLine().trim();
        card.title = newTitle.isEmpty() ? card.title : newTitle;
        System.out.println("Enter the new deadline (press Enter to keep the current deadline):");
        String newDeadline = scanner.nextLine().trim();
        card.deadline = newDeadline.isEmpty() ? card.getDeadline() : newDeadline;
        System.out.println("Enter the new priority level (press Enter to keep the current priority level):");
        String newPriorityLevel = scanner.nextLine().trim();
        card.priorityLevel = newPriorityLevel.isEmpty() ? card.getPriorityLevel() : newPriorityLevel;
        System.out.println("Enter the new info (press Enter to keep the current info):");
        String newInfo = scanner.nextLine().trim();
        card.info = newInfo.isEmpty() ? card.getInfo() : newInfo;
        System.out.println("Task edited successfully.");
    }
}

