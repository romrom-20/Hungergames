package games;

import java.util.ArrayList;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {
         int numDistricts = StdIn.readInt();  
         for (int i = 0; i < numDistricts; i++) {
            int districtID = StdIn.readInt();  
            District district = new District(districtID);
            districts.add(district);  
    }
}
        // WRITE YOUR CODE HERE

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople (String filename) {
        int numPeople = StdIn.readInt();
        for (int i = 0; i < numPeople; i++) {
            String firstName = StdIn.readString();
            String lastName = StdIn.readString();
            int birthMonth = StdIn.readInt();
            int age = StdIn.readInt();
            int districtID = StdIn.readInt();
            int effectiveness = StdIn.readInt();
            Person person = new Person(birthMonth, firstName, lastName, age, districtID, effectiveness);
        if (age >= 12 && age < 18) {
            person.setTessera(true);
        }
        for (District district : districts) {
            if (district.getDistrictID() == districtID) {
                if (birthMonth % 2 == 0) {
                    district.addEvenPerson(person);
                } else {
                    district.addOddPerson(person);
                }
                break;
            }
        }
    }
  
}  // WRITE YOUR CODE HERE

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {
       //bse case
        if( root == null){
            root = new TreeNode(newDistrict, null, null);
            game = root;
            districts.remove(newDistrict);
            return;
        }
        //  the next cases will be if DID is smaller ledt and righ respectively
        if (newDistrict.getDistrictID()< root.getDistrict().getDistrictID()){
            if ( root.getLeft() == null){
                root.setLeft(new TreeNode(newDistrict, null ,null));
                districts.remove(newDistrict);
            } else {
                addDistrictToGame(root.getLeft(), newDistrict);
            }
            } else if (newDistrict.getDistrictID()> root.getDistrict().getDistrictID()){
                if( root.getRight() == null){
                    root.setRight(new TreeNode(newDistrict, null, null));
                    districts.remove(newDistrict);
                } else {
                    addDistrictToGame(root.getRight(), newDistrict);
                }
            }
        }
    

        // WRITE YOUR CODE HERE
    
    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {
        TreeNode current = game; // starts at thre root 
        if( current == null){
            System.out.println("The tree is empty ");
        }
        while ( current!= null){
            System.out.println("Checking district with ID " + current.getDistrict().getDistrictID());
            if (current.getDistrict().getDistrictID()== id){
                System.out.println(" Found  district with ID: " + id);
                return current.getDistrict();
            } else if ( id < current.getDistrict().getDistrictID()){
                current = current.getLeft();
            } else {
                current= current.getRight();
            } 
        }

        System.out.println(" District not found.");
        // WRITE YOUR CODE HERE

        return null; // update this line
    }

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
         Person person1 = findthePerson(true, true, -1);
         Person person2 = findthePerson(false, true, person1 != null ? person1.getDistrictID() : -1);
         if (person1 == null) {
             person1 = findthePerson(true, false, person2 != null ? person2.getDistrictID() : -1);
         }
         if (person2 == null) {
             person2 = findthePerson(false, false, person1 != null ? person1.getDistrictID() : -1);
         }
         if (person1 != null && person2 != null) {
             removePersonFromDistrict(person1);
             removePersonFromDistrict(person2);
             return new DuelPair(person1, person2);
         }
         return null;
         // findas a person with tessera from odd , even population not from same district, finding people from odd or even population
     }
     private Person findthePerson(boolean isOdd, boolean withTessera, int excludeDistrictId) {
         return findPersonRecursively(getRoot(), isOdd, withTessera, excludeDistrictId);
     }
     private Person findPersonRecursively(TreeNode node, boolean isOdd, boolean withTessera, int excludeDistrictId) {
         if (node == null) {
             return null;
         }
         District district = node.getDistrict();
         ArrayList<Person> population = isOdd ? district.getOddPopulation() : district.getEvenPopulation();
         if (district.getDistrictID() != excludeDistrictId) {
             for (Person person : population) {
                 if (withTessera && person.getTessera()) {
                     return person;
                 }// if in the same district then change
             }
             if (!withTessera && !population.isEmpty()) {
                 int randomIndex = StdRandom.uniform(population.size());
                 return population.get(randomIndex);
             }
         }// searching subtrees
         Person leftfind = findPersonRecursively(node.getLeft(), isOdd, withTessera, excludeDistrictId);
         if (leftfind != null) {
             return leftfind;
         }
         return findPersonRecursively(node.getRight(), isOdd, withTessera, excludeDistrictId);
     }
     private void removePersonFromDistrict(Person person) {
         District district = findDistrict(person.getDistrictID());
         ArrayList<Person> population = (person.getBirthMonth() % 2 == 0) ? district.getEvenPopulation() : district.getOddPopulation();
         population.remove(person);
     }
 
    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        TreeNode parent = null;
        TreeNode current = game;  
        boolean isLeftChild = false;
        while (current != null && current.getDistrict().getDistrictID() != id) {
            parent = current;
            if (id < current.getDistrict().getDistrictID()) {
                isLeftChild = true;
                current = current.getLeft();
            } else {
                isLeftChild = false;
                current = current.getRight();
            }
        }
        if (current == null) {
            return;
        }
        if (current.getLeft() == null && current.getRight() == null) {
            if (parent == null) {
                game = null;
            } else if (isLeftChild) {
                parent.setLeft(null);
            } else {
                parent.setRight(null);
            }
        }
        else if (current.getLeft() == null) { // 1 child
            if (parent == null) {
                game = current.getRight();
            } else if (isLeftChild) {
                parent.setLeft(current.getRight());
            } else {
                parent.setRight(current.getRight());
            }
        } else if (current.getRight() == null) {
            if (parent == null) {
                game = current.getLeft();
            } else if (isLeftChild) {
                parent.setLeft(current.getLeft());
            } else {
                parent.setRight(current.getLeft());
            }
        }
        else { // 2 child
            TreeNode successorParent = current;
            TreeNode successor = current.getRight();
            while (successor.getLeft() != null) {
                successorParent = successor;
                successor = successor.getLeft();
            }
            if (successorParent != current) {
                successorParent.setLeft(successor.getRight());
            } else {
                successorParent.setRight(successor.getRight());
            }
            current.setDistrict(successor.getDistrict());
            if (parent == null) {
                game = current;
            } else if (isLeftChild) {
                parent.setLeft(current);
            } else {
                parent.setRight(current);
            }
        }
    }

        // WRITE YOUR CODE HERE


    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        Person person1 = pair.getPerson1();
        Person person2 = pair.getPerson2();
        if (person1 == null || person2 == null) {
            Person existingPerson = (person1 != null) ? person1 : person2;
            District district = findDistrict(existingPerson.getDistrictID());
            if (existingPerson.getBirthMonth() % 2 == 0) {
                district.getEvenPopulation().add(existingPerson);
            } else {
                district.getOddPopulation().add(existingPerson);
            }
            return;
            // checking complate and incomplete pairs 
        }
        Person winner = person1.duel(person2);
        Person loser = (winner == person1) ? person2 : person1;
        District winnerDistrict = findDistrict(winner.getDistrictID());
        if (winner.getBirthMonth() % 2 == 0) {
            winnerDistrict.getEvenPopulation().add(winner);
        } else {
            winnerDistrict.getOddPopulation().add(winner);
        }
        District loserDistrict = findDistrict(loser.getDistrictID());
        if (loserDistrict.getOddPopulation().isEmpty() || loserDistrict.getEvenPopulation().isEmpty()) {
            eliminateDistrict(loserDistrict.getDistrictID());
        }
        if (winnerDistrict.getOddPopulation().isEmpty() || winnerDistrict.getEvenPopulation().isEmpty()) {
            eliminateDistrict(winnerDistrict.getDistrictID());
        }
    }


        // WRITE YOUR CODE HERE
    

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
