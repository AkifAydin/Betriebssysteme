import java.util.concurrent.locks.*;

public class Mensa
{
    static final int KundenAnzahl = 10;
    static final int KassenAnzahl = 3;
    static final int InterruptZeit = 5000;

    public static void main(String[] args)
    {
        Kunde[] kumden = new Kunde[KundenAnzahl];
        Kasse[] kassen = new Kasse[KassenAnzahl];

        for (int i = 0; i < KassenAnzahl; i++)
        {
            kassen[i] = new Kasse(i + 1);
        }

        for (int i = 0; i < KundenAnzahl; i++)
        {
            kumden[i].start(); // kassen info übergeben
        }

        try
        {
            Thread.sleep(InterruptZeit);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            for (int i = 0; i < KundenAnzahl; i++)
            {
                try
                {
                    kumden[i].interrupt();
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Kasse
    {
        private ReentrantLock lock = new ReentrantLock();
        private int kassen_nummer;

        public Kasse(int kassen_nummer)
        {
            this.kassen_nummer = kassen_nummer;
        }

        public void kundeAbfertigen()
        {
            this.lock.lock();
            try
            {
                System.err.println("Kasse " + this.kassen_nummer + " kassiert ab!");
            }
            finally
            {
                this.lock.unlock();
            }
        }
    }

    static class Kunde extends Thread
    {
        public Kunde() // Kassenliste
        {

        }

        public void run()
        {
            //  System.err.println("**** Endzustand ****");
            // while thread is not interrupted
                // beste kasse aussuchen
                // locken
                // keine Bezahl aktion?
                // entlocken
                // Thread.sleep((int) (1000 * Math.random()));
        }
    }
}