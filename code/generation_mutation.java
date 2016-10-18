import java.util.Random;
import java.lang.Math;

public class generation_mutation{

    public static int length         = 8;//1024;
    public static int pop            = 2;
    public static int MaxPop         = 10000;
    public static double p_crossover = .99;
    public static double p_mutation  = .01;
    public static int[][] population = new int[MaxPop][length];
    public static boolean verbose    = true;

    // Population generation.
    public static void popGeneration(){
        Random randGen = new Random();
        for(int i = 0; i < pop; i++){
            for(int j = 0; j < length; j ++){
                population[i][j] = randGen.nextInt(2);
            }
        }
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
        Random randGen = new Random();
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
        Random randGen = new Random();
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
    public static int[] turingMachine(int[] machineEncode, int maxIters, int nStates, int tapeLength){
        int[] tape     = new int[tapeLength];  // Tape.
        int   position = (int) (tapeLength / 2);  // Track position in tape.
        int   k        = 0;    // Operation counter
        nStates = (int)(Math.log(nStates) / Math.log(2)); // pass to log 2
        int[] state    = new int[nStates]; // state
        int next_state = (int)decode(state);
        if(verbose == true){
            System.out.print("\n==================================================================");
            System.out.print("\nMachine Simulation ");
            System.out.println("\n==================================================================");
        }
        while(k < maxIters && position < tape.length && position > 0){
            if(verbose == true){
                System.out.println("\n ========================================= ");
                System.out.println("\n ITER = " + k);
                System.out.println("\n Tape Position = " + position);
                System.out.print("\n Current Instruction = [" + next_state/8  + "]: ");
            }
            int i = 0;
            while(i < nStates){
                state[i] = machineEncode[next_state + i];
                System.out.print(state[i]);
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
        return tape;
    }

    public static double[] evaluate(int[] outputString){
        double[] fit = new double[pop];
        for(int i = 0; i < pop; i++){
            fit[i] = fitness(turingMachine(population[i], 100, 64, 100), outputString);
        }
        return fit;
    }

    /*
     * ==========================================
     * SORTING UTILERIES
     * ==========================================
     */

    public static void exch(double[] scores, int i, int j){
        double aux      = scores[i];
        int[] auxInd = population[i];
        // Exchange indexes in scores.
        scores[i] = scores[j];
        scores[j] = aux;
        // Exchange indexes in individuals.
        auxInd        = population[i];
        population[i] = population[j];
        population[j] = auxInd;
    }

    public static void inSort(double[] scores){
        for(int i = 0; i < pop; i++){
            for(int j = i; j > 0 && (scores[j] < scores[j - 1]); j--){
                exch(scores, j, j - 1);
            }
        }
    }

    // Main Class
    public static void main(String args[]){
        popGeneration();
        printPop();
        crossOver(0,1);
        printPop();
        mutation(0);
        printPop();
        /*int[] tape = turingMachine(population[0], 100, 64, 100);
        System.out.println("\n ======= FITNESS ======== \n");
        double[] scores = evaluate(tape);
        for(int i = 0; i < scores.length; i++){
            System.out.println("\n score[i]: " + scores[i]);
        }
        // SORT
        inSort(scores);
        System.out.println("\n ======= SORTED SCORES ======== \n");
        for(int i = 0; i < scores.length; i++){
            System.out.println("\n score[i]: " + scores[i]);
        }
        System.out.println("\n ======= SORTED POP ======== \n");
        scores = evaluate(tape);
        for(int i = 0; i < scores.length; i++){
            System.out.println("\n score[i]: " + scores[i]);
        }
        */
    }
}
