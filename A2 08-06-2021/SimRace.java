import java.util.Random;
import java.util.Arrays;

public class SimRace
{
    static final int RennFahrer = 5;
    static final int RennRunden = 4;

    public static void main(String[] args)
    {
        Car[] fahrer = new Car[RennFahrer];
        Random fahrer_seeds = new Random(System.currentTimeMillis());

        for (int i = 0; i < RennFahrer; i++)
        {
            (fahrer[i] = new SimRace.Car(i, fahrer_seeds.nextInt())).start();
        }

        for (int i = 0; i < RennFahrer; i++)
        {
            try
            {
                fahrer[i].join();
            }
            catch (InterruptedException e)
            { e.printStackTrace(); }
        }

        Arrays.sort(fahrer);

        System.err.println("**** Endzustand ****");

        for (int i = 0; i < RennFahrer; i++)
        {
            System.err.println((i + 1) + ". Platz: Wagen " + fahrer[i].wagen_nummer + " Zeit: " + fahrer[i].renn_zeit);
        }
    }

    static class Car extends Thread implements Comparable<Car>
    {
        private int seed;
        public int wagen_nummer;
        public int renn_zeit;

        public Car(int wagen_nummer, int seed)
        {
            this.wagen_nummer = wagen_nummer;
            this.seed = seed;
            this.renn_zeit = 0;
        }

        public int compareTo(Car compareCar)
        {
            return this.renn_zeit - compareCar.renn_zeit;
        }

        public void run()
        {
            Random rundenglueck = new Random(this.seed);
            int runden = 0;
            long start_zeit = System.nanoTime();

            try
            {
                while (!Thread.interrupted())
                {
                    if (runden >= SimRace.RennRunden)
                    {
                        break;
                    }

                    runden++;
                    Thread.sleep(rundenglueck.nextInt(100));
                }
            }
            catch (InterruptedException e)
            { e.printStackTrace(); }

            this.renn_zeit = (int)(System.nanoTime() - start_zeit);
        }
    }
}