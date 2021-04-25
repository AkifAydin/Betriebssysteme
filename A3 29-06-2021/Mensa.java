import java.util.concurrent.locks.*;
import java.util.stream.*;
import java.util.Arrays;

public class SimRace // Mensa
{
    static final int KundenAnzahl = 10;
    static final int KassenAnzahl = 3;
    static final int InterruptZeit = 5000;

    public static void main(String[] args)
    {
        Kasse[] kassen = new Kasse[KassenAnzahl];

        for (int i = 0; i < KassenAnzahl; i++)
        {
             kassen[i] = new Kasse(i + 1);
        }

        Kunde[] kumden = Stream.generate(() -> new Kunde(kassen.clone())).limit(KundenAnzahl).toArray(Kunde[]::new);

        for (int i = 0; i < KundenAnzahl; i++)
        {
            kumden[i].start();
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

    static class Kasse implements Comparable<Kasse>
    {
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantLock schhlangen_lock = new ReentrantLock();
        private int kassen_nummer;
        private int anstehende_kunden;

        public Kasse(int kassen_nummer)
        {
            this.kassen_nummer = kassen_nummer;
            this.anstehende_kunden = 0;
        }

        public int compareTo(Kasse compareKasse)
        {
            return this.getAnstehendeKunden() - compareKasse.getAnstehendeKunden();
        }

        public int getAnstehendeKunden()
        {
            int schlange = 0;
            this.schhlangen_lock.lock();
            schlange = this.anstehende_kunden;
            this.schhlangen_lock.unlock();
            return schlange;
        }

        private void modifiziereKundenZeahler(int anzahl)
        {
            this.schhlangen_lock.lock();
            this.anstehende_kunden += anzahl;
            this.schhlangen_lock.unlock();
        }

        public void kundeAbfertigen()
        {
            modifiziereKundenZeahler(1);
            this.lock.lock();
            try
            {
                // Hier könnte Code-Logik sein, welche den Thread blockiert
                System.err.println("Kasse " + this.kassen_nummer + " kassiert ab!");
            }
            finally
            {
                this.lock.unlock();
            }
            modifiziereKundenZeahler(-1);
        }
    }

    static class Kunde extends Thread
    {
        private Kasse[] kassen_liste;

        public Kunde(Kasse[] kassen_liste)
        {
            this.kassen_liste = kassen_liste;
        }

        public void run()
        {
            try
            {
                while (!Thread.interrupted())
                {
                   Arrays.sort(kassen_liste);
                   kassen_liste[0].kundeAbfertigen();
                   Thread.sleep((int) (1000 * Math.random()));
                }
            }
            catch (InterruptedException e) {}
        }
    }
}