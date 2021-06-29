package com.company;
import java.util.concurrent.locks.*;
import java.util.Arrays;

public class Mensa
{
    static final int KundenAnzahl = 100;
    static final int KassenAnzahl = 3;
    static final int InterruptZeit = 5000;
    static Kasse[] Kassen = null;
    static ReentrantLock SchlangenLock = new ReentrantLock();

    public static void main(String[] args)
    {
        Kassen = new Kasse[KassenAnzahl];

        for (int i = 0; i < KassenAnzahl; i++)
        {
            Kassen[i] = new Kasse(i + 1);
        }

        Kunde[] kunden = new Kunde[KundenAnzahl];

        for (int i = 0; i < KundenAnzahl; i++)
        {
            (kunden[i] = new Kunde()).start();
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
                    kunden[i].interrupt();
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
        private ReentrantLock pay_lock;
        private int kassen_nummer;
        private int anstehende_kunden;

        public Kasse(int kassen_nummer)
        {
            this.pay_lock = new ReentrantLock(true);
            this.kassen_nummer = kassen_nummer;
            this.anstehende_kunden = 0;
        }

        public int compareTo(Kasse compareKasse)
        {
            return this.getAnstehendeKunden() - compareKasse.getAnstehendeKunden();
        }

        public int getAnstehendeKunden()
        {
            return this.anstehende_kunden;
        }

        public void anstehen()
        {
            System.err.println("Kunde stellt sich an Kasse " + this.kassen_nummer + " an : " + this.anstehende_kunden);
            this.anstehende_kunden++;
        }

        public void weggehen()
        {
            this.anstehende_kunden--;
        }

        public void kundeAbfertigen()
        {
            try
            {
                this.pay_lock.lock();
                // Hier könnte Code-Logik sein, welche den Thread blockiert
                Thread.sleep((int) (1000 * Math.random()));
                System.err.println("Kasse " + this.kassen_nummer + " kassiert ab!");
            }
            catch (InterruptedException e) {}
            finally
            {
                this.pay_lock.unlock();
            }
        }
    }

    static class Kunde extends Thread
    {
        public void run()
        {
            try
            {
                while (!Thread.interrupted())
                {
                    Kasse anstehendeKasse = null;
                    SchlangenLock.lock();
                    Arrays.sort(Kassen);
                    (anstehendeKasse = Kassen[0]).anstehen();
                    SchlangenLock.unlock();
                    anstehendeKasse.kundeAbfertigen();
                    SchlangenLock.lock();
                    anstehendeKasse.weggehen();
                    SchlangenLock.unlock();
                    Thread.sleep((int) (200 * Math.random()));
                }
            }
            catch (InterruptedException e) {}
            finally
            {
                if (SchlangenLock.isHeldByCurrentThread())
                {
                    SchlangenLock.unlock();
                }
            }
        }
    }
}