import java.util.Random;
import java.lang.Math;

public class generation_mutation{

    public static int length         = 1024;
    public static int pop            = 2;
    public static double p_crossover = .99;
    public static double p_mutation  = .01;
    public static int[][] population = new int[pop][length];

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
        for(int i = 0; i < pop; i++){
            System.out.println("Individual: " + i);
            for(int j = 0; j < length; j ++){
                System.out.print(population[i][j]);
            }
            System.out.println("");
        }
    }


    // Cross Over in ring fashion.
    public static void crossOver(int firstInd, int secondInd){
        Random randGen = new Random();
        int cut        = showRandomInteger(0, (length - 1), randGen);
        int[] aux      = new int[(length - cut)];
        int k          = 0;
        System.out.println("Cut: " + cut);
         // Exchange encoding:
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
    }

    public static void mutation(int indMutation){
        Random randGen = new Random();
        int mute       = showRandomInteger(0, (length - 1), randGen);
        System.out.println("Mute: " + mute);
        population[indMutation][mute] = population[indMutation][mute] ^ 1;
    }

    // Fitness function:
    public static double fitness(int[] string1, int[] string2){
        double inter = 0;
        for(int i = 0; i < length; i++){
            if(string1[i] == string2[i]){
                inter = inter + 1;
            }
        }
        return inter/length;
    }


    public static double decode(int[] code){
        double decode = 0;
        for(int i = 0; i < code.length; i++){
            decode = decode + code[i]*Math.pow(2,i);
        }
        return decode;
    }

    public static void printTape(int[] tape){
        System.out.println("\n=======================");
        for(int i = 0; i < tape.length; i ++){
            System.out.print(tape[i]);
        }
        System.out.println("\n=======================");
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
        while(k < maxIters && position < tape.length && position > 0){
            System.out.println("\n ITER = " + k);
            System.out.println("\n Current State = " + next_state);
            System.out.println("\n Current Position = " + position);
            int i = 0;
            while(i < nStates){
                state[i] = machineEncode[next_state + i];
                i++;
            }
            // Start reading code
            next_state = (int)decode(state);
            System.out.println("\n Next State = " + next_state);

            // Write in tape.
            tape[position] = machineEncode[next_state + i + 1];
            System.out.println("\n Wrote = " + tape[position]);
            // Move position.
            position = position + (int)Math.pow(-1, machineEncode[next_state + i + 2]);
            System.out.println("\n Move = " + position);
            // Increase counter.
            k++;
            printTape(tape);
        }
        return tape;
    }

    // Main Class
    public static void main(String args[]){
        popGeneration();
        printPop();
        /*System.out.println("\n ======= CROSS OVER ======== \n");
        crossOver(0, 1);
        printPop();
        System.out.println("\n ======= MUTATIION ======== \n");
        mutation(0);
        printPop();
        System.out.println("\n ======= FITNESS ======== \n");
        System.out.println(fitness(population[0], population[1]));
        System.out.println("\n ======= DECODE ======== \n");
        System.out.println(decode(population[0]));
        */
        turingMachine(population[0], 100, 64, 100);
    }
}
