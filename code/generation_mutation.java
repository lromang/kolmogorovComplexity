/* #########################################
 * Luis Manuel Román García
 * luis.roangarci@gmail.com
 * #########################################
 *
 * -----------------------------------------
 * General pourpose routines for the 
 * implementation of genetic algorithms. 
 * The application in question attempts to
 * approximate Kolmogorov's Complexity.
 * -----------------------------------------
 *
 */
import java.util.*;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class generation_mutation{

    public static int length         = 1024;
    public static int pop            = 100;
    public static int seed           = 1234;
    public static int MaxPop         = 100000;
    public static int tapeLength     = 10;
    public static int minVisitStates = 64;
    public static Random randGen     = new Random(seed);
    public static int[][] population = new int[MaxPop][length];
    public static boolean verbose    = false;


    // Population generation.
    public static void popGeneration(){
        for(int i = 0; i < pop; i++){
            for(int j = 0; j < length; j ++){
                population[i][j] = randGen.nextInt(2);
            }
        }
    }

    // Random tape generation.
    public static int[] codeGeneration(){
        int[] code     = new int[tapeLength];
        for(int i = 0; i < tapeLength; i ++){
            code[i] = randGen.nextInt(2);
        }
        return code;
    }

    // Random integer generation
    private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
        long range       = (long)aEnd - (long)aStart + 1;
        long fraction    = (long)(range * aRandom.nextDouble());
        int randomNumber = (int)(fraction + aStart);
        return randomNumber;
    }

    // Print population.
    public static void printPop(){
        if(verbose == true){
            for(int i = 0; i < pop; i++){
                System.out.print("\n==================================================================");
                System.out.print("\nMachine #: " + i + " | Length: " + length + " | Individual instructions: " + length / 8);
                System.out.println("\n==================================================================");
                for(int j = 0; j < length; j ++){
                    if(j % 8 == 0){
                        System.out.print("\n[" + j / 8 + "]: ");
                    }
                    System.out.print(population[i][j]);
                }
                System.out.println("");
            }
        }
    }


    // Cross Over in ring fashion.
    public static void crossOver(int firstInd, int secondInd){
        int cut        = showRandomInteger(0, (length - 1), randGen);
        int[] aux      = new int[(length - cut)];
        int k          = 0;
        if(verbose == true){
            System.out.println("Cut: " + cut);
        }
        // Save parents:
        if(pop < (MaxPop - 1)){
            population[pop]     = population[firstInd].clone();
            population[pop + 1] = population[secondInd].clone();
            // Save childs:
            int top = Math.min(length - cut, length / 2);
            for(int i = 0; i < top; i ++){
                aux[k]                         = population[firstInd][cut + i];
                population[firstInd][cut + i]  = population[secondInd][cut + i];
                population[secondInd][cut + i] = aux[k];
                k ++;
            }
            // Close ring.
            if(aux.length < length/2){
                int dist = length/2 - aux.length;
                int[] aux_circ = new int[dist];
                k = 0;
                for(int i = 0; i < dist; i++){
                    aux_circ[k] = population[firstInd][i];
                    population[firstInd][i]  = population[secondInd][i];
                    population[secondInd][i] = aux_circ[k];
                    k ++;
                }
            }
            pop = pop + 2;
        }else{
            System.out.println("Exceeding population limit at Cross-Over stage!!!");
        }
    }

    /*
     * ==========================================
     * MUTATION
     * ==========================================
     * 'Mutates' (changes) a random bit of a selected
     * individual
     *
     * IN:
     *
     * indMutation (int)  = the id (entry)
     * of the individual to be mutated
     *
     */
    public static void mutation(int indMutation){
        int mute       = showRandomInteger(0, (length - 1), randGen);
        if(verbose == true){
            System.out.println("Mute: " + mute);
        }
        if(pop < (MaxPop -1)){
            population[pop]       = population[indMutation].clone();
            population[pop][mute] = population[pop][mute] ^ 1;
            pop = pop + 1;
        }else{
            System.out.println("Exceeding population limit at Mutation stage!!!");
        }
    }


    /*
     * ==========================================
     * FITNESS
     * ==========================================
     * Evaluates the 'similarity' between the
     * bit patterns: string1 and string2.
     *
     * IN:
     *
     * string1 (int[])  = pattern of 0's and 1's to be
     * evaluated.
     *
     * string2 (int[])  = pattern of 0's and 1's to be
     * evaluated.
     *
     * OUT:
     *
     * inter/length     = normalized similarity between
     * string1 and string2. Inter adds 1 for every matching
     * bit and substracts 1 for every non-matching bit
     */
    public static double fitness(int[] string1, int[] string2){
        double inter = 0;
        for(int i = 0; i < string1.length; i++){
            if(string1[i] == string2[i]){
                inter = inter + 1;
            }else{
                inter = inter - 1;
            }
        }
        return inter/string1.length;
    }

    /*
     * ==========================================
     * DECODE
     * ==========================================
     * Transforms a n bit pattern
     * into a number.
     *
     * IN:
     *
     * decode (int[])  = pattern of 0's and 1's to be
     * transformed into a base 10 number.
     *
     * OUT:
     *
     * decode (double) = base 10 representation of the
     * bit pattern.
     */
    public static double decode(int[] code){
        double decode = 0;
        for(int i = 0; i < code.length; i++){
            decode = decode + code[i]*Math.pow(2,i);
        }
        return decode;
    }

    public static void printTape(int[] tape){
        System.out.println("");
        for(int i = 0; i < tape.length; i ++){
            System.out.print(tape[i]);
        }
        System.out.println("");
    }

    /*
     * turingMachine:
     * Receives an array of integers representing the Turing Machine
     * configuration.
     * n_states = 64 (bits required for description = 6)
     */
    public static int[] turingMachine(int machineIndex, int maxIters, int nStates, int tapeLength){
        int [] machineEncode  = population[machineIndex];
        int[] tape     = new int[tapeLength + 1];  // Tape.
        int   position = (int) ((tapeLength + 1) / 2);  // Track position in tape.
        Set<Integer> visitedStates = new HashSet<Integer>();
        int   k        = 0;    // Operation counter
        nStates = (int)(Math.log(nStates) / Math.log(2)); // pass to log 2
        int[] state    = new int[nStates]; // state
        int next_state = (int)decode(state);
        if(verbose == true){
            System.out.print("\n==================================================================");
            System.out.print("\nMachine Simulation ");
            System.out.println("\n==================================================================");
        }
        while(k < maxIters && position < tape.length && position > 1){
            if(verbose == true){
                System.out.println("\n ========================================= ");
                System.out.println("\n Machine = [" + machineIndex +"]");
                System.out.println("\n ITER = " + k);
                System.out.println("\n Tape Position = " + position);
                System.out.print("\n Current Instruction = [" + next_state/8  + "]: ");
            }
            if(tape[position] == 1){
                next_state = next_state*2;
            }
            // Add visited states.
            visitedStates.add(next_state);
            int i = 0;
            while(i < nStates){
                state[i] = machineEncode[next_state + i];
                i++;
            }
            if(verbose == true){
                System.out.println(machineEncode[next_state + (i + 1)] + "" + machineEncode[next_state + (i + 2)]);
            }
            // Start reading code
            next_state = ((int)decode(state)) * 8;

            if(verbose == true){
                System.out.println("\n Next State = " + next_state/8);
            }
            // Write in tape.
            tape[position] = machineEncode[next_state + i + 1];
            if(verbose == true){
                System.out.println("\n Wrote = " + tape[position]);
            }
            // Move position.
            position = position + (int)Math.pow(-1, machineEncode[next_state + i + 2]);
            if(verbose == true){
                System.out.println("\n Move = " + position);
            }
            // Increase counter.
            k++;
            if(verbose == true){
                System.out.println("\n Tape: ");
                printTape(tape);
            }
        }
        /*if(visitedStates.size() < minVisitStates){
            minVisitStates = visitedStates.size();
        }
        */
        tape[0] = visitedStates.size();
        return tape;
    }

    public static double[] evaluate(int[] evaluationString){
        double[] fit = new double[pop];
        int[] turingString = new int[tapeLength];
        // Evaluate
        for(int i = 0; i < pop; i++){
            int[] aux = turingMachine(i, 100, 64, tapeLength);
            for(int j = 1; j < tapeLength + 1; j++){
                turingString[j - 1] = aux[j];
            }
            fit[i] = fitness(turingString, evaluationString);
        }
        return fit;
    }

    /*
     * ==========================================
     * SORTING UTILERIES
     * ==========================================
     */

    public static void exch(double[] scores, int i, int j){
        double aux   = scores[i];
        int[] auxInd = population[i].clone();
        // Exchange indexes in scores.
        scores[i] = scores[j];
        scores[j] = aux;
        // Exchange indexes in individuals.
        auxInd        = population[i].clone();
        population[i] = population[j].clone();
        population[j] = auxInd.clone();
    }

    public static void inSort(double[] scores){
        for(int i = 0; i < pop; i++){
            for(int j = i; j > 0 && (scores[j] > scores[j - 1]); j--){
                exch(scores, j, j - 1);
            }
        }
    }

    public static int[] toAscii(String A){
        byte[] bytes         = A.getBytes();
        int[] binary_rep     = new int[8*A.length()];
        int k                = 0;
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes){
                int val = b;
                for (int i = 0; i < 8; i++){
                    binary.append((val & 128) == 0 ? 0 : 1);
                    binary_rep[k] = (val & 128) == 0 ? 0 : 1;
                    val <<= 1;
                    k++;
                }
            }
        return binary_rep;
    }

    public static void naturalSelection(int[] evaluationString, int cutPoint){
        double[] scores = evaluate(evaluationString);
        inSort(scores);
        for(int i = (int) Math.floor(pop / cutPoint); i < pop; i++){
            for(int j = 0; j < length; j++){
                population[i][j] = 0;
            }
        }
        pop = (int) Math.floor(pop / cutPoint);
    }

    public static void geneticAlg(int generations, int[] evaluationString,  int cutPoint, double pCross, double pMut){
        int k = 0;
        double maxSimilarity = -1;
        double baseSimilarity = -1;
        double[] scores;
        double[] sortedScores;
        while(k < generations && maxSimilarity < 1){
            int stablePop  = pop;
            int indCross   = (int) Math.floor(stablePop * pCross);
            int indMut     = (int) Math.floor(stablePop * pMut);
            // Cross over
            for(int i = 0; i < (int) Math.floor(stablePop / 2); i++){
                double cross = randGen.nextDouble();
                if(cross > pCross){
                    crossOver(i, stablePop - (i + 1));
                }
            }
            // Mutation
            stablePop = pop;
            for(int i = 0; i < stablePop; i++){
                double mutate = randGen.nextDouble();
                if(mutate > pMut){
                    mutation(i);
                }
            }
            // Natural Selection
            naturalSelection(evaluationString, cutPoint);
            // Get scores
            scores       = evaluate(evaluationString);
            inSort(scores);
            if(scores.length > 0 && maxSimilarity <= scores[0]){
                maxSimilarity = scores[0];
            }
            if(k == 0){
                baseSimilarity = maxSimilarity;
            }
            System.out.println("Generation: " + k);
            System.out.println("\n===================================================\n");
            System.out.println("Max  Similarity: " + maxSimilarity);
            System.out.printf("Improvement from first generation: %.2f",  (maxSimilarity/baseSimilarity - 1)*100);
            System.out.println("%");

            k++;
        }
        System.out.println("\n\n");
        System.out.println("\n===================================================\n");
        System.out.println("\nSUMMARY \n");
        System.out.println("\n===================================================\n");
        System.out.println("\n Best Machine: ");
        for(int i = 0; i < length; i++){
            if(i % 64 == 0){
                System.out.println("");
            }
            System.out.print(population[0][i]);
        }
        System.out.println("");
        System.out.println("\n Objective string: ");
        for(int i = 0; i < evaluationString.length; i++){
            if(i % 10 == 0){
                System.out.println("");
            }
            System.out.print(evaluationString[i]);
        }
        System.out.println("");
        int[] finalString = turingMachine(0, 100, 64, tapeLength);
        System.out.println("\n Closest String: ");
        for(int i = 1; i < finalString.length; i++){
            if(i % 10 == 0){
                System.out.println("");
            }
            System.out.print(finalString[i]);
        }
        System.out.println("");
        System.out.println("\nSimilarity: " + maxSimilarity);
        System.out.println("\nNumber of valid states: " + finalString[0]);
        System.out.println("\nKolmogorov Complexity: " + finalString[0]*16);
        System.out.printf("Improvement from first generation: %.2f",  (maxSimilarity/baseSimilarity - 1)*100);
        System.out.println("%");
        System.out.println("\n===================================================\n");
    }

    // Main Class
    public static void main(String args[]){
        Scanner scanner  = new Scanner(System.in);
        int generations  = 300;
        double pCross    = .99;
        double pMutation = .01;
        boolean run      = false;
        /* =============================================
         * Read in Genetic Algorithm parameters.
         * =============================================
         */
        
        System.out.println("========================================");
        System.out.println("========== GENETIC ALGORITHMS ==========");
        System.out.println("========================================\n");
        System.out.println("\nPlease enter a seed for the random generator: ");
        String seed = scanner.next();
        System.out.println("\nYou have chosen seed: " + seed);
        randGen = new Random(Integer.parseInt(seed));
        System.out.println("\nPlease enter a string to match: ");
        String randomChar = scanner.next();
        int[] code        = toAscii(randomChar);
        // Adjust length of individuals.
        tapeLength = randomChar.length()*8;
        while(true){
            System.out.println("\n===============================================================\n");
            System.out.println("These are the current execution parameters:");
            System.out.println("");
            System.out.println("1) Generations: "            + generations);
            System.out.println("2) Individuals: "            + pop);
            System.out.println("3) Length of individuals: "  + length);
            System.out.println("4) Length of tape (not recommended to change): " + tapeLength);
            System.out.println("5) Cross-over probability: " + pCross);
            System.out.println("6) Mutation probability: "   + pMutation);
            System.out.println("7) Tape:\n");
            for(int i = 0; i < randomChar.length()*8; i++){
                            System.out.print(code[i]);
            }
            System.out.println("");
            System.out.println("\n===============================================================\n");
            System.out.println("\nIs this correct? (y/n): ");
            String shouldRun = scanner.next();
            if(shouldRun.charAt(0) == 'y'){
                break;
            }else{
                boolean again;
                do{
                    again = false;
                    System.out.println("\nEnter a number to change the parameter: ");
                    int optionChange = Integer.parseInt(scanner.next());
                    switch (optionChange) {
                    case 1:
                        System.out.println("\nEnter the new value for Generations: ");
                        generations = Integer.parseInt(scanner.next());
                        break;
                    case 2:
                        System.out.println("\nEnter the new value for Individuals: ");
                        pop = Integer.parseInt(scanner.next());
                        break;
                    case 3:
                        System.out.println("\nEnter the new value for Length of individuals: ");
                        length = Integer.parseInt(scanner.next());
                        break;
                    case 4:
                        System.out.println("\nEnter the new value for Length of tape: ");
                        tapeLength = Integer.parseInt(scanner.next());
                        break;
                    case 5:
                        System.out.println("\nEnter the new value for Cross-over probability: ");
                        pCross = Double.parseDouble(scanner.next());
                        break;
                    case 6:
                        System.out.println("\nEnter the new value for Mutation probability: ");
                        pMutation = Double.parseDouble(scanner.next());
                        break;
                    case 7:
                        System.out.println("\nEnter the new string to match: ");
                        randomChar = scanner.next();
                        tapeLength = randomChar.length()*8;
                        code       = toAscii(randomChar);
                        break;
                    default:
                        System.out.println("\nPlease enter a digit between 1 and 7");
                        again = true;
                        break;
                    }
                }while(again);
            }
        }
        /* =============================================
         * Read in Genetic Algorithm parameters.
         * =============================================
         */

        popGeneration();
        if(args.length > 0){
            verbose = true;
        }
        // Objective code.
        // int[] code = codeGeneration();
        System.out.println("\n ======= Genetic Algorithm ======== \n");
        geneticAlg(generations, code, 2, pCross, pMutation);
    }
}
