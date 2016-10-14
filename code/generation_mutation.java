import java.util.Random;

public class generation_mutation{

    public static int length         = 12;//1024;
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
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        long range       = (long)aEnd - (long)aStart + 1;
        long fraction    = (long)(range * aRandom.nextDouble());
        int randomNumber =  (int)(fraction + aStart);
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
        // Remaining ring
        for(int i = cut; i < (length - 1); i ++){
            aux[k]                   = population[firstInd][i];
            population[firstInd][i]  = population[secondInd][i];
            population[secondInd][i] = aux[k];
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

    // Main Clas
    public static void main(String args[]){
        popGeneration();
        printPop();
        crossOver(0, 1);
        printPop();
        mutation(0);
        printPop();
    }
}
